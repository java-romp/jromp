package jromp.parallel.var;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * It is initialized with the default value and keeps the last value set.
 *
 * @param <T> the type of the variable.
 */
public class LastPrivateVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private T value;

    /**
     * The last value set.
     */
    private T lastValue;

    private transient Function<T, Void> endCallback;

    /**
     * Constructs a new private variable with the default value.
     */
    public LastPrivateVariable(T value) {
        this.value = value;
        this.lastValue = this.value;
    }

    @Override
    public T value() {
        return this.lastValue;
    }

    @Override
    public void set(T value) {
        this.value = value;
        this.lastValue = value;
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        this.value = operator.apply(this.value);
        this.lastValue = this.value;
    }

    @Override
    public Variable<T> copy() {
        T defaultValue = (T) InitialValues.getInitialValue(value.getClass());
        LastPrivateVariable<T> lastPrivateVariable = new LastPrivateVariable<>(SerializationUtils.clone(defaultValue));

        lastPrivateVariable.endCallback = (T val) -> {
            this.value = val;
            this.lastValue = val;
            return null;
        };

        return lastPrivateVariable;
    }

    @Override
    public void end() {
        this.value = this.lastValue;

        if (this.endCallback != null) {
            this.endCallback.apply(this.value);
        }
    }

    @Override
    public String toString() {
        return "LastPrivateVariable{value=%s, lastValue=%s}".formatted(this.value, this.lastValue);
    }
}
