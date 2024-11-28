package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitwiseAndTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> op = ReductionOperations.band();
        assertThat(op.identifier()).isEqualTo("&");
        assertThat(ReductionOperations.fromIdentifier("&")).isEqualTo(op);
    }

    @Test
    void testInitialize() {
        Variable<Integer> variable = new PrivateVariable<>(0);
        ReductionOperation<Integer> op = ReductionOperations.band();
        op.initialize(variable);
        assertThat(variable.value()).isEqualTo(Integer.valueOf(~0)); // ~0 = -1
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> op = ReductionOperations.band();
        assertThat(op.combine(0, 0)).isZero();
        assertThat(op.combine(0, 1)).isZero();
        assertThat(op.combine(1, 0)).isZero();
        assertThat(op.combine(1, 1)).isEqualTo(1);
    }
}
