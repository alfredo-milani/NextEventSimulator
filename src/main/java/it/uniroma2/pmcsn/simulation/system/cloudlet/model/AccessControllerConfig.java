package it.uniroma2.pmcsn.simulation.system.cloudlet.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.IAccessControl;

import javax.annotation.Nonnull;

public final class AccessControllerConfig {

    private final IAccessControl algorithm;

    public AccessControllerConfig(@Nonnull IAccessControl algorithm) {
        Preconditions.checkNotNull(algorithm, "Algorithm can not be null (current: %s)", algorithm);

        this.algorithm = algorithm;
    }

    public IAccessControl getAlgorithm() {
        return algorithm;
    }

    public static class ControllerConfigBuilder {

        private final IAccessControl algorithm;

        public ControllerConfigBuilder(IAccessControl algorithm) {
            this.algorithm = algorithm;
        }

        public AccessControllerConfig build() {
            return new AccessControllerConfig(algorithm);
        }

        @FunctionalInterface
        public interface AlgorithmBuilder {
            ControllerConfigBuilder algorithm(IAccessControl algorithm);
        }

        public static AlgorithmBuilder builder() {
            return algorithm -> new ControllerConfigBuilder(algorithm);
        }

    }

}
