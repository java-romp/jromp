package jromp.parallel;

import jromp.parallel.builder.SectionBuilder;
import jromp.parallel.var.LastPrivateVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTests {
	@Test
	void testBasicSection() {
		List<LastPrivateVariable<Integer>> counters = List.of(new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0));
		SectionBuilder builder =
				SectionBuilder.create()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 1))
				              .variables(Variables.create().add("counter", counters.get(0)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 2))
				              .variables(Variables.create().add("counter", counters.get(1)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 3))
				              .variables(Variables.create().add("counter", counters.get(2)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 4))
				              .variables(Variables.create().add("counter", counters.get(3)))
				              .add();

		Parallel.withThreads(4)
		        .sections(builder)
		        .join();

		assertThat(counters).extracting(LastPrivateVariable::value).containsExactly(1, 2, 3, 4);
	}

	@Test
	void testBasicSectionWithFor() {
		List<LastPrivateVariable<Integer>> counters = List.of(new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0),
		                                                      new LastPrivateVariable<>(0));
		SectionBuilder builder =
				SectionBuilder.create()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 1))
				              .variables(Variables.create().add("counter", counters.get(0)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 2))
				              .variables(Variables.create().add("counter", counters.get(1)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 3))
				              .variables(Variables.create().add("counter", counters.get(2)))
				              .add()
				              .task((id, variables) -> variables.<Integer>get("counter").update(v -> v + 4))
				              .variables(Variables.create().add("counter", counters.get(3)))
				              .add()
				              .task((id, variables) -> {
					              for (int i = 0; i < 10; i++) {
						              variables.<Integer>get("counter").update(v -> v + 1);
					              }
				              })
				              .variables(Variables.create().add("counter", counters.get(4)))
				              .add();

		Parallel.withThreads(4)
		        .sections(builder)
		        .join();

		assertThat(counters).extracting(LastPrivateVariable::value).containsExactly(1, 2, 3, 4, 10);
	}
}
