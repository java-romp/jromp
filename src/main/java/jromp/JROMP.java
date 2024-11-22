package jromp;

import jromp.concurrent.JrompExecutorWrapper;
import jromp.concurrent.JrompThread;
import jromp.concurrent.ThreadTeam;
import jromp.task.ForTask;
import jromp.task.Task;
import jromp.var.ReductionVariable;
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
     * The context for the current parallel block.
     */
    private static final Context context = new Context();

    /**
     * The thread executor used to execute the tasks.
     */
    private final JrompExecutorWrapper executor;

    /**
     * The list of variables used in all blocks to perform the
     * {@link Variable#end()} operation when joining the threads.
     */
    @Deprecated
    private final List<Variables> variablesList = new ArrayList<>();

    /**
     * Create a new instance for the parallel runtime.
     *
     * @param threads        The number of threads.
     * @param threadsPerTeam The number of threads per team.
     */
    private JROMP(int threads, int threadsPerTeam) {
        context.threads = checkThreads(threads);
        context.threadsPerTeam = checkThreadsPerTeam(context.threads, threadsPerTeam);
        this.executor = new JrompExecutorWrapper(
                context.threads,
                JrompThread.newThreadFactory(context.threadsPerTeam)
        );

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
        return new JROMP(threads, threads);
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
        return new JROMP(threads, threadsPerTeam);
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

    public JROMP registerVariables(Variable<?>... vars) {
        for (Variable<?> variable : vars) {
            context.registerVariable(variable);
        }

        return this;
    }

    /**
     * Set the variables to use in a parallel block.
     *
     * @param variables The variables to use.
     *
     * @return The parallel runtime.
     */
    @Deprecated
    public JROMP withVariables(Variables variables) {
        context.variables = variables;
        return this;
    }

    /**
     * Wait for all threads to finish and perform the necessary operations.
     */
    public void join() {
        executor.shutdown();

        while (!executor.isTerminated()) {
            waitForFinish();
        }

        variablesList.add(context.variables);

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
    public JROMP parallel(Task task) {
        for (int i = 0; i < context.threads; i++) {
            final Variables finalVariables = context.variables.copy();
            this.variablesList.add(finalVariables);

            executor.execute(() -> task.run(finalVariables));
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
        Barrier barrier = new Barrier("ParallelFor", context.threads);
        barrier.setNowait(nowait);

        for (int i = 0; i < context.threads; i++) {
            // Calculate the start and end indices for the current thread.
            int chunkSize = (end - start) / context.threads;
            int chunkStart = start + i * chunkSize;
            int chunkEnd;

            // If this is the last thread, make sure to include the remaining elements.
            if (i == context.threads - 1) {
                chunkEnd = end;
            } else {
                chunkEnd = chunkStart + chunkSize;
            }

            final Variables finalVariables = context.variables.copy();
            this.variablesList.add(finalVariables);
            executor.execute(() -> {
                task.run(chunkStart, chunkEnd, finalVariables);
                barrier.await();
            });
        }

        return this;
    }

    /**
     * Executes a for loop in parallel with the given start and end indices, using
     * a task implementation.
     *
     * @param start The start index of the for loop.
     * @param end   The end index of the for loop.
     * @param task  The task to be executed in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP parallelFor(int start, int end, ForTask task) {
        return parallelFor(start, end, false, task);
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
        if (tasks.length <= context.threads) {
            Barrier barrier = new Barrier("Sections", tasks.length);
            barrier.setNowait(nowait);

            for (Task task : tasks) {
                executor.execute(() -> {
                    task.run(variables);
                    barrier.await();
                });
            }
        } else {
            // If there are more sections than threads, submit the tasks in batches of threads.
            for (int i = 0; i < tasks.length; i += context.threads) {
                int end = Math.min(i + context.threads, tasks.length);
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
        Variables vars = context.variables.copy();
        this.variablesList.add(vars);

        return sections(nowait, vars, tasks);
    }

    /**
     * Executes the given tasks in separate sections.
     *
     * @param tasks The tasks to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP sections(Task... tasks) {
        return sections(false, tasks);
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
     * Executes the given tasks in separate sections.
     *
     * @param tasks The tasks to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP sections(List<Task> tasks) {
        return sections(false, tasks);
    }

    /**
     * Submits a block that can only be executed by a single thread.
     *
     * @param nowait Whether to wait for the threads to finish.
     * @param task   The task to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP single(boolean nowait, Task task) {
        AtomicBoolean executed = new AtomicBoolean(false);
        Barrier barrier = new Barrier("Single", context.threads);
        barrier.setNowait(nowait);

        for (int i = 0; i < context.threads; i++) {
            this.variablesList.add(context.variables);

            executor.execute(() -> {
                if (executed.compareAndSet(false, true)) {
                    // Only execute the task once.
                    task.run(context.variables);
                }
                // Other threads will pass through without executing the task.

                barrier.await();
            });
        }

        return this;
    }

    /**
     * Submits a block that can only be executed by a single thread.
     *
     * @param task The task to run in parallel.
     *
     * @return The parallel runtime.
     */
    public JROMP single(Task task) {
        return single(false, task);
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
        for (int i = 0; i < context.threads; i++) {
            final Variables finalVariables = context.variables.copy();
            this.variablesList.add(finalVariables);

            executor.execute(() -> {
                if (getThreadNum() == filter) {
                    task.run(finalVariables);
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

    /**
     * Blocks the execution of the threads until all threads reach the barrier.
     *
     * @return The parallel runtime.
     */
    public JROMP barrier() {
        Barrier barrier = new Barrier("Barrier", context.threads);

        for (int i = 0; i < context.threads; i++) {
            executor.execute(barrier::await);
        }

        return this;
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

    /**
     * Get the number of threads used in the current parallel block. If not in a parallel context,
     * the number of threads is 1.
     *
     * @return The number of threads.
     */
    public static int getNumThreads() {
        return isInParallelContext() ? context.threads : 1;
    }

    /**
     * Get the number of threads per team used in the current parallel block. If not in a parallel context,
     * the number of threads per team is 1.
     *
     * @return The number of threads per team.
     */
    public static int getNumThreadsPerTeam() {
        return isInParallelContext() ? context.threadsPerTeam : 1;
    }

    /**
     * Check if the current thread is in a parallel context.
     *
     * @return <code>true</code> if the thread is in a parallel context, <code>false</code> otherwise.
     */
    private static boolean isInParallelContext() {
        return getThreadTeam() != null;
    }

    /**
     * The context for the current parallel block. This class is used to store several properties
     * that has a parallel block:
     * <ul>
     *     <li>The number of threads used in the current parallel block.</li>
     *     <li>The number of threads per team used in the current parallel block.</li>
     *     <li>The variables used in the current parallel block.</li>
     * </ul>
     */
    private static class Context {
        /**
         * The number of threads used in the current parallel block.
         */
        private int threads;

        /**
         * The number of threads per team used in the current parallel block.
         */
        private int threadsPerTeam;

        /**
         * The variables used in the current parallel block.
         */
        @Deprecated
        private Variables variables;

        private final List<Variable<?>> variablesList = new ArrayList<>();

        void registerVariable(Variable<?> variable) {
            variablesList.add(variable);
        }
    }
}
