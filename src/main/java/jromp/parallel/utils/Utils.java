package jromp.parallel.utils;

import jromp.Constants;

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
    public static int checkThreads(int threads) {
        if (threads < Constants.MIN_THREADS) {
            throw new IllegalArgumentException("Number of threads must be greater than 0.");
        }

        return Math.min(threads, Constants.MAX_THREADS);
    }

    /**
     * Check if the thread is the master thread.
     *
     * @param id The thread ID.
     *
     * @return <code>true</code> if the thread is the master thread, <code>false</code> otherwise.
     */
    public static boolean isMaster(int id) {
        return id == 0;
    }

    /**
     * Get the elapsed wall clock time.
     *
     * @return The elapsed wall clock time.
     */
    public static double getWTime() {
        return System.nanoTime() / 1e9;
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
        @SuppressWarnings("unchecked")
        public static <T extends Number> T getT(T value, double num) {
            if (value == null) {
                throw new IllegalArgumentException("Unsupported type (null)");
            }

            String name = value.getClass().getName();
            return switch (name) {
                case "java.lang.Double" -> (T) Double.valueOf(num);
                case "java.lang.Integer" -> (T) Integer.valueOf((int) num);
                case "java.lang.Long" -> (T) Long.valueOf((long) num);
                case "java.lang.Float" -> (T) Float.valueOf((float) num);
                case "java.lang.Short" -> (T) Short.valueOf((short) num);
                case "java.lang.Byte" -> (T) Byte.valueOf((byte) num);
                default -> throw new IllegalArgumentException("Unsupported type (" + name + ")");
            };
        }
    }
}
