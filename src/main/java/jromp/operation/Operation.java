package jromp.operation;

import java.io.Serializable;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Operation interface that defines the basic structure of an operation.
 *
 * @param <T> the type of the operation.
 */
public interface Operation<T extends Serializable> extends Supplier<UnaryOperator<T>> {
    /**
     * Returns the identifier of the operation.
     *
     * @return String representation of the operation.
     */
    String identifier();

    /**
     * Returns the operation as a {@link UnaryOperator}.
     *
     * @return the operation as a {@link UnaryOperator}.
     */
    @Override
    UnaryOperator<T> get();

    /**
     * Applies the operation to the specified value.
     *
     * @param value the value to apply the operation to.
     *
     * @return the result of applying the operation to the value.
     */
    default T apply(T value) {
        return get().apply(value);
    }
}
