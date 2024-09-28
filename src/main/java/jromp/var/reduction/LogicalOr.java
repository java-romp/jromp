package jromp.var.reduction;

import jromp.var.Variable;

/**
 * Reduction operation for logical OR.
 */
public class LogicalOr implements ReductionOperation<Boolean> {
    LogicalOr() {
    }

    @Override
    public String identifier() {
        return "||";
    }

    @Override
    public void initialize(Variable<Boolean> variable) {
        variable.set(false);
    }

    @Override
    public Boolean combine(Boolean a, Boolean b) {
        return a || b;
    }

    @Override
    public String toString() {
        return "LogicalOr";
    }
}
