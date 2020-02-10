package it.uniroma2.pmcsn.simulation.system.cloudlet;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.IAccessControl;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.AccessControllerConfig;

import javax.annotation.Nonnull;

class AccessController {

    private final AccessControllerConfig accessControllerConfig;
    private final IAccessControl accessControlAlgorithm;

    AccessController(@Nonnull AccessControllerConfig accessControllerConfig) {
        Preconditions.checkNotNull(
                accessControllerConfig,
                "AccessControllerConfig can not be null (current: %s)",
                accessControllerConfig
        );

        this.accessControllerConfig = accessControllerConfig;
        this.accessControlAlgorithm = accessControllerConfig.getAlgorithm();
    }

    public Action offloadOn(@Nonnull Task offloadRequest, @Nonnull IState state) {
        Preconditions.checkNotNull(offloadRequest, "OffloadRequest can not be null (current: %s)", offloadRequest);
        Preconditions.checkNotNull(state, "State can not be null (current: %s)", state);

        return accessControlAlgorithm.accessControl(offloadRequest, state);
    }

}