package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MulTests {
    @Test
    void testIdentifier() {
        Mul<Integer> mul = new Mul<>();
        assertThat(mul.identifier()).isEqualTo("*");
    }

    @Test
    void testInitialize() {
        Mul<Integer> mul = new Mul<>();
        Variable<Integer> variable = new PrivateVariable<>(0);
        mul.initialize(variable);
        assertThat(variable.value()).isOne();
    }

    @Test
    void testCombine() {
        Mul<Integer> mul = new Mul<>();
        assertThat(mul.combine(0, 0)).isZero();
        assertThat(mul.combine(0, 1)).isZero();
        assertThat(mul.combine(1, 0)).isZero();
        assertThat(mul.combine(1, 3)).isEqualTo(3);
    }
}
