package jromp.parallel.construct.atomic;

import jromp.parallel.Parallel;
import jromp.parallel.construct.atomic.operation.Operations;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtomicOperationTests {
    @Test
    void testAssign() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .block(variables, (id, vars) -> {
                    Atomic.update("x", Operations.assign(1), vars);
                })
                .join();

        assertThat(variables.get("x").value()).isEqualTo(1);
    }

    @Test
    void testAdd() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        Parallel.withThreads(4)
                .block(variables, (id, vars) -> {
                    Atomic.update("x", Operations.add(1), vars);
                })
                .join();

        assertThat(variables.get("x").value()).isEqualTo(4);
    }
}
