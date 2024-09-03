package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SumTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> sum = ReductionOperations.sum();
        assertThat(sum.identifier()).isEqualTo("+");
    }

    @Test
    void testInitialize() {
        ReductionOperation<Integer> sum = ReductionOperations.sum();
        Variable<Integer> variable = new PrivateVariable<>(0);
        sum.initialize(variable);
        assertThat(variable.value()).isZero();
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> sum = ReductionOperations.sum();
        assertThat(sum.combine(0, 0)).isZero();
        assertThat(sum.combine(0, 1)).isOne();
        assertThat(sum.combine(1, 0)).isOne();
        assertThat(sum.combine(1, 1)).isEqualTo(2);
    }

    @Test
    void testSubtraction() {
        ReductionOperation<Integer> sum = ReductionOperations.sum();
        assertThat(sum.combine(0, -1)).isEqualTo(-1);
        assertThat(sum.combine(1, -1)).isZero();
    }
}
