package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> min = ReductionOperations.min();
        assertThat(min.identifier()).isEqualTo("min");
    }

    @Test
    void testInitialize() {
        ReductionOperation<Integer> min = ReductionOperations.min();
        Variable<Integer> variable = new PrivateVariable<>(0);
        min.initialize(variable);
        assertThat(variable.value()).isEqualTo((int) Double.POSITIVE_INFINITY);
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> min = ReductionOperations.min();
        assertThat(min.combine(0, 0)).isZero();
        assertThat(min.combine(0, -2)).isEqualTo(-2);
        assertThat(min.combine(1, 2)).isOne();
        assertThat(min.combine(1, 1)).isOne();
    }
}
