package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LastPrivateVariableTest {
	@Test
	void testGetZero() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		assertThat(lastPrivateVariable.get()).isZero();
	}

	@Test
	void testGetConstructedWithNonZero() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(10);
		assertThat(lastPrivateVariable.get()).isZero();
	}

	@Test
	void testSet() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		lastPrivateVariable.set(1);
		assertThat(lastPrivateVariable.get()).isOne();
	}

	@Test
	void testUpdate() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		lastPrivateVariable.update(x -> x + 1);
		assertThat(lastPrivateVariable.get()).isOne();
	}

	@Test
	void testCopy() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		Variable<Integer> copy = lastPrivateVariable.copy();
		assertThat(copy.get()).isZero();
		assertThat(copy).isInstanceOf(LastPrivateVariable.class)
				.isNotEqualTo(lastPrivateVariable);
	}

	@Test
	void testParallelForLastPrivateVarSet() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new LastPrivateVariable<>(0));

		Parallel.withThreads(threads)
				.parallelFor(0, iterations, vars, (id, start, end, variables) -> {
					for (int i = start; i < end; i++) {
						Variable<Integer> sum = variables.get("sum");
						sum.set(sum.get() + 1);
					}
				})
				.join();

		assertThat(vars.get("sum").get()).isEqualTo(25);
	}

	@Test
	void testParallelForLastPrivateVarUpdate() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new LastPrivateVariable<>(0));

		Parallel.withThreads(threads)
				.parallelFor(0, iterations, vars, (id, start, end, variables) -> {
					for (int i = start; i < end; i++) {
						variables.<Integer>get("sum").update(old -> old + 1);
					}
				})
				.join();

		assertThat(vars.get("sum").get()).isEqualTo(25);
	}
}
