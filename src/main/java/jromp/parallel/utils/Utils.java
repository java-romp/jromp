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
		if (threads <= 0) {
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
}
