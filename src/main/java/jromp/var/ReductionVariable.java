package jromp.var;

import jromp.concurrent.JrompThreadLocal;
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
    private final Map<Long, InternalVariable<T>> threadLocalValues = new ConcurrentHashMap<>();

    /**
     * The names of the threads that have accessed the reduction variable.
     */
    private final Map<Long, String> threadNames = new ConcurrentHashMap<>();

    /**
     * The result of the reduction operation.
     */
    private final transient JrompThreadLocal<InternalVariable<T>> value;

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
     * <p>
     * Note: The value of the "private" variables is initialized to the default initial value
     * of the reduction variable.
     *
     * @param operation    the reduction operation.
     * @param initialValue the initial value of the reduction variable.
     */
    public ReductionVariable(ReductionOperation<T> operation, T initialValue) {
        this.operation = operation;

        this.value = JrompThreadLocal.withInitial(() -> {
            T initValue = InitialValues.getInitialValue(castClass(initialValue.getClass()));
            InternalVariable<T> iVar = new InternalVariable<>(initValue);
            Thread thread = Thread.currentThread();

            this.threadLocalValues.put(thread.threadId(), iVar);
            this.threadNames.put(thread.threadId(), thread.getName());

            return iVar;
        }); // Other threads will initialize their variables when they access them (with the default initial value).

        // The creator thread should initialize its variable immediately with the value passed to the constructor.
        InternalVariable<T> variable = new InternalVariable<>(initialValue);

        this.threadLocalValues.put(this.creatorThread.threadId(), variable);
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
        this.value.get() // Initialize and add the new variable to the map (if not initialized yet).
                  .set(value);
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        this.value.get() // Initialize and add the new variable to the map (if not initialized yet).
                  .update(operator);
    }

    @Override
    public void end() {
        if (Thread.currentThread() == creatorThread) {
            // NOTE: The creator thread should not remove its variable,
            // because it is the result of the reduction operation.
            return;
        }

        // Remove the value from the other threads, not the creator one.
        this.value.remove();
    }

    /**
     * Indicates whether the reduction variable has been merged.
     *
     * @return {@code true} if the reduction variable has been merged; {@code false} otherwise.
     */
    public boolean isMerged() {
        return merged;
    }

    /**
     * Merges the private variables of the threads to the result of the reduction operation.
     */
    public void merge() {
        if (merged) {
            return;
        }

        Variable<T> result = this.value.get();
        this.operation.initialize(result);
        this.threadLocalValues.entrySet()
                              .stream()
                              .filter(entry -> entry.getKey() != this.creatorThread.threadId())
                              .map(Map.Entry::getValue)
                              .map(InternalVariable::value)
                              .forEach(val -> result.update(oldResult -> this.operation.combine(oldResult, val)));
        this.merged = true;
    }

    @Override
    public String toString() {
        String privateVariablesString = this.threadLocalValues.entrySet()
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
