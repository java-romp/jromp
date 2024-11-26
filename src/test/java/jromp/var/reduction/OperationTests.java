package jromp.var.reduction;

import jromp.var.PrivateVariable;
import jromp.var.SharedVariable;
import jromp.var.Variable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationTests {
    @Test
    void testGetTDifferentTypesOperations() {
        Variable<Integer> variableI = new PrivateVariable<>(0);
        ReductionOperations.<Integer>sum().initialize(variableI);
        assertThat(variableI.value()).isZero();

        Variable<Short> variableS = new PrivateVariable<>((short) 0);
        ReductionOperations.<Short>sum().initialize(variableS);
        assertThat(variableS.value()).isZero();

        Variable<Byte> variableB = new PrivateVariable<>((byte) 0);
        ReductionOperations.<Byte>sum().initialize(variableB);
        assertThat(variableB.value()).isZero();

        Variable<Float> variableF = new PrivateVariable<>(0.0f);
        ReductionOperations.<Float>sum().initialize(variableF);
        assertThat(variableF.value()).isZero();

        Variable<Long> variableL = new PrivateVariable<>(0L);
        ReductionOperations.<Long>sum().initialize(variableL);
        assertThat(variableL.value()).isZero();

        Variable<Double> variableD = new PrivateVariable<>(0d);
        ReductionOperations.<Double>sum().initialize(variableD);
        assertThat(variableD.value()).isZero();

        // Unsupported types
        Variable<BigDecimal> variableBD = new SharedVariable<>(BigDecimal.ZERO);
        ReductionOperation<BigDecimal> sumBigDecimal = ReductionOperations.sum();
        assertThatThrownBy(() -> sumBigDecimal.initialize(variableBD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported type (java.math.BigDecimal)");

        Variable<BigInteger> variableBI = new SharedVariable<>(BigInteger.ZERO);
        ReductionOperation<BigInteger> sumBigInteger = ReductionOperations.sum();
        assertThatThrownBy(() -> sumBigInteger.initialize(variableBI))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported type (java.math.BigInteger)");

        Variable<BigInteger> variableSB = new SharedVariable<>(null);
        assertThatThrownBy(() -> sumBigInteger.initialize(variableSB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported type (null)");
    }
}
