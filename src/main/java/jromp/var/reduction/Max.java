package jromp.var.reduction;

import jromp.operation.Operations;
import jromp.var.Variable;

import static jromp.Utils.NumberUtils.getT;

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
