package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LastPrivateVariableTest {
    @Test
    void testValueZero() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
        assertThat(lastPrivateVariable.value()).isZero();
    }

    @Test
    void testValueConstructedWithNonZero() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(10);
        assertThat(lastPrivateVariable.value()).isEqualTo(10);
    }

    @Test
    void testSet() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
        lastPrivateVariable.set(1);
        assertThat(lastPrivateVariable.value()).isOne();
    }

    @Test
    void testUpdate() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
        lastPrivateVariable.update(x -> x + 1);
        assertThat(lastPrivateVariable.value()).isOne();
    }

    @Test
    void testCopy() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
        Variable<Integer> copy = lastPrivateVariable.copy();
        assertThat(copy.value()).isZero();
        assertThat(copy).isInstanceOf(LastPrivateVariable.class)
                        .isNotEqualTo(lastPrivateVariable);
    }

    @Test
    void testParallelForLastPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new LastPrivateVariable<>(0));

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        Variable<Integer> sum = variables.get("sum");
                        sum.set(sum.value() + 1);
                    }
                })
                .join();

        assertThat(vars.get("sum").value()).isEqualTo(25);
    }

    @Test
    void testParallelForLastPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new LastPrivateVariable<>(0));

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        variables.<Integer>get("sum").update(old -> old + 1);
                    }
                })
                .join();

        assertThat(vars.get("sum").value()).isEqualTo(25);
    }

    @Test
    void testToString() {
        LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
        assertThat(lastPrivateVariable.toString()).hasToString("LastPrivateVariable{value=0, lastValue=0}");

        lastPrivateVariable.set(1);
        assertThat(lastPrivateVariable.toString()).hasToString("LastPrivateVariable{value=1, lastValue=1}");
    }

    @Test
    void testKeepLastValueAfterExecution() {
        LastPrivateVariable<Integer> variable = new LastPrivateVariable<>(0);
        Variables vars = Variables.create().add("sum", variable);
        variable.set(2);

        Parallel.withThreads(4)
                .withVariables(vars)
                .block((id, variables) -> {
                    Variable<Integer> sum = variables.get("sum");
                    assertThat(sum.value()).isZero();

                    for (int i = 0; i < 2; i++) {
                        sum.update(old -> old + 1);
                    }

                    assertThat(sum.value()).isEqualTo(2);
                })
                .join();

        assertThat(vars.get("sum").value()).isEqualTo(2);
    }

    @Test
    void testChainedEnd() {
        Variable<Integer> var1 = new LastPrivateVariable<>(0);
        Variable<Integer> var2 = var1.copy();
        Variable<Integer> var3 = var2.copy();

        var1.set(1);
        var2.set(2);
        var3.set(3);

        var1.end();
        var2.end();
        var3.end();

        assertThat(var1.value()).isEqualTo(2);
        assertThat(var2.value()).isEqualTo(3);
        assertThat(var3.value()).isEqualTo(3);
    }
}
