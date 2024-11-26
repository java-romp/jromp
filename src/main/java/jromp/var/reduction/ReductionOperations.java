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

    /**
     * Casts a reduction operation to the correct type.
     *
     * @param operation the operation to cast.
     * @param <T>       the type of the operation.
     *
     * @return the operation cast to the correct type.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Serializable> ReductionOperation<T> cast(ReductionOperation<?> operation) {
        return (ReductionOperation<T>) operation;
    }

    /**
     * Returns a sum operation.
     *
     * @param <T> the type of the values to sum.
     *
     * @return the sum operation.
     */
    public static <T extends Number> ReductionOperation<T> sum() {
        return cast(SUM);
    }

    /**
     * Returns a multiplication operation.
     *
     * @param <T> the type of the values to multiply.
     *
     * @return the multiplication operation.
     */
    public static <T extends Number> ReductionOperation<T> mul() {
        return cast(MUL);
    }

    /**
     * Returns a bitwise AND operation.
     *
     * @param <T> the type of the values to AND.
     *
     * @return the bitwise AND operation.
     */
    public static <T extends Number> ReductionOperation<T> band() {
        return cast(BAND);
    }

    /**
     * Returns a bitwise OR operation.
     *
     * @param <T> the type of the values to OR.
     *
     * @return the bitwise OR operation.
     */
    public static <T extends Number> ReductionOperation<T> bor() {
        return cast(BOR);
    }

    /**
     * Returns a bitwise XOR operation.
     *
     * @param <T> the type of the values to XOR.
     *
     * @return the bitwise XOR operation.
     */
    public static <T extends Number> ReductionOperation<T> bxor() {
        return cast(BXOR);
    }

    /**
     * Returns a logical AND operation.
     *
     * @return the logical AND operation.
     */
    public static ReductionOperation<Boolean> land() {
        return cast(LAND);
    }

    /**
     * Returns a logical OR operation.
     *
     * @return the logical OR operation.
     */
    public static ReductionOperation<Boolean> lor() {
        return cast(LOR);
    }

    /**
     * Returns a maximum operation.
     *
     * @param <T> the type of the values to compare.
     *
     * @return the maximum operation.
     */
    public static <T extends Number> ReductionOperation<T> max() {
        return cast(MAX);
    }

    /**
     * Returns a minimum operation.
     *
     * @param <T> the type of the values to compare.
     *
     * @return the minimum operation.
     */
    public static <T extends Number> ReductionOperation<T> min() {
        return cast(MIN);
    }

    /**
     * Returns a reduction operation from an identifier.
     *
     * @param identifier the identifier of the operation.
     * @param <T>        the type of the values to reduce.
     *
     * @return the reduction operation.
     *
     * @throws IllegalArgumentException if the identifier is unknown.
     */
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
