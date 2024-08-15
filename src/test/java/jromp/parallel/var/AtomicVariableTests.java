package jromp.parallel.var;

import jromp.parallel.Parallel;
import jromp.parallel.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtomicVariableTests {
    @Test
    void testParallelForAtomicVarUpdateInside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        Variables vars = Variables.create().add("sum", new AtomicVariable<>(0));

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        Variable<Integer> insideSum = variables.get("sum");
                        insideSum.update(old -> old + 1);
                        countsPerThread[id]++;
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

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        outsideSum.update(old -> old + 1);
                        countsPerThread[id]++;
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

        Parallel.withThreads(threads)
                .withVariables(vars)
                .parallelFor(0, iterations, false, (id, start, end, variables) -> {
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

        Parallel.withThreads(4)
                .withVariables(vars)
                .block((id, variables) -> {
                    for (int i = 0; i < 2; i++) {
                        Variable<Integer> sum = variables.get("sum");

                        // The master thread will sleep for a while to end up with a different value
                        if (Utils.isMaster(id)) {
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
