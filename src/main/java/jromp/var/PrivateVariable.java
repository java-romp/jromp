package jromp.var;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import static jromp.Utils.castClass;
import static jromp.var.InitialValues.getInitialValue;

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
    private final transient ThreadLocal<T> value;

    /**
     * Constructs a new private variable with the given value.
     *
     * @param value the value of the variable.
     */
    public PrivateVariable(T value) {
        this.value = ThreadLocal.withInitial(() -> getInitialValue(castClass(value.getClass())));
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
        this.value.remove();
    }

    @Override
    public String toString() {
        return "PrivateVariable{value=%s}".formatted(this.value.get());
    }
}
