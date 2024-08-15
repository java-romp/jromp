package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirstPrivateVariableTests {
    @Test
    void testValueZero() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        assertThat(firstPrivateVariable.value()).isZero();
    }

    @Test
    void testValueOne() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(1);
        assertThat(firstPrivateVariable.value()).isOne();
    }

    @Test
    void testSet() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        firstPrivateVariable.set(1);
        assertThat(firstPrivateVariable.value()).isOne();
    }

    @Test
    void testUpdate() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        firstPrivateVariable.update(x -> x + 1);
        assertThat(firstPrivateVariable.value()).isOne();
    }

    @Test
    void testCopy() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        Variable<Integer> copy = firstPrivateVariable.copy();
        assertThat(copy.value()).isZero();
    }

    @Test
    void testParallelForFirstPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
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
    void testParallelForFirstPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
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
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=0, initialValue=0}");

        firstPrivateVariable.set(1);
        assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=1, initialValue=0}");
    }

    @Test
    void testDontKeepValueAfterExecution() {
        Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

        Parallel.withThreads(4)
                .withVariables(vars)
                .block((id, variables) -> {
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
