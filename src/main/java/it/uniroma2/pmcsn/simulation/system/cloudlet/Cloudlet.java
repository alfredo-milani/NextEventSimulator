package it.uniroma2.pmcsn.simulation.system.cloudlet;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.IExecutor;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.AccessControlFactory;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.AccessControllerConfig;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletConfig;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.TaskDispatcherConfig;
import it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt.ITaskInterrupt;
import it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt.TaskInterruptFactory;

import javax.annotation.Nonnull;

import static it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action.SEND_CLOUD;

public class Cloudlet implements IExecutor {

    public static final int TASK_CLASS1_RNG_STREAM = 2;
    public static final int TASK_CLASS2_RNG_STREAM = 3;

    // Cloudlet configuration parameters
    private final CloudletConfig cloudletConfig;

    // Cloudlet controller which manages tasks access control
    private final AccessController accessController;
    // Algorithm to choose which task to interrupt
    private final ITaskInterrupt taskInterruptAlgorithm;
    // Cloudlet dispatcher which send offload request to destination
    private final TaskDispatcher taskDispatcher;
    // Cloudlet state
    private final CloudletState cloudletState;

    public Cloudlet(@Nonnull CloudletConfig cloudletConfig) {
        Preconditions.checkNotNull(cloudletConfig, "CloudletConfig can not be null (current: %s)", cloudletConfig);

        this.cloudletConfig = cloudletConfig;
        this.cloudletState = new CloudletState(cloudletConfig.getThresholdN(), cloudletConfig.getThresholdS());

        AccessControllerConfig accessControllerConfig = AccessControllerConfig.ControllerConfigBuilder.builder()
                .algorithm(AccessControlFactory.get(cloudletConfig.getAccessControlAlgorithm()))
                .build();
        this.accessController = new AccessController(accessControllerConfig);
        this.taskInterruptAlgorithm = TaskInterruptFactory.get(cloudletConfig.getInterruptTaskAlgorithm());
        TaskDispatcherConfig taskDispatcherConfig = TaskDispatcherConfig.TaskDispatcherConfigBuilder.builder()
                .cloudletNode(this)
                .cloudNode(cloudletConfig.getCloud())
                .build();
        this.taskDispatcher = new TaskDispatcher(taskDispatcherConfig);
    }

    public CloudletState getCloudletState() {
        return cloudletState;
    }

    @Override
    public void receive(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        cloudletState.add(task);
    }

    @Override
    public void receive(@Nonnull Task task, @Nonnull Task.Class preemptionClass) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);
        Preconditions.checkNotNull(preemptionClass, "PreemptionClass can not be null (current: %s)", preemptionClass);

        Task interruptedTask;
        switch (preemptionClass) {
            case CLASS1:
                throw new UnsupportedOperationException(
                        Task.Class.CLASS1.name(),
                        new Throwable("Cloudlet does not support preemption for tasks belonging to this class")
                );

            case CLASS2:
                interruptedTask = taskInterruptAlgorithm.getTaskToInterruptFrom(
                        task.getArrival(),
                        cloudletState.getTasksClass2()
                );
                break;

            default:
                throw new TypeNotPresentException(
                        preemptionClass.name(),
                        new Throwable("Cloudlet does not support preemption for tasks belonging to this class")
                );
        }

        Preconditions.checkState(
                interruptedTask != null,
                "Offload request with preemption received but not class 2 task are in the cloudlet"
        );

        cloudletState.remove(interruptedTask);
        cloudletState.add(task);

        // current instant is the instant of the current task arrival
        double currentTime = task.getArrival();
        double residualCloudletServiceTime = interruptedTask.getCompletion() - currentTime;
        interruptedTask.setInterrupted(true);
        interruptedTask.setCompletion(residualCloudletServiceTime * cloudletConfig.getMu2());

        // cloudlet send interrupted class 2 task on the cloud
        taskDispatcher.dispatch(interruptedTask, SEND_CLOUD);
    }

    @Override
    public void execute(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        cloudletState.setCompletionOf(task, task.getArrival() + computeServiceTimeFor(task.getTaskClass()));
    }

    @Override
    public void result(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        cloudletState.remove(task);
    }

    public Action accept(@Nonnull Task offloadRequest) {
        Preconditions.checkNotNull(offloadRequest, "Task can not be null (current: %s)", offloadRequest);

        // ask the controller on which execution node the new task should be processed
        Action action = accessController.offloadOn(offloadRequest, cloudletState);
        // call the dispatcher to send the task to the appropriate execution node
        taskDispatcher.dispatch(offloadRequest, action);
        return action;
    }

    private double computeServiceTimeFor(@Nonnull Task.Class taskClass) {
        Preconditions.checkNotNull(taskClass, "Task class must be not null (current: %s)", taskClass);

        switch (taskClass) {
            case CLASS1:
                return cloudletConfig.getDistribution().fromExponential(TASK_CLASS1_RNG_STREAM, 1 / cloudletConfig.getMu1());

            case CLASS2:
                return cloudletConfig.getDistribution().fromExponential(TASK_CLASS2_RNG_STREAM, 1 / cloudletConfig.getMu2());

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task class not supported"));
        }
    }

}
