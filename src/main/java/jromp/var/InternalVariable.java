package jromp.var;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * This type of variable is intended for internal use only.
 *
 * @param <T> the type of the variable.
 */
class InternalVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private T value;

    /**
     * Constructs a new internal variable with the given value.
     *
     * @param value the value of the variable.
     */
    public InternalVariable(T value) {
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
    public void end() {
        // Do nothing
    }

    @Override
    public String toString() {
        return "InternalVariable{value=%s}".formatted(this.value);
    }
}
