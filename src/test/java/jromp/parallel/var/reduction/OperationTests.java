package jromp.parallel.var.reduction;

import jromp.parallel.var.PrivateVariable;
import jromp.parallel.var.Variable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationTests {
	@Test
	void testFromIdentifier() {
		assertThat(Operation.fromIdentifier("+")).isInstanceOf(Sum.class);
		assertThat(Operation.fromIdentifier("-")).isInstanceOf(Sub.class);
		assertThat(Operation.fromIdentifier("*")).isInstanceOf(Mul.class);
		assertThat(Operation.fromIdentifier("&&")).isInstanceOf(LogicalAnd.class);
		assertThat(Operation.fromIdentifier("||")).isInstanceOf(LogicalOr.class);
		assertThat(Operation.fromIdentifier("&")).isInstanceOf(BitwiseAnd.class);
		assertThat(Operation.fromIdentifier("|")).isInstanceOf(BitwiseOr.class);
		assertThat(Operation.fromIdentifier("^")).isInstanceOf(BitwiseXor.class);
		assertThat(Operation.fromIdentifier("max")).isInstanceOf(Max.class);
		assertThat(Operation.fromIdentifier("min")).isInstanceOf(Min.class);
	}

	@Test
	void testCombine() {
		assertThat(Operation.SUM.combine(1, 2)).isEqualTo(3);
		assertThat(Operation.SUB.combine(1, 2)).isEqualTo(-1);
		assertThat(Operation.MUL.combine(2, 3)).isEqualTo(6);
		assertThat(Operation.BAND.combine(1, 2)).isZero();
		assertThat(Operation.BOR.combine(1, 2)).isEqualTo(3);
		assertThat(Operation.BXOR.combine(1, 2)).isEqualTo(3);
		assertThat(Operation.LAND.combine(true, false)).isFalse();
		assertThat(Operation.LOR.combine(true, false)).isTrue();
		assertThat(Operation.MAX.combine(1, 2)).isEqualTo(2);
		assertThat(Operation.MIN.combine(1, 2)).isEqualTo(1);
	}

	@Test
	void testGetIdentifier() {
		assertThat(Operation.SUM.getIdentifier()).isEqualTo("+");
		assertThat(Operation.SUB.getIdentifier()).isEqualTo("-");
		assertThat(Operation.MUL.getIdentifier()).isEqualTo("*");
		assertThat(Operation.BAND.getIdentifier()).isEqualTo("&");
		assertThat(Operation.BOR.getIdentifier()).isEqualTo("|");
		assertThat(Operation.BXOR.getIdentifier()).isEqualTo("^");
		assertThat(Operation.LAND.getIdentifier()).isEqualTo("&&");
		assertThat(Operation.LOR.getIdentifier()).isEqualTo("||");
		assertThat(Operation.MAX.getIdentifier()).isEqualTo("max");
		assertThat(Operation.MIN.getIdentifier()).isEqualTo("min");
	}

	@Test
	void testGetOp() {
		assertThat(Operation.SUM.getOp()).isInstanceOf(Sum.class);
		assertThat(Operation.SUB.getOp()).isInstanceOf(Sub.class);
		assertThat(Operation.MUL.getOp()).isInstanceOf(Mul.class);
		assertThat(Operation.BAND.getOp()).isInstanceOf(BitwiseAnd.class);
		assertThat(Operation.BOR.getOp()).isInstanceOf(BitwiseOr.class);
		assertThat(Operation.BXOR.getOp()).isInstanceOf(BitwiseXor.class);
		assertThat(Operation.LAND.getOp()).isInstanceOf(LogicalAnd.class);
		assertThat(Operation.LOR.getOp()).isInstanceOf(LogicalOr.class);
		assertThat(Operation.MAX.getOp()).isInstanceOf(Max.class);
		assertThat(Operation.MIN.getOp()).isInstanceOf(Min.class);
	}

	@Test
	void testInitialize() {
		Variable<Integer> variable = new PrivateVariable<>(0);
		Operation.SUM.initialize(variable);
		assertThat(variable.get()).isZero();
		Operation.SUB.initialize(variable);
		assertThat(variable.get()).isZero();
		Operation.MUL.initialize(variable);
		assertThat(variable.get()).isEqualTo(1);
		Operation.BAND.initialize(variable);
		assertThat(variable.get()).isEqualTo(~0);
		Operation.BOR.initialize(variable);
		assertThat(variable.get()).isZero();
		Operation.BXOR.initialize(variable);
		assertThat(variable.get()).isZero();
		Variable<Boolean> variable2 = new PrivateVariable<>(false);
		Operation.LAND.initialize(variable2);
		assertThat(variable2.get()).isTrue();
		Operation.LOR.initialize(variable2);
		assertThat(variable2.get()).isFalse();
		Variable<Double> variable3 = new PrivateVariable<>(0.0);
		Operation.MAX.initialize(variable3);
		assertThat(variable3.get()).isEqualTo(Double.NEGATIVE_INFINITY);
		Operation.MIN.initialize(variable3);
		assertThat(variable3.get()).isEqualTo(Double.POSITIVE_INFINITY);
	}

	@Test
	void testGetTDifferentTypesOperations() {
		Variable<Integer> variableI = new PrivateVariable<>(0);
		Operation.SUM.initialize(variableI);
		assertThat(variableI.get()).isZero();

		Variable<Short> variableS = new PrivateVariable<>((short) 0);
		Operation.SUM.initialize(variableS);
		assertThat(variableS.get()).isZero();

		Variable<Byte> variableB = new PrivateVariable<>((byte) 0);
		Operation.SUM.initialize(variableB);
		assertThat(variableB.get()).isZero();

		Variable<Float> variableF = new PrivateVariable<>(0.0f);
		Operation.SUM.initialize(variableF);
		assertThat(variableF.get()).isZero();

		Variable<Long> variableL = new PrivateVariable<>(0L);
		Operation.SUM.initialize(variableL);
		assertThat(variableL.get()).isZero();

		Variable<Double> variableD = new PrivateVariable<>(0d);
		Operation.SUM.initialize(variableD);
		assertThat(variableD.get()).isZero();

		// Unsupported types
		Variable<BigDecimal> variableBD = new PrivateVariable<>(BigDecimal.ZERO);
		assertThatThrownBy(() -> Operation.SUM.initialize(variableBD))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unsupported type");

		Variable<BigInteger> variableBI = new PrivateVariable<>(BigInteger.ZERO);
		assertThatThrownBy(() -> Operation.SUM.initialize(variableBI))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unsupported type");
	}
}
