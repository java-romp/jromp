package jromp.parallel.var.reduction;

import jromp.parallel.operation.Operations;
import jromp.parallel.var.Variable;

import static jromp.parallel.utils.NumberUtils.getT;

/**
 * Reduction operation for maximum.
 *
 * @param <T> the type of the reduction operation.
 */
public class Max<T extends Number> implements ReductionOperation<T> {
    Max() {
    }

    @Override
    public String identifier() {
        return "max";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), Double.NEGATIVE_INFINITY));
    }

    @Override
    public T combine(T a, T b) {
        return Operations.max(a).apply(b);
    }

    @Override
    public String toString() {
        return "Max";
    }
}
