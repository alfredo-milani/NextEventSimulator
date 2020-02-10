package it.uniroma2.pmcsn.simulation.simulator.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.MobileSet;
import it.uniroma2.pmcsn.simulation.simulator.Clock;
import it.uniroma2.pmcsn.simulation.simulator.statistics.Statistics;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;
import it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class Context {

    private final Statistics statistics;
    private final double start;
    private final double stop;
    private final MobileSet mobileSet;
    private final Cloudlet cloudlet;
    private final Cloud cloud;
    private final Clock clock;

    public Context(@Nonnull Statistics statistics, @Nonnegative double start,
                   @Nonnegative double stop, @Nonnull MobileSet mobileSet,
                   Cloudlet cloudlet, Cloud cloud, Clock clock) {
        Preconditions.checkNotNull(statistics, "Statistics object can not be null (current: %s)", statistics);
        Preconditions.checkArgument(
                start >= 0 && start < Double.MAX_VALUE,
                "Start simulation time must be in range [%s, %s) (current: %s)",
                0,
                Double.MAX_VALUE,
                start
        );
        Preconditions.checkArgument(
                stop >= 1 && stop <= Double.MAX_VALUE,
                "Stop simulation time must be in range [%s, %s] (current: %s)",
                1,
                Double.MAX_VALUE,
                stop
        );
        Preconditions.checkArgument(
                start < stop,
                "Start simulation time must be less than stop simulation time (current: %s, %s)",
                start,
                stop
        );
        Preconditions.checkNotNull(mobileSet, "MobileSet can not be null (current: %s)", mobileSet);
        Preconditions.checkNotNull(cloudlet, "Cloudlet can not be null (current: %s)", cloudlet);
        Preconditions.checkNotNull(cloud, "Cloud can not be null (current: %s)", cloud);
        Preconditions.checkNotNull(clock, "Clock can not be null (current: %s)", clock);

        this.statistics = statistics;
        this.start = start;
        this.stop = stop;
        this.mobileSet = mobileSet;
        this.cloudlet = cloudlet;
        this.cloud = cloud;
        this.clock = clock;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public double getStart() {
        return start;
    }

    public double getStop() {
        return stop;
    }

    public MobileSet getMobileSet() {
        return mobileSet;
    }

    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public Clock getClock() {
        return clock;
    }

    public static class ContextBuilder {

        private final Statistics statistics;
        private final double start;
        private final double stop;
        private final MobileSet mobileSet;
        private final Cloudlet cloudlet;
        private final Cloud cloud;
        private final Clock clock;

        public ContextBuilder(Statistics statistics, double start, double stop,
                              MobileSet mobileSet, Cloudlet cloudlet, Cloud cloud, Clock clock) {
            this.statistics = statistics;
            this.start = start;
            this.stop = stop;
            this.mobileSet = mobileSet;
            this.cloudlet = cloudlet;
            this.cloud = cloud;
            this.clock = clock;
        }

        public Context build() {
            return new Context(statistics, start, stop, mobileSet,
                    cloudlet, cloud, clock);
        }

        @FunctionalInterface
        public interface  StatisticsBuilder {
            StartBuilder statistics(Statistics statistics);
        }

        @FunctionalInterface
        public interface StartBuilder {
            StopBuilder start(double start);
        }

        @FunctionalInterface
        public interface StopBuilder {
            MobileSetBuilder stop(double stop);
        }

        @FunctionalInterface
        public interface MobileSetBuilder {
            CloudletBuilder mobileSet(MobileSet mobileSet);
        }

        @FunctionalInterface
        public interface CloudletBuilder {
            CloudBuilder cloudlet(Cloudlet cloudlet);
        }

        @FunctionalInterface
        public interface CloudBuilder {
            ClockBuilder cloud(Cloud cloud);
        }

        @FunctionalInterface
        public interface ClockBuilder {
            ContextBuilder clock(Clock clock);
        }

        public static StatisticsBuilder builder() {
            return statistics -> start -> stop -> mobileSet -> cloudlet -> cloud -> clock ->
                    new ContextBuilder(statistics, start, stop, mobileSet, cloudlet, cloud, clock);
        }

    }

}
