package jromp.operation;

import jromp.Utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a bitwise XOR operation on a value.
 *
 * @param <T> the type of the value.
 */
public class BitwiseXorOperation<T extends Number> implements Operation<T> {
    /**
     * The value to perform the bitwise XOR operation with.
     */
    private final T value;

    /**
     * Constructs a bitwise XOR operation.
     *
     * @param value the value to perform the bitwise XOR operation with.
     */
    BitwiseXorOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "^";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.longValue() ^ value.longValue());
    }
}
