package jromp;

import jromp.var.AtomicVariable;
import jromp.var.PrivateVariable;
import jromp.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JROMPTests {
    @Test
    void testWithThreadsValid() {
        JROMP p = JROMP.withThreads(4);
        assertThat(p).isNotNull();
    }

    @Test
    void testWithThreadsLessThanMinimum() {
        assertThatThrownBy(() -> JROMP.withThreads(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads must be greater than 0.");
    }

    @Test
    void testWithThreadsTooMany() {
        int availableProcessors = Constants.MAX_THREADS;
        assertThat(JROMP.withThreads(availableProcessors + 10)).isNotNull();
    }

    @Test
    void testWithThreadsPerTeamValid() {
        JROMP p = JROMP.withThreads(4, 2);
        assertThat(p).isNotNull();
    }

    @Test
    void testWithThreadsLessThanMinimumPerTeam() {
        assertThatThrownBy(() -> JROMP.withThreads(4, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads per team must be greater than 0.");
    }

    @Test
    void testWithThreadsTeamGreaterThanThreads() {
        assertThatThrownBy(() -> JROMP.withThreads(4, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads per team must be less than or equal to the number of threads.");
    }

    @Test
    void testWithThreadsNotDivisible() {
        assertThatThrownBy(() -> JROMP.withThreads(4, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads must be divisible by the number of threads per team.");
    }

    @Test
    void testParallelForWithoutVariables() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];

        JROMP.withThreads(threads)
             .parallelFor(0, iterations, false, (id, start, end, vars) -> {
                 assertThat(vars).isNotNull();

                 for (int i = start; i < end; i++) {
                     countsPerThread[id]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnly(iterations / threads);
    }

    @Test
    void testSections() {
        int[] result = new int[4];

        JROMP.withThreads(4)
             .sections(
                     false,
                     (id, vars) -> result[id] = 1,
                     (id, vars) -> result[id] = 2,
                     (id, vars) -> result[id] = 3,
                     (id, vars) -> result[id] = 4
             )
             .join();

        assertThat(result).containsOnly(1, 2, 3, 4);
    }

    @Test
    void testSectionsMoreThreadsThanSections() {
        int threads = 4;
        int[] result = new int[threads];

        JROMP.withThreads(threads)
             .sections(
                     false,
                     (id, vars) -> result[id] = 1,
                     (id, vars) -> result[id] = 2,
                     (id, vars) -> result[id] = 3
             )
             .join();

        assertThat(result).containsOnly(1, 2, 3, 0);
    }

    @Test
    void testSectionsMoreSectionsThanThreads() {
        int threads = 2;
        int[] result = new int[threads];

        JROMP.withThreads(threads)
             .sections(
                     false,
                     (id, vars) -> result[id] = 1,
                     (id, vars) -> result[id] = 2,
                     (id, vars) -> result[id] = 3,
                     (id, vars) -> result[id] = 4,
                     (id, vars) -> result[id] = 5
             )
             .join();

        assertThat(result).containsAnyOf(4, 5);
    }

    @Test
    void testSectionsMoreSectionsThanThreadsKeepLastValueSystemArrayCopy() {
        int threads = 3;
        Variables variables = Variables.create().add("num", new AtomicVariable<>(0));

        JROMP.withThreads(threads)
             .withVariables(variables)
             .sections(
                     false,
                     (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
                     (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
                     (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
                     (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
                     (id, vars) -> vars.<Integer>get("num").update(n -> n + 1)
             )
             .join();

        assertThat(variables.<Integer>get("num").value()).isEqualTo(5);
    }

    @Test
    void testParallelBlockWithDefaultVariables() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];

        JROMP.withThreads(threads)
             .block((id, vars) -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();
                 assertThat(vars.size()).isEqualTo(1);
                 assertThat(vars.get(Constants.NUM_THREADS).value()).isEqualTo(threads);

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[id]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnly(iterations);
    }

    @Test
    void testParallelBlockWithVariables() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        Variables variables = Variables.create().add("iterations", new PrivateVariable<>(iterations));

        JROMP.withThreads(threads)
             .withVariables(variables)
             .block((id, vars) -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[id]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnly(iterations);
    }

    @Test
    void testParallelConstructionWithDefaultConfiguration() {
        String[] result = new String[Constants.MAX_THREADS];

        JROMP.allThreads()
             .block((id, variables) -> result[id] = "Hello, world!")
             .join();

        assertThat(result).containsOnly("Hello, world!");
    }

    @Test
    void testSingle() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        boolean[] singleBlockExecuted = new boolean[threads];
        int[] singleBlockExecutionId = new int[1];
        Variables variables = Variables.create().add("iterations", new PrivateVariable<>(iterations));

        JROMP.withThreads(threads)
             .withVariables(variables)
             .block((id, vars) -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[id]++;
                 }
             })
             .singleBlock(false, (id, vars) -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();
                 assertThat(vars.size()).isEqualTo(2);
                 assertThat(vars.get(Constants.NUM_THREADS).value()).isEqualTo(threads);
                 singleBlockExecuted[id] = true;
                 singleBlockExecutionId[0] = id;

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[id]++;
                 }
             })
             .block((id, vars) -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[id]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnlyOnce(iterations * 3);
        assertThat(singleBlockExecuted).containsOnlyOnce(true);

        // In the position of the single block execution id, the value should be true
        assertThat(singleBlockExecuted[singleBlockExecutionId[0]]).isTrue();
        // In the other positions, the value should be false
        for (int i = 0; i < threads; i++) {
            if (i != singleBlockExecutionId[0]) {
                assertThat(singleBlockExecuted[i]).isFalse();
            }
        }
    }

    @Test
    void testThreadsStopOnImplicitBarrier() {
        int threads = 4;
        int[] value = new int[1];

        JROMP.withThreads(threads)
             .block((id, vars) -> assertThat(value[0]).isZero())
             .singleBlock(false, (id, vars) -> value[0] = 1)
             .block((id, vars) -> assertThat(value[0]).isOne())
             .join();
    }

    @Test
    void testMaskedMaster() {
        int threads = 4;
        int[] values = new int[threads];

        JROMP.withThreads(threads)
             .masked((id, vars) -> values[id] = 1)
             .join();

        assertThat(values).containsExactly(1, 0, 0, 0);
    }

    @Test
    void testMaskedOtherThread() {
        int threads = 4;
        int[] values = new int[threads];

        JROMP.withThreads(threads)
             .masked(2, (id, vars) -> values[id] = 1)
             .join();

        assertThat(values).containsExactly(0, 0, 1, 0);
    }

    @Test
    void testThreadNameString() {
        JROMP.withThreads(4)
             .masked((id, vars) -> assertThat(Thread.currentThread().getName()).isEqualTo("JrompThread-0-0"))
             .join();
    }

    @Test
    void testIsMaster() {
        assertThat(JROMP.isMaster(0)).isTrue();
        assertThat(JROMP.isMaster(1)).isFalse();
    }

    @Test
    void testGetWTime() {
        assertThat(JROMP.getWTime()).isGreaterThan(0.0);
    }

    @Test
    void testGetThreadNum() {
        assertThat(JROMP.getThreadNum()).isZero();
    }
}
