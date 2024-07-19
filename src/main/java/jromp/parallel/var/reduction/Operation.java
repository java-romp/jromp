package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Enum for reduction operations.
 */
@SuppressWarnings("rawtypes")
public enum Operation {
	/**
	 * Sum reduction operation.
	 */
	SUM(new Sum()),

	/**
	 * Multiplication reduction operation.
	 */
	MUL(new Mul()),

	/**
	 * Bitwise AND reduction operation.
	 */
	BAND(new BitwiseAnd()),

	/**
	 * Bitwise OR reduction operation.
	 */
	BOR(new BitwiseOr()),

	/**
	 * Bitwise XOR reduction operation.
	 */
	BXOR(new BitwiseXor()),

	/**
	 * Logical AND reduction operation.
	 */
	LAND(new LogicalAnd()),

	/**
	 * Logical OR reduction operation.
	 */
	LOR(new LogicalOr()),

	/**
	 * Maximum reduction operation.
	 */
	MAX(new Max()),

	/**
	 * Minimum reduction operation.
	 */
	MIN(new Min());

	/**
	 * The reduction operation.
	 */
	private final ReductionOperation op;


	/**
	 * Initializes a new Operation with the given ReductionOperation.
	 *
	 * @param op the ReductionOperation for this Operation.
	 */
	Operation(ReductionOperation op) {
		this.op = op;
	}

	/**
	 * Get the operation.
	 *
	 * @return the operation.
	 */
	public ReductionOperation getOp() {
		return op;
	}

	/**
	 * Get the identifier of the operation.
	 *
	 * @return the identifier of the operation.
	 */
	public String getIdentifier() {
		return op.identifier();
	}


	/**
	 * Initializes the variable with the identity value of the reduction operation.
	 *
	 * @param variable the variable to initialize.
	 * @param <T>      the type of the variable.
	 */
	@SuppressWarnings("unchecked")
	public <T> void initialize(Variable<T> variable) {
		op.initialize(variable);
	}

	/**
	 * Combines two values of the reduction operation.
	 *
	 * @param <T> the type of the values to combine.
	 * @param a   the first value.
	 * @param b   the second value.
	 *
	 * @return the result of the combination.
	 */
	@SuppressWarnings("unchecked")
	public <T> T combine(T a, T b) {
		return (T) op.combine(a, b);
	}

	/**
	 * Returns the reduction operation corresponding to the given identifier.
	 *
	 * @param identifier the identifier of the reduction operation.
	 * @return the reduction operation corresponding to the identifier, or null if not found.
	 */
	public static ReductionOperation fromIdentifier(String identifier) {
		for (Operation operation : Operation.values()) {
			if (operation.getIdentifier().equals(identifier)) {
				return operation.getOp();
			}
		}

		return null;
	}
}
