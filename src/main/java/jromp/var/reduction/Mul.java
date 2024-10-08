package jromp.var.reduction;

import jromp.operation.Operations;
import jromp.var.Variable;

import static jromp.Utils.NumberUtils.getT;

/**
 * Reduction operation for multiplication.
 *
 * @param <T> the type of the reduction operation.
 */
public class Mul<T extends Number> implements ReductionOperation<T> {
    Mul() {
    }

    @Override
    public String identifier() {
        return "*";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), 1));
    }

    @Override
    public T combine(T a, T b) {
        return Operations.multiply(a).apply(b);
    }

    @Override
    public String toString() {
        return "Mul";
    }
}
