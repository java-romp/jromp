package jromp.parallel;

import jromp.parallel.task.Task;
import jromp.parallel.var.Variables;

/**
 * A section of a parallel block.
 *
 * @param task The task to be executed.
 * @param variables The variables to be used.
 */
public record Section(Task task, Variables variables) {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		Section section = (Section) obj;
		return task.equals(section.task) && variables.equals(section.variables);
	}
}
