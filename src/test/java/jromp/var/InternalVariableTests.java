package jromp.var;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalVariableTests {
    @Test
    void testToString() {
        InternalVariable<Integer> internalVariable = new InternalVariable<>(0);
        assertThat(internalVariable.toString()).hasToString("InternalVariable{value=0}");

        internalVariable.set(1);
        assertThat(internalVariable.toString()).hasToString("InternalVariable{value=1}");
    }
}
