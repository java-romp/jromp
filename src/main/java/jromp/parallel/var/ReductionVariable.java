package jromp.parallel.var;

import jromp.parallel.var.reduction.ReductionOperation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Represents a reduction variable that its value is reduced from private variables using a reduction operation.
 *
 * @param <T> the type of the variable.
 */
public class ReductionVariable<T extends Serializable> implements Variable<T> {
	/**
	 * The reduction operation.
	 */
	private final transient ReductionOperation<T> operation;

	/**
	 * The initial value of the reduction variable.
	 */
	private final T initialValue;

	/**
	 * The private variables that are used to reduce the result.
	 */
	private final List<PrivateVariable<T>> privateVariables = new ArrayList<>();

	/**
	 * The result of the reduction operation.
	 */
	private final PrivateVariable<T> result;

	/**
	 * Indicates whether the reduction variable has been merged.
	 */
	private boolean merged = false;

	/**
	 * Constructs a new reduction variable with the given reduction operation and initial value.
	 *
	 * @param operation    the reduction operation.
	 * @param initialValue the initial value of the reduction variable.
	 */
	public ReductionVariable(ReductionOperation<T> operation, T initialValue) {
		this.operation = operation;
		this.initialValue = initialValue;
		this.result = new PrivateVariable<>(initialValue);
	}

	@Override
	public T value() {
		if (!merged) {
			throw new IllegalStateException("ReductionVariable must be merged before getting the result.");
		}

		return result.value();
	}

	@Override
	public void set(T value) {
		throw new UnsupportedOperationException("ReductionVariable cannot be set.");
	}

	@Override
	public void update(UnaryOperator<T> operator) {
		throw new UnsupportedOperationException("ReductionVariable cannot be updated.");
	}

	@Override
	public Variable<T> copy() {
		PrivateVariable<T> variable = new PrivateVariable<>(initialValue);
		operation.initialize(variable);
		privateVariables.add(variable);
		return variable;
	}

	@Override
	public void end() {
		privateVariables.forEach(Variable::end);
		privateVariables.clear();
		// Do not end the result variable because it is a PrivateVariable,
		// and it will be set to the default value of T.
	}

	public boolean isMerged() {
		return merged;
	}

	public void merge() {
		if (merged) {
			throw new IllegalStateException("ReductionVariable has already been merged.");
		}

		operation.initialize(result);
		privateVariables.forEach(
				variable -> result.update(oldResult -> operation.combine(oldResult, variable.value())));
		merged = true;
	}

	@Override
	public String toString() {
		List<String> privateVariableList = privateVariables.stream()
		                                                   .map(PrivateVariable::toString)
		                                                   .toList();
		String privateVariablesString = String.join("\n    ", privateVariableList);

		return "ReductionVariable{%n  operation=%s,%n  initialValue=%s,%n  privateVariables=[%n    %s%n  ],%n  result=%s,%n  merged=%s}"
				.formatted(operation, initialValue, privateVariablesString, result, merged);
	}
}
