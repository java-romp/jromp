package jromp.parallel.construct.atomic.operation;

import jromp.parallel.utils.NumberUtils;

import java.util.function.UnaryOperator;

public class BitwiseOrOperation<T extends Number> implements Operation<T> {
    private final T value;

    public BitwiseOrOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "|";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.longValue() | value.longValue());
    }
}
