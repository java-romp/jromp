package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

/**
 * Reduction operation for subtraction.
 *
 * @param <T> the type of the reduction operation.
 *
 * @deprecated This class is deprecated and will be removed in a future release.
 */
@Deprecated
public class Sub<T extends Number> implements ReductionOperation<T> {
	@Override
	public String identifier() {
		return "-";
	}

	@Override
	public void initialize(Variable<T> variable) {
		variable.set(getT(variable.get(), 0));
	}

	@Override
	public T combine(T a, T b) {
		return getT(a, a.doubleValue() - b.doubleValue());
	}
}
