package jromp.task;

import jromp.var.Variables;

/**
 * Interface for a `for loop` task.
 */
public interface ForTask {
    /**
     * Run the task.
     *
     * @param start     The start index.
     * @param end       The end index.
     * @param variables The variables to use in the task.
     */
    void run(int start, int end, Variables variables);

    /**
     * Run the task.
     *
     * @param start The start index.
     * @param end   The end index.
     */
    default void run(int start, int end) {
        this.run(start, end, Variables.create());
    }
}
