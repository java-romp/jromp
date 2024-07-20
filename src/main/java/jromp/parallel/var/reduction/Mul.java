package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Reduction operation for multiplication.
 *
 * @param <T> the type of the reduction operation.
 */
public class Mul<T extends Number> implements ReductionOperation<T> {
	@Override
	public String identifier() {
		return "*";
	}

	@Override
	public void initialize(Variable<T> variable) {
		variable.set(getT(variable.get(), 1));
	}

	@Override
	public T combine(T a, T b) {
		return getT(a, a.doubleValue() * b.doubleValue());
	}

	@Override
	public String toString() {
		return "Mul";
	}
}
