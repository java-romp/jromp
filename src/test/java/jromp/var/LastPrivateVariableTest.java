package jromp.var;

import jromp.JROMP;
import jromp.operation.Operations;
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
    void testParallelForLastPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        LastPrivateVariable<Integer> sum = new LastPrivateVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.set(sum.value() + 1);
                 }
             })
             .join();

        assertThat(sum.value()).isEqualTo(25);
    }

    @Test
    void testParallelForLastPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        LastPrivateVariable<Integer> sum = new LastPrivateVariable<>(0);
        sum.set(2);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 assertThat(sum.value()).isZero();

                 for (int i = start; i < end; i++) {
                     sum.update(Operations.add(1));
                 }

                 assertThat(sum.value()).isEqualTo(25);
             })
             .join();

        assertThat(sum.value()).isEqualTo(25);
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
        LastPrivateVariable<Integer> sum = new LastPrivateVariable<>(0);
        sum.set(2);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 assertThat(sum.value()).isZero();

                 for (int i = 0; i < 2; i++) {
                     sum.update(Operations.add(1));
                 }

                 assertThat(sum.value()).isEqualTo(2);
             })
             .join();

        assertThat(sum.value()).isEqualTo(2);
    }

    @Test
    void testDoubleParallel() {
        LastPrivateVariable<Integer> sum = new LastPrivateVariable<>(12);

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

        assertThat(sum.value()).isEqualTo(80);
    }
}
