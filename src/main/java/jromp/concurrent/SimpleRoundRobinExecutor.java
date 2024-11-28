package jromp.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A simple round-robin executor that distributes tasks to a fixed number of single-threaded executors.
 */
public class SimpleRoundRobinExecutor {
    /**
     * The executors to distribute tasks to.
     */
    private final ExecutorService[] executors;

    /**
     * The index of the current executor to distribute tasks to.
     */
    private int currentExecutor;

    /**
     * Constructs a new simple round-robin executor with the given number of threads.
     *
     * @param nThreads      The number of threads to use.
     * @param threadFactory The thread factory to use for creating threads.
     */
    public SimpleRoundRobinExecutor(int nThreads, ThreadFactory threadFactory) {
        this.executors = new ExecutorService[nThreads];

        for (int i = 0; i < nThreads; i++) {
            this.executors[i] = Executors.newSingleThreadExecutor(threadFactory);
        }

        this.currentExecutor = 0;
    }

    /**
     * Shuts down all executors.
     */
    public void shutdown() {
        for (ExecutorService executor : this.executors) {
            executor.shutdown();
        }
    }

    /**
     * Returns whether all executors are terminated.
     *
     * @return {@code true} if all executors are terminated, {@code false} otherwise.
     */
    public boolean isTerminated() {
        for (ExecutorService executor : this.executors) {
            if (!executor.isTerminated()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Executes the given runnable on the next executor in the round-robin sequence.
     * The current executor is updated to the next executor in the sequence.
     *
     * @param runnable The runnable to send to the next executor.
     */
    public void execute(Runnable runnable) {
        this.executors[this.currentExecutor].execute(runnable);
        this.currentExecutor = (this.currentExecutor + 1) % this.executors.length;
    }
}
