package jromp.concurrent;

import java.util.concurrent.ThreadFactory;

public class JrompThread extends Thread {
    private final int tid;
    private final int team;
    private final String threadName;

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

    private static class JrompThreadFactory implements ThreadFactory {
        private final int threadsPerTeam;
        private int tid = 0;
        private int team = 0;

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
