package it.uniroma2.pmcsn.simulation.system.cloudlet.access_control;

import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnull;

public interface IAccessControl {

    @Nonnull Action accessControl(@Nonnull Task offloadRequest, @Nonnull IState state);

}
