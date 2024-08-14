package jromp.parallel.var;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * The Variable interface represents a variable that can store a value of any type.
 * It provides methods to get and set the value, update the value using a {@link UnaryOperator}
 * and create a copy of the variable.
 *
 * @param <T> the type of the variable.
 */
public interface Variable<T> extends Serializable {
    /**
     * Retrieves the value of the variable.
     *
     * @return the value of the variable.
     */
    T value();

    /**
     * Sets the value of the variable.
     *
     * @param value the new value to be set.
     */
    void set(T value);

    /**
     * Applies the given {@link UnaryOperator} to update the value of the variable.
     *
     * @param operator the unary operator to apply.
     */
    void update(UnaryOperator<T> operator);

    /**
     * Creates a copy of the variable.
     *
     * @return a copy of the variable.
     */
    Variable<T> copy();

    /**
     * Performs any cleanup operations that may be required by the variable.
     */
    void end();
}
