package jromp.task;

import jromp.var.Variables;

/**
 * A task to be run in parallel.
 */
public interface Task {
    /**
     * Run the task.
     *
     * @param variables The variables to use.
     */
    void run(Variables variables);
}
