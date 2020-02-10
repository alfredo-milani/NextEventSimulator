package it.uniroma2.pmcsn.simulation.system.cloudlet.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.AccessControlFactory;
import it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt.TaskInterruptFactory;
import it.uniroma2.pmcsn.simulation.util.Distribution;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class CloudletConfig {

    private final Cloud cloud;
    private final Distribution distribution;
    private final double mu1;
    private final double mu2;
    private final int thresholdN;
    private final AccessControlFactory.Algorithm accessControlAlgorithm;
    private final TaskInterruptFactory.Algorithm interruptTaskAlgorithm;

    private final int thresholdS;

    public CloudletConfig(@Nonnull Cloud cloud, @Nonnull Distribution distribution,
                          @Nonnegative double mu1, @Nonnegative double mu2, @Nonnegative int thresholdN,
                          @Nonnull AccessControlFactory.Algorithm accessControlAlgorithm,
                          @Nonnull TaskInterruptFactory.Algorithm interruptTaskAlgorithm,
                          @Nonnegative int thresholdS) {
        Preconditions.checkNotNull(cloud, "Cloud can not be null (current: %s)", cloud);
        Preconditions.checkNotNull(distribution, "Distribution can not be null (current: %s)", distribution);
        Preconditions.checkArgument(mu1 >= 0, "Mu1 must be >= 0 (current: %s)", mu1);
        Preconditions.checkArgument(mu2 >= 0, "Mu2 must be >= 0 (current: %s)", mu2);
        Preconditions.checkArgument(thresholdN > 0, "ThresholdN must be > 0 (current: %s)", thresholdN);
        Preconditions.checkNotNull(accessControlAlgorithm, "AccessControlAlgorithm must be not null (current: %s)", accessControlAlgorithm);
        Preconditions.checkNotNull(interruptTaskAlgorithm, "InterruptTask must be not null (current: %s)", interruptTaskAlgorithm);
        Preconditions.checkArgument(thresholdS >= 0, "ThresholdS must be >= 0 (current: %s)", thresholdS);

        this.cloud = cloud;
        this.distribution = distribution;
        this.mu1 = mu1;
        this.mu2 = mu2;
        this.thresholdN = thresholdN;
        this.accessControlAlgorithm = accessControlAlgorithm;
        this.interruptTaskAlgorithm = interruptTaskAlgorithm;
        this.thresholdS = thresholdS;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public double getMu1() {
        return mu1;
    }

    public double getMu2() {
        return mu2;
    }

    public int getThresholdN() {
        return thresholdN;
    }

    public AccessControlFactory.Algorithm getAccessControlAlgorithm() {
        return accessControlAlgorithm;
    }

    public TaskInterruptFactory.Algorithm getInterruptTaskAlgorithm() {
        return interruptTaskAlgorithm;
    }

    public int getThresholdS() {
        return thresholdS;
    }

    public static class CloudletConfigBuilder {

        private final Cloud cloud;
        private final Distribution distribution;
        private final double mu1;
        private final double mu2;
        private final int thresholdN;
        private final AccessControlFactory.Algorithm accessControlAlgorithm;
        private final TaskInterruptFactory.Algorithm interruptTaskAlgorithm;

        private int thresholdS;

        public CloudletConfigBuilder(Cloud cloud, Distribution distribution, double mu1, double mu2, int thresholdN,
                                     AccessControlFactory.Algorithm accessControlAlgorithm, TaskInterruptFactory.Algorithm interruptTaskAlgorithm) {
            this.cloud = cloud;
            this.distribution = distribution;
            this.mu1 = mu1;
            this.mu2 = mu2;
            this.thresholdN = thresholdN;
            this.accessControlAlgorithm = accessControlAlgorithm;
            this.interruptTaskAlgorithm = interruptTaskAlgorithm;
        }

        public CloudletConfig build() {
            return new CloudletConfig(cloud, distribution, mu1, mu2,
                    thresholdN, accessControlAlgorithm, interruptTaskAlgorithm, thresholdS);
        }

        @FunctionalInterface
        public interface CloudBuilder {
            DistributionBuilder cloud(Cloud cloud);
        }

        @FunctionalInterface
        public interface DistributionBuilder {
            Mu1Builder distribution(Distribution distribution);
        }

        @FunctionalInterface
        public interface Mu1Builder {
            Mu2Builder mu1(double mu1);
        }

        @FunctionalInterface
        public interface Mu2Builder {
            ThresholdN mu2(double mu2);
        }

        @FunctionalInterface
        public interface ThresholdN {
            AcessControlAlgorithmBuilder thresholdN(int thresholdN);
        }

        @FunctionalInterface
        public interface AcessControlAlgorithmBuilder {
            InterruptTaskAlgorithmBuilder accessControlAlgorithm(AccessControlFactory.Algorithm accessControlAlgorithm);
        }

        @FunctionalInterface
        public interface InterruptTaskAlgorithmBuilder {
            CloudletConfigBuilder interruptTaskAlgorithm(TaskInterruptFactory.Algorithm interruptTaskAlgorithm);
        }

        public CloudletConfigBuilder thresholdS(int thresholdS) {
            this.thresholdS = thresholdS;
            return this;
        }

        public static CloudBuilder builder() {
            return cloud -> distribution -> mu1 -> mu2 -> thresholdN -> accessControlAlgorithm -> interruptTaskAlgorithm ->
                    new CloudletConfigBuilder(cloud, distribution, mu1, mu2, thresholdN, accessControlAlgorithm, interruptTaskAlgorithm);
        }

    }

}
