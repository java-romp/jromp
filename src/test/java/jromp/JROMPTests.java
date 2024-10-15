package jromp;

import jromp.var.AtomicVariable;
import jromp.var.PrivateVariable;
import jromp.var.Variables;
import org.junit.jupiter.api.Test;

import static jromp.JROMP.getNumThreads;
import static jromp.JROMP.getThreadNum;
import static jromp.JROMP.getThreadTeam;
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
             .parallelFor(0, iterations, false, (start, end, vars) -> {
                 assertThat(vars).isNotNull();

                 for (int i = start; i < end; i++) {
                     countsPerThread[getThreadNum()]++;
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
                     vars -> result[getThreadNum()] = 1,
                     vars -> result[getThreadNum()] = 2,
                     vars -> result[getThreadNum()] = 3,
                     vars -> result[getThreadNum()] = 4
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
                     vars -> result[getThreadNum()] = 1,
                     vars -> result[getThreadNum()] = 2,
                     vars -> result[getThreadNum()] = 3
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
                     vars -> result[getThreadNum()] = 1,
                     vars -> result[getThreadNum()] = 2,
                     vars -> result[getThreadNum()] = 3,
                     vars -> result[getThreadNum()] = 4,
                     vars -> result[getThreadNum()] = 5
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
                     vars -> vars.<Integer>get("num").update(n -> n + 1),
                     vars -> vars.<Integer>get("num").update(n -> n + 1),
                     vars -> vars.<Integer>get("num").update(n -> n + 1),
                     vars -> vars.<Integer>get("num").update(n -> n + 1),
                     vars -> vars.<Integer>get("num").update(n -> n + 1)
             )
             .join();

        assertThat(variables.<Integer>get("num").value()).isEqualTo(5);
    }

    @Test
    void testParallelWithDefaultVariables() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];

        JROMP.withThreads(threads)
             .parallel(vars -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isTrue();
                 assertThat(vars.size()).isZero();

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[getThreadNum()]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnly(iterations);
    }

    @Test
    void testParallelWithVariables() {
        int threads = 4;
        int iterations = 1000;
        int[] countsPerThread = new int[threads];
        Variables variables = Variables.create().add("iterations", new PrivateVariable<>(iterations));

        JROMP.withThreads(threads)
             .withVariables(variables)
             .parallel(vars -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();
                 assertThat(vars.size()).isEqualTo(1);

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[getThreadNum()]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnly(iterations);
    }

    @Test
    void testParallelConstructionWithDefaultConfiguration() {
        String[] result = new String[Constants.MAX_THREADS];

        JROMP.allThreads()
             .parallel(vars -> result[getThreadNum()] = "Hello, world!")
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
             .singleBlock(false, vars -> {
                 assertThat(vars).isNotNull();
                 assertThat(vars.isEmpty()).isFalse();
                 assertThat(vars.size()).isEqualTo(1);

                 int tid = getThreadNum();
                 singleBlockExecuted[tid] = true;
                 singleBlockExecutionId[0] = tid;

                 for (int i = 0; i < iterations; i++) {
                     countsPerThread[tid]++;
                 }
             })
             .join();

        assertThat(countsPerThread).containsOnlyOnce(iterations);
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
             .parallel(vars -> assertThat(value[0]).isZero())
             .singleBlock(false, vars -> value[0] = 1)
             .parallel(vars -> assertThat(value[0]).isOne())
             .join();
    }

    @Test
    void testMaskedMaster() {
        JROMP.withThreads(4)
             .masked(vars -> assertThat(getThreadNum()).isZero())
             .join();
    }

    @Test
    void testMaskedOtherThread() {
        JROMP.withThreads(4)
             .masked(1, vars -> assertThat(getThreadNum()).isEqualTo(1))
             .masked(2, vars -> assertThat(getThreadNum()).isEqualTo(2))
             .join();
    }

    @Test
    void testMaskedTwoTeams() {
        int threads = 4;
        int threadsPerTeam = 2;
        int[] values = new int[threads];

        JROMP.withThreads(threads, threadsPerTeam)
             .masked(1, vars -> values[getThreadNum()]++)
             .join();

        assertThat(values).containsExactly(0, 2, 0, 0);
    }

    @Test
    void testThreadNameString() {
        JROMP.withThreads(4)
             .masked(vars -> assertThat(Thread.currentThread().getName()).isEqualTo("JrompThread-0-0"))
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
        assertThat(getThreadNum()).isZero();
    }

    @Test
    void testGetThreadTeam() {
        assertThat(getThreadTeam()).isNull();

        JROMP.withThreads(4, 2)
             .parallel(vars -> assertThat(getThreadTeam()).isNotNull())
             .join();

        assertThat(getThreadTeam()).isNull();
    }

    @Test
    void testGetNumThreads() {
        assertThat(getNumThreads()).isEqualTo(1);

        JROMP.withThreads(4, 2)
             .parallel(vars -> assertThat(getNumThreads()).isEqualTo(4))
             .join();

        assertThat(getNumThreads()).isEqualTo(1);
    }
}
