package jromp.var;

import jromp.operation.Operations;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VariableTests {
    @Test
    void testVariablesCreationFromMap() {
        Map<String, Variable<?>> varMap = Map.of("sum", new SharedVariable<>(0));
        Variables vars = Variables.create(varMap);

        assertThat(vars.get("sum").value()).isEqualTo(0);
    }

    @Test
    void testToStringEmpty() {
        Variables vars = Variables.create();
        assertThat(vars.toString()).hasToString("No variables");
    }

    @Test
    void testToString() {
        Variables vars = Variables.create()
                                  .add("sum", new SharedVariable<>(1))
                                  .add("count", new SharedVariable<>(2));

        assertThat(vars.toString()).hasToString("count -> SharedVariable{value=2}\nsum -> SharedVariable{value=1}");
    }

    @Test
    void testCopySharedVariable() {
        SharedVariable<Integer> sharedVariable = new SharedVariable<>(0);
        Variables vars = Variables.create().add("sum", sharedVariable);
        Variables copy = vars.copy();

        assertThat(copy.get("sum").value()).isEqualTo(0);
        assertThat(copy.get("sum")).isSameAs(sharedVariable);
    }

    @Test
    void testCopyPrivateVariable() {
        PrivateVariable<Integer> privateVariable = new PrivateVariable<>(0);
        Variables vars = Variables.create().add("sum", privateVariable);
        Variables copy = vars.copy();

        assertThat(copy.get("sum").value()).isEqualTo(0);
        assertThat(copy.get("sum")).isNotSameAs(privateVariable);
    }

    @Test
    void testIsEmpty() {
        Variables vars = Variables.create();
        assertThat(vars.isEmpty()).isTrue();

        vars.add("sum", new SharedVariable<>(0));
        assertThat(vars.isEmpty()).isFalse();
    }

    @Test
    void testSize() {
        Variables vars = Variables.create();
        assertThat(vars.size()).isZero();

        vars.add("sum", new SharedVariable<>(0));
        assertThat(vars.size()).isOne();
    }

    @Test
    void testCreate() {
        Variables vars = Variables.create();
        assertThat(vars.isEmpty()).isTrue();
    }

    @Test
    void testValueVariablesOfTypePrivateVariable() {
        Variables vars = Variables.create();
        vars.add("sum1", new PrivateVariable<>(0));
        vars.add("sum2", new PrivateVariable<>(0));
        vars.add("sum3", new PrivateVariable<>(0));
        vars.add("count", new SharedVariable<>(0));

        assertThat(vars.getVariablesOfType(PrivateVariable.class)).hasSize(3);
    }

    @Test
    void testContains() {
        Variables vars = Variables.create();
        vars.add("sum1", new PrivateVariable<>(0));

        assertThat(vars.contains("sum1")).isTrue();
    }

    @Test
    void testContainsNot() {
        Variables vars = Variables.create();
        vars.add("sum1", new PrivateVariable<>(0));

        assertThat(vars.contains("sum2")).isFalse();
    }

    @Test
    void testUpdateOperation() {
        SharedVariable<Integer> sum = new SharedVariable<>(0);
        sum.update(Operations.add(1));

        assertThat(sum.value()).isEqualTo(1);
    }
}
