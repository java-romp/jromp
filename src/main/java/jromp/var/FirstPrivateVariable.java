package jromp.var;

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
    private final transient ThreadLocal<T> value;

    /**
     * The thread that created this variable.
     */
    private final transient Thread creatorThread = Thread.currentThread();

    /**
     * The initial value of the variable.
     */
    private T initialValue;

    /**
     * Constructs a new private variable with the given value.
     *
     * @param value the value of the variable.
     */
    public FirstPrivateVariable(T value) {
        // Creator thread takes the same value as the other threads.
        this.value = ThreadLocal.withInitial(() -> this.initialValue);

        this.initialValue = value;
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
            this.initialValue = value;
        }

        this.value.set(value);
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        if (Thread.currentThread() == creatorThread) {
            this.initialValue = operator.apply(this.initialValue);
        }

        this.value.set(operator.apply(this.value.get()));
    }

    @Override
    public void end() {
        if (Thread.currentThread() == creatorThread) {
            return;
        }

        // Remove the value from the other threads, not the creator one.
        this.value.remove();
    }

    @Override
    public String toString() {
        return "FirstPrivateVariable{value=%s}".formatted(this.value.get());
    }
}
