package it.uniroma2.pmcsn.simulation.system.cloudlet.access_control;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState;

import javax.annotation.Nonnull;

import static it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action.SEND_CLOUD;
import static it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action.SEND_CLOUDLET;

class Algorithm1 implements IAccessControl {

    @Override
    public @Nonnull Action accessControl(@Nonnull Task offloadRequest, @Nonnull IState state) {
        Preconditions.checkNotNull(offloadRequest, "Task must be not null (current: %s)", offloadRequest);
        Preconditions.checkNotNull(state, "State must be not null (current: %s)", state);

        CloudletState cloudletState = (CloudletState) state;
        int N = cloudletState.getThresholdN();
        int n1 = cloudletState.getSizeTasksClass1();
        int n2 = cloudletState.getSizeTasksClass2();

        if (n1 + n2 == N) {
            return SEND_CLOUD;
        } else {
            return SEND_CLOUDLET;
        }
    }

}
