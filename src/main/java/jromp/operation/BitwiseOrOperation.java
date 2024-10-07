package jromp.operation;

import jromp.utils.Utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a bitwise OR operation on a value.
 *
 * @param <T> the type of the value.
 */
public class BitwiseOrOperation<T extends Number> implements Operation<T> {
    /**
     * The value to perform the bitwise OR operation with.
     */
    private final T value;

    /**
     * Constructs a bitwise OR operation.
     *
     * @param value the value to perform the bitwise OR operation with.
     */
    BitwiseOrOperation(T value) {
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
