package jromp.concurrent;

/**
 * A thread-local flag that can be activated and deactivated.
 * <p>
 * This class is useful for storing flags that are specific to a thread, such as
 * whether a block has parallelism enabled.
 * <p>
 * The flag is initialized to a given value, which is {@code false} by default.
 */
public class ThreadLocalFlag extends JrompThreadLocal<Boolean> {
    private final boolean initialValue;

    /**
     * Creates a new thread-local flag with the initial value set to {@code false}.
     */
    public ThreadLocalFlag() {
        this(false);
    }

    /**
     * Creates a new thread-local flag with the given initial value.
     *
     * @param initialValue the initial value of the flag.
     */
    public ThreadLocalFlag(boolean initialValue) {
        super();
        this.initialValue = initialValue;
    }

    @Override
    protected Boolean initialValue() {
        return initialValue;
    }

    /**
     * Returns whether the flag is active.
     *
     * @return {@code true} if the flag is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return get();
    }

    public boolean isInactive() {
        return !get();
    }

    /**
     * Activates the flag (sets it to {@code true}).
     */
    public void activate() {
        set(true);
    }

    /**
     * Deactivates the flag (sets it to {@code false}).
     */
    public void deactivate() {
        set(false);
    }

    @Override
    public String toString() {
        return "ThreadLocalFlag{" +
                "value=" + get() +
                '}';
    }
}
