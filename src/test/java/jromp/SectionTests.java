package jromp;

import jromp.operation.Operations;
import jromp.task.Task;
import jromp.var.LastPrivateVariable;
import jromp.var.Variables;
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
        List<Task> tasks = List.of(
                variables -> variables.<Integer>get("counter1").update(Operations.add(1)),
                variables -> variables.<Integer>get("counter2").update(Operations.add(2)),
                variables -> variables.<Integer>get("counter3").update(Operations.add(3)),
                variables -> variables.<Integer>get("counter4").update(Operations.add(4))
        );

        Variables vars = Variables.create()
                                  .add("counter1", counters.get(0))
                                  .add("counter2", counters.get(1))
                                  .add("counter3", counters.get(2))
                                  .add("counter4", counters.get(3));
        JROMP.withThreads(4)
             .withVariables(vars)
             .sections(tasks)
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
        List<Task> tasks = List.of(
                variables -> variables.<Integer>get("counter1").update(Operations.add(1)),
                variables -> variables.<Integer>get("counter2").update(Operations.add(2)),
                variables -> variables.<Integer>get("counter3").update(Operations.add(3)),
                variables -> variables.<Integer>get("counter4").update(Operations.add(4)),
                variables -> {
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

        JROMP.withThreads(4)
             .withVariables(vars)
             .sections(tasks)
             .join();

        assertThat(counters).extracting(LastPrivateVariable::value).containsExactly(1, 2, 3, 4, 10);
    }
}
