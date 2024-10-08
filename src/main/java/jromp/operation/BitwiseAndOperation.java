package jromp.operation;

import jromp.Utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a bitwise AND operation on a value.
 *
 * @param <T> the type of the value.
 */
public class BitwiseAndOperation<T extends Number> implements Operation<T> {
    /**
     * The value to perform the bitwise AND operation with.
     */
    private final T value;

    /**
     * Constructs a bitwise AND operation.
     *
     * @param value the value to perform the bitwise AND operation with.
     */
    BitwiseAndOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "&";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.longValue() & value.longValue());
    }
}
