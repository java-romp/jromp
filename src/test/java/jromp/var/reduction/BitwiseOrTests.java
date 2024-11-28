package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitwiseOrTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> bitwiseOr = ReductionOperations.bor();
        assertThat(bitwiseOr.identifier()).isEqualTo("|");
        assertThat(ReductionOperations.fromIdentifier("|")).isEqualTo(bitwiseOr);
    }

    @Test
    void testInitialize() {
        ReductionOperation<Integer> bitwiseOr = ReductionOperations.bor();
        Variable<Integer> variable = new PrivateVariable<>(0);
        bitwiseOr.initialize(variable);
        assertThat(variable.value()).isZero();
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> bitwiseOr = ReductionOperations.bor();
        assertThat(bitwiseOr.combine(0, 0)).isZero();
        assertThat(bitwiseOr.combine(0, 1)).isOne();
        assertThat(bitwiseOr.combine(1, 0)).isOne();
        assertThat(bitwiseOr.combine(1, 1)).isOne();
    }
}
