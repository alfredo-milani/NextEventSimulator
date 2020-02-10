package it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class TaskInterruptFactory {

    public enum Algorithm {

        MIN_ARRIVAL_TIME,
        MAX_ARRIVAL_TIME,
        MIN_COMPLETION_TIME,
        MAX_COMPLETION_TIME;

        public static @Nonnull Algorithm from(@Nonnull String algorithm) {
            for (Algorithm a : Algorithm.values()) {
                if (algorithm.equalsIgnoreCase(a.name())) {
                    return a;
                }
            }
            throw new TypeNotPresentException(algorithm, new Throwable("Selected strategy not yet implemented"));
        }

    }

    public static @Nonnull ITaskInterrupt get(@Nonnull String strategy) {
        return get(Algorithm.from(strategy));
    }

    public static @Nonnull ITaskInterrupt get(@Nonnull Algorithm algorithm) {
        Preconditions.checkNotNull(algorithm, "Strategy can not be null");

        switch (algorithm) {
            case MIN_ARRIVAL_TIME:
                return new MinArrivalTimeTaskInterrupt();

            case MAX_ARRIVAL_TIME:
                return new MaxArrivalTimeTaskInterrupt();

            case MIN_COMPLETION_TIME:
                return new MinCompletionTimeTaskInterrupt();

            case MAX_COMPLETION_TIME:
                return new MaxCompletionTimeTaskInterrupt();

            default:
                throw new TypeNotPresentException(
                        algorithm.name(),
                        new Throwable("Strategy not yet implemented")
                );
        }
    }

}
