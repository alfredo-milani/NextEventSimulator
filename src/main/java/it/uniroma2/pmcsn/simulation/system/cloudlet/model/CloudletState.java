package it.uniroma2.pmcsn.simulation.system.cloudlet.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;
import org.checkerframework.checker.index.qual.NonNegative;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 *
 * Using the Arrays would be ideal (fixed length) but to use the features
 *  of java8 (lambda expression) it would require the creation of a List at each use.
 * Workaround: use List and check max requested size at each insertion.
 */
public final class CloudletState implements IState {

    private final int thresholdN;
    private final int thresholdS;

    private List<Task> tasksClass1;
    private List<Task> tasksClass2;

    public CloudletState(@Nonnegative int thresholdN) {
        this(thresholdN, thresholdN);
    }

    public CloudletState(@Nonnegative int thresholdN, @Nonnegative int thresholdS) {
        Preconditions.checkArgument(thresholdN > 0, "ThresholdN must be > 0 (current: %s)", thresholdN);
        Preconditions.checkArgument(thresholdS <= thresholdN, "ThresholdS must be in range [0, %s] (current: %s)", thresholdN, thresholdS);

        this.thresholdN = thresholdN;
        this.thresholdS = thresholdS;

        this.tasksClass1 = new ArrayList<>(thresholdN);
        this.tasksClass2 = new ArrayList<>(thresholdN);
    }

    public int getThresholdN() {
        return thresholdN;
    }

    public int getThresholdS() {
        return thresholdS;
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

    public @Nullable Task getNextCompletionClass1() {
        return getTaskFrom(tasksClass1, Task::isCompleted, Task.minCompletionTime);
    }

    public @Nullable Task getNextCompletionClass2() {
        return getTaskFrom(tasksClass2, Task::isCompleted, Task.minCompletionTime);
    }

    /**
     * Get next {@link Task} from {@link it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet} population.
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
     * The {@link it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet} has limited
     * resources, so can accept only a limited number of {@link Task}s.
     *
     * @param task to add to the system
     * @throws IllegalStateException iif {@link it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet}
     * can not accept incoming {@link Task}
     */
    @Override
    public void add(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);
        Preconditions.checkState(
                getSizeTasksClass1() + getSizeTasksClass2() < thresholdN,
                "Cloudlet can not accept other tasks: max size reached (current: %s)",
                thresholdN
        );

        Task.Class taskClass = task.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                Preconditions.checkState(
                        !tasksClass1.contains(task),
                        "Cloudlet already contains %s (current: %s)",
                        task,
                        tasksClass1
                );
                tasksClass1.add(task);
                break;

            case CLASS2:
                Preconditions.checkState(
                        !tasksClass2.contains(task),
                        "Cloudlet already contains %s (current: %s)",
                        task,
                        tasksClass2
                );
                tasksClass2.add(task);
                break;

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task type not supported"));
        }
    }

    public void setCompletionOf(@Nonnull Task task, @NonNegative double completion) {
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
