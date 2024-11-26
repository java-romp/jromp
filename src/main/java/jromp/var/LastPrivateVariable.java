package jromp.var;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * Same as {@link PrivateVariable}, but the value of the variable (on the creator thread) is the last
 * value set by any thread.
 *
 * @param <T> the type of the variable.
 */
public class LastPrivateVariable<T extends Serializable> implements Variable<T> {
    /**
     * The value of the variable.
     */
    private final transient ThreadLocal<InternalVariable<T>> value;

    /**
     * The private variables that are used to get the last value set.
     * Each thread has its own private variable.
     */
    private final Map<Long, InternalVariable<T>> threadLocals = new ConcurrentHashMap<>();

    /**
     * The last thread that accessed the variable.
     */
    private long lastThreadId;

    /**
     * Constructs a new private variable with the default value.
     *
     * @param value the value of the variable.
     */
    public LastPrivateVariable(T value) {
        this.value = ThreadLocal.withInitial(() -> {
            InternalVariable<T> internalVariable = new InternalVariable<>(value);
            this.threadLocals.put(Thread.currentThread().threadId(), internalVariable);
            return internalVariable;
        });

        Thread creatorThread = Thread.currentThread();
        InternalVariable<T> variable = new InternalVariable<>(value);
        this.threadLocals.put(creatorThread.threadId(), variable);
        this.value.set(variable); // Set the value of the creator thread
        this.lastThreadId = creatorThread.threadId();
    }

    @Override
    public T value() {
        return this.value.get().value();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: This method is synchronized because we must ensure that the value is set by
     * the same thread that updates the lastThreadId field.
     */
    @Override
    public synchronized void set(T value) {
        this.value.get().set(value);
        this.lastThreadId = Thread.currentThread().threadId();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: This method is synchronized because we must ensure that the value is set by
     * the same thread that updates the lastThreadId field.
     */
    @Override
    public synchronized void update(UnaryOperator<T> operator) {
        this.value.get().update(operator);
        this.lastThreadId = Thread.currentThread().threadId();
    }

    @Override
    public void end() {
        this.value.set(this.threadLocals.get(this.lastThreadId));
        // this.threadLocals.values().forEach(Variable::end); // This is a no-op, but kept this comment for understanding.
        // this.threadLocals.clear();
        // Todo: Add a method to the interface to remove the thread-local variables.
    }

    @Override
    public String toString() {
        return "LastPrivateVariable{value=%s, lastValue=%s}".formatted(value.get().value(),
                                                                       this.threadLocals.get(this.lastThreadId).value());
    }
}
