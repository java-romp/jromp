package jromp.parallel.construct.atomic;

import jromp.parallel.construct.atomic.operation.Operation;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variables;

import java.io.Serializable;

/**
 * Class to perform atomic operations on shared variables.
 */
public class Atomic {
    /**
     * Private constructor to prevent instantiation.
     */
    private Atomic() {
    }

    /**
     * Reads the value of a shared variable.
     *
     * @param name      the name of the shared variable.
     * @param variables the collection of variables.
     * @param <T>       the type of the shared variable.
     *
     * @return the value of the shared variable.
     */
    public static synchronized <T extends Serializable>
    T read(String name, Variables variables) {
        SharedVariable<T> sharedVariable = getSharedVariable(name, variables);
        return sharedVariable.toAtomic().value();
    }

    /**
     * Writes a value to a shared variable.
     *
     * @param name      the name of the shared variable.
     * @param value     the value to be written.
     * @param variables the collection of variables.
     * @param <T>       the type of the value and shared variable.
     */
    public static synchronized <T extends Serializable>
    void write(String name, T value, Variables variables) {
        getSharedVariable(name, variables).toAtomic().set(value);
    }

    /**
     * Updates the value of a shared variable based on the provided operation.
     * This method is thread-safe and synchronized.
     *
     * @param name      the name of the shared variable.
     * @param operation the operation to perform on the shared variable.
     * @param variables the collection of variables containing the shared variable.
     * @param <T>       the type of the shared variable.
     */
    public static synchronized <T extends Serializable>
    void update(String name, Operation<T> operation, Variables variables) {
        SharedVariable<T> sharedVariable = getSharedVariable(name, variables);
        sharedVariable.toAtomic().update(operation.get());
    }

    /**
     * Retrieves a shared variable from the provided collection of variables.
     *
     * @param name      the name of the shared variable.
     * @param variables the collection of variables.
     * @param <T>       the type of the shared variable.
     *
     * @return the shared variable with the specified name, or null if it does not exist.
     */
    private static <T extends Serializable>
    SharedVariable<T> getSharedVariable(String name, Variables variables) {
        return (SharedVariable<T>) variables.<T>get(name);
    }
}
