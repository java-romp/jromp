package jromp.var;

import jromp.concurrent.JrompThreadLocal;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A variable that is private to each thread.
 * It is initialized with the given value.
 *
 * @param <T> the type of the variable.
 */
public class ThreadPrivateVariable<T extends Serializable> implements Variable<T> {
    /**
     * The thread local variable that holds the value of the variable.
     */
    private final transient JrompThreadLocal<T> value;

    /**
     * Constructs a new thread private variable with the given value.
     *
     * @param value the initial value of the variable.
     */
    public ThreadPrivateVariable(T value) {
        this.value = JrompThreadLocal.withInitial(() -> value);
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
        return "ThreadPrivateVariable{value=%s}".formatted(this.value.get());
    }
}
