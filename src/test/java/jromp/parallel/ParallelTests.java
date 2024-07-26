package jromp.parallel;

import jromp.Constants;
import jromp.parallel.task.Task;
import jromp.parallel.var.AtomicVariable;
import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParallelTests {
	@Test
	void testWithThreadsValid() {
		Parallel p = Parallel.withThreads(4);
		assertThat(p).isNotNull();
	}

	@Test
	void testWithThreadsZero() {
		assertThatThrownBy(() -> Parallel.withThreads(0))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Number of threads must be greater than 0.");
	}

	@Test
	void testWithThreadsNegative() {
		assertThatThrownBy(() -> Parallel.withThreads(-1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Number of threads must be greater than 0.");
	}

	@Test
	void testWithThreadsTooMany() {
		int availableProcessors = Constants.MAX_THREADS;
		assertThat(Parallel.withThreads(availableProcessors + 10)).isNotNull();
	}

	@Test
	void testBeginJoinSimple() {
		String[] result = new String[4];
		Task simpleTask = (id, variables) -> result[id] = "Hello, world!";

		Parallel.withThreads(4)
		        .begin(simpleTask)
		        .join();

		assertThat(result).containsOnly("Hello, world!");
	}

	@Test
	void testParallelForWithoutVariables() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];

		Parallel.withThreads(threads)
		        .parallelFor(0, iterations, (id, start, end, vars) -> {
			        assertThat(vars).isNotNull();

			        for (int i = start; i < end; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(countsPerThread).containsOnly(iterations / threads);
	}

	@Test
	void testSections() {
		int[] result = new int[4];

		Parallel.withThreads(4)
		        .sections(
				        (id, vars) -> result[id] = 1,
				        (id, vars) -> result[id] = 2,
				        (id, vars) -> result[id] = 3,
				        (id, vars) -> result[id] = 4
		        )
		        .join();

		assertThat(result).containsOnly(1, 2, 3, 4);
	}

	@Test
	void testSectionsMoreThreadsThanSections() {
		int threads = 4;
		int[] result = new int[threads];

		Parallel.withThreads(threads)
		        .sections(
				        (id, vars) -> result[id] = 1,
				        (id, vars) -> result[id] = 2,
				        (id, vars) -> result[id] = 3
		        )
		        .join();

		assertThat(result).containsOnly(1, 2, 3, 0);
	}

	@Test
	void testSectionsMoreSectionsThanThreads() {
		int threads = 2;
		int[] result = new int[threads];

		Parallel.withThreads(threads)
		        .sections(
				        (id, vars) -> result[id] = 1,
				        (id, vars) -> result[id] = 2,
				        (id, vars) -> result[id] = 3,
				        (id, vars) -> result[id] = 4,
				        (id, vars) -> result[id] = 5
		        )
		        .join();

		assertThat(result).containsAnyOf(4, 5);
	}

	@Test
	void testSectionsMoreSectionsThanThreadsKeepLastValueSystemArrayCopy() {
		int threads = 3;
        Variables variables = Variables.create().add("num", new AtomicVariable<>(0));

		Parallel.withThreads(threads)
		        .sections(
				        variables,
				        (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
				        (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
				        (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
				        (id, vars) -> vars.<Integer>get("num").update(n -> n + 1),
				        (id, vars) -> vars.<Integer>get("num").update(n -> n + 1)
		        )
		        .join();

		assertThat(variables.<Integer>get("num").value()).isEqualTo(5);
	}

	@Test
	void testParallelBlockWithDefaultVariables() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];

		Parallel.withThreads(threads)
		        .block((id, vars) -> {
			        assertThat(vars).isNotNull();
			        assertThat(vars.isEmpty()).isFalse();
			        assertThat(vars.size()).isEqualTo(1);
			        assertThat(vars.get(Constants.NUM_THREADS).value()).isEqualTo(threads);

			        for (int i = 0; i < iterations; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(countsPerThread).containsOnly(iterations);
	}

	@Test
	void testParallelBlockWithVariables() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];
		Variables variables = Variables.create().add("iterations", new PrivateVariable<>(iterations));

		Parallel.withThreads(threads)
		        .block(variables, (id, vars) -> {
			        assertThat(vars).isNotNull();
			        assertThat(vars.isEmpty()).isFalse();

			        for (int i = 0; i < iterations; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(countsPerThread).containsOnly(iterations);
	}

	@Test
	void testParallelConstructionWithDefaultConfiguration() {
		String[] result = new String[Constants.MAX_THREADS];

		Parallel.defaultConfig()
		        .begin((id, variables) -> result[id] = "Hello, world!")
		        .join();

		assertThat(result).containsOnly("Hello, world!");
	}

	@Test
	void testSingle() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];
		boolean[] singleBlockExecuted = new boolean[threads];
		int[] singleBlockExecutionId = new int[1];
		Variables variables = Variables.create().add("iterations", new PrivateVariable<>(iterations));

		Parallel.withThreads(threads)
		        .block(variables, (id, vars) -> {
			        assertThat(vars).isNotNull();
			        assertThat(vars.isEmpty()).isFalse();

			        for (int i = 0; i < iterations; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .singleBlock((id, vars) -> {
			        assertThat(vars).isNotNull();
			        assertThat(vars.isEmpty()).isFalse();
			        assertThat(vars.size()).isEqualTo(2);
			        assertThat(vars.get(Constants.NUM_THREADS).value()).isEqualTo(threads);
			        singleBlockExecuted[id] = true;
			        singleBlockExecutionId[0] = id;

			        for (int i = 0; i < iterations; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .block(variables, (id, vars) -> {
			        assertThat(vars).isNotNull();
			        assertThat(vars.isEmpty()).isFalse();

			        for (int i = 0; i < iterations; i++) {
				        countsPerThread[id]++;
			        }
		        })
		        .join();

		assertThat(countsPerThread).containsOnlyOnce(iterations * 3);
		assertThat(singleBlockExecuted).containsOnlyOnce(true);

		// In the position of the single block execution id, the value should be true
		assertThat(singleBlockExecuted[singleBlockExecutionId[0]]).isTrue();
		// In the other positions, the value should be false
		for (int i = 0; i < threads; i++) {
			if (i != singleBlockExecutionId[0]) {
				assertThat(singleBlockExecuted[i]).isFalse();
			}
		}
	}

	@Test
	void testThreadsStopOnImplicitBarrier() {
		int threads = 4;
		int[] value = new int[1];

		Parallel.withThreads(threads)
		        .block((id, vars) -> assertThat(value[0]).isZero())
		        .singleBlock((id, vars) -> value[0] = 1)
		        .block((id, vars) -> assertThat(value[0]).isOne())
		        .join();
	}
}
