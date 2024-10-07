package jromp.var.reduction;

import jromp.operation.Operations;
import jromp.var.Variable;

import static jromp.utils.Utils.NumberUtils.getT;

/**
 * Reduction operation for bitwise XOR.
 *
 * @param <T> the type of the reduction operation.
 */
public class BitwiseXor<T extends Number> implements ReductionOperation<T> {
    BitwiseXor() {
    }

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
        return Operations.bitwiseXor(a).apply(b);
    }

    @Override
    public String toString() {
        return "BitwiseXor";
    }
}
