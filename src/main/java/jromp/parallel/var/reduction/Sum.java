package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Reduction operation for addition.
 *
 * @param <T> the type of the reduction operation.
 */
public class Sum<T extends Number> implements ReductionOperation<T> {
	@Override
	public String identifier() {
		return "+";
	}

	@Override
	public void initialize(Variable<T> variable) {
		variable.set(getT(variable.get(), 0));
	}

	@Override
	public T combine(T a, T b) {
		return getT(a, a.doubleValue() + b.doubleValue());
	}
}
