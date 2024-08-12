package jromp.parallel.var;

import jromp.parallel.Parallel;
import jromp.parallel.var.reduction.Sum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;

class ReductionVariableTests {
    @Test
    void testReductionVariableAndKeepValueAfterExecution() {
        int threads = 4;
        int iterations = 1000;
        Variables vars = Variables.create().add("sum", new ReductionVariable<>(new Sum<>(), 0));

        Parallel.withThreads(threads)
                .parallelFor(0, iterations, vars, false, (id, start, end, variables) -> {
                    for (int i = start; i < end; i++) {
                        Variable<Integer> insideSum = variables.get("sum");
                        insideSum.update(old -> old + 1);
                    }
                })
                .join();

        assertThat(vars.get("sum").value()).isEqualTo(iterations);
    }

    @Test
    void testReductionPi() {
        int threads = 4;
        int n = 1000000;
        ReductionVariable<Double> result = new ReductionVariable<>(new Sum<>(), 0D);
        Variables vars = Variables.create().add("pi", result);
        double h = 1.0 / (double) n;

        Parallel.withThreads(threads)
                .parallelFor(0, n, vars, false, (id, start, end, variables) -> {
                    double x, sum = 0.0;

                    for (int i = start; i < end; i++) {
                        x = h * ((double) i - 0.5);
                        sum += 4.0 / (1.0 + x * x);
                    }

                    final double finalSum = sum;
                    variables.<Double>get("pi").update(old -> old + finalSum);
                })
                .join();

        double finalResult = h * result.value();
        assertThat(finalResult).isCloseTo(Math.PI, offset(1e-5));
    }

    @Test
    void testValueBeforeMerge() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        assertThatThrownBy(sum::value).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testSet() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        assertThatThrownBy(() -> sum.set(1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testUpdate() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        assertThatThrownBy(() -> sum.update($ -> 1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testCopy() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        Variable<Integer> copy = sum.copy();
        assertThat(copy.value()).isZero();
        assertThat(copy).isInstanceOf(PrivateVariable.class);
    }

    @Test
    void testMerge() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        sum.copy().update($ -> 1);
        sum.copy().update($ -> 2);
        sum.copy().update($ -> 3);
        sum.merge();
        assertThat(sum.value()).isEqualTo(6);
    }

    @Test
    void testIsMerged() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        assertThat(sum.isMerged()).isFalse();
        sum.merge();
        assertThat(sum.isMerged()).isTrue();
    }

    @Test
    void testAlreadyMerged() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);
        sum.merge();
        assertThatThrownBy(sum::merge).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testToString() {
        ReductionVariable<Integer> sum = new ReductionVariable<>(new Sum<>(), 0);

        assertThat(sum.toString()).hasToString(
                "ReductionVariable{\n  operation=Sum,\n  initialValue=0,\n  privateVariables=[\n    \n  ],\n  result=PrivateVariable{value=0},\n  merged=false}");

        sum.copy();
        sum.copy();
        sum.copy();
        sum.merge();

        assertThat(sum.toString()).hasToString(
                "ReductionVariable{\n  operation=Sum,\n  initialValue=0,\n  privateVariables=[\n    PrivateVariable{value=0}\n    PrivateVariable{value=0}\n    PrivateVariable{value=0}\n  ],\n  result=PrivateVariable{value=0},\n  merged=true}");
    }
}
