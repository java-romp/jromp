package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogicalOrTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Boolean> logicalOr = ReductionOperations.lor();
        assertThat(logicalOr.identifier()).isEqualTo("||");
    }

    @Test
    void testInitialize() {
        ReductionOperation<Boolean> logicalOr = ReductionOperations.lor();
        Variable<Boolean> variable = new PrivateVariable<>(false);
        logicalOr.initialize(variable);
        assertThat(variable.value()).isFalse();
    }

    @Test
    void testCombine() {
        ReductionOperation<Boolean> logicalOr = ReductionOperations.lor();
        assertThat(logicalOr.combine(true, true)).isTrue();
        assertThat(logicalOr.combine(true, false)).isTrue();
        assertThat(logicalOr.combine(false, true)).isTrue();
        assertThat(logicalOr.combine(false, false)).isFalse();
    }
}
