package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubTests {
	@Test
	void testIdentifier() {
		Sub<Integer> sub = new Sub<>();
		assertThat(sub.identifier()).isEqualTo("-");
	}

	@Test
	void testInitialize() {
		Sub<Integer> sub = new Sub<>();
		Variable<Integer> variable = new PrivateVariable<>(0);
		sub.initialize(variable);
		assertThat(variable.get()).isZero();
	}

	@Test
	void testCombine() {
		Sub<Integer> sub = new Sub<>();
		assertThat(sub.combine(0, 0)).isZero();
		assertThat(sub.combine(0, 1)).isEqualTo(-1);
		assertThat(sub.combine(1, 0)).isOne();
		assertThat(sub.combine(1, 1)).isZero();
	}
}
