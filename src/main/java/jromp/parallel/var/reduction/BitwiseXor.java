package jromp.parallel.var.reduction;

import jromp.parallel.var.Variable;

import static jromp.parallel.utils.NumberUtils.getT;

/**
 * Reduction operation for bitwise XOR.
 *
 * @param <T> the type of the reduction operation.
 */
public class BitwiseXor<T extends Number> implements ReductionOperation<T> {
    @Override
    public String identifier() {
        return "^";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), 0));
    }

    @Override
    public T combine(T a, T b) {
        return getT(a, a.longValue() ^ b.longValue());
    }

    @Override
    public String toString() {
        return "BitwiseXor";
    }
}
