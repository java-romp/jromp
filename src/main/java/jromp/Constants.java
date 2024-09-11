package jromp;

import jromp.parallel.utils.Utils;

/**
 * Constants used in the library.
 */
public final class Constants {
    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * The name of the variable that specifies the number of threads.
     */
    public static final String NUM_THREADS = "numThreads";

    /**
     * The maximum number of threads that can be used.
     */
    public static final int MAX_THREADS;

    /**
     * The minimum number of threads that can be used.
     */
    public static final int MIN_THREADS = 1;

    /**
     * The default number of threads to use.
     */
    public static final int DEFAULT_THREADS;

    static class Environment {
        private Environment() {
        }

        static final String THREADS = "JROMP_NUM_THREADS";
    }

    static {
        // Check if the number of threads is specified in the environment.
        String threads = System.getenv(Environment.THREADS);

        if (threads != null) {
            try {
                MAX_THREADS = Utils.checkThreads(Integer.parseInt(threads));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number of threads specified in the environment.");
            }
        } else {
            MAX_THREADS = Runtime.getRuntime().availableProcessors();
        }

        DEFAULT_THREADS = MAX_THREADS;
    }
}
