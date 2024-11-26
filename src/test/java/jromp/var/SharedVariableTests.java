package jromp.var;

import jromp.JROMP;
import jromp.operation.Operations;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import static jromp.JROMP.getThreadNum;
import static org.assertj.core.api.Assertions.assertThat;

class SharedVariableTests {
    @Test
    void testParallelForSharedVarUpdateInside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        SharedVariable<Integer> sum = new SharedVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.update(Operations.add(1));
                     countsPerThread[getThreadNum()]++;
                 }
             })
             .join();

        // The value of the shared variable should be less than or equal to the number of iterations
        // because the update operation is not atomic and race conditions can occur.
        assertThat(sum.value()).satisfies(new Condition<>(value -> value <= iterations, "value <= iterations"));
        assertThat(countsPerThread).containsOnly(iterations / threads);
    }

    @Test
    void testParallelForSharedVarSet() {
        int threads = 2;
        int iterations = 10;
        SharedVariable<Integer> sum = new SharedVariable<>(0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.set(1);
                 }
             })
             .join();

        assertThat(sum.value()).isEqualTo(1);
    }

    @Test
    void testToString() {
        SharedVariable<Integer> sharedVariable = new SharedVariable<>(0);
        assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=0}");

        sharedVariable.set(1);
        assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=1}");
    }

    @Test
    void testKeepLastValueAfterExecution() {
        SharedVariable<Integer> sum = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(sum)
             .parallel(() -> {
                 for (int i = 0; i < 2; i++) {
                     // The master thread will sleep for a while to end up with a different value
                     if (JROMP.isMaster(getThreadNum())) {
                         try {
                             Thread.sleep(100);
                         } catch (InterruptedException e) {
                             throw new RuntimeException(e);
                         }

                         sum.set(200);
                     } else {
                         sum.update(Operations.add(1));
                     }
                 }
             })
             .join();

        assertThat(sum.value()).isEqualTo(200);
    }
}
