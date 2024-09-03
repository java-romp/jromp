package jromp.parallel.var.reduction;

import jromp.parallel.operation.Operations;
import jromp.parallel.var.Variable;

import static jromp.parallel.utils.NumberUtils.getT;

/**
 * Reduction operation for bitwise AND.
 *
 * @param <T> the type of the reduction operation.
 */
public class BitwiseAnd<T extends Number> implements ReductionOperation<T> {
    BitwiseAnd() {
    }

    @Override
    public String identifier() {
        return "&";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), ~0));
    }

    @Override
    public T combine(T a, T b) {
        return Operations.bitwiseAnd(a).apply(b);
    }

    @Override
    public String toString() {
        return "BitwiseAnd";
    }
}
