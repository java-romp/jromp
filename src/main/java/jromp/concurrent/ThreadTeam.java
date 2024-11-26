package jromp.concurrent;

import java.util.ArrayList;

/**
 * A thread team is a group of threads that are working together
 * to execute some parallel task.
 */
public class ThreadTeam {
    /**
     * The list of threads in this team.
     */
    private final ArrayList<JrompThread> threads;

    /**
     * The team ID.
     */
    private final int teamId;

    /**
     * Constructs a new {@link ThreadTeam} with the given team ID.
     *
     * @param teamId the team ID.
     */
    ThreadTeam(int teamId) {
        this.teamId = teamId;
        this.threads = new ArrayList<>();
    }

    /**
     * Adds the given {@link JrompThread} to this team.
     *
     * @param thread the {@link JrompThread} to be added.
     */
    void addThread(JrompThread thread) {
        threads.add(thread);
    }

    /**
     * Team ID getter.
     *
     * @return the team ID.
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Returns the number of threads in this team.
     *
     * @return the number of threads in this team.
     */
    public int size() {
        return threads.size();
    }
}
