package jromp.concurrent;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * A thread class that extends {@link Thread} and provides additional information about the thread.
 */
public class JrompThread extends Thread {
    public static final String CLASS_NAME = JrompThread.class.getSimpleName();

    /**
     * The thread ID.
     */
    private final int tid;

    /**
     * The team of this thread.
     */
    private final ThreadTeam team;

    /**
     * The thread name.
     */
    private final String threadName;

    /**
     * Constructs a new {@link JrompThread} with the given {@link Runnable}, thread ID, and team.
     *
     * @param runnable the {@link Runnable} to be executed by this thread.
     * @param tid      the thread ID.
     * @param team     the team of this thread.
     *
     * @see #generateThreadName(ThreadTeam, int)
     */
    public JrompThread(Runnable runnable, int tid, ThreadTeam team) {
        super(runnable);

        this.tid = tid;
        this.team = team;
        this.threadName = generateThreadName(team, tid);

        setName(threadName);
        this.team.addThread(this);
    }

    public int getTid() {
        return tid;
    }

    public ThreadTeam getTeam() {
        return team;
    }

    public String getThreadName() {
        return threadName;
    }

    /**
     * Generates a thread name with the given team and thread ID.
     *
     * @param team the team of the thread.
     * @param tid  the thread ID.
     *
     * @return the generated thread name.
     */
    private static String generateThreadName(ThreadTeam team, int tid) {
        return "%s-%d-%d".formatted(CLASS_NAME, team.getTeamId(), tid);
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
         * The team of the thread.
         */
        private ThreadTeam team = new ThreadTeam(0);

        /**
         * The list of thread teams.
         */
        private final ArrayList<ThreadTeam> threadTeams = new ArrayList<>();

        /**
         * Constructs a new {@link JrompThreadFactory} with the given number of threads per team.
         *
         * @param threadsPerTeam the number of threads per team.
         */
        public JrompThreadFactory(int threadsPerTeam) {
            this.threadsPerTeam = threadsPerTeam;
            this.threadTeams.add(team);
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new JrompThread(runnable, tid++, team);

            if (tid == threadsPerTeam) {
                tid = 0;
                team = new ThreadTeam(threadTeams.size());
                threadTeams.add(team);
            }

            return thread;
        }
    }
}
