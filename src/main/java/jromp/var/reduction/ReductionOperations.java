package jromp.var.reduction;

import java.io.Serializable;

/**
 * Operations for reduction variables.
 */
public class ReductionOperations {
    private static final ReductionOperation<Number> SUM = new Sum<>();
    private static final ReductionOperation<Number> MUL = new Mul<>();
    private static final ReductionOperation<Number> BAND = new BitwiseAnd<>();
    private static final ReductionOperation<Number> BOR = new BitwiseOr<>();
    private static final ReductionOperation<Number> BXOR = new BitwiseXor<>();
    private static final ReductionOperation<Boolean> LAND = new LogicalAnd();
    private static final ReductionOperation<Boolean> LOR = new LogicalOr();
    private static final ReductionOperation<Number> MAX = new Max<>();
    private static final ReductionOperation<Number> MIN = new Min<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ReductionOperations() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> ReductionOperation<T> cast(ReductionOperation<?> operation) {
        return (ReductionOperation<T>) operation;
    }

    public static <T extends Number> ReductionOperation<T> sum() {
        return cast(SUM);
    }

    public static <T extends Number> ReductionOperation<T> mul() {
        return cast(MUL);
    }

    public static <T extends Number> ReductionOperation<T> band() {
        return cast(BAND);
    }

    public static <T extends Number> ReductionOperation<T> bor() {
        return cast(BOR);
    }

    public static <T extends Number> ReductionOperation<T> bxor() {
        return cast(BXOR);
    }

    public static ReductionOperation<Boolean> land() {
        return cast(LAND);
    }

    public static ReductionOperation<Boolean> lor() {
        return cast(LOR);
    }

    public static <T extends Number> ReductionOperation<T> max() {
        return cast(MAX);
    }

    public static <T extends Number> ReductionOperation<T> min() {
        return cast(MIN);
    }

    public static <T extends Serializable> ReductionOperation<T> fromIdentifier(String identifier) {
        return switch (identifier) {
            case "+" -> cast(SUM);
            case "*" -> cast(MUL);
            case "&" -> cast(BAND);
            case "|" -> cast(BOR);
            case "^" -> cast(BXOR);
            case "&&" -> cast(LAND);
            case "||" -> cast(LOR);
            case "max" -> cast(MAX);
            case "min" -> cast(MIN);
            default -> throw new IllegalArgumentException("Unknown reduction identifier: " + identifier);
        };
    }
}
