package jromp.parallel.var;

import jromp.parallel.Parallel;
import jromp.parallel.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LastPrivateVariableTest {
	@Test
	void testValueZero() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		assertThat(lastPrivateVariable.value()).isZero();
	}

	@Test
	void testValueConstructedWithNonZero() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(10);
		assertThat(lastPrivateVariable.value()).isZero();
	}

	@Test
	void testSet() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		lastPrivateVariable.set(1);
		assertThat(lastPrivateVariable.value()).isOne();
	}

	@Test
	void testUpdate() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		lastPrivateVariable.update(x -> x + 1);
		assertThat(lastPrivateVariable.value()).isOne();
	}

	@Test
	void testCopy() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		Variable<Integer> copy = lastPrivateVariable.copy();
		assertThat(copy.value()).isZero();
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
				        sum.set(sum.value() + 1);
			        }
		        })
		        .join();

		assertThat(vars.get("sum").value()).isEqualTo(25);
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

		assertThat(vars.get("sum").value()).isEqualTo(25);
	}

	@Test
	void testToString() {
		LastPrivateVariable<Integer> lastPrivateVariable = new LastPrivateVariable<>(0);
		assertThat(lastPrivateVariable.toString()).hasToString("LastPrivateVariable{value=0, lastValue=0}");

		lastPrivateVariable.set(1);
		assertThat(lastPrivateVariable.toString()).hasToString("LastPrivateVariable{value=1, lastValue=1}");
	}

	@Test
	void testKeepLastValueAfterExecution() {
		Variables vars = Variables.create().add("sum", new LastPrivateVariable<>(0));

		Parallel.withThreads(4)
		        .block(vars, (id, variables) -> {
			        for (int i = 0; i < 2; i++) {
				        Variable<Integer> sum = variables.get("sum");

				        // The master thread will sleep for a while to end up with a different value
				        if (Utils.isMaster(id)) {
					        try {
						        Thread.sleep(100);
					        } catch (InterruptedException e) {
						        throw new RuntimeException(e);
					        }

					        sum.set(200);
				        } else {
					        sum.update(old -> old + 1);
				        }
			        }
		        })
		        .join();

		assertThat(vars.get("sum").value()).isEqualTo(200);
	}
}
