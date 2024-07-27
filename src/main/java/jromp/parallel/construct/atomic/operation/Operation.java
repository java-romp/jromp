package jromp.parallel.construct.atomic.operation;

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
}
