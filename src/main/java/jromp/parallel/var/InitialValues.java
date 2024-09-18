package jromp.parallel.var;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that provides initial values for variables.
 */
public class InitialValues {
    /**
     * A map that contains the initial values for each class.
     */
    private static final Map<Class<?>, Object> INITIAL_VALUES_MAP = new HashMap<>();

    private InitialValues() {
    }

    static {
        INITIAL_VALUES_MAP.put(Integer.class, 0);
        INITIAL_VALUES_MAP.put(Long.class, 0L);
        INITIAL_VALUES_MAP.put(Float.class, 0.0f);
        INITIAL_VALUES_MAP.put(Double.class, 0.0d);
        INITIAL_VALUES_MAP.put(Character.class, '\u0000');
        INITIAL_VALUES_MAP.put(Boolean.class, false);
        INITIAL_VALUES_MAP.put(Byte.class, (byte) 0);
        INITIAL_VALUES_MAP.put(Short.class, (short) 0);
        INITIAL_VALUES_MAP.put(String.class, "");
    }

    /**
     * Returns the initial value for the given class.
     *
     * @param clazz the class of the variable.
     * @param <T>   the type of the variable.
     *
     * @return the initial value for the given class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInitialValue(Class<T> clazz) {
        return (T) INITIAL_VALUES_MAP.get(clazz);
    }

    /**
     * Registers an initial value for the given class.
     *
     * @param clazz the class of the variable.
     * @param value the initial value for the class.
     * @param <T>   the type of the variable.
     */
    public static <T> void registerInitialValue(Class<T> clazz, T value) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' cannot be null");
        }

        if (INITIAL_VALUES_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Initial value for " + clazz.getName() + " already registered");
        }

        INITIAL_VALUES_MAP.put(clazz, value);
    }

    /**
     * Registers the initial values for big numbers ({@link BigInteger} and {@link BigDecimal}), with
     * initial values of {@link BigInteger#ZERO} and {@link BigDecimal#ZERO} respectively.
     */
    public static void registerBigNumbers() {
        registerInitialValue(BigInteger.class, BigInteger.ZERO);
        registerInitialValue(BigDecimal.class, BigDecimal.ZERO);
    }
}
