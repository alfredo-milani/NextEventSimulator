package it.uniroma2.pmcsn.simulation.system.cloudlet;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.IExecutor;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.TaskDispatcherConfig;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action.*;

/**
 * System component which manages interactions between cloudlet and distant cloud.
 */
class TaskDispatcher {

    private final TaskDispatcherConfig taskDispatcherConfig;
    private final Map<Action, IExecutor> executionNodes;

    public TaskDispatcher(@Nonnull TaskDispatcherConfig taskDispatcherConfig) {
        Preconditions.checkNotNull(taskDispatcherConfig, "TaskDispatcherConfig must be not null (current: %s)", taskDispatcherConfig);

        this.taskDispatcherConfig = taskDispatcherConfig;
        this.executionNodes = new HashMap<>() {{
            put(SEND_CLOUDLET, taskDispatcherConfig.getCloudletNode());
            put(SEND_CLOUDLET_PREEMPTION_CLASS2, taskDispatcherConfig.getCloudletNode());
            put(SEND_CLOUD, taskDispatcherConfig.getCloudNode());
        }};
    }

    public void dispatch(@Nonnull Task offloadRequest, @Nonnull Action action) {
        Preconditions.checkNotNull(offloadRequest, "Task must be not null (current: %s)", offloadRequest);
        Preconditions.checkNotNull(action, "Action must be not null (current: %s)", action);

        IExecutor executor = executionNodes.get(action);
        switch (action) {
            case SEND_CLOUDLET:
            case SEND_CLOUD:
                executor.receive(offloadRequest);
                break;

            case SEND_CLOUDLET_PREEMPTION_CLASS2:
                executor.receive(offloadRequest, Task.Class.CLASS2);
                break;

            default:
                throw new TypeNotPresentException(
                        action.name(),
                        new Throwable("Can not send offload request to selected execution layer: action not supported")
                );
        }
    }

}
