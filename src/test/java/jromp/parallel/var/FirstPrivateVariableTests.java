package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirstPrivateVariableTests {
	@Test
	void testGetZero() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
		assertThat(firstPrivateVariable.get()).isZero();
	}

	@Test
	void testGetOne() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(1);
		assertThat(firstPrivateVariable.get()).isOne();
	}

	@Test
	void testSet() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
		firstPrivateVariable.set(1);
		assertThat(firstPrivateVariable.get()).isOne();
	}

	@Test
	void testUpdate() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
		firstPrivateVariable.update(x -> x + 1);
		assertThat(firstPrivateVariable.get()).isOne();
	}

	@Test
	void testCopy() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
		Variable<Integer> copy = firstPrivateVariable.copy();
		assertThat(copy.get()).isZero();
	}

	@Test
	void testParallelForFirstPrivateVarSet() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

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
	void testParallelForFirstPrivateVarUpdate() {
		int threads = 4;
		int iterations = 100;
		Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

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

	@Test
	void testToString() {
		FirstPrivateVariable<Integer> firstPrivateVariable = new FirstPrivateVariable<>(0);
		assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=0, initialValue=0}");

		firstPrivateVariable.set(1);
		assertThat(firstPrivateVariable.toString()).hasToString("FirstPrivateVariable{value=1, initialValue=0}");
	}

	@Test
	void testDontKeepValueAfterExecution() {
		Variables vars = Variables.create().add("sum", new FirstPrivateVariable<>(0));

		Parallel.withThreads(4)
		        .block(vars, (id, variables) -> {
			        for (int i = 0; i < 20; i++) {
				        Variable<Integer> sum = variables.get("sum");
				        sum.update(old -> old + 1);
			        }
		        })
		        .join();

		// The value is zero because each thread has its own copy of the variable
		assertThat(vars.get("sum").get()).isEqualTo(0);
	}
}
