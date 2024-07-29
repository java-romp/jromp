package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

import static jromp.parallel.utils.NumberUtils.getT;

/**
 * Reduction operation for minimum.
 *
 * @param <T> the type of the reduction operation.
 */
public class Min<T extends Number> implements ReductionOperation<T> {
    @Override
    public String identifier() {
        return "min";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), Double.POSITIVE_INFINITY));
    }

    @Override
    public T combine(T a, T b) {
        return a.doubleValue() < b.doubleValue() ? a : b;
    }

    @Override
    public String toString() {
        return "Min";
    }
}
