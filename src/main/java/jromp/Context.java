package jromp;

import jromp.var.Variables;

/**
 * The context for the current parallel block. This class is used to store several properties
 * that has a parallel block:
 * <ul>
 *     <li>The number of threads used in the current parallel block.</li>
 *     <li>The number of threads per team used in the current parallel block.</li>
 *     <li>The variables used in the current parallel block.</li>
 * </ul>
 */
public class Context {
    private static final Context instance = new Context();

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
    private Variables variables;

    public static Context getInstance() {
        return instance;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getThreadsPerTeam() {
        return threadsPerTeam;
    }

    public void setThreadsPerTeam(int threadsPerTeam) {
        this.threadsPerTeam = threadsPerTeam;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }
}
