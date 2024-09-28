package jromp.operation;

import java.util.function.UnaryOperator;

/**
 * Operation to perform a min operation on a value.
 *
 * @param <T> the type of the value.
 */
public class MinOperation<T extends Number> implements Operation<T> {
    /**
     * The value to perform the min operation with.
     */
    private final T value;

    /**
     * Constructs a min operation.
     *
     * @param value the value to perform the min operation with.
     */
    MinOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "min";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> a.doubleValue() < value.doubleValue() ? a : value;
    }
}
