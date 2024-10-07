package jromp.construct.critical;

import jromp.task.Task;
import jromp.var.Variables;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Critical class provides a mechanism for executing tasks in parallel with critical sections.
 */
public class Critical {
    /**
     * A map of locks for each critical section.
     */
    private static final Map<String, Object> locks = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private Critical() {
    }

    /**
     * Enters a critical section and executes the provided task.
     *
     * @param name      the name of the critical section.
     * @param id        the thread identifier.
     * @param variables the collection of variables.
     * @param task      the task to execute.
     */
    public static void enter(String name, int id, Variables variables, Task task) {
        synchronized (locks.computeIfAbsent(name, k -> new Object())) {
            task.run(id, variables);
        }
    }
}
