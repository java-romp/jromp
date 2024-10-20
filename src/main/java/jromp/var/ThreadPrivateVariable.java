package jromp.var;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class ThreadPrivateVariable<T extends Serializable> implements Variable<T> {
    private final transient ThreadLocal<T> value;

    public ThreadPrivateVariable(T value) {
        this.value = ThreadLocal.withInitial(() -> value);
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
    public ThreadPrivateVariable<T> copy() {
        return this;
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
