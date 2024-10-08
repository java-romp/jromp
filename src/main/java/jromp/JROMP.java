package jromp;

import jromp.concurrent.JrompExecutorWrapper;
import jromp.concurrent.JrompThread;
import jromp.concurrent.ThreadTeam;
import jromp.task.ForTask;
import jromp.task.Task;
import jromp.var.ReductionVariable;
import jromp.var.SharedVariable;
import jromp.var.Variable;
import jromp.var.Variables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static jromp.Utils.checkThreads;
import static jromp.Utils.checkThreadsPerTeam;

/**
 * The main class for the parallel runtime.
 */
public class JROMP {
    /**
     * The number of threads used in the current parallel block.
     */
    private final int threads;

    /**
     * The thread executor used to execute the tasks.
     */
    private final JrompExecutorWrapper executor;

    /**
     * The variables used in the current parallel block.
     */
    private Variables variables;

    /**
     * The list of variables used in all blocks to perform the
     * {@link Variable#end()} operation when joining the threads.
     */
    private final List<Variables> variablesList = new ArrayList<>();

    /**
     * Create a new instance for the parallel runtime.
     *
     * @param threads        The number of threads.
     * @param threadsPerTeam The number of threads per team.
     */
    private JROMP(int threads, int threadsPerTeam) {
        this.threads = threads;
        this.executor = new JrompExecutorWrapper(threads, JrompThread.newThreadFactory(threadsPerTeam));

        withVariables(Variables.create());
    }

    /**
     * Configures the parallel runtime with the default number of threads.
     *
     * @return The parallel runtime.
     */
    public static JROMP allThreads() {
        return new JROMP(Constants.DEFAULT_THREADS, Constants.DEFAULT_THREADS);
    }

    /**
     * Configures the parallel runtime with the specified number of threads.
     *
     * @param threads The number of threads to use.
     *
     * @return The parallel runtime.
     */
    public static JROMP withThreads(int threads) {
        return new JROMP(checkThreads(threads), threads);
    }

    /**
     * Configures the parallel runtime with the specified number of threads and threads per team.
     *
     * @param threads        The number of threads to use.
     * @param threadsPerTeam The number of threads per team.
     *
     * @return The parallel runtime.
     */
    public static JROMP withThreads(int threads, int threadsPerTeam) {
        return new JROMP(checkThreads(threads), checkThreadsPerTeam(threads, threadsPerTeam));
    }

    /**
     * Wait for the current thread to finish.
     */
    private static void waitForFinish() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Set the variables to use in a parallel block.
     *
     * @param variables The variables to use.
     *
     * @return The parallel runtime.
     */
    public JROMP withVariables(Variables variables) {
        this.variables = variables;
        addNumThreadsToVariables(this.variables);

        return this;
    }

    /**
     * Add the number of threads to the variables.
     *
     * @param variables The variables to add the number of threads to.
     */
    private void addNumThreadsToVariables(Variables variables) {
        variables.add(Constants.NUM_THREADS, new SharedVariable<>(this.threads));
    }

    /**
     * Wait for all threads to finish and perform the necessary operations.
     */
    public void join() {
        executor.shutdown();

        while (!executor.isTerminated()) {
            waitForFinish();
        }

        variablesList.add(this.variables);

        // Perform the last operation on all variables.
        for (Variables vars : variablesList) {
            // Merge all reduction variables after the parallel block has ended.
            vars.getVariablesOfType(ReductionVariable.class)
                .forEach(ReductionVariable::merge);

            // End all variables.
            vars.end();
        }
    }

    /**
     * Executes a task in a parallel block, using the current variables.
     *
     * @param task The task to run.
     *
     * @return The parallel runtime.
     */
    public JROMP block(Task task) {
        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            final Variables finalVariables = this.variables.copy();
            this.variablesList.add(finalVariables);

            executor.execute(() -> task.run(finalI, finalVariables));
        }

