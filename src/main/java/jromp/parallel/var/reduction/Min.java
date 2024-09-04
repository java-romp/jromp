package jromp.parallel.var.reduction;

import jromp.parallel.operation.Operations;
import jromp.parallel.var.Variable;

import static jromp.parallel.utils.Utils.NumberUtils.getT;

/**
 * Reduction operation for minimum.
 *
 * @param <T> the type of the reduction operation.
 */
public class Min<T extends Number> implements ReductionOperation<T> {
    Min() {
    }

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
        return Operations.min(a).apply(b);
    }

    @Override
    public String toString() {
        return "Min";
    }
}
