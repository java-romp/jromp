package jromp.var.reduction;

import jromp.var.Variable;

/**
 * Reduction operation for logical AND.
 */
public class LogicalAnd implements ReductionOperation<Boolean> {
    LogicalAnd() {
    }

    @Override
    public String identifier() {
        return "&&";
    }

    @Override
    public void initialize(Variable<Boolean> variable) {
        variable.set(true);
    }

    @Override
    public Boolean combine(Boolean a, Boolean b) {
        return a && b;
    }

    @Override
    public String toString() {
        return "LogicalAnd";
    }
}
