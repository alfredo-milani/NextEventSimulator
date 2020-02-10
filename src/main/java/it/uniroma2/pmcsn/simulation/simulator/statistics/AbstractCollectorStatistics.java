package it.uniroma2.pmcsn.simulation.simulator.statistics;

public abstract class AbstractCollectorStatistics extends Statistics {

    public abstract void collect();

    protected abstract void doCollect();

}
