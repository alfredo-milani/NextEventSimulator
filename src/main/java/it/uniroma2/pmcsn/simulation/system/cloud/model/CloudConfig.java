package it.uniroma2.pmcsn.simulation.system.cloud.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.util.Distribution;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class CloudConfig {

    private final Distribution distribution;
    private final double mu1;
    private final double mu2;

    private final double meanSetupTime;

    public CloudConfig(@Nonnull Distribution distribution, @Nonnegative double mu1,
                       @Nonnegative double mu2, @Nonnegative double meanSetupTime) {
        Preconditions.checkNotNull(distribution, "Distribution can not be null (current: %s)", distribution);
        Preconditions.checkArgument(mu1 >= 0, "Mu1 must be >= 0 (current: %s)", mu1);
        Preconditions.checkArgument(mu2 >= 0, "Mu2 must be >= 0 (current: %s)", mu2);
        Preconditions.checkArgument(meanSetupTime >= 0, "MeanSetupTime must be >= 0 (cuurent: %s)", meanSetupTime);

        this.distribution = distribution;
        this.mu1 = mu1;
        this.mu2 = mu2;
        this.meanSetupTime = meanSetupTime;
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

    public double getMeanSetupTime() {
        return meanSetupTime;
    }

    public static class CloudConfigBuilder {

        private final Distribution distribution;
        private final double mu1;
        private final double mu2;

        private double meanSetupTime;

        public CloudConfigBuilder(Distribution distribution, double mu1, double mu2) {
            this.distribution = distribution;
            this.mu1 = mu1;
            this.mu2 = mu2;
        }

        public CloudConfig build() {
            return new CloudConfig(distribution, mu1, mu2, meanSetupTime);
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
            CloudConfigBuilder mu2(double mu2);
        }

        public CloudConfigBuilder meanSetupTime(double meanSetupTime) {
            this.meanSetupTime = meanSetupTime;
            return this;
        }

        public static DistributionBuilder builder() {
            return distribution -> mu1 -> mu2 -> new CloudConfigBuilder(distribution, mu1, mu2);
        }

    }

}
