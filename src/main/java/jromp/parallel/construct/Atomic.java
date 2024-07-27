package jromp.parallel.construct;

import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variables;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class Atomic {
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
    void update(String name, UnaryOperator<T> valueOp, Variables variables) {
        SharedVariable<T> sharedVariable = getSharedVariable(name, variables);
        sharedVariable.toAtomic().update(valueOp);
    }

    private static <T extends Serializable>
    SharedVariable<T> getSharedVariable(String name, Variables variables) {
        return (SharedVariable<T>) variables.<T>get(name);
    }
}
