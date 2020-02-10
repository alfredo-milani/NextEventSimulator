package it.uniroma2.pmcsn.simulation.mobile_set;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetConfig;
import it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetState;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.UUID;

public class MobileSet implements IGenerator {

    public static final int TASK_CLASS1_RNG_STREAM = 0;
    public static final int TASK_CLASS2_RNG_STREAM = 1;

    public static final double TIME_OFFSET = 0.0;

    // Mobile set configuration
    private final MobileSetConfig mobileSetConfig;
    // Mobile set state
    private final MobileSetState mobileSetState;

    public MobileSet(@Nonnull MobileSetConfig mobileSetConfig) {
        Preconditions.checkNotNull(mobileSetConfig, "MobileSetConfig can not be null (current: %s)", mobileSetConfig);

        this.mobileSetConfig = mobileSetConfig;
        this.mobileSetState = new MobileSetState();
    }

    public MobileSetState getMobileSetState() {
        return mobileSetState;
    }

    @Override
    public void generate(@Nonnull Task.Class taskClass) {
        Preconditions.checkNotNull(taskClass, "TaskClass can not be null (current: %s)", taskClass);

        generate(taskClass, TIME_OFFSET);
    }

    @Override
    public void generate(@Nonnull Task.Class taskClass, @Nonnegative double timeOffset) {
        Preconditions.checkNotNull(taskClass, "TaskClass must be not null (current: %s)", taskClass);
        Preconditions.checkArgument(timeOffset >= 0, "Time offset can not be less than 0 (current: %s)", timeOffset);

        double arrivalTime = timeOffset + computeArrivalTimeFor(taskClass);
        mobileSetState.add(
                Task.TaskBuilder.builder()
                        .uuid(UUID.randomUUID())
                        .taskClass(taskClass)
                        .arrival(arrivalTime)
                        .build()
        );
    }

    @Override
    public void receive(@Nonnull Task taskResult) {
        Preconditions.checkNotNull(taskResult, "TaskResult must be not null (current: %s)", taskResult);
        Preconditions.checkState(taskResult.isCompleted(), "Task must be completed to retrieve its results (current: %s)", taskResult);

        mobileSetState.remove(taskResult);
    }

    private double computeArrivalTimeFor(@Nonnull Task.Class taskClass) {
        Preconditions.checkNotNull(taskClass, "Task class must be not null (current: %s)", taskClass);

        switch (taskClass) {
            case CLASS1:
                return mobileSetConfig.getDistribution().fromExponential(TASK_CLASS1_RNG_STREAM, 1 / mobileSetConfig.getLambda1());

            case CLASS2:
                return mobileSetConfig.getDistribution().fromExponential(TASK_CLASS2_RNG_STREAM, 1 / mobileSetConfig.getLambda2());

            default:
                throw new TypeNotPresentException(taskClass.name(), new Throwable("Task class not supported"));
        }
    }

}
