package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> max = ReductionOperations.max();
        assertThat(max.identifier()).isEqualTo("max");
        assertThat(ReductionOperations.fromIdentifier("max")).isEqualTo(max);
    }

    @Test
    void testInitialize() {
        ReductionOperation<Double> max = ReductionOperations.max();
        Variable<Double> variable = new PrivateVariable<>(0.0);
        max.initialize(variable);
        assertThat(variable.value()).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> max = ReductionOperations.max();
        assertThat(max.combine(0, 0)).isZero();
        assertThat(max.combine(0, 2)).isEqualTo(2);
        assertThat(max.combine(1, 2)).isEqualTo(2);
        assertThat(max.combine(1, 1)).isEqualTo(1);
    }
}
