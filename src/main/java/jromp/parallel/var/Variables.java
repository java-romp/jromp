package jromp.parallel.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a collection of variables.
 */
public class Variables {
	/**
	 * The map of variables.
	 */
	private final Map<String, Variable<?>> variableMap = new HashMap<>();

	private Variables() {
		// Prevent instantiation.
	}

	/**
	 * Creates a new instance of the Variables class.
	 *
	 * @return the newly created Variables object.
	 */
	public static Variables create() {
		return new Variables();
	}

	/**
	 * Creates a new instance of the Variables class with the provided variable map.
	 *
	 * @param varMap the map of variables to be added to the new instance.
	 *
	 * @return the newly created Variables object.
	 */
	public static Variables create(Map<String, Variable<?>> varMap) {
		Variables variables = new Variables();
		variables.variableMap.putAll(varMap);
		return variables;
	}

	/**
	 * Retrieves the map of variables.
	 *
	 * @return the map of variables.
	 */
	public Map<String, Variable<?>> getVariables() {
		return this.variableMap;
	}

	/**
	 * Adds a variable with the specified name to the map of variables.
	 *
	 * @param name     the name of the variable.
	 * @param variable the variable to be added.
	 * @param <T>      the type of the variable.
	 *
	 * @return the updated Variables object.
	 */
	public <T> Variables add(String name, Variable<T> variable) {
		this.variableMap.put(name, variable);
		return this;
	}

	/**
	 * Retrieves the variable with the specified name from the Variables object.
	 *
	 * @param name the name of the variable to retrieve.
	 * @param <T>  the type of the variable.
	 *
	 * @return the variable with the specified name, or null if it does not exist.
	 */
	@SuppressWarnings("unchecked")
	public <T> Variable<T> get(String name) {
		return (Variable<T>) this.variableMap.get(name);
	}

	/**
	 * Checks if the variable map is empty.
	 *
	 * @return {@code true} if the variable map is empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return this.variableMap.isEmpty();
	}

	/**
	 * Returns the size of the map of variables.
	 *
	 * @return the size of the map of variables.
	 */
	public int size() {
		return this.variableMap.size();
	}

	/**
	 * Returns a string representation of the Variables object.
	 * The string contains all the variables in the variable map,
	 * formatted as {@code "name: value, "}.
	 * If the variable map is empty, the string "No variables" is returned.
	 *
	 * @return a string representation of the Variables object.
	 */
	@Override
	public String toString() {
		if (this.variableMap.isEmpty()) {
			return "No variables";
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Variable<?>> entry : this.variableMap.entrySet()) {
			sb.append(entry.getKey())
			  .append(": ")
			  .append(entry.getValue().get())
			  .append(", ");
		}

		return sb.delete(sb.length() - 2, sb.length())
		         .toString();
	}

	/**
	 * Creates a copy of the Variables object.
	 *
	 * @return a copy of the Variables object.
	 */
	public Variables copy() {
		Variables copy = create();

		this.variableMap.forEach((key, value) -> copy.add(key, value.copy()));

		return copy;
	}

	/**
	 * Retrieves a list of variables of a specified type.
	 *
	 * @param <T>  the type of the variables.
	 * @param type the Class object representing the type of variables to retrieve.
	 *
	 * @return a list of variables of the specified type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Variable<?>> List<T> getVariablesOfType(Class<T> type) {
		List<T> variables = new ArrayList<>();

		this.variableMap.forEach((key, value) -> {
			if (type.isInstance(value)) {
				variables.add((T) value);
			}
		});

		return variables;
	}
}
