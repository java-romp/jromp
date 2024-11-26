package jromp.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A wrapper class for the {@link ExecutorService} that provides a simple interface to execute runnables.
 */
public class JrompExecutorWrapper {
    /**
     * The executor service that runs the threads.
     */
    private final ExecutorService executor;

    /**
     * Constructs a new {@link JrompExecutorWrapper} with the given number of threads and thread factory.
     *
     * @param nThreads      the number of threads.
     * @param threadFactory the thread factory.
     */
    public JrompExecutorWrapper(int nThreads, ThreadFactory threadFactory) {
        this.executor = Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    /**
     * Shuts down the executor service.
     *
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Checks if the executor service is terminated.
     *
     * @return <code>true</code> if the executor service is terminated, <code>false</code> otherwise.
     *
     * @see ExecutorService#isTerminated()
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    /**
     * Executes the given runnable.
     *
     * @param runnable the runnable.
     *
     * @see ExecutorService#execute(Runnable)
     */
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
