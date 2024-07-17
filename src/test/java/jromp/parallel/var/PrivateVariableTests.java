package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateVariableTests {
	@Test
	void testGetZeroConstructedNormal() {
		PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
		assertThat(privateVariable.get()).isZero();
	}

	@Test
	void testGetConstructedWithNonZero() {
		PrivateVariable<Integer> privateVariable = new PrivateVariable<>(10);
		assertThat(privateVariable.get()).isZero();
	}

	@Test
	void testSet() {
		PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
		privateVariable.set(1);
		assertThat(privateVariable.get()).isOne();
	}

	@Test
	void testUpdate() {
		PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
		privateVariable.update(x -> x + 1);
		assertThat(privateVariable.get()).isOne();
	}

	@Test
	void testCopy() {
		PrivateVariable<Integer> privateVariable = new PrivateVariable<>(2);
		Variable<Integer> copy = privateVariable.copy();
		assertThat(copy.get()).isZero();
		assertThat(copy).isInstanceOf(PrivateVariable.class)
				.isNotEqualTo(privateVariable);
	}

	@Test
	void testParallelForPrivateVarSet() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

		Parallel.withThreads(threads)
				.parallelFor(0, iterations, vars, (id, start, end, variables) -> {
					for (int i = start; i < end; i++) {
						Variable<Integer> sum = variables.get("sum");
						sum.set(sum.get() + 1);
					}
				})
				.join();

		// The value is zero because each thread has its own copy of the variable
		assertThat(vars.get("sum").get()).isEqualTo(0);
	}

	@Test
	void testParallelForPrivateVarUpdate() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new PrivateVariable<>(0));

		Parallel.withThreads(threads)
				.parallelFor(0, iterations, vars, (id, start, end, variables) -> {
					for (int i = start; i < end; i++) {
						variables.<Integer>get("sum").update(old -> old + 1);
					}
				})
				.join();

		// The value is zero because each thread has its own copy of the variable
		assertThat(vars.get("sum").get()).isEqualTo(0);
	}
}
