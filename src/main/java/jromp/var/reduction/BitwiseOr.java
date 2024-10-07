package jromp.var.reduction;

import jromp.operation.Operations;
import jromp.var.Variable;

import static jromp.utils.Utils.NumberUtils.getT;

/**
 * Reduction operation for bitwise OR.
 *
 * @param <T> the type of the reduction operation.
 */
public class BitwiseOr<T extends Number> implements ReductionOperation<T> {
    BitwiseOr() {
    }

    @Override
    public String identifier() {
        return "|";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), 0));
    }

    @Override
    public T combine(T a, T b) {
        return Operations.bitwiseOr(a).apply(b);
    }

    @Override
    public String toString() {
        return "BitwiseOr";
    }
}
