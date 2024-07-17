package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitwiseOrTests {
	@Test
	void testIdentifier() {
		BitwiseOr<Integer> bitwiseOr = new BitwiseOr<>();
		assertThat(bitwiseOr.identifier()).isEqualTo("|");
	}

	@Test
	void testInitialize() {
		BitwiseOr<Integer> bitwiseOr = new BitwiseOr<>();
		Variable<Integer> variable = new PrivateVariable<>(0);
		bitwiseOr.initialize(variable);
		assertThat(variable.get()).isZero();
	}

	@Test
	void testCombine() {
		BitwiseOr<Integer> bitwiseOr = new BitwiseOr<>();
		assertThat(bitwiseOr.combine(0, 0)).isZero();
		assertThat(bitwiseOr.combine(0, 1)).isOne();
		assertThat(bitwiseOr.combine(1, 0)).isOne();
		assertThat(bitwiseOr.combine(1, 1)).isOne();
	}
}
