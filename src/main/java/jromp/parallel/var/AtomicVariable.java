package jromp.parallel.var;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * A variable that performs atomic operations over its value.
 *
 * @param <T> the type of the variable.
 */
public class AtomicVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private final AtomicReference<T> value;

    /**
     * Constructs a new atomic variable with the given value.
     *
     * @param value the value of the variable.
     */
    public AtomicVariable(T value) {
        this.value = new AtomicReference<>(value);
    }

    @Override
    public T value() {
        return this.value.get();
    }

    @Override
    public void set(T value) {
        this.value.getAndSet(value);
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        this.value.updateAndGet(operator);
    }

    @Override
    public Variable<T> copy() {
        return this;
    }

    @Override
    public void end() {
        // Do nothing (keep the last value).
    }

    @Override
    public String toString() {
        return "AtomicVariable{value=%s}".formatted(this.value.get());
    }
}
