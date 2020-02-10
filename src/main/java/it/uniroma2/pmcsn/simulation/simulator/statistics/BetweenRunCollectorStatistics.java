package it.uniroma2.pmcsn.simulation.simulator.statistics;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.SimulationConfig;
import it.uniroma2.pmcsn.simulation.simulator.Simulator;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class BetweenRunCollectorStatistics extends AbstractCollectorStatistics {

    public static final long DEFAULT_RUN = 100;

    private long counter = 0;
    private final long run;
    private final SimulationConfig simulationConfig;

    public BetweenRunCollectorStatistics(@Nonnull Object[] args) {
        this(DEFAULT_RUN, args);
    }

    public BetweenRunCollectorStatistics(long run, @Nonnull Object[] args) {
        Preconditions.checkNotNull(args, "SimulationConfig (args[0]) can not be null (current: %s)", args);

        this.run = run;
        this.simulationConfig = (SimulationConfig) args[0];
    }

    @Override
    public final void collect() {
        while (counter < run) {
            int seed = ThreadLocalRandom.current().nextInt(700000, 1000000000);
            simulationConfig.setSeed(seed);
            doCollect();
            ++counter;
        }
    }

    @Override
    protected void doCollect() {
        Simulator simulator = new Simulator(simulationConfig);
        simulator.start();
        System.out.println(String.format(
                "%s %s",
                counter + 1,
                simulator.getStatistics().getSystemResponseTime()
        ));
    }

}
