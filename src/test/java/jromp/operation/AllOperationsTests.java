package jromp.operation;

import jromp.JROMP;
import jromp.construct.atomic.Atomic;
import jromp.var.SharedVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllOperationsTests {
    @Test
    void testAssign() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.assign(1)))
             .join();

        assertThat(x.value()).isEqualTo(1);
    }

    @Test
    void testAdd() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.add(1)))
             .join();

        assertThat(x.value()).isEqualTo(4);
    }

    @Test
    void testMultiply() {
        SharedVariable<Integer> x = new SharedVariable<>(1);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.multiply(2)))
             .join();

        assertThat(x.value()).isEqualTo(16);
    }

    @Test
    void testSubtract() {
        SharedVariable<Integer> x = new SharedVariable<>(10);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.subtract(2)))
             .join();

        assertThat(x.value()).isEqualTo(2);
    }

    @Test
    void testDivide() {
        SharedVariable<Float> x = new SharedVariable<>(10.0f);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.divide(2.0f)))
             .join();

        assertThat(x.value()).isEqualTo(0.625f);
    }

    @Test
    void testLogicalAnd() {
        SharedVariable<Boolean> x = new SharedVariable<>(true);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.logicalAnd(false)))
             .join();

        assertThat(x.value()).isFalse();
    }

    @Test
    void testLogicalOr() {
        SharedVariable<Boolean> x = new SharedVariable<>(false);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.logicalOr(true)))
             .join();

        assertThat(x.value()).isTrue();
    }

    @Test
    void testBitwiseAnd() {
        SharedVariable<Integer> x = new SharedVariable<>(10);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.bitwiseAnd(2)))
             .join();

        assertThat(x.value()).isEqualTo(2);
    }

    @Test
    void testBitwiseOr() {
        SharedVariable<Integer> x = new SharedVariable<>(10);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.bitwiseOr(2)))
             .join();

        assertThat(x.value()).isEqualTo(10);
    }

    @Test
    void testBitwiseXor() {
        SharedVariable<Integer> x = new SharedVariable<>(10);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.bitwiseXor(2)))
             .join();

        // Since there are 4 threads, the result will be
        // 10 ^ 2 (= 8) ^ 2 (= 10) ^ 2 (= 8) ^ 2 (= 10) = 10
        assertThat(x.value()).isEqualTo(10);
    }

    @Test
    void testShiftLeft() {
        SharedVariable<Integer> x = new SharedVariable<>(0b00001010);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.shiftLeft(1)))
             .join();

        assertThat(x.value()).isEqualTo(0b10100000);
    }

    @Test
    void testShiftRight() {
        SharedVariable<Integer> x = new SharedVariable<>(0b00001010);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.shiftRight(1)))
             .join();

        assertThat(x.value()).isZero();
    }

    @Test
    void testMax() {
        SharedVariable<Integer> x = new SharedVariable<>(5);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.max(10)))
             .join();

        assertThat(x.value()).isEqualTo(10);
    }

    @Test
    void testMin() {
        SharedVariable<Integer> x = new SharedVariable<>(5);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Atomic.update(x, Operations.min(10)))
             .join();

        assertThat(x.value()).isEqualTo(5);
    }

    @Test
    void testOperationIdentifiers() {
        assertThat(Operations.assign(0).identifier()).isEqualTo("=");
        assertThat(Operations.add(0).identifier()).isEqualTo("+");
        assertThat(Operations.multiply(0).identifier()).isEqualTo("*");
        assertThat(Operations.subtract(0).identifier()).isEqualTo("-");
        assertThat(Operations.divide(0).identifier()).isEqualTo("/");
        assertThat(Operations.logicalAnd(false).identifier()).isEqualTo("&&");
        assertThat(Operations.logicalOr(false).identifier()).isEqualTo("||");
        assertThat(Operations.bitwiseAnd(0).identifier()).isEqualTo("&");
        assertThat(Operations.bitwiseOr(0).identifier()).isEqualTo("|");
        assertThat(Operations.bitwiseXor(0).identifier()).isEqualTo("^");
        assertThat(Operations.shiftLeft(0).identifier()).isEqualTo("<<");
        assertThat(Operations.shiftRight(0).identifier()).isEqualTo(">>");
        assertThat(Operations.max(0).identifier()).isEqualTo("max");
        assertThat(Operations.min(0).identifier()).isEqualTo("min");
    }
}
