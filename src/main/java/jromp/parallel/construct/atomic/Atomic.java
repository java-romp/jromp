package jromp.parallel.construct.atomic;

import jromp.parallel.construct.atomic.operation.Operation;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variables;

import java.io.Serializable;

public class Atomic {
    private Atomic() {
    }

    public static synchronized <T extends Serializable>
    T read(String name, Variables variables) {
        SharedVariable<T> sharedVariable = getSharedVariable(name, variables);
        return sharedVariable.toAtomic().value();
    }

    public static synchronized <T extends Serializable>
    void write(String name, T value, Variables variables) {
        getSharedVariable(name, variables).toAtomic().set(value);
    }

    public static synchronized <T extends Serializable>
    void update(String name, Operation<T> operation, Variables variables) {
        SharedVariable<T> sharedVariable = getSharedVariable(name, variables);
        sharedVariable.toAtomic().update(operation.get());
    }

    private static <T extends Serializable>
    SharedVariable<T> getSharedVariable(String name, Variables variables) {
        return (SharedVariable<T>) variables.<T>get(name);
    }
}
