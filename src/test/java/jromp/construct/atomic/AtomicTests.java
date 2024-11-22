package jromp.construct.atomic;

import jromp.JROMP;
import jromp.operation.Operations;
import jromp.var.SharedVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtomicTests {
    @Test
    void testRead() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> {
                 Integer value = Atomic.read(x);

                 assertThat(x.hasAtomic()).isTrue();
                 assertThat(value).isZero();
             })
             .join();

        // The atomic variable is removed after the parallel block is executed
        assertThat(x.hasAtomic()).isFalse();
    }

    @Test
    void testWrite() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> {
                 Atomic.write(x, 1);

                 // Only the atomic variable should be updated here
                 assertThat(x.hasAtomic()).isTrue();
                 assertThat(x.value()).isZero();
             })
             .join();

        // The atomic variable changes its value to the final value on the "parent" shared variable
        // after the parallel block is executed, and the atomic variable is removed.
        assertThat(x.value()).isEqualTo(1);
        assertThat(x.hasAtomic()).isFalse();
    }

    @Test
    void testUpdate() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> {
                 Atomic.update(x, Operations.add(1));

                 // Only the atomic variable should be updated here
                 assertThat(x.hasAtomic()).isTrue();
                 assertThat(x.value()).isZero();
             })
             .join();

        // The atomic variable changes its value to the final value on the "parent" shared variable
        // after the parallel block is executed, and the atomic variable is removed.
        assertThat(x.value()).isEqualTo(4);
        assertThat(x.hasAtomic()).isFalse();
    }
}
