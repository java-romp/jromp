package jromp.task;

/**
 * Interface for a `for loop` task.
 */
public interface ForTask {
    /**
     * Run the task.
     *
     * @param start The start index.
     * @param end   The end index.
     */
    void run(int start, int end);
}
