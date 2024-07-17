package jromp;

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
	public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

	/**
	 * The minimum number of threads that can be used.
	 */
	public static final int MIN_THREADS = 1;

	/**
	 * The default number of threads to use.
	 */
	public static final int DEFAULT_THREADS = MAX_THREADS;
}
