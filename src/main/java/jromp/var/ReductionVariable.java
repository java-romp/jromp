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
     * The initial value of the reduction variable.
     */
    private final T initialValue;

    /**
     * The private variables that are used to reduce the result.
     * Each thread has its own private variable.
     */
    private final Map<Thread, InternalVariable<T>> threadLocalVariables = new ConcurrentHashMap<>();

    /**
     * The result of the reduction operation.
     */
    private final InternalVariable<T> result;

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
        this.initialValue = initialValue;
        this.result = new InternalVariable<>(initialValue);
    }

    @Override
    public T value() {
        if (Thread.currentThread() == creatorThread) {
            return result.value(); // The creator thread can get the result directly.
        }

        return threadLocalVariables.computeIfAbsent(Thread.currentThread(),
                                                    k -> new InternalVariable<>(initValue(initialValue)))
                                   .value(); // Get the variable of the current thread.
    }

    @Override
    public void set(T value) {
        if (Thread.currentThread() == creatorThread) {
            result.set(value); // The creator thread can set the result directly.
        } else {
            threadLocalVariables.computeIfAbsent(Thread.currentThread(),
                                                 k -> new InternalVariable<>(initValue(value)))
                                .set(value); // Set the variable of the current thread.
        }
    }

    @Override
    public void update(UnaryOperator<T> operator) {
        if (Thread.currentThread() == creatorThread) {
            result.update(operator); // The creator thread can update the result directly.
        } else {
            threadLocalVariables.computeIfAbsent(Thread.currentThread(),
                                                 k -> new InternalVariable<>(initValue(initialValue)))
                                .update(operator); // Update the variable of the current thread.
        }
    }

    @Override
    public void end() {
        threadLocalVariables.values().forEach(Variable::end); // It is a no-op.
        threadLocalVariables.clear();
    }

    public boolean isMerged() {
        return merged;
    }

    public void merge() {
        if (merged) {
            return;
        }

        operation.initialize(result);
        threadLocalVariables.values().forEach(
                tlVar -> result.update(oldResult -> operation.combine(oldResult, tlVar.value())));
        merged = true;
    }

    @Override
    public String toString() {
        String privateVariablesString = threadLocalVariables.values()
                                                            .stream()
                                                            .map(InternalVariable::toString)
                                                            .collect(Collectors.joining("\n    "));

        return "ReductionVariable{%n  operation=%s,%n  initialValue=%s,%n  threadLocalVariables=[%n    %s%n  ],%n  result=%s,%n  merged=%s,%n  creatorThread=%s%n}"
                .formatted(operation, initialValue, privateVariablesString, result, merged, creatorThread);
    }

    private T initValue(T val) {
        return InitialValues.getInitialValue(castClass(val.getClass()));
    }
}
