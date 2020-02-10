package it.uniroma2.pmcsn.simulation.simulator.event;

import it.uniroma2.pmcsn.simulation.simulator.model.Context;

import javax.annotation.Nonnull;

public interface IEvent {

    void simulate(@Nonnull Context context);

}
