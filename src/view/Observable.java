package view;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for observable objects in the Observer design pattern.
 * <p>
 * Manages a list of {@link Observer}s that can be notified when a change occurs.
 */
public abstract class Observable {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer to this observable by adding it to the list.
     *
     * @param o The observer that should get added.
     */
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Removes a previously registered observer from this observable.
     * If the list does not contain the observer, it remains unchanged.
     *
     * @param o The observer that should be removed.
     */
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * Notifies all registered observers by calling their {@link Observer#update()} method.
     */
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}
