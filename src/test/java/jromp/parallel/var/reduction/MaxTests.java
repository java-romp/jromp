package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxTests {
    @Test
    void testIdentifier() {
        Max<Integer> max = new Max<>();
        assertThat(max.identifier()).isEqualTo("max");
    }

    @Test
    void testInitialize() {
        Max<Integer> max = new Max<>();
        Variable<Integer> variable = new PrivateVariable<>(0);
        max.initialize(variable);
        assertThat(variable.value()).isEqualTo((int) Double.NEGATIVE_INFINITY);
    }

    @Test
    void testCombine() {
        Max<Integer> max = new Max<>();
        assertThat(max.combine(0, 0)).isZero();
        assertThat(max.combine(0, 2)).isEqualTo(2);
        assertThat(max.combine(1, 2)).isEqualTo(2);
        assertThat(max.combine(1, 1)).isEqualTo(1);
    }
}
