package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinTests {
	@Test
	void testIdentifier() {
		Min<Integer> min = new Min<>();
		assertThat(min.identifier()).isEqualTo("min");
	}

	@Test
	void testInitialize() {
		Min<Integer> min = new Min<>();
		Variable<Integer> variable = new PrivateVariable<>(0);
		min.initialize(variable);
		assertThat(variable.get()).isEqualTo((int) Double.POSITIVE_INFINITY);
	}

	@Test
	void testCombine() {
		Min<Integer> min = new Min<>();
		assertThat(min.combine(0, 0)).isZero();
		assertThat(min.combine(0, -2)).isEqualTo(-2);
		assertThat(min.combine(1, 2)).isOne();
		assertThat(min.combine(1, 1)).isOne();
	}
}
