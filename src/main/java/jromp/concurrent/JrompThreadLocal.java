package jromp.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static jromp.Utils.getThreadId;

/**
 * A simple thread-local variable.
 *
 * @param <T> the type of the thread-local variable.
 */
public class JrompThreadLocal<T> {
    /**
     * The map of thread-local variables.
     */
    private final Map<Long, T> threadLocalMap = new ConcurrentHashMap<>();

    /**
     * Creates a new thread-local variable.
     */
    public JrompThreadLocal() {
        //
    }

    /**
     * Returns the initial value for the variable of the current thread.
     *
     * @return the initial value for the variable of the current thread.
     */
    protected T initialValue() {
        return null;
    }

    /**
     * Returns the value of the thread-local variable for the current thread.
     *
     * @return the value of the thread-local variable for the current thread.
     */
    public T get() {
        return get(Thread.currentThread());
    }

    /**
     * Returns the value of the thread-local variable for the given thread.
     *
     * @param t the thread for which to get the value.
     *
     * @return the value of the thread-local variable for the given thread.
     */
    public T get(Thread t) {
        return this.threadLocalMap.computeIfAbsent(t.threadId(), k -> initialValue());
    }

    /**
     * Sets the value of the thread-local variable for the current thread.
     *
     * @param value the value to set.
     */
    public void set(T value) {
        this.threadLocalMap.put(getThreadId(), value);
    }

    /**
     * Removes the value of the thread-local variable for the current thread.
     */
    public void remove() {
        this.threadLocalMap.remove(getThreadId());
    }

    /**
     * Creates a new thread-local variable with the given initial value.
     *
     * @param supplier the supplier for the initial value.
     * @param <S>      the type of the thread-local variable.
     *
     * @return a new thread-local variable with the given initial value.
     */
    public static <S> JrompThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    /**
     * A thread-local variable with an initial value supplied by a {@link Supplier}.
     *
     * @param <T> the type of the thread-local variable.
     */
    static final class SuppliedThreadLocal<T> extends JrompThreadLocal<T> {
        /**
         * The supplier for the initial value.
         */
        private final Supplier<? extends T> supplier;

        /**
         * Creates a new thread-local variable with the given {@link Supplier} for the initial value.
         *
         * @param supplier the supplier for the initial value.
         */
        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected T initialValue() {
            return this.supplier.get();
        }
    }
}