        return this;
    }

    /**
     * Executes a for loop in parallel with the given start and end indices, using
     * a task implementation.
     *
     * @param start  The start index of the for loop.
     * @param end    The end index of the for loop.
     * @param nowait Whether to wait for the threads to finish.
     * @param task   The task to be executed in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP parallelFor(int start, int end, boolean nowait, ForTask task) {
        Barrier barrier = new Barrier("ParallelFor", this.threads);
        barrier.setNowait(nowait);

        for (int i = 0; i < this.threads; i++) {
            // Calculate the start and end indices for the current thread.
            int chunkSize = (end - start) / this.threads;
            int chunkStart = start + i * chunkSize;
            int chunkEnd;

            // If this is the last thread, make sure to include the remaining elements.
            if (i == this.threads - 1) {
                chunkEnd = end;
            } else {
                chunkEnd = chunkStart + chunkSize;
            }

            final int finalI = i;
            final Variables finalVariables = this.variables.copy();
            this.variablesList.add(finalVariables);
            executor.execute(() -> {
                task.run(finalI, chunkStart, chunkEnd, finalVariables);
                barrier.await();
            });
        }

        return this;
    }

    /**
     * Executes the given tasks in separate sections with the given variables.
     *
     * @param nowait    Whether to wait for the threads to finish.
     * @param variables The variables to use in the sections block.
     * @param tasks     The tasks to run in parallel.
     *
     * @return The parallel runtime.
     */
    private JROMP sections(boolean nowait, Variables variables, Task... tasks) {
        if (tasks.length <= this.threads) {
            Barrier barrier = new Barrier("Sections", tasks.length);
            barrier.setNowait(nowait);

            for (int i = 0; i < tasks.length; i++) {
                final int finalI = i;
                Task task = tasks[i];

                executor.execute(() -> {
                    task.run(finalI, variables);
                    barrier.await();
                });
            }
        } else {
            // If there are more sections than threads, submit the tasks in batches of threads.
            for (int i = 0; i < tasks.length; i += this.threads) {
                int end = Math.min(i + this.threads, tasks.length);
                int batchSize = end - i;
                Task[] batch = new Task[batchSize];

                System.arraycopy(tasks, i, batch, 0, batchSize);
                this.sections(nowait, variables, batch);
            }
        }

        return this;
    }

    /**
     * Executes the given tasks in separate sections.
     *
     * @param nowait Whether to wait for the threads to finish.
     * @param tasks  The tasks to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP sections(boolean nowait, Task... tasks) {
        Variables vars = this.variables.copy();
        this.variablesList.add(vars);

        return sections(nowait, vars, tasks);
    }

    /**
     * Executes the given tasks in separate sections.
     *
     * @param nowait Whether to wait for the threads to finish.
     * @param tasks  The tasks to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP sections(boolean nowait, List<Task> tasks) {
        return sections(nowait, tasks.toArray(Task[]::new));
    }

    /**
     * Submits a block that can only be executed by a single thread.
     *
     * @param nowait Whether to wait for the threads to finish.
     * @param task   The task to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP singleBlock(boolean nowait, Task task) {
        AtomicBoolean executed = new AtomicBoolean(false);
        Barrier barrier = new Barrier("SingleBlock", this.threads);
        barrier.setNowait(nowait);

        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            this.variablesList.add(this.variables);

            executor.execute(() -> {
                if (executed.compareAndSet(false, true)) {
                    // Only execute the task once.
                    task.run(finalI, this.variables);
                }
                // Other threads will pass through without executing the task.

                barrier.await();
            });
        }

        return this;
    }

    /**
     * Executes the given task in the specified thread.
     *
     * @param filter The thread to run the task in.
     * @param task   The task to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP masked(int filter, Task task) {
        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            final Variables finalVariables = this.variables.copy();
            this.variablesList.add(finalVariables);

            executor.execute(() -> {
                if (finalI == filter) {
                    task.run(finalI, finalVariables);
                }
            });
        }

        return this;
    }

    /**
     * Executes the given task in the master thread.
     *
     * @param task The task to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP masked(Task task) {
        return masked(0, task);
    }

    // Utility methods

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

    /**
     * Get the elapsed wall clock time.
     *
     * @return The elapsed wall clock time.
     */
    public static double getWTime() {
        return System.nanoTime() / 1e9;
    }

    /**
     * Get the current thread number.
     *
     * @return The thread number.
     */
    public static int getThreadNum() {
        if (Thread.currentThread() instanceof JrompThread jt) {
            return jt.getTid();
        }

        return 0;
    }

    /**
     * Get the team of the current thread.
     *
     * @return The team of the current thread or <code>null</code> if the thread is not a JROMP thread.
     */
    public static ThreadTeam getThreadTeam() {
        if (Thread.currentThread() instanceof JrompThread jt) {
            return jt.getTeam();
        }

        return null;
    }
}
