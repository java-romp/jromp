package jromp.parallel.operation;

import jromp.parallel.utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to subtract a value from another value.
 *
 * @param <T> the type of the value.
 */
public class SubtractOperation<T extends Number> implements Operation<T> {
    /**
     * The value to subtract.
     */
    private final T value;

    /**
     * Constructs a subtract operation.
     *
     * @param value the value to subtract.
     */
    public SubtractOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "-";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.doubleValue() - value.doubleValue());
    }
}
