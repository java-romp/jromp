package jromp.parallel.builder;

import jromp.parallel.Section;
import jromp.parallel.task.Task;
import jromp.parallel.var.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for parallel sections.
 */
public class SectionBuilder implements Builder<List<Section>> {
    /**
     * The task to execute.
     */
    private Task task;

    /**
     * The variables to use inside the section.
     */
    private Variables variables;

    /**
     * The list of sections.
     */
    private final List<Section> sections = new ArrayList<>();

    /**
     * Private constructor.
     */
    private SectionBuilder() {
    }

    /**
     * Create a new section builder.
     *
     * @return The section builder.
     */
    public static SectionBuilder create() {
        return new SectionBuilder();
    }

    /**
     * Add a new section to the builder.
     *
     * @return The section builder.
     */
    public SectionBuilder add() {
        sections.add(new Section(task, variables));
        return this;
    }

    /**
     * Sets the task to be executed in the section.
     *
     * @param task The task to execute.
     *
     * @return The section builder.
     */
    public SectionBuilder task(Task task) {
        this.task = task;
        return this;
    }

    /**
     * Sets the {@link Variables} to be used inside the section.
     *
     * @param variables The variables to use inside the section.
     *
     * @return The {@code SectionBuilder} instance.
     */
    public SectionBuilder variables(Variables variables) {
        this.variables = variables;
        return this;
    }

    /**
     * Builds and returns a list of sections.
     *
     * @return The list of sections.
     */
    @Override
    public List<Section> build() {
        return sections;
    }
}
