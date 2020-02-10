package it.uniroma2.pmcsn.simulation.simulator.event;

import it.uniroma2.pmcsn.simulation.simulator.model.Context;

import javax.annotation.Nonnull;

/**
 * The state machine associated should be in this state iif all {@link it.uniroma2.pmcsn.simulation.model.Task}s in
 * {@link it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetState} have arrival time greater than simulation
 * end time and both {@link it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState} and
 * {@link it.uniroma2.pmcsn.simulation.system.cloud.model.CloudState} are empty.
 */
public class StopEvent implements IEvent {

    @Override
    public void simulate(@Nonnull Context context) {

    }

}
