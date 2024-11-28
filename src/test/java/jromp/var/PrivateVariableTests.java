package jromp.var;

import jromp.JROMP;
import jromp.operation.Operations;
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
        privateVariable.update(Operations.add(1));
        assertThat(privateVariable.value()).isOne();
    }

    @Test
    void testParallelForPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        PrivateVariable<Integer> sum = new PrivateVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.set(sum.value() + 1);
                 }
             })
             .join();

        assertThat(sum.value()).isZero();
    }

    @Test
    void testParallelForPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        PrivateVariable<Integer> sum = new PrivateVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.update(Operations.add(1));
                 }
             })
             .join();

        assertThat(sum.value()).isZero();
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
        PrivateVariable<Integer> sum = new PrivateVariable<>(10);
        sum.set(12);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 assertThat(sum.value()).isZero();

                 for (int i = 0; i < 20; i++) {
                     sum.update(Operations.add(1));
                 }

                 assertThat(sum.value()).isEqualTo(20);
             })
             .join();

        assertThat(sum.value()).isEqualTo(12);
    }

    @Test
    void testDoubleParallel() {
        PrivateVariable<Integer> sum = new PrivateVariable<>(12);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 assertThat(sum.value()).isZero();

                 for (int i = 0; i < 20; i++) {
                     sum.update(Operations.add(1));
                 }

                 assertThat(sum.value()).isEqualTo(20);
             })
             .parallel(() -> {
                 assertThat(sum.value()).isEqualTo(20);

                 for (int i = 0; i < 60; i++) {
                     sum.update(Operations.add(1));
                 }

                 assertThat(sum.value()).isEqualTo(80);
             })
             .join();

        assertThat(sum.value()).isEqualTo(12);
    }
}
