package it.uniroma2.pmcsn.simulation.system.cloud;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.IExecutor;
import it.uniroma2.pmcsn.simulation.system.cloud.model.CloudConfig;
import it.uniroma2.pmcsn.simulation.system.cloud.model.CloudState;

import javax.annotation.Nonnull;

public class Cloud implements IExecutor {

    public static final int TASK_CLASS1_RNG_STREAM = 4;
    public static final int TASK_CLASS2_RNG_STREAM = 5;
    public static final int SETUP_RNG_STREAM = 6;

    private final CloudConfig cloudConfig;

    private final CloudState cloudState;

    public Cloud(@Nonnull CloudConfig cloudConfig)  {
        Preconditions.checkNotNull(cloudConfig, "CloudConfig can not be null (current: %s)", cloudConfig);

        this.cloudConfig = cloudConfig;
        this.cloudState = new CloudState();
    }

    public CloudState getCloudState() {
        return cloudState;
    }

    @Override
    public void receive(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        cloudState.add(task);
    }

    @Override
    public void receive(@Nonnull Task task, @Nonnull Task.Class preemptionClass) {
        throw new UnsupportedOperationException("Cloud layer does not allow tasks preemption");
    }

    @Override
    public void execute(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        if (task.isInterrupted()) {
            // using heuristic for generation of interrupted task completion time
            //  residualCloudletServiceTime = scheduledCloudletCompletion - currentTime
            //  timeSpentOnExecutionInCloudlet = residualCloudletServiceTime * mu2Cloudlet
            //  timeSpentOnExecutionInCloudlet = task.getCompletion()
            double residualCloudServiceTime = task.getCompletion() / cloudConfig.getMu2();
            double completionTime = task.getArrival() + computeSetupTimeForInterruptedTaskClass2() + residualCloudServiceTime;
            task.setInterrupted(false);
            cloudState.setCompletionOf(task, completionTime);
        } else {
            cloudState.setCompletionOf(task, task.getArrival() + computeServiceTimeFor(task.getTaskClass()));
        }
    }

    @Override
    public void result(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        cloudState.remove(task);
    }

    private double computeServiceTimeFor(@Nonnull Task.Class taskClass) {
        Preconditions.checkNotNull(taskClass, "Task class must be not null (current: %s)", taskClass);

        switch (taskClass) {
            case CLASS1:
                return cloudConfig.getDistribution()
                        .fromExponential(TASK_CLASS1_RNG_STREAM, 1 / cloudConfig.getMu1());

            case CLASS2:
                return cloudConfig.getDistribution()
                        .fromExponential(TASK_CLASS2_RNG_STREAM, 1 / cloudConfig.getMu2());

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task class not supported"));
        }
    }

    private double computeSetupTimeForInterruptedTaskClass2() {
        return cloudConfig.getDistribution()
                .fromExponential(SETUP_RNG_STREAM, cloudConfig.getMeanSetupTime());
    }

}
