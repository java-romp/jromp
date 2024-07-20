package jromp.parallel;

import jromp.parallel.builder.SectionBuilder;
import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTests {
	@Test
	void testBasicSection() {
		List<PrivateVariable<Integer>> counters = List.of(new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0));
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

		assertThat(counters).extracting(PrivateVariable::get).containsExactly(0, 0, 0, 0);
	}

	@Test
	void testBasicSectionWithFor() {
		List<PrivateVariable<Integer>> counters = List.of(new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0),
		                                                  new PrivateVariable<>(0));
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

		assertThat(counters).extracting(PrivateVariable::get).containsExactly(0, 0, 0, 0, 0);
	}
}
