package jromp.parallel;

import jromp.parallel.task.Task;

/**
 * Parallel execution block.
 */
public interface ParallelBlock {
    /**
     * Begins the execution of a task in a parallel block.
     *
     * @param task The task to be executed.
     */
    void begin(Task task);

    /**
     * Ends the execution of the parallel block.
     */
    void end();
}
