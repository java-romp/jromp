package jromp.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class JrompExecutorWrapper {
    /**
     * The executor service that runs the threads.
     */
    private final ExecutorService executor;

    /**
     * Constructs a new {@link JrompExecutorWrapper} with the given number of threads and thread factory.
     *
     * @param nThreads the number of threads.
     * @param threadFactory the thread factory.
     */
    public JrompExecutorWrapper(int nThreads, ThreadFactory threadFactory) {
        this.executor = Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    /**
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * @see ExecutorService#shutdownNow()
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    /**
     * @see ExecutorService#execute(Runnable)
     */
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
