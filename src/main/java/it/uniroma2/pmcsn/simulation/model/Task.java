package it.uniroma2.pmcsn.simulation.model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public final class Task {

    public static final double NOT_ARRIVED = Double.MIN_VALUE;
    public static final double NOT_COMPLETED = Double.MAX_VALUE;

    // Comparators
    public static Comparator<Task> minArrivalTime = Comparator.comparingDouble(Task::getArrival);
    public static Comparator<Task> maxArrivalTime = Comparator.comparingDouble(Task::getArrival).reversed();
    public static Comparator<Task> minCompletionTime = Comparator.comparingDouble(Task::getCompletion);
    public static Comparator<Task> maxCompletionTime = Comparator.comparingDouble(Task::getCompletion).reversed();
    public static Comparator<Task> absoluteTime = (t1, t2) -> {
        if (!t1.isArrived() || !t2.isArrived()) {
            throw new IllegalArgumentException(
                    String.format("Comparison error: invalid tasks %s and %s", t1, t2));
        }
        double timeT1 = t1.isCompleted() ? t1.getCompletion() : t1.getArrival();
        double timeT2 = t2.isCompleted() ? t2.getCompletion() : t2.getArrival();
        return Double.compare(timeT1, timeT2);
    };

    public enum Class {

        CLASS1,
        CLASS2

    }

    private final UUID uuid;

    // Task class type
    private Class taskClass;
    // Task arrival time in the system
    private double arrival;
    // Task completion time
    private double completion;
    // Task interrupted flag
    private boolean interrupted;

    public Task(@Nonnull UUID uuid) {
        this(uuid, null, NOT_ARRIVED, NOT_COMPLETED, false);
    }

    public Task(@Nonnull UUID uuid, Class taskClass, double arrival, double completion, boolean interrupted) {
        Preconditions.checkNotNull(uuid, "UUID can not be null (current: %s)", uuid);
        if (isInterrupted()) {
            Preconditions.checkArgument(isArrived(), "Interrupted task must have arrived (current: %s)", arrival);
            Preconditions.checkArgument(isCompleted(), "Interrupted task must have partial completed (current: %s)", completion);
        }
        Preconditions.checkArgument(
                arrival < completion,
                "Arrival time must be less than completion time (current: %s, %s)",
                arrival,
                completion
        );

        this.uuid = uuid;

        this.taskClass = taskClass;
        this.arrival = arrival;
        this.completion = completion;
        this.interrupted = interrupted;
    }

    public Task(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Other task can not be null (current: %s)", task);

        this.uuid = task.uuid;
        this.taskClass = task.taskClass;
        this.arrival = task.arrival;
        this.completion = task.completion;
        this.interrupted = task.interrupted;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Class getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(Class taskClass) {
        this.taskClass = taskClass;
    }

    /**
     * Get arrival time of the {@link Task} in the system.
     *
     * @return time instant of arrival in the system
     */
    public double getArrival() {
        return arrival;
    }

    public void setArrival(double arrival) {
        this.arrival = arrival;
    }

    /**
     * Method used to know weather or not {@link Task} is arrived in the system.
     *
     * @return {@code true} iif arrival time is well-defined.
     */
    public boolean isArrived() {
        return arrival != NOT_ARRIVED;
    }

    /**
     * Get completion time of the {@link Task} (arrival time + service time).
     *
     * @return time instant of completion by the system
     */
    public double getCompletion() {
        return completion;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    /**
     * Method used to know weather or not {@link Task} has been processed by the system.
     *
     * @return {@code true} iif completion time is well-defined.
     */
    public boolean isCompleted() {
        return completion != NOT_COMPLETED;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Method used to know weather or not {@link Task} has been interrupted by the system.
     *
     * @return {@code true} iif task has been interrupted.
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    public static class TaskBuilder {

        private final UUID uuid;

        private Class taskClass;
        private double arrival;
        private double completion;
        private boolean interrupted;

        public TaskBuilder(UUID uuid) {
            this.uuid = uuid;

            this.taskClass = null;
            this.arrival = NOT_ARRIVED;
            this.completion = NOT_COMPLETED;
            this.interrupted = false;
        }

        public Task build() {
            return new Task(uuid, taskClass, arrival, completion, interrupted);
        }

        @FunctionalInterface
        public interface UuidBuilder {
            TaskBuilder uuid(UUID uuid);
        }

        public TaskBuilder taskClass(Class taskClass) {
            this.taskClass = taskClass;
            return this;
        }

        public TaskBuilder arrival(double arrival) {
            this.arrival = arrival;
            return this;
        }

        public TaskBuilder completion(double completion) {
            this.completion = completion;
            return this;
        }

        public TaskBuilder interrupted(boolean interrupted) {
            this.interrupted = interrupted;
            return this;
        }

        public static UuidBuilder builder() {
            return uuid -> new TaskBuilder(uuid);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return uuid.equals(task.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return String.format(
                "%s@%s=(UUID=%s, class=%s, arrival=%s, completion=%s, interrupted=%s)",
                getClass().getSimpleName(),
                Integer.toHexString(hashCode()),
                uuid,
                taskClass,
                arrival == NOT_ARRIVED ? null : arrival,
                completion == NOT_COMPLETED ? null : completion,
                interrupted
        );
    }

}
