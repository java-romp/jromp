package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitwiseXorTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> bitwiseXor = ReductionOperations.bxor();
        assertThat(bitwiseXor.identifier()).isEqualTo("^");
        assertThat(ReductionOperations.fromIdentifier("^")).isEqualTo(bitwiseXor);
    }

    @Test
    void testInitialize() {
        ReductionOperation<Integer> bitwiseXor = ReductionOperations.bxor();
        Variable<Integer> variable = new PrivateVariable<>(0);
        bitwiseXor.initialize(variable);
        assertThat(variable.value()).isZero();
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> bitwiseXor = ReductionOperations.bxor();
        assertThat(bitwiseXor.combine(0, 0)).isZero();
        assertThat(bitwiseXor.combine(0, 1)).isOne();
        assertThat(bitwiseXor.combine(1, 0)).isOne();
        assertThat(bitwiseXor.combine(1, 1)).isZero();
    }
}
