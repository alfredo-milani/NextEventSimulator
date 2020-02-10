package it.uniroma2.pmcsn.simulation.system.cloud.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class CloudState implements IState {

    private List<Task> tasksClass1;
    private List<Task> tasksClass2;

    public CloudState() {
        this.tasksClass1 = new ArrayList<>();
        this.tasksClass2 = new ArrayList<>();
    }

    public int getSizeTasksClass1() {
        return tasksClass1.size();
    }

    public int getSizeTasksClass2() {
        return tasksClass2.size();
    }

    public @Nonnull List<Task> getTasksClass1() {
        return new ArrayList<>(tasksClass1);
    }

    public @Nonnull List<Task> getTasksClass2() {
        return new ArrayList<>(tasksClass2);
    }

    public @Nullable Task getTaskFrom(@Nonnull List<Task> taskList, @Nonnull Predicate<Task> predicate,
                                       @Nonnull Comparator<Task> comparator) {
        Preconditions.checkNotNull(taskList, "TaskList can not be null (current: %s)", taskList);
        Preconditions.checkNotNull(predicate, "Predicate can not be null (current: %s)", predicate);
        Preconditions.checkNotNull(comparator, "Comparator can not be null (current: %s)", comparator);

        return taskList.parallelStream()
                .filter(predicate)
                .min(comparator)
                .orElse(null);
    }

    public @Nullable Task getNextArrivalClass1() {
        return getTaskFrom(tasksClass1, Task::isArrived, Task.minArrivalTime);
    }

    public @Nullable Task getNextArrivalClass2() {
        return getTaskFrom(tasksClass2, Task::isArrived, Task.minArrivalTime);
    }

    public @Nullable Task getNextInterruptedClass1() {
        return getTaskFrom(tasksClass1, Task::isInterrupted, Task.minCompletionTime);
    }

    public @Nullable Task getNextInterruptedClass2() {
        return getTaskFrom(tasksClass2, Task::isInterrupted, Task.minCompletionTime);
    }

    public @Nullable Task getNextCompletionClass1() {
        return getTaskFrom(tasksClass1, Task::isCompleted, Task.minCompletionTime);
    }

    public @Nullable Task getNextCompletionClass2() {
        return getTaskFrom(tasksClass2, Task::isCompleted, Task.minCompletionTime);
    }

    /**
     * Get next {@link Task} from {@link it.uniroma2.pmcsn.simulation.system.cloud.Cloud} population.
     *
     * @return the {@link Task} with the shortest **completion** time. If no completed task is found, return null.
     */
    @Override
    public @Nullable Task getNextEvent() {
        Task taskClass1 = getNextCompletionClass1();
        Task taskClass2 = getNextCompletionClass2();

        if (taskClass1 != null && taskClass2 != null) {
            return taskClass1.getCompletion() > taskClass2.getCompletion() ? taskClass2 : taskClass1;
        } else if (taskClass1 != null) {
            return taskClass1;
        } else {
            return taskClass2;
        }
    }

    /**
     * The {@link it.uniroma2.pmcsn.simulation.system.cloud.Cloud} has infinite virtual
     * resources, so adding new {@link Task} is always permitted.
     *
     * @param task to add to the system
     */
    @Override
    public void add(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        Task.Class taskClass = task.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                Preconditions.checkState(
                        !tasksClass1.contains(task),
                        "Cloud already contains %s (current: %s)",
                        task,
                        tasksClass1
                );
                tasksClass1.add(task);
                break;

            case CLASS2:
                Preconditions.checkState(
                        !tasksClass2.contains(task),
                        "Cloud already contains %s (current: %s)",
                        task,
                        tasksClass2
                );
                tasksClass2.add(task);
                break;

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task type not supported"));
        }
    }

    public void setCompletionOf(@Nonnull Task task, @Nonnegative double completion) {
        Preconditions.checkNotNull(task, "Task must be not null (current: %s)", task);
        Preconditions.checkArgument(completion > 0, "Completion can not be negative (current: %s)", completion);
        Preconditions.checkState(
                task.getArrival() < completion,
                "Task completion time must be greater than task arrival time (current: %s, %s)",
                task.getArrival(),
                completion
        );

        Task.Class taskClass = task.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                Task taskClass1ToUpdate = tasksClass1.parallelStream()
                        .filter(t -> t.equals(task))
                        .findFirst()
                        .orElse(null);
                Preconditions.checkNotNull(
                        taskClass1ToUpdate,
                        "Expected to find task %s in list tasksClass1 (current: %s)",
                        task.toString(),
                        tasksClass1
                );
                taskClass1ToUpdate.setCompletion(completion);
                break;

            case CLASS2:
                Task taskClass2ToUpdate = tasksClass2.parallelStream()
                        .filter(t -> t.equals(task))
                        .findFirst()
                        .orElse(null);
                Preconditions.checkNotNull(
                        taskClass2ToUpdate,
                        "Expected to find task %s in list tasksClass2 (current: %s)",
                        task.toString(),
                        tasksClass2
                );
                taskClass2ToUpdate.setCompletion(completion);
                break;

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task type not supported"));
        }
    }

    @Override
    public void remove(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        Task.Class taskClass = task.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                tasksClass1.remove(task);
                break;

            case CLASS2:
                tasksClass2.remove(task);
                break;

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task type not supported"));
        }
    }

    @Override
    public boolean isEmpty() {
        return getSizeTasksClass1() == 0 && getSizeTasksClass2() == 0;
    }

}
