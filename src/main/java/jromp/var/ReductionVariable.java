package jromp.var;

import jromp.var.reduction.ReductionOperation;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static jromp.Utils.castClass;

/**
 * Represents a reduction variable that its value is reduced from "custom" thread-local
 * variables using a reduction operation.
 *
 * @param <T> the type of the variable.
 */
public class ReductionVariable<T extends Serializable> implements Variable<T> {
    /**
     * The reduction operation.
     */
    private final transient ReductionOperation<T> operation;

    /**
     * The private variables that are used to reduce the result.
     * Each thread has its own private variable.
     */
    private final Map<Long, InternalVariable<T>> threadLocals = new ConcurrentHashMap<>();

    /**
     * The names of the threads that have accessed the reduction variable.
     */
    private final Map<Long, String> threadNames = new ConcurrentHashMap<>();

    /**
     * The result of the reduction operation.
     */
    private final transient ThreadLocal<InternalVariable<T>> value;

    /**
     * Indicates whether the reduction variable has been merged.
     */
    private boolean merged = false;

    /**
     * The thread that created the reduction variable.
     */
    private final transient Thread creatorThread = Thread.currentThread();

    /**
     * Constructs a new reduction variable with the given reduction operation and initial value.
     *
     * @param operation    the reduction operation.
     * @param initialValue the initial value of the reduction variable.
     *
     * @implNote The value of the "private" variables is initialized to the default initial value
     * of the reduction variable.
     */
    public ReductionVariable(ReductionOperation<T> operation, T initialValue) {
        this.operation = operation;

        this.value = ThreadLocal.withInitial(() -> {
            T initValue = InitialValues.getInitialValue(castClass(initialValue.getClass()));
            InternalVariable<T> iVar = new InternalVariable<>(initValue);
            Thread thread = Thread.currentThread();

            this.threadLocals.put(thread.threadId(), iVar);
            this.threadNames.put(thread.threadId(), thread.getName());

            return iVar;
        }); // Other threads will initialize their variables when they access them (with the default initial value).

        // The creator thread should initialize its variable immediately with the value passed to the constructor.
        InternalVariable<T> variable = new InternalVariable<>(initialValue);

        this.threadLocals.put(this.creatorThread.threadId(), variable);
        this.threadNames.put(this.creatorThread.threadId(), this.creatorThread.getName());

        this.value.set(variable); // Initialize the creator thread's variable and omit the thread-local initialization.
    }

    @Override
    public T value() {
        // Creator thread can access the result directly.
        // Other threads should access their private variables (or initialize them if not initialized yet).
        return this.value.get().value();
    }

    @Override
    public void set(T value) {
        long currentThreadId = Thread.currentThread().threadId();

        if (this.threadLocals.containsKey(currentThreadId)) {
            this.threadLocals.get(currentThreadId).set(value);
        } else {
            // This will occur only once on sub-threads (first time they access the variable using this method).
            this.value.get() // Initialize and add the new variable to the map.
                      .set(value);
        }
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        long currentThreadId = Thread.currentThread().threadId();

        if (this.threadLocals.containsKey(currentThreadId)) {
            this.threadLocals.get(currentThreadId).update(operator);
        } else {
            // This will occur only once on sub-threads (first time they access the variable using this method).
            this.value.get() // Initialize and add the new variable to the map.
                      .update(operator);
        }
    }

    @Override
    public void end() {
        this.threadLocals.values().forEach(Variable::end); // It is a no-op.

        if (Thread.currentThread() == creatorThread) {
            return;
        }

        // Remove the value from the other threads, not the creator one.
        this.value.remove();
    }

    public boolean isMerged() {
        return merged;
    }

    public void merge() {
        if (merged) {
            return;
        }

        Variable<T> result = this.value.get();
        this.operation.initialize(result);
        this.threadLocals.values().forEach(
                tlVar -> result.update(oldResult -> this.operation.combine(oldResult, tlVar.value())));
        this.merged = true;
    }

    @Override
    public String toString() {
        String privateVariablesString = this.threadLocals.entrySet()
                                                         .stream()
                                                         .map(entry -> {
                                                             long threadId = entry.getKey();
                                                             String threadName = this.threadNames.get(threadId);
                                                             InternalVariable<T> iVar = entry.getValue();

                                                             return "%s => %s".formatted(threadName, iVar);
                                                         })
                                                         .collect(Collectors.joining("\n    "));

        return "ReductionVariable{%n  operation=%s,%n  threadLocalVariables=[%n    %s%n  ],%n  value=%s,%n  merged=%s,%n  creatorThread=%s%n}"
                .formatted(this.operation, privateVariablesString, this.value.get(), this.merged,
                           this.creatorThread.getName());
    }
}
