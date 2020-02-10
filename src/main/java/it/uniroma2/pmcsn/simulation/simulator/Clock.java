package it.uniroma2.pmcsn.simulation.simulator;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.simulator.event.OffloadRequestEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class Clock {

    public static final double NULL_INSTANT = 0.0;

    // Last instant
    private double previousInstant;
    // Next instant
    private double nextInstant;
    // Last processed event
    private Task previousEvent;
    // Event to process
    private Task nextEvent;

    public Clock() {
        this.previousInstant = NULL_INSTANT;
        this.nextInstant = NULL_INSTANT;
        this.previousEvent = null;
        this.nextEvent = null;
    }

    /**
     * Get previous {@link Task}
     *
     * @return previous {@link Task}
     */
    public @Nullable Task getPreviousEvent() {
        return previousEvent;
    }

    /**
     * Get next scheduled {@link Task}
     *
     * @return next scheduled {@link Task}
     */
    public @Nullable Task getNextEvent() {
        return nextEvent;
    }

    public double getNextInstant() {
        return nextInstant;
    }

    public double getPreviousInstant() {
        return previousInstant;
    }

    /**
     * Update {@link Clock} state to most imminent {@link Task} to process and updates
     * previous and next time instant accordingly.
     *
     * Note that a well-defined {@link Task}:
     * - should have at least arrival time specified;
     * - should have a completion time iif previous state
     *   of state machine was {@link OffloadRequestEvent}.
     *
     * @param nextEvent is the most imminent event to process
     */
    public void update(@Nonnull Task nextEvent) {
        Preconditions.checkNotNull(nextEvent, "NextEvent can not be null (current: %s)", nextEvent);

        this.previousInstant = this.nextInstant;
        this.nextInstant = nextEvent.isCompleted() ? nextEvent.getCompletion() : nextEvent.getArrival();

        this.previousEvent = this.nextEvent;
        this.nextEvent = nextEvent;
    }

    /**
     * Choose most imminent event to process.
     * This method uses {@link Task#absoluteTime} to compare {@link Task}s.
     * A {@link Task} is considered most imminent compared to another iif both are marked
     * as arrived ({@link Task#isArrived()}) and it has the shortest completion time
     * ({@link Task#getCompletion()}), if present, or arrival time ({@link Task#getArrival()}).
     *
     * @param tasks to choose from. Null {@link Task} will be ignored.
     */
    public void update(Task... tasks) {
        if (tasks == null)
            return;

        Arrays.stream(tasks)
                .filter(Objects::nonNull)
                .min(Task.absoluteTime)
                .ifPresent(this::update);
    }

    @Override
    public String toString() {
        return String.format(
                "NextInstant=%s - PreviousInstant=%s | NextEvent=%s - PreviousEvent=%s",
                nextInstant,
                previousInstant,
                nextEvent,
                previousEvent
        );
    }

}
