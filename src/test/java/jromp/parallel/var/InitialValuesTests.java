package jromp.parallel.var;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InitialValuesTests {
    @Test
    void testProvidedValues() {
        assertThat(InitialValues.getInitialValue(Integer.class)).isZero();
        assertThat(InitialValues.getInitialValue(Long.class)).isZero();
        assertThat(InitialValues.getInitialValue(Float.class)).isZero();
        assertThat(InitialValues.getInitialValue(Double.class)).isZero();
        assertThat(InitialValues.getInitialValue(Character.class)).isEqualTo('\u0000');
        assertThat(InitialValues.getInitialValue(Boolean.class)).isFalse();
        assertThat(InitialValues.getInitialValue(Byte.class)).isEqualTo((byte) 0);
        assertThat(InitialValues.getInitialValue(Short.class)).isEqualTo((short) 0);
        assertThat(InitialValues.getInitialValue(String.class)).isEmpty();
        assertThat(InitialValues.getInitialValue(Object.class)).isNull();
    }

    @Test
    void testRegisterValues() {
        InitialValues.registerInitialValue(BigInteger.class, BigInteger.ZERO);
        InitialValues.registerInitialValue(BigDecimal.class, BigDecimal.ZERO);

        assertThat(InitialValues.getInitialValue(BigInteger.class)).isEqualTo(BigInteger.ZERO);
        assertThat(InitialValues.getInitialValue(BigDecimal.class)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testRegisterNullType() {
        assertThatThrownBy(() -> InitialValues.registerInitialValue(null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'clazz' cannot be null");
    }

    @Test
    void testRegisterExistingType() {
        assertThatThrownBy(() -> InitialValues.registerInitialValue(Integer.class, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Initial value for java.lang.Integer already registered");
    }
}
