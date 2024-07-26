package jromp.parallel.var;

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
        // Do nothing (keep the last value).
    }

    @Override
    public String toString() {
        return "SharedVariable{value=%s}".formatted(value);
    }
}
