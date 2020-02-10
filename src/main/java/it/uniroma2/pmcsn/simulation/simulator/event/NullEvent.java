package it.uniroma2.pmcsn.simulation.simulator.event;

import it.uniroma2.pmcsn.simulation.simulator.model.Context;

import javax.annotation.Nonnull;

/**
 * Class representing null {@link IEvent}.
 */
public class NullEvent implements IEvent {

    @Override
    public void simulate(@Nonnull Context context) {

    }

}
