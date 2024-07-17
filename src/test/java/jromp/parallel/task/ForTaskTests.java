package jromp.parallel.task;

import jromp.parallel.var.PrivateVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ForTaskTests {
	@Test
	void testDefaultForLoop() {
		PrivateVariable<Integer> count = new PrivateVariable<>(0);
		ForTask forTask = (id, start, end, variables) -> {
			for (int i = start; i < end; i++) {
				count.update(old -> old + 1);
			}
		};

		forTask.run(0, 0, 10);

		assertThat(count.get()).isEqualTo(10);
	}

	@Test
	void testDefaultForLoopWithVariables() {
		PrivateVariable<Integer> count = new PrivateVariable<>(0);
		ForTask forTask = (id, start, end, variables) -> {
			for (int i = start; i < end; i++) {
				count.update(old -> old + 1);
			}
		};

		forTask.run(0, 0, 10, null);

		assertThat(count.get()).isEqualTo(10);
	}
}
