package jromp.parallel.var;

import jromp.parallel.Parallel;
import jromp.parallel.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SharedVariableTests {
    @Test
    void testParallelForSharedVarUpdateInside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        Variables vars = Variables.create().add("sum", new SharedVariable<>(0));

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
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
    void testParallelForSharedVarUpdateOutside() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        SharedVariable<Integer> outsideSum = new SharedVariable<>(0);
        Variables vars = Variables.create().add("sum", outsideSum);

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
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
    void testParallelForSharedVarSet() {
        int threads = 2;
        int iterations = 10;
        Variables vars = Variables.create().add("sum", new SharedVariable<>(0));

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
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
        SharedVariable<Integer> sharedVariable = new SharedVariable<>(0);
        assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=0}");

        sharedVariable.set(1);
        assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=1}");
    }

    @Test
    void testKeepLastValueAfterExecution() {
        Variables vars = Variables.create().add("sum", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .block(vars, (id, variables) -> {
                    for (int i = 0; i < 2; i++) {
                        Variable<Integer> sum = variables.get("sum");

                        // The master thread will sleep for a while to end up with a different value
                        if (Utils.isMaster(id)) {
                            try {
                                Thread.sleep(5);
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
