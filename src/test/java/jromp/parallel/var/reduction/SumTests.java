package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SumTests {
	@Test
	void testIdentifier() {
		Sum<Integer> sum = new Sum<>();
		assertThat(sum.identifier()).isEqualTo("+");
	}

	@Test
	void testInitialize() {
		Sum<Integer> sum = new Sum<>();
		Variable<Integer> variable = new PrivateVariable<>(0);
		sum.initialize(variable);
		assertThat(variable.get()).isZero();
	}

	@Test
	void testCombine() {
		Sum<Integer> sum = new Sum<>();
		assertThat(sum.combine(0, 0)).isZero();
		assertThat(sum.combine(0, 1)).isOne();
		assertThat(sum.combine(1, 0)).isOne();
		assertThat(sum.combine(1, 1)).isEqualTo(2);
	}

	@Test
	void testSubtraction() {
		Sum<Integer> sum = new Sum<>();
		assertThat(sum.combine(0, -1)).isEqualTo(-1);
		assertThat(sum.combine(1, -1)).isZero();
	}
}
