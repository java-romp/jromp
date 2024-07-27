package jromp.parallel.construct.atomic.operation;

import jromp.parallel.utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to divide a value by another value.
 *
 * @param <T> the type of the value.
 */
public class DivideOperation<T extends Number> implements Operation<T> {
    /**
     * The value to divide by.
     */
    private final T value;

    /**
     * Constructs a divide operation.
     *
     * @param value the value to divide by.
     */
    public DivideOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "/";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.doubleValue() / value.doubleValue());
    }
}
