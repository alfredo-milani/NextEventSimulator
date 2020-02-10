package it.uniroma2.pmcsn.simulation.mobile_set.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class MobileSetState implements IState {

    private List<Task> tasksClass1;
    private List<Task> tasksClass2;

    public MobileSetState() {
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

    /**
     * Get next {@link Task} from {@link it.uniroma2.pmcsn.simulation.mobile_set.MobileSet} population.
     *
     * @return the {@link Task} with the shortest **arrival** time. If no completed task is found, return null.
     */
    @Override
    public @Nullable Task getNextEvent() {
        Task taskClass1 = getNextArrivalClass1();
        Task taskClass2 = getNextArrivalClass2();

        if (taskClass1 != null && taskClass2 != null) {
            return taskClass1.getArrival() > taskClass2.getArrival() ? taskClass2 : taskClass1;
        } else if (taskClass1 != null) {
            return taskClass1;
        } else {
            return taskClass2;
        }
    }

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
