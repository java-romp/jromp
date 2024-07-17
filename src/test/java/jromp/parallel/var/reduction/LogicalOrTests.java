package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogicalOrTests {
	@Test
	void testIdentifier() {
		LogicalOr logicalOr = new LogicalOr();
		assertThat(logicalOr.identifier()).isEqualTo("||");
	}

	@Test
	void testInitialize() {
		LogicalOr logicalOr = new LogicalOr();
		Variable<Boolean> variable = new PrivateVariable<>(false);
		logicalOr.initialize(variable);
		assertThat(variable.get()).isFalse();
	}

	@Test
	void testCombine() {
		LogicalOr logicalOr = new LogicalOr();
		assertThat(logicalOr.combine(true, true)).isTrue();
		assertThat(logicalOr.combine(true, false)).isTrue();
		assertThat(logicalOr.combine(false, true)).isTrue();
		assertThat(logicalOr.combine(false, false)).isFalse();
	}
}
