package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateVariableTests {
    @Test
    void testValueZeroConstructedNormal() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
        assertThat(privateVariable.value()).isZero();
    }

    @Test
    void testValueConstructedWithNonZero() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(10);
        assertThat(privateVariable.value()).isZero();
    }

    @Test
    void testSet() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
        privateVariable.set(1);
        assertThat(privateVariable.value()).isOne();
    }

    @Test
    void testUpdate() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
        privateVariable.update(x -> x + 1);
        assertThat(privateVariable.value()).isOne();
    }

    @Test
    void testCopy() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(2);
        Variable<Integer> copy = privateVariable.copy();
        assertThat(copy.value()).isZero();
        assertThat(copy).isInstanceOf(PrivateVariable.class)
                        .isNotEqualTo(privateVariable);
    }

    @Test
    void testParallelForPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        Variable<Integer> sum = variables.get("sum");
                        sum.set(sum.value() + 1);
                    }
                })
                .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(vars.get("sum").value()).isEqualTo(0);
    }

    @Test
    void testParallelForPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        variables.<Integer>get("sum").update(old -> old + 1);
                    }
                })
                .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(vars.get("sum").value()).isEqualTo(0);
    }

    @Test
    void testToString() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
        assertThat(privateVariable.toString()).hasToString("PrivateVariable{value=0}");

        privateVariable.set(1);
        assertThat(privateVariable.toString()).hasToString("PrivateVariable{value=1}");
    }

    @Test
    void testDontKeepValueAfterExecution() {
        Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

        Parallel.withThreads(4)
                .block(vars, (id, variables) -> {
                    for (int i = 0; i < 20; i++) {
                        Variable<Integer> sum = variables.get("sum");
                        sum.update(old -> old + 1);
                    }
                })
                .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(vars.get("sum").value()).isEqualTo(0);
    }
}
