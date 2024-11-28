package jromp.construct.critical;

import jromp.JROMP;
import jromp.operation.Operations;
import jromp.var.SharedVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriticalTests {
    @Test
    void testSimple() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> Critical.enter("x", () -> x.update(Operations.add(1))))
             .join();

        assertThat(x.value()).isEqualTo(4);
    }

    @Test
    void testSameNameMulti() {
        SharedVariable<Integer> x = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> {
                 Critical.enter("x", () -> x.update(Operations.add(1)));
                 Critical.enter("x", () -> x.update(Operations.add(1)));
             })
             .join();

        assertThat(x.value()).isEqualTo(8);
    }

    @Test
    void testDifferentName() {
        SharedVariable<Integer> x = new SharedVariable<>(0);
        SharedVariable<Integer> y = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x, y)
             .parallel(() -> {
                 Critical.enter("x", () -> x.update(Operations.add(1)));
                 Critical.enter("y", () -> y.update(Operations.add(1)));
             })
             .join();

        assertThat(x.value()).isEqualTo(4);
        assertThat(y.value()).isEqualTo(4);
    }

    @Test
    void testDifferentNameMulti() {
        SharedVariable<Integer> x = new SharedVariable<>(0);
        SharedVariable<Integer> y = new SharedVariable<>(0);

        JROMP.withThreads(4)
             .registerVariables(x, y)
             .parallel(() -> {
                 Critical.enter("x", () -> x.update(Operations.add(1)));
                 Critical.enter("y", () -> y.update(Operations.add(1)));
                 Critical.enter("x", () -> x.update(Operations.add(1)));
                 Critical.enter("y", () -> y.update(Operations.add(1)));
             })
             .join();

        assertThat(x.value()).isEqualTo(8);
        assertThat(y.value()).isEqualTo(8);
    }

    @Test
    void testMoreOperationsInsideCritical() {
        SharedVariable<StringBuilder> x = new SharedVariable<>(new StringBuilder());

        JROMP.withThreads(4)
             .registerVariables(x)
             .parallel(() -> {
                 Critical.enter("x", () -> {
                     x.update(sb -> sb.append("x"));
                     x.update(sb -> sb.append(" "));
                     x.update(sb -> sb.append("more"));
                 });
             })
             .join();

        assertThat(x.value().toString()).hasToString("x more".repeat(4));
    }
}
