package it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt;

import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface ITaskInterrupt {

    @Nullable Task getTaskToInterruptFrom(@Nonnegative double currentTime, @Nonnull List<Task> taskList);

}
