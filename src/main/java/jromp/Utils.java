package jromp;

/**
 * Utility methods.
 */
public final class Utils {
    /**
     * Prevent instantiation of this class.
     */
    private Utils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Check the number of threads.
     *
     * @param threads The number of threads.
     *
     * @return The number of threads.
     */
    static int checkThreads(int threads) {
        if (threads < Constants.MIN_THREADS) {
            throw new IllegalArgumentException("Number of threads must be greater than 0.");
        }

        return Math.min(threads, Constants.MAX_THREADS);
    }

    static int checkThreadsPerTeam(int threads, int threadsPerTeam) {
        if (threadsPerTeam < Constants.MIN_THREADS) {
            throw new IllegalArgumentException("Number of threads per team must be greater than 0.");
        }

        if (threads < threadsPerTeam) {
            throw new IllegalArgumentException("Number of threads per team must be less than or equal to the number of threads.");
        }

        if (threads % threadsPerTeam != 0) {
            throw new IllegalArgumentException("Number of threads must be divisible by the number of threads per team.");
        }

        return threadsPerTeam;
    }

    public static class NumberUtils {
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
        @SuppressWarnings({ "unchecked", "unused" })
        public static <T extends Number> T getT(T value, double num) {
            if (value == null) {
                throw new IllegalArgumentException("Unsupported type (null)");
            }

            return switch (value) {
                case Double d -> (T) Double.valueOf(num);
                case Integer i -> (T) Integer.valueOf((int) num);
                case Long l -> (T) Long.valueOf((long) num);
                case Float f -> (T) Float.valueOf((float) num);
                case Short s -> (T) Short.valueOf((short) num);
                case Byte b -> (T) Byte.valueOf((byte) num);
                default -> throw new IllegalArgumentException("Unsupported type (" + value.getClass().getName() + ")");
            };
        }
    }
}
