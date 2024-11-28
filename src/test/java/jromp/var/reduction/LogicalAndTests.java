package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogicalAndTests {
    @Test
    void testIdentifier() {
        ReductionOperation<Boolean> logicalAnd = ReductionOperations.land();
        assertThat(logicalAnd.identifier()).isEqualTo("&&");
        assertThat(ReductionOperations.fromIdentifier("&&")).isEqualTo(logicalAnd);
    }

    @Test
    void testInitialize() {
        ReductionOperation<Boolean> logicalAnd = ReductionOperations.land();
        Variable<Boolean> variable = new PrivateVariable<>(false);
        logicalAnd.initialize(variable);
        assertThat(variable.value()).isTrue();
    }

    @Test
    void testCombine() {
        ReductionOperation<Boolean> logicalAnd = ReductionOperations.land();
        assertThat(logicalAnd.combine(true, true)).isTrue();
        assertThat(logicalAnd.combine(true, false)).isFalse();
        assertThat(logicalAnd.combine(false, true)).isFalse();
        assertThat(logicalAnd.combine(false, false)).isFalse();
    }
}
