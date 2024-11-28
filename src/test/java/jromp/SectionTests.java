package jromp;

import jromp.operation.Operations;
import jromp.task.Task;
import jromp.var.LastPrivateVariable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTests {
    @Test
    void testBasicSection() {
        LastPrivateVariable<Integer> counter1 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter2 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter3 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter4 = new LastPrivateVariable<>(0);

        List<Task> tasks = List.of(
                () -> counter1.update(Operations.add(1)),
                () -> counter2.update(Operations.add(2)),
                () -> counter3.update(Operations.add(3)),
                () -> counter4.update(Operations.add(4))
        );

        JROMP.withThreads(4)
             .registerVariables(counter1, counter2, counter3, counter4)
             .sections(tasks)
             .join();

        assertThat(List.of(counter1, counter2, counter3, counter4))
                .extracting(LastPrivateVariable::value)
                .containsExactly(1, 2, 3, 4);
    }

    @Test
    void testBasicSectionWithFor() {
        LastPrivateVariable<Integer> counter1 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter2 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter3 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter4 = new LastPrivateVariable<>(0);
        LastPrivateVariable<Integer> counter5 = new LastPrivateVariable<>(0);

        List<Task> tasks = List.of(
                () -> counter1.update(Operations.add(1)),
                () -> counter2.update(Operations.add(2)),
                () -> counter3.update(Operations.add(3)),
                () -> counter4.update(Operations.add(4)),
                () -> {
                    for (int i = 0; i < 10; i++) {
                        counter5.update(Operations.add(1));
                    }
                }
        );

        JROMP.withThreads(4)
             .registerVariables(counter1, counter2, counter3, counter4, counter5)
             .sections(tasks)
             .join();

        assertThat(List.of(counter1, counter2, counter3, counter4, counter5))
                .extracting(LastPrivateVariable::value)
                .containsExactly(1, 2, 3, 4, 10);
    }
}
