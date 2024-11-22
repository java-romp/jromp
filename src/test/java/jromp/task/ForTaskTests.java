package jromp.task;

import jromp.var.PrivateVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ForTaskTests {
    @Test
    void testDefaultForLoop() {
        PrivateVariable<Integer> count = new PrivateVariable<>(0);
        ForTask forTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                count.update(old -> old + 1);
            }
        };

        forTask.run(0, 10);

        assertThat(count.value()).isEqualTo(10);
    }

    @Test
    void testDefaultForLoopWithVariables() {
        PrivateVariable<Integer> count = new PrivateVariable<>(0);
        ForTask forTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                count.update(old -> old + 1);
            }
        };

        forTask.run(0, 10);

        assertThat(count.value()).isEqualTo(10);
    }
}
