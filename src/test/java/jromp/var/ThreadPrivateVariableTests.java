package jromp.var;

import jromp.JROMP;
import jromp.operation.Operations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadPrivateVariableTests {
    @Test
    void testValueZeroConstructedNormal() {
        ThreadPrivateVariable<Integer> threadPrivateVariable = new ThreadPrivateVariable<>(0);
        assertThat(threadPrivateVariable.value()).isZero();
    }

    @Test
    void testValueConstructedWithNonZero() {
        ThreadPrivateVariable<Integer> threadPrivateVariable = new ThreadPrivateVariable<>(10);
        assertThat(threadPrivateVariable.value()).isEqualTo(10);
    }

    @Test
    void testSet() {
        ThreadPrivateVariable<Integer> threadPrivateVariable = new ThreadPrivateVariable<>(0);
        threadPrivateVariable.set(1);
        assertThat(threadPrivateVariable.value()).isOne();
    }

    @Test
    void testUpdate() {
        ThreadPrivateVariable<Integer> threadPrivateVariable = new ThreadPrivateVariable<>(0);
        threadPrivateVariable.update(Operations.add(1));
        assertThat(threadPrivateVariable.value()).isOne();
    }

    @Test
    void testParallelForThreadPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        ThreadPrivateVariable<Integer> variable = new ThreadPrivateVariable<>(0);

        JROMP.withThreads(threads)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     variable.set(variable.value() + 1);
                     assertThat(variable.value()).isBetween(0, iterations / threads);
                 }
             })
             .join();

        assertThat(variable.value()).isZero();
    }

    @Test
    void testParallelForThreadPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        ThreadPrivateVariable<Integer> variable = new ThreadPrivateVariable<>(0);

        JROMP.withThreads(threads)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     variable.update(Operations.add(1));
                     assertThat(variable.value()).isBetween(0, iterations / threads);
                 }
             })
             .join();

        assertThat(variable.value()).isZero();
    }

    @Test
    void testToString() {
        ThreadPrivateVariable<Integer> threadPrivateVariable = new ThreadPrivateVariable<>(0);
        assertThat(threadPrivateVariable.toString()).hasToString("ThreadPrivateVariable{value=0}");
    }
}
