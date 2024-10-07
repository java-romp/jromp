package jromp.var;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * It is initialized with the given value.
 *
 * @param <T> the type of the variable.
 */
public class PrivateVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private T value;

    /**
     * Constructs a new private variable with the given value.
     *
     * @param value the value of the variable.
     */
    public PrivateVariable(T value) {
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
    @SuppressWarnings("unchecked")
    public PrivateVariable<T> copy() {
        T defaultValue = (T) InitialValues.getInitialValue(value.getClass());
        return new PrivateVariable<>(SerializationUtils.clone(defaultValue));
    }

    @Override
    public void end() {
        // Do nothing
    }

    @Override
    public String toString() {
        return "PrivateVariable{value=%s}".formatted(value);
    }
}
