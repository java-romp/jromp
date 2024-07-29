package jromp.parallel.operation;

import jromp.parallel.utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a shift right operation on a value.
 *
 * @param <T> the type of the value.
 */
public class ShiftRightOperation<T extends Number> implements Operation<T> {
    /**
     * The number of bits to shift to the right.
     */
    private final T value;

    /**
     * Constructs a shift right operation.
     *
     * @param value the number of bits to shift to the right.
     */
    public ShiftRightOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return ">>";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.longValue() >> value.longValue());
    }
}
