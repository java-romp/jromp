package jromp.parallel.var;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * It is initialized with the given value.
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

    /**
     * Callback to set the value of the variable when the block ends.
     */
    private transient Consumer<T> endCallback;

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
    @SuppressWarnings("unchecked")
    public Variable<T> copy() {
        T defaultValue = (T) InitialValues.getInitialValue(value.getClass());
        LastPrivateVariable<T> lastPrivateVariable = new LastPrivateVariable<>(SerializationUtils.clone(defaultValue));

        lastPrivateVariable.endCallback = val -> {
            this.value = val;
            this.lastValue = val;
        };

        return lastPrivateVariable;
    }

    @Override
    public void end() {
        this.value = this.lastValue;

        if (this.endCallback != null) {
            this.endCallback.accept(this.value);
        }
    }

    @Override
    public String toString() {
        return "LastPrivateVariable{value=%s, lastValue=%s}".formatted(this.value, this.lastValue);
    }
}
