package it.uniroma2.pmcsn.simulation.system.cloudlet.access_control;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState;

import javax.annotation.Nonnull;

import static it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action.*;

class Algorithm2 implements IAccessControl {

    @Override
    public @Nonnull Action accessControl(@Nonnull Task offloadRequest, @Nonnull IState state) {
        Preconditions.checkNotNull(offloadRequest, "Task must be not null (current: %s)", offloadRequest);
        Preconditions.checkNotNull(state, "State must be not null (current: %s)", state);

        CloudletState cloudletState = (CloudletState) state;
        int N = cloudletState.getThresholdN();
        int S = cloudletState.getThresholdS();
        int n1 = cloudletState.getSizeTasksClass1();
        int n2 = cloudletState.getSizeTasksClass2();

        Task.Class taskClass = offloadRequest.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                if (n1 == N) {
                    return SEND_CLOUD;
                } else if (n1 + n2 < S) {
                    return SEND_CLOUDLET;
                } else if (n2 > 0) {
                    return SEND_CLOUDLET_PREEMPTION_CLASS2;
                } else {
                    return SEND_CLOUDLET;
                }

            case CLASS2:
                if (n1 + n2 >= S) {
                    return SEND_CLOUD;
                } else {
                    return SEND_CLOUDLET;
                }

            default:
                throw new TypeNotPresentException(
                        taskClass.name(),
                        new Throwable("Task class not yet supported")
                );
        }
    }

}
