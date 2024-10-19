package jromp.operation;

import java.util.function.UnaryOperator;

public class LogicalAndOperation implements Operation<Boolean> {
    /**
     * The value to operate on.
     */
    private final Boolean value;

    /**
     * Constructs a logical AND operation.
     *
     * @param value the value to operate on.
     */
    LogicalAndOperation(Boolean value) {
        this.value = value;
    }

    @Override
    public String identifier() {
        return "&&";
    }

    @Override
    public UnaryOperator<Boolean> get() {
        return a -> a && value;
    }
}
