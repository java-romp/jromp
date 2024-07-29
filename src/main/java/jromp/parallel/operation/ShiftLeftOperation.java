package jromp.parallel.operation;

import jromp.parallel.utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a left shift operation on a value.
 *
 * @param <T> the type of the value.
 */
public class ShiftLeftOperation<T extends Number> implements Operation<T> {
    /**
     * The number of bits to shift to the left.
     */
    private final T value;

    /**
     * Constructs a left shift operation.
     *
     * @param value the number of bits to shift to the left.
     */
    public ShiftLeftOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "<<";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.longValue() << value.longValue());
    }
}
