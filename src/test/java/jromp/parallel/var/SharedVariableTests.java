package jromp.parallel.var;

import jromp.parallel.Parallel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SharedVariableTests {
	@Test
	void testParallelForSharedVarUpdateInside() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];
		Variables vars = Variables.create().add("sum", new SharedVariable<>(0));

		Parallel.withThreads(threads)
		        .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
			        for (int i = start; i < end; i++) {
				        Variable<Integer> insideSum = variables.get("sum");
				        insideSum.update(old -> old + 1);
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(vars.get("sum").get()).isEqualTo(iterations);
		assertThat(countsPerThread).containsOnly(iterations / threads);
	}

	@Test
	void testParallelForSharedVarUpdateOutside() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];
		SharedVariable<Integer> outsideSum = new SharedVariable<>(0);
		Variables vars = Variables.create().add("sum", outsideSum);

		Parallel.withThreads(threads)
		        .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
			        for (int i = start; i < end; i++) {
				        outsideSum.update(old -> old + 1);
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(outsideSum.get()).isEqualTo(iterations);
		assertThat(countsPerThread).containsOnly(iterations / threads);
	}

	@Test
	void testParallelForSharedVarSet() {
		int threads = 2;
		int iterations = 10;
		Variables vars = Variables.create().add("sum", new SharedVariable<>(0));

		Parallel.withThreads(threads)
		        .parallelFor(0, iterations, vars, (id, start, end, variables) -> {
			        for (int i = start; i < end; i++) {
				        Variable<Integer> sum = variables.get("sum");
				        sum.set(1);
			        }
		        })
		        .join();

		assertThat(vars.get("sum").get()).isEqualTo(1);
	}

	@Test
	void testToString() {
		SharedVariable<Integer> sharedVariable = new SharedVariable<>(0);
		assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=0}");

		sharedVariable.set(1);
		assertThat(sharedVariable.toString()).hasToString("SharedVariable{value=1}");
	}
}
