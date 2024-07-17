package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Reduction operation for maximum.
 *
 * @param <T> the type of the reduction operation.
 */
public class Max<T extends Number> implements ReductionOperation<T> {
	@Override
	public String identifier() {
		return "max";
	}

	@Override
	public void initialize(Variable<T> variable) {
		variable.set(getT(variable.get(), Double.NEGATIVE_INFINITY));
	}

	@Override
	public T combine(T a, T b) {
		return a.doubleValue() > b.doubleValue() ? a : b;
	}
}
