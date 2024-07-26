package jromp.parallel.builder;

/**
 * Interface for a builder.
 *
 * @param <T> The type of the object to build.
 */
public interface Builder<T> {
    /**
     * Build the object.
     *
     * @return The built object.
     */
    T build();
}
