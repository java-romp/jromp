package jromp.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * A thread class that extends {@link Thread} and provides additional information about the thread.
 */
public class JrompThread extends Thread {
    /**
     * The thread ID.
     */
    private final int tid;

    /**
     * The team ID.
     */
    private final int team;

    /**
     * The thread name.
     */
    private final String threadName;

    /**
     * Constructs a new {@link JrompThread} with the given {@link Runnable}, thread ID, and team ID.
     *
     * @param runnable the {@link Runnable} to be executed by this thread.
     * @param tid      the thread ID.
     * @param team     the team ID.
     *
     * @see #generateThreadName(int, int)
     */
    public JrompThread(Runnable runnable, int tid, int team) {
        super(runnable);

        this.tid = tid;
        this.team = team;
        this.threadName = generateThreadName(team, tid);

        setName(threadName);
    }

    public int getTid() {
        return tid;
    }

    public int getTeam() {
        return team;
    }

    public String getThreadName() {
        return threadName;
    }

    /**
     * Generates a thread name with the given team ID and thread ID.
     *
     * @param team the team ID.
     * @param tid  the thread ID.
     *
     * @return the generated thread name.
     */
    private static String generateThreadName(int team, int tid) {
        return "%s-%d-%d".formatted(JrompThread.class.getSimpleName(), team, tid);
    }

    @Override
    public String toString() {
        return threadName;
    }

    public static ThreadFactory newThreadFactory(int threadsPerTeam) {
        return new JrompThreadFactory(threadsPerTeam);
    }

    /**
     * A {@link ThreadFactory} that creates new {@link JrompThread} instances.
     * The number of threads per team is specified in the constructor.
     */
    private static class JrompThreadFactory implements ThreadFactory {
        /**
         * The number of threads per team.
         */
        private final int threadsPerTeam;

        /**
         * The thread ID.
         */
        private int tid = 0;

        /**
         * The team ID.
         */
        private int team = 0;

        /**
         * Constructs a new {@link JrompThreadFactory} with the given number of threads per team.
         *
         * @param threadsPerTeam the number of threads per team.
         */
        public JrompThreadFactory(int threadsPerTeam) {
            this.threadsPerTeam = threadsPerTeam;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new JrompThread(runnable, tid++, team);

            if (tid == threadsPerTeam) {
                tid = 0;
                team++;
            }

            return thread;
        }
    }
}
