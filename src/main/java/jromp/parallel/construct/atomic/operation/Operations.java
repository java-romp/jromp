package jromp.parallel.construct.atomic.operation;

/**
 * Operations to perform on values.
 */
public class Operations {
    /**
     * Private constructor to prevent instantiation.
     */
    private Operations() {
    }

    /**
     * Creates an assign operation.
     *
     * @param value the value to assign.
     * @param <T>   the type of the value.
     *
     * @return the assign operation.
     */
    public static <T extends Number> Operation<T> assign(T value) {
        return new AssignOperation<>(value);
    }

    /**
     * Creates an add operation.
     *
     * @param value the value to add.
     * @param <T>   the type of the value.
     *
     * @return the add operation.
     */
    public static <T extends Number> Operation<T> add(T value) {
        return new AddOperation<>(value);
    }

    /**
     * Creates a multiply operation.
     *
     * @param value the value to multiply.
     * @param <T>   the type of the value.
     *
     * @return the multiply operation.
     */
    public static <T extends Number> Operation<T> multiply(T value) {
        return new MultiplyOperation<>(value);
    }

    /**
     * Creates a subtract operation.
     *
     * @param value the value to subtract.
     * @param <T>   the type of the value.
     *
     * @return the subtract operation.
     */
    public static <T extends Number> Operation<T> subtract(T value) {
        return new SubtractOperation<>(value);
    }

    /**
     * Creates a divide operation.
     *
     * @param value the value to divide.
     * @param <T>   the type of the value.
     *
     * @return the divide operation.
     */
    public static <T extends Number> Operation<T> divide(T value) {
        return new DivideOperation<>(value);
    }

    /**
     * Creates a bitwise AND operation.
     *
     * @param value the value to AND.
     * @param <T>   the type of the value.
     *
     * @return the bitwise AND operation.
     */
    public static <T extends Number> Operation<T> bitwiseAnd(T value) {
        return new BitwiseAndOperation<>(value);
    }

    /**
     * Creates a bitwise OR operation.
     *
     * @param value the value to OR.
     * @param <T>   the type of the value.
     *
     * @return the bitwise OR operation.
     */
    public static <T extends Number> Operation<T> bitwiseOr(T value) {
        return new BitwiseOrOperation<>(value);
    }

    /**
     * Creates a bitwise XOR operation.
     *
     * @param value the value to XOR.
     * @param <T>   the type of the value.
     *
     * @return the bitwise XOR operation.
     */
    public static <T extends Number> Operation<T> bitwiseXor(T value) {
        return new BitwiseXorOperation<>(value);
    }

    /**
     * Creates a shift left operation.
     *
     * @param value the number of bits to shift to the left.
     * @param <T>   the type of the value.
     *
     * @return the shift left operation.
     */
    public static <T extends Number> Operation<T> shiftLeft(T value) {
        return new ShiftLeftOperation<>(value);
    }

    /**
     * Creates a shift right operation.
     *
     * @param value the number of bits to shift to the right.
     * @param <T>   the type of the value.
     *
     * @return the shift right operation.
     */
    public static <T extends Number> Operation<T> shiftRight(T value) {
        return new ShiftRightOperation<>(value);
    }
}
