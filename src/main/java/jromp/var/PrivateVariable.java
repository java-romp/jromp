package jromp.var;

import jromp.concurrent.JrompThreadLocal;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import static jromp.Utils.castClass;
import static jromp.var.InitialValues.getInitialValue;

/**
 * A variable that is not shared between threads.
 * The creator thread has the initial value of the variable. The worker threads have their own private variables,
 * initialized with the default value for the type.
 * After the end method is called, the value of the variable is removed, and the creator thread restores
 * the value prior to the execution of the parallel region.
 *
 * @param <T> the type of the variable.
 */
public class PrivateVariable<T extends Serializable> implements Variable<T> {
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
    public PrivateVariable(T value) {
        this.value = JrompThreadLocal.withInitial(() -> getInitialValue(castClass(value.getClass())));

        this.priorValue = value;
        this.value.set(value); // Value for the creator thread.
        // ^^^
        // Prevent the current thread from getting the initial value from the Supplier callback.
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

        if (Thread.currentThread() == creatorThread) {
            // We must restore the prior value for the creator thread.
            this.value.set(this.priorValue);
        }
    }

    @Override
    public String toString() {
        return "PrivateVariable{value=%s}".formatted(this.value.get());
    }
}
