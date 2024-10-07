package jromp.operation;

import java.util.function.UnaryOperator;

/**
 * Operation to assign a value to another value.
 *
 * @param <T> the type of the value.
 */
public class AssignOperation<T extends Number> implements Operation<T> {
    /**
     * The value to assign.
     */
    private final T value;

    /**
     * Constructs an assign operation.
     *
     * @param value the value to assign.
     */
    AssignOperation(T value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "=";
    }

    @Override
    public UnaryOperator<T> get() {
        return a -> value;
    }
}
