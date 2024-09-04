package jromp.parallel.operation;

import jromp.parallel.utils.Utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to multiply a value by another value.
 *
 * @param <T> the type of the value.
 */
public class MultiplyOperation<T extends Number> implements Operation<T> {
    /**
     * The value to multiply by.
     */
    private final T value;

    /**
     * Constructs a multiply operation.
     *
     * @param value the value to multiply by.
     */
    MultiplyOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "*";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.doubleValue() * value.doubleValue());
    }
}
