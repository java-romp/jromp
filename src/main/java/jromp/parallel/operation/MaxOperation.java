package jromp.parallel.operation;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a max operation on a value.
 *
 * @param <T> the type of the value.
 */
public class MaxOperation<T extends Number> implements Operation<T> {
    /**
     * The value to perform the max operation with.
     */
    private final T value;

    /**
     * Constructs a max operation.
     *
     * @param value the value to perform the max operation with.
     */
    public MaxOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "max";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> a.doubleValue() > value.doubleValue() ? a : value;
    }
}
