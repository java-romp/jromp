package jromp.operation;

import jromp.JROMP;
import jromp.construct.atomic.Atomic;
import jromp.var.SharedVariable;
import jromp.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllOperationsTests {
    @Test
    void testAssign() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.assign(1), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(1);
    }

    @Test
    void testAdd() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.add(1), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(4);
    }

    @Test
    void testMultiply() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(1));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.multiply(2), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(16);
    }

    @Test
    void testSubtract() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(10));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.subtract(2), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(2);
    }

    @Test
    void testDivide() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(10.0f));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.divide(2.0f), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(0.625f);
    }

    @Test
    void testBitwiseAnd() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(10));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.bitwiseAnd(2), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(2);
    }

    @Test
    void testBitwiseOr() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(10));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.bitwiseOr(2), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(10);
    }

    @Test
    void testBitwiseXor() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(10));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.bitwiseXor(2), vars))
             .join();

        // Since there are 4 threads, the result will be
        // 10 ^ 2 (= 8) ^ 2 (= 10) ^ 2 (= 8) ^ 2 (= 10) = 10
        assertThat(variables.get("x").value()).isEqualTo(10);
    }

    @Test
    void testShiftLeft() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0b00001010));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.shiftLeft(1), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(0b10100000);
    }

    @Test
    void testShiftRight() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0b00001010));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.shiftRight(1), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(0);
    }

    @Test
    void testMax() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(5));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.max(10), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(10);
    }

    @Test
    void testMin() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(5));

        JROMP.withThreads(4)
             .withVariables(variables)
             .block((id, vars) -> Atomic.update("x", Operations.min(10), vars))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(5);
    }
}
