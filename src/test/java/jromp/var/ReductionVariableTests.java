package jromp.var;

import jromp.JROMP;
import jromp.operation.Operations;
import jromp.var.reduction.ReductionOperations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ReductionVariableTests {
    @Test
    void testReductionVariableAndKeepValueAfterExecution() {
        int threads = 4;
        int iterations = 1000;
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);

        JROMP.withThreads(threads)
             .registerVariables(sum)
             .parallelFor(0, iterations, (start, end) -> {
                 for (int i = start; i < end; i++) {
                     sum.update(old -> old + 1);
                 }
             })
             .join();

        assertThat(sum.value()).isEqualTo(iterations);
    }

    @Test
    void testReductionPi() {
        int threads = 4;
        int n = 1000000;
        ReductionVariable<Double> pi = new ReductionVariable<>(ReductionOperations.sum(), 0D);
        double h = 1.0 / (double) n;

        JROMP.withThreads(threads)
             .registerVariables(pi)
             .parallelFor(0, n, (start, end) -> {
                 double x, sum = 0.0;

                 for (int i = start; i < end; i++) {
                     x = h * ((double) i - 0.5);
                     sum += 4.0 / (1.0 + x * x);
                 }

                 final double finalSum = sum;
                 pi.update(old -> old + finalSum);
             })
             .join();

        double finalResult = h * pi.value();
        assertThat(finalResult).isCloseTo(Math.PI, offset(1e-5));
    }

    @Test
    void testValueBeforeMerge() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        assertThat(sum.value()).isZero();
    }

    @Test
    void testSet() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        sum.set(1);
        assertThat(sum.value()).isOne();
    }

    @Test
    void testKeepOldValueAndResetOnParallelStart() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 20);
        assertThat(sum.value()).isEqualTo(20);

        JROMP.withThreads(2)
             .registerVariables(sum)
             .parallel(() -> {
                 assertThat(sum.value()).isZero(); // The value should be reset to the initial value.
                 sum.update(Operations.add(1));
             })
             .join();

        assertThat(sum.value()).isEqualTo(2);
    }

    @Test
    void testUpdate() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        sum.update(old -> old + 1);
        assertThat(sum.value()).isOne();
    }

    @Test
    void testIsMerged() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        assertThat(sum.isMerged()).isFalse();
        sum.merge();
        assertThat(sum.isMerged()).isTrue();
    }

    @Test
    void testAlreadyMerged() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        sum.merge();
        sum.merge();
        assertThat(sum.value()).isZero();
    }
}
