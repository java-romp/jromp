package jromp.parallel.task;

import jromp.parallel.var.Variables;

/**
 * Interface for a `for loop` task.
 */
public interface ForTask {
	/**
	 * Run the task.
	 *
	 * @param id        The thread ID.
	 * @param start     The start index.
	 * @param end       The end index.
	 * @param variables The variables to use in the task.
	 */
	void run(int id, int start, int end, Variables variables);

	/**
	 * Run the task.
	 *
	 * @param id    The thread ID.
	 * @param start The start index.
	 * @param end   The end index.
	 */
	default void run(int id, int start, int end) {
		this.run(id, start, end, Variables.create());
	}
}
