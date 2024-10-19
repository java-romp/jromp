package jromp.operation;

import java.util.function.UnaryOperator;

public class LogicalOrOperation implements Operation<Boolean> {
    /**
     * The value to operate on.
     */
    private final Boolean value;

    /**
     * Constructs a logical OR operation.
     *
     * @param value the value to operate on.
     */
    LogicalOrOperation(Boolean value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "||";
    }

    @Override
    public UnaryOperator<Boolean> get() {
        return a -> a || value;
    }
}
