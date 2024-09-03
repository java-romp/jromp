package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.SharedVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationTests {
    @Test
    void testFromIdentifier() {
        assertThat(ReductionOperations.fromIdentifier("+")).isInstanceOf(Sum.class);
        assertThat(ReductionOperations.fromIdentifier("*")).isInstanceOf(Mul.class);
        assertThat(ReductionOperations.fromIdentifier("&&")).isInstanceOf(LogicalAnd.class);
        assertThat(ReductionOperations.fromIdentifier("||")).isInstanceOf(LogicalOr.class);
        assertThat(ReductionOperations.fromIdentifier("&")).isInstanceOf(BitwiseAnd.class);
        assertThat(ReductionOperations.fromIdentifier("|")).isInstanceOf(BitwiseOr.class);
        assertThat(ReductionOperations.fromIdentifier("^")).isInstanceOf(BitwiseXor.class);
        assertThat(ReductionOperations.fromIdentifier("max")).isInstanceOf(Max.class);
        assertThat(ReductionOperations.fromIdentifier("min")).isInstanceOf(Min.class);
    }

    @Test
    void testCombine() {
        assertThat(ReductionOperations.<Integer>sum().combine(1, 2)).isEqualTo(3);
        assertThat(ReductionOperations.<Integer>mul().combine(2, 3)).isEqualTo(6);
        assertThat(ReductionOperations.<Integer>band().combine(1, 2)).isZero();
        assertThat(ReductionOperations.<Integer>bor().combine(1, 2)).isEqualTo(3);
        assertThat(ReductionOperations.<Integer>bxor().combine(1, 2)).isEqualTo(3);
        assertThat(ReductionOperations.land().combine(true, false)).isFalse();
        assertThat(ReductionOperations.lor().combine(true, false)).isTrue();
        assertThat(ReductionOperations.<Integer>max().combine(1, 2)).isEqualTo(2);
        assertThat(ReductionOperations.<Integer>min().combine(1, 2)).isEqualTo(1);
    }

    @Test
    void testGetIdentifier() {
        assertThat(ReductionOperations.sum().identifier()).isEqualTo("+");
        assertThat(ReductionOperations.mul().identifier()).isEqualTo("*");
        assertThat(ReductionOperations.band().identifier()).isEqualTo("&");
        assertThat(ReductionOperations.bor().identifier()).isEqualTo("|");
        assertThat(ReductionOperations.bxor().identifier()).isEqualTo("^");
        assertThat(ReductionOperations.land().identifier()).isEqualTo("&&");
        assertThat(ReductionOperations.lor().identifier()).isEqualTo("||");
        assertThat(ReductionOperations.max().identifier()).isEqualTo("max");
        assertThat(ReductionOperations.min().identifier()).isEqualTo("min");
    }

    @Test
    void testGetOp() {
        assertThat(ReductionOperations.sum()).isInstanceOf(Sum.class);
        assertThat(ReductionOperations.mul()).isInstanceOf(Mul.class);
        assertThat(ReductionOperations.band()).isInstanceOf(BitwiseAnd.class);
        assertThat(ReductionOperations.bor()).isInstanceOf(BitwiseOr.class);
        assertThat(ReductionOperations.bxor()).isInstanceOf(BitwiseXor.class);
        assertThat(ReductionOperations.land()).isInstanceOf(LogicalAnd.class);
        assertThat(ReductionOperations.lor()).isInstanceOf(LogicalOr.class);
        assertThat(ReductionOperations.max()).isInstanceOf(Max.class);
        assertThat(ReductionOperations.min()).isInstanceOf(Min.class);
    }

    @Test
    void testInitialize() {
        Variable<Integer> variable = new PrivateVariable<>(0);
        ReductionOperations.<Integer>sum().initialize(variable);
        assertThat(variable.value()).isZero();
        ReductionOperations.<Integer>mul().initialize(variable);
        assertThat(variable.value()).isEqualTo(1);
        ReductionOperations.<Integer>band().initialize(variable);
        assertThat(variable.value()).isEqualTo(~0);
        ReductionOperations.<Integer>bor().initialize(variable);
        assertThat(variable.value()).isZero();
        ReductionOperations.<Integer>bxor().initialize(variable);
        assertThat(variable.value()).isZero();
        Variable<Boolean> variable2 = new PrivateVariable<>(false);
        ReductionOperations.land().initialize(variable2);
        assertThat(variable2.value()).isTrue();
        ReductionOperations.lor().initialize(variable2);
        assertThat(variable2.value()).isFalse();
        Variable<Double> variable3 = new PrivateVariable<>(0.0);
        ReductionOperations.<Double>max().initialize(variable3);
        assertThat(variable3.value()).isEqualTo(Double.NEGATIVE_INFINITY);
        ReductionOperations.<Double>min().initialize(variable3);
        assertThat(variable3.value()).isEqualTo(Double.POSITIVE_INFINITY);
    }

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
