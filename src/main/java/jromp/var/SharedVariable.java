package jromp.var;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A variable that is shared between threads.
 *
 * @param <T> the type of the variable.
 */
public class SharedVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private T value;

    /**
     * The atomic variable representation of the shared variable. This is used
     * when the shared variable is converted to an atomic variable.
     */
    private AtomicVariable<T> atomicVariable;

    /**
     * Constructs a new shared variable with the given value.
     *
     * @param value the value of the variable.
     */
    public SharedVariable(T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        this.value = operator.apply(this.value);
    }

    @Override
    public SharedVariable<T> copy() {
        return this;
    }

    @Override
    public void end() {
        if (this.atomicVariable != null) {
            this.value = this.atomicVariable.value();
            this.atomicVariable = null;
        }
    }

    @Override
    public String toString() {
        return "SharedVariable{value=%s}".formatted(value);
    }

    /**
     * Converts a {@link SharedVariable} to an {@link AtomicVariable}. It only
     * creates the atomic variable once and returns the same instance on
     * subsequent calls.
     *
     * @return the {@link AtomicVariable} representation of the shared variable.
     */
    public AtomicVariable<T> toAtomic() {
        if (this.atomicVariable == null) {
            this.atomicVariable = new AtomicVariable<>(this.value);
        }

        return this.atomicVariable;
    }

    /**
     * Checks if the shared variable has an atomic variable representation.
     *
     * @return {@code true} if the shared variable has an atomic variable,
     * {@code false} otherwise.
     */
    public boolean hasAtomic() {
        return this.atomicVariable != null;
    }
}
