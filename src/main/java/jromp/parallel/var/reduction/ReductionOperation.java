package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Interface for reduction operations.
 *
 * @param <T> the type of the reduction operation.
 * <p>
 * All reduction identifiers are defined in the point
 * <a href="https://www.openmp.org/spec-html/5.2/openmpsu47.html#x83-87001r1">
 * 5.5.3  Implicitly Declared OpenMP Reduction Identifiers
 * </a>.
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

	/**
	 * Returns the value of type T based on the given value and number.
	 *
	 * @param value the value to be converted.
	 * @param num   the number to be converted to the desired type.
	 *
	 * @return the converted value of type T.
	 *
	 * @throws IllegalArgumentException if the type is not supported.
	 */
	@SuppressWarnings("unchecked")
	default T getT(T value, double num) {
		if (value instanceof Double) {
			return (T) Double.valueOf(num);
		} else if (value instanceof Integer) {
			return (T) Integer.valueOf((int) num);
		} else if (value instanceof Long) {
			return (T) Long.valueOf((long) num);
		} else if (value instanceof Float) {
			return (T) Float.valueOf((float) num);
		} else if (value instanceof Short) {
			return (T) Short.valueOf((short) num);
		} else if (value instanceof Byte) {
			return (T) Byte.valueOf((byte) num);
		} else {
			throw new IllegalArgumentException("Unsupported type");
		}
	}
}
