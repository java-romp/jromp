package jromp.parallel.var.reduction;

import jromp.parallel.operation.Operations;
import jromp.parallel.var.Variable;

import static jromp.parallel.utils.NumberUtils.getT;

/**
 * Reduction operation for addition.
 *
 * @param <T> the type of the reduction operation.
 */
public class Sum<T extends Number> implements ReductionOperation<T> {
    @Override
    public String identifier() {
        return "+";
    }

    @Override
    public void initialize(Variable<T> variable) {
        variable.set(getT(variable.value(), 0));
    }

    @Override
    public T combine(T a, T b) {
        return Operations.add(a).apply(b);
    }

    @Override
    public String toString() {
        return "Sum";
    }
}
