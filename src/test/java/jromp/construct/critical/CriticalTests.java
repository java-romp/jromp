package jromp.construct.critical;

import jromp.JROMP;
import jromp.var.SharedVariable;
import jromp.var.Variable;
import jromp.var.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriticalTests {
    @Test
    void testSimple() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .parallel(vars -> Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1)))
             .join();

        assertThat(variables.get("x").value()).isEqualTo(4);
    }

    @Test
    void testSameNameMulti() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .parallel(vars -> {
                 Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1));
                 Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1));
             })
             .join();

        assertThat(variables.get("x").value()).isEqualTo(8);
    }

    @Test
    void testDifferentName() {
        Variables variables = Variables.create()
                                       .add("x", new SharedVariable<>(0))
                                       .add("y", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .parallel(vars -> {
                 Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1));
                 Critical.enter("y", vars, v -> v.<Integer>get("y").update(y -> y + 1));
             })
             .join();

        assertThat(variables.get("x").value()).isEqualTo(4);
        assertThat(variables.get("y").value()).isEqualTo(4);
    }

    @Test
    void testDifferentNameMulti() {
        Variables variables = Variables.create()
                                       .add("x", new SharedVariable<>(0))
                                       .add("y", new SharedVariable<>(0));

        JROMP.withThreads(4)
             .withVariables(variables)
             .parallel(vars -> {
                 Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1));
                 Critical.enter("y", vars, v -> v.<Integer>get("y").update(y -> y + 1));
                 Critical.enter("x", vars, v -> v.<Integer>get("x").update(x -> x + 1));
                 Critical.enter("y", vars, v -> v.<Integer>get("y").update(y -> y + 1));
             })
             .join();

        assertThat(variables.get("x").value()).isEqualTo(8);
        assertThat(variables.get("y").value()).isEqualTo(8);
    }

    @Test
    void testMoreOperationsInsideCritical() {
        Variables variables = Variables.create().add("x", new SharedVariable<>(new StringBuilder()));

        JROMP.withThreads(4)
             .withVariables(variables)
             .parallel(vars -> {
                 Critical.enter("x", vars, v -> {
                     Variable<StringBuilder> variable = v.get("x");
                     variable.update(sb -> sb.append("x"));
                     variable.update(sb -> sb.append(" "));
                     variable.update(sb -> sb.append("more"));
                 });
             })
             .join();

        assertThat(variables.get("x").value().toString()).hasToString("x more".repeat(4));
    }
}
