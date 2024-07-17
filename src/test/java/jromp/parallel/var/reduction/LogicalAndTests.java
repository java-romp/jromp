package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogicalAndTests {
	@Test
	void testIdentifier() {
		LogicalAnd logicalAnd = new LogicalAnd();
		assertThat(logicalAnd.identifier()).isEqualTo("&&");
	}

	@Test
	void testInitialize() {
		LogicalAnd logicalAnd = new LogicalAnd();
		Variable<Boolean> variable = new PrivateVariable<>(false);
		logicalAnd.initialize(variable);
		assertThat(variable.get()).isTrue();
	}

	@Test
	void testCombine() {
		LogicalAnd logicalAnd = new LogicalAnd();
		assertThat(logicalAnd.combine(true, true)).isTrue();
		assertThat(logicalAnd.combine(true, false)).isFalse();
		assertThat(logicalAnd.combine(false, true)).isFalse();
		assertThat(logicalAnd.combine(false, false)).isFalse();
	}
}
