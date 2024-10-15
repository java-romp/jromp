package jromp.var;

import jromp.JROMP;
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
        assertThat(privateVariable.value()).isEqualTo(10);
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

        JROMP.withThreads(threads)
             .withVariables(vars)
             .parallelFor(0, iterations, false, (start, end, variables) -> {
                 for (int i = start; i < end; i++) {
                     Variable<Integer> sum = variables.get("sum");
                     sum.set(sum.value() + 1);
                 }
             })
             .join();

        assertThat(vars.<Integer>get("sum").value()).isZero();
    }

    @Test
    void testParallelForPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

        JROMP.withThreads(threads)
             .withVariables(vars)
             .parallelFor(0, iterations, false, (start, end, variables) -> {
                 for (int i = start; i < end; i++) {
                     variables.<Integer>get("sum").update(old -> old + 1);
                 }
             })
             .join();

        assertThat(vars.<Integer>get("sum").value()).isZero();
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
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(10);
        Variables vars = Variables.create().add("sum", privateVariable);
        privateVariable.set(12);

        JROMP.withThreads(4)
             .withVariables(vars)
             .parallel(variables -> {
                 assertThat(variables.<Integer>get("sum").value()).isZero();

                 for (int i = 0; i < 20; i++) {
                     Variable<Integer> sum = variables.get("sum");
                     sum.update(old -> old + 1);
                 }

                 assertThat(variables.<Integer>get("sum").value()).isEqualTo(20);
             })
             .join();

        assertThat(vars.<Integer>get("sum").value()).isEqualTo(12);
    }
}
