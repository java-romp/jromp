package jromp.var;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static jromp.Utils.castClass;
import static jromp.var.InitialValues.getInitialValue;

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
    private final transient ThreadLocal<T> value;

    /**
     * The last value set.
     */
    private T lastValue;

    /**
     * Callback to set the value of the variable when the block ends.
     */
    private final transient Consumer<T> endCallback;

    /**
     * Constructs a new private variable with the default value.
     */
    public LastPrivateVariable(T value) {
        this.value = ThreadLocal.withInitial(() -> getInitialValue(castClass(value.getClass())));
        this.lastValue = value;
        this.endCallback = val -> {
            this.value.set(val);
            this.lastValue = val;
        };
    }

    @Override
    public T value() {
        return this.lastValue;
    }

    @Override
    public void set(T value) {
        this.value.set(value);
        this.lastValue = value;
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        this.value.set(operator.apply(this.value.get()));
        this.lastValue = this.value.get();
    }

    @Override
    public void end() {
        this.value.set(this.lastValue);

        if (this.endCallback != null) {
            this.endCallback.accept(this.value.get());
        }

        this.value.remove();
    }

    @Override
    public String toString() {
        return "LastPrivateVariable{value=%s, lastValue=%s}".formatted(this.value.get(), this.lastValue);
    }
}
