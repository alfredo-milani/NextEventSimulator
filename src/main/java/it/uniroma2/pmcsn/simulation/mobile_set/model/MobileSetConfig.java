package it.uniroma2.pmcsn.simulation.mobile_set.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.util.Distribution;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class MobileSetConfig {

    private final Distribution distribution;
    private final double lambda1;
    private final double lambda2;

    public MobileSetConfig(@Nonnull Distribution distribution, @Nonnegative double lambda1,
                           @Nonnegative double lambda2) {
        Preconditions.checkNotNull(distribution, "Distribution can not be null (current: %s)", distribution);
        Preconditions.checkArgument(lambda1 >= 0, "Lambda1 must be >= 0 (current: %s)", lambda1);
        Preconditions.checkArgument(lambda2 >= 0, "Lambda2 must be >= 0 (current: %s)", lambda2);

        this.distribution = distribution;
        this.lambda1 = lambda1;
        this.lambda2 = lambda2;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public double getLambda1() {
        return lambda1;
    }

    public double getLambda2() {
        return lambda2;
    }

    public static class MobileSetConfigBuilder {

        private final Distribution distribution;
        private final double lambda1;
        private final double lambda2;

        public MobileSetConfigBuilder(Distribution distribution, double lambda1, double lambda2) {
            this.distribution = distribution;
            this.lambda1 = lambda1;
            this.lambda2 = lambda2;
        }

        public MobileSetConfig build() {
            return new MobileSetConfig(distribution, lambda1, lambda2);
        }

        @FunctionalInterface
        public interface DistributionBuilder {
            Lambda1Builder distribution(Distribution distribution);
        }

        @FunctionalInterface
        public interface Lambda1Builder {
            Lambda2Builder lambda1(double lambda1);
        }

        @FunctionalInterface
        public interface Lambda2Builder {
            MobileSetConfigBuilder lambda2(double lambda2);
        }

        public static DistributionBuilder builder() {
            return distribution -> lambda1 -> lambda2 -> new MobileSetConfigBuilder(distribution, lambda1, lambda2);
        }

    }

}
