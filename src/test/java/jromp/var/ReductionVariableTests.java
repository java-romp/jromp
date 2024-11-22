package jromp.var;

import jromp.JROMP;
import jromp.var.reduction.ReductionOperations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThatThrownBy(sum::value).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testSet() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        assertThatThrownBy(() -> sum.set(1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testUpdate() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);
        assertThatThrownBy(() -> sum.update($ -> 1)).isInstanceOf(UnsupportedOperationException.class);
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

    @Test
    void testToString() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(ReductionOperations.sum(), 0);

        assertThat(sum.toString()).hasToString(
                "ReductionVariable{\n  operation=Sum,\n  initialValue=0,\n  privateVariables=[\n    \n  ],\n  result=PrivateVariable{value=0},\n  merged=false}");

        // Todo: the assert below should be checked inside a parallel block, to have the private variables initialized
        sum.merge();

        assertThat(sum.toString()).hasToString(
                "ReductionVariable{\n  operation=Sum,\n  initialValue=0,\n  privateVariables=[\n    PrivateVariable{value=0}\n    PrivateVariable{value=0}\n    PrivateVariable{value=0}\n  ],\n  result=PrivateVariable{value=0},\n  merged=true}");
    }
}
