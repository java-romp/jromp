package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitwiseAndTests {
	@Test
	void testIdentifier() {
		BitwiseAnd<Integer> op = new BitwiseAnd<>();
		assertThat(op.identifier()).isEqualTo("&");
	}

	@Test
	void testInitialize() {
		Variable<Integer> variable = new PrivateVariable<>(0);
		BitwiseAnd<Integer> op = new BitwiseAnd<>();
		op.initialize(variable);
		assertThat(variable.value()).isEqualTo(Integer.valueOf(~0)); // ~0 = -1
	}

	@Test
	void testCombine() {
		BitwiseAnd<Integer> op = new BitwiseAnd<>();
		assertThat(op.combine(0, 0)).isZero();
		assertThat(op.combine(0, 1)).isZero();
		assertThat(op.combine(1, 0)).isZero();
		assertThat(op.combine(1, 1)).isEqualTo(1);
	}
}
