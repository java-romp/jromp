package jromp.parallel.var;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * A variable that is shared between threads.
 *
 * @param <T> the type of the variable.
 */
public class SharedVariable<T> implements Variable<T> {
	/**
	 * The value of the variable.
	 */
	private final AtomicReference<T> value;

	/**
	 * Constructs a new shared variable with the given value.
	 *
	 * @param value the value of the variable.
	 */
	public SharedVariable(T value) {
		this.value = new AtomicReference<>(value);
	}

	@Override
	public T value() {
		return this.value.get();
	}

	/**
	 * Set the value of the variable to the given value.
	 * <i><b>The atomicity of this operation is not guaranteed, use it with caution.</b></i>
	 * <p>
	 * <b>This method is not atomic.</b>
	 * It is possible that another thread could read the value of the variable
	 * before it is set to the new value.
	 * This is because the value is read and set in two separate operations.
	 * If you need to set the value atomically, use the {@link #update(UnaryOperator)} method.
	 * </p>
	 *
	 * @param value The new value of the variable.
	 *
	 * @see #update(UnaryOperator)
	 */
	@Override
	public void set(T value) {
		this.update(old -> value);
	}

	@Override
	public void update(UnaryOperator<T> operator) {
		this.value.updateAndGet(operator);
	}

	@Override
	public SharedVariable<T> copy() {
		return this;
	}

	@Override
	public void end() {
		// Do nothing (keep the last value).
	}

	@Override
	public String toString() {
		return "SharedVariable{value=%s}".formatted(value.get());
	}
}
