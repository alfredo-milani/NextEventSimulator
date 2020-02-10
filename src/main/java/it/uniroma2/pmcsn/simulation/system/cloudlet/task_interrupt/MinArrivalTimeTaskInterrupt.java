package it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

class MinArrivalTimeTaskInterrupt implements ITaskInterrupt {

    @Override
    public @Nullable Task getTaskToInterruptFrom(@Nonnegative double currentTime, @Nonnull List<Task> taskList) {
        Preconditions.checkArgument(currentTime > 0, "CurrentEvent must be > 0 (current: %s)", currentTime);
        Preconditions.checkNotNull(taskList, "TaskList can not be null (current: %s)", taskList);

        return taskList.parallelStream()
                .filter(Task::isArrived)
                .min(Task.minArrivalTime)
                .orElse(null);
    }

}
