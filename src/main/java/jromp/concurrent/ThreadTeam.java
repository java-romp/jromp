package jromp.concurrent;

import java.util.ArrayList;

public class ThreadTeam {
    private final ArrayList<JrompThread> threads;
    private final int teamId;

    ThreadTeam(int teamId) {
        this.teamId = teamId;
        this.threads = new ArrayList<>();
    }

    void addThread(JrompThread thread) {
        threads.add(thread);
    }

    public int getTeamId() {
        return teamId;
    }

    public int size() {
        return threads.size();
    }
}
