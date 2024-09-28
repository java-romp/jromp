package jromp;

import jromp.task.ForTask;
import jromp.task.Task;
import jromp.utils.Utils;
import jromp.var.ReductionVariable;
import jromp.var.SharedVariable;
import jromp.var.Variable;
import jromp.var.Variables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Parallel execution block.
 */
public class Parallel {
    /**
     * The number of threads used in the current block.
     */
    private final int threads;

    /**
     * The thread executor used to execute the tasks.
     */
    private final ExecutorService threadExecutor;

    /**
     * The variables used in the current block.
     */
    private Variables variables;

    /**
     * The list of variables used in the current block to perform the
     * {@link Variable#end()} operation when joining the threads.
     */
    private final List<Variables> variablesList = new ArrayList<>();

    /**
     * Create a new parallel execution block.
     *
     * @param threads The number of threads.
     */
    private Parallel(int threads) {
        this.threads = threads;
        this.threadExecutor = Executors.newFixedThreadPool(threads);

        withVariables(Variables.create());
    }

    /**
     * Create a new parallel execution block with the default number of threads.
     *
     * @return The parallel execution block.
     */
    public static Parallel defaultConfig() {
        return new Parallel(Constants.DEFAULT_THREADS);
    }

    /**
     * Create a new parallel execution block with the given number of threads.
     *
     * @param threads The number of threads to use.
     *
     * @return The parallel execution block.
     */
    public static Parallel withThreads(int threads) {
        return new Parallel(Utils.checkThreads(threads));
    }

    private static void waitForFinish() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Set the variables to use in the parallel block.
     *
     * @param variables The variables to use.
     *
     * @return The parallel execution block.
     */
    public Parallel withVariables(Variables variables) {
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
     * Wait for all threads to finish.
     */
    public void join() {
        threadExecutor.shutdown();

        while (!threadExecutor.isTerminated()) {
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
     * Executes a task in a parallel block, using the default variables.
     *
     * @param task The task to run.
     *
     * @return The parallel execution block.
     */
    public Parallel block(Task task) {
        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            final Variables finalVariables = this.variables.copy();
            this.variablesList.add(finalVariables);

            threadExecutor.execute(() -> task.run(finalI, finalVariables));
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
     * @return The parallel execution block.
     */
    public Parallel parallelFor(int start, int end, boolean nowait, ForTask task) {
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
            threadExecutor.execute(() -> {
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
     * @return The parallel execution block.
     */
    private Parallel sections(boolean nowait, Variables variables, Task... tasks) {
        if (tasks.length <= this.threads) {
            Barrier barrier = new Barrier("Sections", tasks.length);
            barrier.setNowait(nowait);

            for (int i = 0; i < tasks.length; i++) {
                final int finalI = i;
                Task task = tasks[i];

                threadExecutor.execute(() -> {
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
     * @return The parallel execution block.
     */
    public Parallel sections(boolean nowait, Task... tasks) {
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
     * @return The parallel execution block.
     */
    public Parallel sections(boolean nowait, List<Task> tasks) {
        return sections(nowait, tasks.toArray(Task[]::new));
    }

    /**
     * Submits a block that can only be executed by a single thread.
     *
     * @param nowait Whether to wait for the threads to finish.
     * @param task   The task to run in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel singleBlock(boolean nowait, Task task) {
        AtomicBoolean executed = new AtomicBoolean(false);
        Barrier barrier = new Barrier("SingleBlock", this.threads);
        barrier.setNowait(nowait);

        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            this.variablesList.add(this.variables);

            threadExecutor.execute(() -> {
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
     * @return The parallel execution block.
     */
    public Parallel masked(int filter, Task task) {
        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            final Variables finalVariables = this.variables.copy();
            this.variablesList.add(finalVariables);

            threadExecutor.execute(() -> {
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
     * @return The parallel execution block.
     */
    public Parallel masked(Task task) {
        return masked(0, task);
    }
}
