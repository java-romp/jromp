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

	/**
	 * Returns the value of the number received, converted to the
	 * type of the reduction operation.
	 *
	 * @param value the value used to determine the type of the conversion.
	 * @param num   the number to be converted to the desired type.
	 *
	 * @return the converted value with type T.
	 *
	 * @throws IllegalArgumentException if the type is not supported.
	 */
	@SuppressWarnings("unchecked")
	default T getT(T value, double num) {
		return switch (value) {
			case Double ignoredV -> (T) Double.valueOf(num);
			case Integer ignoredI -> (T) Integer.valueOf((int) num);
			case Long ignoredL -> (T) Long.valueOf((long) num);
			case Float ignoredV -> (T) Float.valueOf((float) num);
			case Short ignoredI -> (T) Short.valueOf((short) num);
			case Byte ignoredB -> (T) Byte.valueOf((byte) num);
			case null, default -> throw new IllegalArgumentException("Unsupported type");
		};
	}
}
