package jromp.var;

import jromp.concurrent.JrompThreadLocal;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import static jromp.Utils.castClass;
import static jromp.var.InitialValues.getInitialValue;

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
    private final transient JrompThreadLocal<InternalVariable<T>> value;

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
     * The thread that created this variable.
     */
    private final transient Thread creatorThread = Thread.currentThread();

    /**
     * Constructs a new private variable with the default value.
     *
     * @param value the value of the variable.
     */
    public LastPrivateVariable(T value) {
        this.value = JrompThreadLocal.withInitial(() -> {
            // New initialValue variable just to be sure that the references are different
            T initialValue = getInitialValue(castClass(value.getClass()));
            InternalVariable<T> internalVariable = new InternalVariable<>(initialValue);
            this.threadLocals.put(Thread.currentThread().threadId(), internalVariable);
            return internalVariable;
        });

        InternalVariable<T> variable = new InternalVariable<>(value);
        this.threadLocals.put(this.creatorThread.threadId(), variable);
        this.value.set(variable); // Set the value of the creator thread
        this.lastThreadId = this.creatorThread.threadId();
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
        this.value.remove(); // Remove the value from the current thread to avoid memory leaks

        // The value is restored from the map because the value of the variable is the last value set by any thread
        if (Thread.currentThread() == creatorThread) {
            this.value.set(this.threadLocals.get(this.lastThreadId)); // Only creator thread's value is restored
            // Since the variables stored in the map are InternalVariable, we can avoid calling the end method
            // because it is a noop.
        }
    }

    @Override
    public String toString() {
        return "LastPrivateVariable{value=%s, lastValue=%s}".formatted(value.get().value(),
                                                                       threadLocals.get(lastThreadId).value());
    }
}
