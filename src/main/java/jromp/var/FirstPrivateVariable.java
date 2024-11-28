package jromp.var;

import jromp.concurrent.JrompThreadLocal;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * Same as {@link PrivateVariable}, but the initial value of the variable (on the worker threads) is the value
 * set by the creator thread prior to the execution of the parallel region.
 *
 * @param <T> the type of the variable.
 */
public class FirstPrivateVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private final transient JrompThreadLocal<T> value;

    /**
     * The thread that created this variable.
     */
    private final transient Thread creatorThread = Thread.currentThread();

    /**
     * The value of the variable prior to the parallel region.
     */
    private T priorValue;

    /**
     * Constructs a new private variable with the given value.
     *
     * @param value the value of the variable.
     */
    public FirstPrivateVariable(T value) {
        // Creator thread takes the same value as the other threads.
        this.value = JrompThreadLocal.withInitial(() -> this.priorValue);

        this.priorValue = value;
        this.value.set(value);
        // ^^^
        // Prevent the current thread from getting the initial value from the Supplier callback.
        // If this is not done, double update can happen and the initial value will be wrong.
    }

    @Override
    public T value() {
        return this.value.get();
    }

    @Override
    public void set(T value) {
        if (Thread.currentThread() == creatorThread) {
            this.priorValue = value;
        }

        this.value.set(value);
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        if (Thread.currentThread() == creatorThread) {
            this.priorValue = operator.apply(this.priorValue);
        }

        this.value.set(operator.apply(this.value.get()));
    }

    @Override
    public void end() {
        this.value.remove();
        /*
         * NOTE: if the variable is used after the parallel region, the value will be restored from
         * the one kept in the initialValue field (value prior to the parallel region).
         */
    }

    @Override
    public String toString() {
        return "FirstPrivateVariable{value=%s}".formatted(this.value.get());
    }
}
