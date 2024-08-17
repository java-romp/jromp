package jromp.parallel;

import jromp.parallel.operation.Operations;
import jromp.parallel.task.Task;
import jromp.parallel.var.LastPrivateVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTests {
    @Test
    @Disabled("LastPrivateVariable is not working correctly")
    void testBasicSection() {
        List<LastPrivateVariable<Integer>> counters = List.of(new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0));
        List<Task> tasks = List.of(
                (id, variables) -> variables.<Integer>get("counter1").update(Operations.add(1)),
                (id, variables) -> variables.<Integer>get("counter2").update(Operations.add(2)),
                (id, variables) -> variables.<Integer>get("counter3").update(Operations.add(3)),
                (id, variables) -> variables.<Integer>get("counter4").update(Operations.add(4))
        );

        Variables vars = Variables.create()
                                  .add("counter1", counters.get(0))
                                  .add("counter2", counters.get(1))
                                  .add("counter3", counters.get(2))
                                  .add("counter4", counters.get(3));
        Parallel.withThreads(4)
                .withVariables(vars)
                .sections(false, tasks)
                .join();

        assertThat(counters).extracting(LastPrivateVariable::value).containsExactly(1, 2, 3, 4);
    }

    @Test
    @Disabled("LastPrivateVariable is not working correctly")
    void testBasicSectionWithFor() {
        List<LastPrivateVariable<Integer>> counters = List.of(new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0),
                                                              new LastPrivateVariable<>(0));
        List<Task> tasks = List.of(
                (id, variables) -> variables.<Integer>get("counter1").update(Operations.add(1)),
                (id, variables) -> variables.<Integer>get("counter2").update(Operations.add(2)),
                (id, variables) -> variables.<Integer>get("counter3").update(Operations.add(3)),
                (id, variables) -> variables.<Integer>get("counter4").update(Operations.add(4)),
                (id, variables) -> {
                    for (int i = 0; i < 10; i++) {
                        variables.<Integer>get("counter5").update(Operations.add(1));
                    }
                }
        );

        Variables vars = Variables.create()
                                  .add("counter1", counters.get(0))
                                  .add("counter2", counters.get(1))
                                  .add("counter3", counters.get(2))
                                  .add("counter4", counters.get(3))
                                  .add("counter5", counters.get(4));

        Parallel.withThreads(4)
                .withVariables(vars)
                .sections(false, tasks)
                .join();

        assertThat(counters).extracting(LastPrivateVariable::value).containsExactly(1, 2, 3, 4, 10);
    }
}
