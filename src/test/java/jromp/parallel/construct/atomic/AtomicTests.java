package jromp.parallel.construct.atomic;

import jromp.parallel.Parallel;
import jromp.parallel.operation.Operations;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtomicTests {
    @Test
    void testRead() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .withVariables(variables)
                .block((id, vars) -> {
                    Integer value = Atomic.read("x", vars);

                    assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isTrue();
                    assertThat(value).isZero();
                })
                .join();

        // The atomic variable is removed after the parallel block is executed
        assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isFalse();
    }

    @Test
    void testWrite() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .withVariables(variables)
                .block((id, vars) -> {
                    Atomic.write("x", 1, vars);

                    // Only the atomic variable should be updated here
                    assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isTrue();
                    assertThat(variables.<Integer>get("x").value()).isZero();
                })
                .join();

        // The atomic variable changes its value to the final value on the "parent" shared variable
        // after the parallel block is executed, and the atomic variable is removed.
        assertThat(variables.get("x").value()).isEqualTo(1);
        assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isFalse();
    }

    @Test
    void testUpdate() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .withVariables(variables)
                .block((id, vars) -> {
                    Atomic.update("x", Operations.add(1), vars);

                    // Only the atomic variable should be updated here
                    assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isTrue();
                    assertThat(variables.<Integer>get("x").value()).isZero();
                })
                .join();

        // The atomic variable changes its value to the final value on the "parent" shared variable
        // after the parallel block is executed, and the atomic variable is removed.
        assertThat(variables.get("x").value()).isEqualTo(4);
        assertThat(((SharedVariable<Integer>) variables.<Integer>get("x")).hasAtomic()).isFalse();
    }
}
