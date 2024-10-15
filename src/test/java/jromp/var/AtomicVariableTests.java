package jromp.var;

import jromp.JROMP;
import org.junit.jupiter.api.Test;

import static jromp.JROMP.getThreadNum;
import static org.assertj.core.api.Assertions.assertThat;

class AtomicVariableTests {
    @Test
    void testParallelForAtomicVarUpdateInside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        Variables vars = Variables.create().add("sum", new AtomicVariable<>(0));

        JROMP.withThreads(threads)
             .withVariables(vars)
             .parallelFor(0, iterations, false, (start, end, variables) -> {
                 for (int i = start; i < end; i++) {
                     Variable<Integer> insideSum = variables.get("sum");
                     insideSum.update(old -> old + 1);
                     countsPerThread[getThreadNum()]++;
                 }
             })
             .join();

        assertThat(vars.get("sum").value()).isEqualTo(iterations);
        assertThat(countsPerThread).containsOnly(iterations / threads);
    }

    @Test
    void testParallelForAtomicVarUpdateOutside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        AtomicVariable<Integer> outsideSum = new AtomicVariable<>(0);
        Variables vars = Variables.create().add("sum", outsideSum);

        JROMP.withThreads(threads)
             .withVariables(vars)
             .parallelFor(0, iterations, false, (start, end, variables) -> {
                 for (int i = start; i < end; i++) {
                     outsideSum.update(old -> old + 1);
                     countsPerThread[getThreadNum()]++;
                 }
             })
             .join();

        assertThat(outsideSum.value()).isEqualTo(iterations);
        assertThat(countsPerThread).containsOnly(iterations / threads);
    }

    @Test
    void testParallelForAtomicVarSet() {
        int threads = 2;
        int iterations = 10;
        Variables vars = Variables.create().add("sum", new AtomicVariable<>(0));

        JROMP.withThreads(threads)
             .withVariables(vars)
             .parallelFor(0, iterations, false, (start, end, variables) -> {
                 for (int i = start; i < end; i++) {
                     Variable<Integer> sum = variables.get("sum");
                     sum.set(1);
                 }
             })
             .join();

        assertThat(vars.get("sum").value()).isEqualTo(1);
    }

    @Test
    void testToString() {
        AtomicVariable<Integer> sharedVariable = new AtomicVariable<>(0);
        assertThat(sharedVariable.toString()).hasToString("AtomicVariable{value=0}");

        sharedVariable.set(1);
        assertThat(sharedVariable.toString()).hasToString("AtomicVariable{value=1}");
    }

    @Test
    void testKeepLastValueAfterExecution() {
        Variables vars = Variables.create().add("sum", new AtomicVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(vars)
             .parallel(variables -> {
                 for (int i = 0; i < 2; i++) {
                     Variable<Integer> sum = variables.get("sum");

                     // The master thread will sleep for a while to end up with a different value
                     if (JROMP.isMaster(getThreadNum())) {
                         try {
                             Thread.sleep(100);
                         } catch (InterruptedException e) {
                             throw new RuntimeException(e);
                         }

                         sum.set(200);
                     } else {
                         sum.update(old -> old + 1);
                     }
                 }
             })
             .join();

        assertThat(vars.get("sum").value()).isEqualTo(200);
    }
}
