package jromp.parallel.var;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * A variable that is not shared between threads.
 * It is initialized with the default value of the given type.
 *
 * @param <T> the type of the variable.
 */
public class PrivateVariable<T extends Serializable> implements Variable<T> {
	/**
	 * The value of the variable.
	 */
	private T value;

	/**
	 * Constructs a new private variable with the default value of the given type.
	 */
	@SuppressWarnings("unchecked")
	public PrivateVariable(T value) {
		this.value = (T) InitialValues.getInitialValue(value.getClass());
	}

	@Override
	public T get() {
		return this.value;
	}

	@Override
	public void set(T value) {
		this.value = value;
	}

	@Override
	public void update(UnaryOperator<T> operator) {
		this.value = operator.apply(this.value);
	}

	@Override
	public PrivateVariable<T> copy() {
		return new PrivateVariable<>(SerializationUtils.clone(this.value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void end() {
		this.value = (T) InitialValues.getInitialValue(value.getClass());
	}
}
