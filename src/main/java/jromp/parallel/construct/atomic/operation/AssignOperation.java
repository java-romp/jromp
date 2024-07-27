package jromp.parallel.construct.atomic.operation;

import java.util.function.UnaryOperator;

public class AssignOperation<T extends Number> implements Operation<T> {
    private final T value;

    public AssignOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "=";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> value;
    }
}
