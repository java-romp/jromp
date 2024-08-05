package jromp.parallel.utils;

public class NumberUtils {
    private NumberUtils() {
    }

    /**
     * Returns the value of the number received, converted to the
     * type of the reduction operation.
     *
     * @param value the value used to determine the type of the conversion.
     * @param num   the number to be converted to the desired type.
     *
     * @return the converted value with type T.
     *
     * @throws IllegalArgumentException if the type is not supported.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getT(T value, double num) {
        if (value == null) {
            throw new IllegalArgumentException("Unsupported type (null)");
        }

        return switch (value.getClass().getName()) {
            case "java.lang.Double" -> (T) Double.valueOf(num);
            case "java.lang.Integer" -> (T) Integer.valueOf((int) num);
            case "java.lang.Long" -> (T) Long.valueOf((long) num);
            case "java.lang.Float" -> (T) Float.valueOf((float) num);
            case "java.lang.Short" -> (T) Short.valueOf((short) num);
            case "java.lang.Byte" -> (T) Byte.valueOf((byte) num);
            default -> throw new IllegalArgumentException("Unsupported type (" + value.getClass().getName() + ")");
        };
    }
}
