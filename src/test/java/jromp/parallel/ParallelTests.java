package jromp.parallel;

import jromp.Constants;
import jromp.parallel.task.Task;
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
	void testParallelBlockWithDefaultVariables() {
		int threads = 4;
		int iterations = 1000;
		int[] countsPerThread = new int[threads];

		Parallel.withThreads(threads)
				.block((id, vars) -> {
					assertThat(vars).isNotNull();
					assertThat(vars.isEmpty()).isFalse();
					assertThat(vars.size()).isEqualTo(1);
					assertThat(vars.get(Constants.NUM_THREADS).get()).isEqualTo(threads);

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
}
