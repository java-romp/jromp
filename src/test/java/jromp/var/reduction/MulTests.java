package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MulTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Integer> mul = ReductionOperations.mul();
        assertThat(mul.identifier()).isEqualTo("*");
    }

    @Test
    void testInitialize() {
        ReductionOperation<Integer> mul = ReductionOperations.mul();
        Variable<Integer> variable = new PrivateVariable<>(0);
        mul.initialize(variable);
        assertThat(variable.value()).isOne();
    }

    @Test
    void testCombine() {
        ReductionOperation<Integer> mul = ReductionOperations.mul();
        assertThat(mul.combine(0, 0)).isZero();
        assertThat(mul.combine(0, 1)).isZero();
        assertThat(mul.combine(1, 0)).isZero();
        assertThat(mul.combine(1, 3)).isEqualTo(3);
    }
}
