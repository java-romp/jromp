package jromp.parallel;

import jromp.Constants;
import jromp.parallel.builder.SectionBuilder;
import jromp.parallel.task.ForTask;
import jromp.parallel.task.Task;
import jromp.parallel.utils.Utils;
import jromp.parallel.var.ReductionVariable;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variable;
import jromp.parallel.var.Variables;

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
    private Variables variables = Variables.create();

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
    }

    /**
     * Create a new parallel execution block with the default number of threads.
     *
     * @return The parallel execution block.
     */
    public static Parallel defaultConfig() {
        return new Parallel(Constants.MAX_THREADS);
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

        // Perform the last operation on all variables.
        for (Variables vars : variablesList) {
            // Merge all reduction variables after the parallel block has ended.
            vars.getVariablesOfType(ReductionVariable.class)
                .forEach(variable -> ((ReductionVariable<?>) variable).merge());

            // End all variables.
            vars.getVariables().values().forEach(Variable::end);
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
        return this.block(Variables.create(), task);
    }

    /**
     * Executes a task in a parallel block, using the given variables.
     *
     * @param variables The variables to use.
     * @param task      The task to run.
     *
     * @return The parallel execution block.
     */
    public Parallel block(Variables variables, Task task) {
        this.variables = variables;
        addNumThreadsToVariables(this.variables);

        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            this.variablesList.add(this.variables);

            threadExecutor.execute(() -> task.run(finalI, this.variables));
        }

        return this;
    }

    /**
     * Executes a for loop in parallel with the given start and end indices, using a task implementation.
     *
     * @param start The start index of the for loop.
     * @param end   The end index of the for loop.
     * @param task  The task to be executed in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel parallelFor(int start, int end, ForTask task) {
        return this.parallelFor(start, end, Variables.create(), task);
    }

    /**
     * Executes a for loop in parallel with the given start and end indices, using a
     * task implementation and variables.
     *
     * @param start     The start index of the for loop.
     * @param end       The end index of the for loop.
     * @param variables The variables to use in the task.
     * @param forTask   The task to be executed in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel parallelFor(int start, int end, Variables variables, ForTask forTask) {
        this.variables = variables;
        addNumThreadsToVariables(this.variables);
        this.variablesList.add(this.variables);
        Barrier barrier = new Barrier("ParallelFor", this.threads);

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
            final Variables finalVariables = variables.copy();
            this.variablesList.add(finalVariables);
            threadExecutor.execute(() -> {
                forTask.run(finalI, chunkStart, chunkEnd, finalVariables);
                barrier.await();
            });
        }

        return this;
    }

    /**
     * Executes the given tasks in separate sections.
     *
     * @param tasks The tasks to run in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel sections(Task... tasks) {
        return this.sections(Variables.create(), tasks);
    }

    /**
     * Executes the given tasks in separate sections.
     *
     * @param variables The variables to use.
     * @param tasks     The tasks to run in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel sections(Variables variables, Task... tasks) {
        List<Section> sections = new ArrayList<>();

        for (Task task : tasks) {
            sections.add(new Section(task, variables));
        }

        return this.sections(sections.toArray(Section[]::new));
    }

    /**
     * Executes the given sections in parallel.
     *
     * @param sections The sections to run in parallel.
     *
     * @return The parallel execution block.
     */
    public Parallel sections(Section... sections) {
        if (sections.length <= this.threads) {
            Barrier barrier = new Barrier("Sections", sections.length);

            for (int i = 0; i < sections.length; i++) {
                final int finalI = i;
                Task task = sections[i].task();
                Variables vars = sections[i].variables();
                addNumThreadsToVariables(vars);
                this.variablesList.add(vars);

                threadExecutor.execute(() -> {
                    task.run(finalI, vars);
                    barrier.await();
                });
            }
        } else {
            // If there are more sections than threads, submit the tasks in batches of threads.
            for (int i = 0; i < sections.length; i += this.threads) {
                int end = Math.min(i + this.threads, sections.length);
                int batchSize = end - i;
                Section[] batch = new Section[batchSize];

                System.arraycopy(sections, i, batch, 0, batchSize);
                this.sections(batch);
            }
        }

        return this;
    }

    /**
     * Executes the given sections in parallel.
     *
     * @param sectionBuilder The builder for parallel sections.
     *
     * @return The parallel execution block.
     */
    public Parallel sections(SectionBuilder sectionBuilder) {
        return this.sections(sectionBuilder.build().toArray(Section[]::new));
    }

    public Parallel singleBlock(Task task) {
        AtomicBoolean executed = new AtomicBoolean(false);
        Barrier barrier = new Barrier("SingleBlock", this.threads);

        for (int i = 0; i < this.threads; i++) {
            final int finalI = i;
            this.variablesList.add(this.variables);

            threadExecutor.execute(() -> {
                if (executed.compareAndSet(false, true)) {
                    // Only execute the task once.
                    task.run(finalI, this.variables);
                }
                // Other threads will pass through without executing the task.

                barrier.await(); // Wait for all threads to reach the barrier.
            });
        }

        return this;
    }
}
