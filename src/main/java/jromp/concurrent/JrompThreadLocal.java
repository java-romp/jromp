package jromp.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static jromp.Utils.getThreadId;

public class JrompThreadLocal<T> {
    private final Map<Long, T> threadLocalMap = new ConcurrentHashMap<>();

    public JrompThreadLocal() {
        //
    }

    protected T initialValue() {
        return null;
    }

    public T get() {
        return get(Thread.currentThread());
    }

    public T get(Thread t) {
        return this.threadLocalMap.computeIfAbsent(t.threadId(), k -> initialValue());
    }

    public void set(T value) {
        this.threadLocalMap.put(getThreadId(), value);
    }

    public void remove() {
        this.threadLocalMap.remove(getThreadId());
    }

    public static <S> JrompThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    static final class SuppliedThreadLocal<T> extends JrompThreadLocal<T> {
        private final Supplier<? extends T> supplier;

        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected T initialValue() {
            return this.supplier.get();
        }
    }
}
