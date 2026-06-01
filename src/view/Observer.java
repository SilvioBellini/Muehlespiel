package view;

/**
 * Interface for the Observer in the Observer design pattern.
 * <p>
 * Implementing classes can be registered to an {@link Observable} to be notified of changes.
 */
public interface Observer {
    /**
     * Gets called when the {@link Observable} this observer is registered to
     * notifies all observers of a change.
     */
    void update();
}
