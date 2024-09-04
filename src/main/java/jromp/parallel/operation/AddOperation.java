package jromp.parallel.operation;

import jromp.parallel.utils.Utils.NumberUtils;

import java.util.function.UnaryOperator;

/**
 * Operation to add a value to another value.
 *
 * @param <T> the type of the value.
 */
public class AddOperation<T extends Number> implements Operation<T> {
    /**
     * The value to add.
     */
    private final T value;

    /**
     * Constructs an add operation.
     *
     * @param value the value to add.
     */
    AddOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "+";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> NumberUtils.getT(a, a.doubleValue() + value.doubleValue());
    }
}
