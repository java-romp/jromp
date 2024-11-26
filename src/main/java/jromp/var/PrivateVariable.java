package jromp.var;

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
    private final transient ThreadLocal<T> value;

    /**
     * The thread that created this variable.
     */
    private final transient Thread creatorThread = Thread.currentThread();

    /**
     * Constructs a new private variable with the given value.
     *
     * @param value the value of the variable.
     */
    public PrivateVariable(T value) {
        this.value = ThreadLocal.withInitial(() -> getInitialValue(castClass(value.getClass())));
        this.value.set(value); // Value for the creator thread.
    }

    @Override
    public T value() {
        return this.value.get();
    }

    @Override
    public void set(T value) {
        this.value.set(value);
    }

    @Override
    public void update(UnaryOperator<T> operator) {
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
        return "PrivateVariable{value=%s}".formatted(this.value.get());
    }
}
