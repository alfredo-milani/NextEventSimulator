package it.uniroma2.pmcsn.simulation.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the state of a system or sub-system.
 */
public interface IState {

    /**
     * Get next {@link Task} from system or sub-system population.
     *
     * @return the {@link Task} with the shortest **completion** (if present) or **arrival** time.
     * If no completed task is found, return null.
     */
    @Nullable Task getNextEvent();

    /**
     * Add task to the system's population
     *
     * @param task to add to the system
     */
    void add(@Nonnull Task task);

    /**
     * Remove {@link Task} from the system's population.
     *
     * @param task to remove to the system
     */
    void remove(@Nonnull Task task);

    /**
     * Check if the system is empty.
     *
     * @return {@code true} iif there is at least one {@link Task} in the system population.
     * Otherwise return {@code false}
     */
    boolean isEmpty();

}
