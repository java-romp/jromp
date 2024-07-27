package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Interface for reduction operations.
 * <p>
 * All reduction identifiers are defined in the point
 * <a href="https://www.openmp.org/spec-html/5.2/openmpsu47.html#x83-87001r1">
 * 5.5.3  Implicitly Declared OpenMP Reduction Identifiers
 * </a>.
 *
 * @param <T> the type of the reduction operation.
 */
public interface ReductionOperation<T> {
    /**
     * Returns the identifier of the reduction operation.
     *
     * @return String representation of the operation.
     */
    String identifier();

    /**
     * Initializes the variable with the identity value of the reduction operation.
     *
     * @param variable the variable to initialize.
     */
    void initialize(Variable<T> variable);

    /**
     * Combines two values of the reduction operation.
     *
     * @param a the first value.
     * @param b the second value.
     *
     * @return the result of the combination.
     */
    T combine(T a, T b);
}
