package jromp.parallel.construct.atomic.operation;

public class Operations {
    private Operations() {
    }

    public static <T extends Number> Operation<T> assign(T value) {
        return new AssignOperation<>(value);
    }

    public static <T extends Number> Operation<T> add(T value) {
        return new AddOperation<>(value);
    }

    public static <T extends Number> Operation<T> multiply(T value) {
        return new MultiplyOperation<>(value);
    }

    public static <T extends Number> Operation<T> subtract(T value) {
        return new SubtractOperation<>(value);
    }

    public static <T extends Number> Operation<T> divide(T value) {
        return new DivideOperation<>(value);
    }

    public static <T extends Number> Operation<T> bitwiseAnd(T value) {
        return new BitwiseAndOperation<>(value);
    }

    public static <T extends Number> Operation<T> bitwiseOr(T value) {
        return new BitwiseOrOperation<>(value);
    }

    public static <T extends Number> Operation<T> bitwiseXor(T value) {
        return new BitwiseXorOperation<>(value);
    }

    public static <T extends Number> Operation<T> shiftLeft(T value) {
        return new ShiftLeftOperation<>(value);
    }

    public static <T extends Number> Operation<T> shiftRight(T value) {
        return new ShiftRightOperation<>(value);
    }
}
