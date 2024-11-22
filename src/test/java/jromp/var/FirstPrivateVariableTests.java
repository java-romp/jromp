package jromp.var;

import jromp.JROMP;
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
    void testParallelForFirstPrivateVarSet() {
        int threads = 4;
        int iterations = 100;
        FirstPrivateVariable<Integer> sum = new FirstPrivateVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.set(sum.value() + 1);
                 }
             })
             .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(sum.value()).isZero();
    }

    @Test
    void testParallelForFirstPrivateVarUpdate() {
        int threads = 4;
        int iterations = 100;
        FirstPrivateVariable<Integer> sum = new FirstPrivateVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.update(old -> old + 1);
                 }
             })
             .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(sum.value()).isZero();
    }

    @Test
    void testToString() {
        FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
        assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=0}");

        firstPrivateVariable.set(1);
        assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=1}");
    }

    @Test
    void testDontKeepValueAfterExecution() {
        FirstPrivateVariable<Integer> sum = new FirstPrivateVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 for (int i = 0; i < 20; i++) {
                     sum.update(old -> old + 1);
                 }
             })
             .join();

        // The value is zero because each thread has its own copy of the variable
        assertThat(sum.value()).isZero();
    }

    @Test
    void testKeepOldValueAfterEnd() {
        FirstPrivateVariable<Integer> sum = new FirstPrivateVariable<>(20);
        sum.set(15);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 assertThat(sum.value()).isEqualTo(15);

                 for (int i = 0; i < 20; i++) {
                     sum.update(old -> old + 1);
                 }

                 assertThat(sum.value()).isEqualTo(35);
             })
             .join();

        assertThat(sum.value()).isEqualTo(15);
    }
}
