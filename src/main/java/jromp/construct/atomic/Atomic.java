package jromp.construct.atomic;

import jromp.operation.Operation;
import jromp.var.SharedVariable;

import java.io.Serializable;

/**
 * Class to perform atomic operations on {@link SharedVariable}s.
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
     * @param <T>      the type of the shared variable.
     * @param variable the shared variable to read.
     *
     * @return the value of the shared variable.
     */
    public static synchronized <T extends Serializable>
    T read(SharedVariable<T> variable) {
        return variable.toAtomic().value();
    }

    /**
     * Writes a value to a shared variable.
     *
     * @param <T>      the type of the value and shared variable.
     * @param variable the shared variable to write to.
     * @param value    the value to be written.
     */
    public static synchronized <T extends Serializable>
    void write(SharedVariable<T> variable, T value) {
        variable.toAtomic().set(value);
    }

    /**
     * Updates the value of a shared variable based on the provided operation.
     *
     * @param <T>       the type of the shared variable.
     * @param variable  the shared variable to update.
     * @param operation the operation to perform on the shared variable.
     */
    public static synchronized <T extends Serializable>
    void update(SharedVariable<T> variable, Operation<T> operation) {
        variable.toAtomic().update(operation);
    }
}
