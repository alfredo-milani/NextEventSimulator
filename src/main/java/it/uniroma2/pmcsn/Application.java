package it.uniroma2.pmcsn;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.SimulationConfig;
import it.uniroma2.pmcsn.simulation.simulator.Simulator;
import it.uniroma2.pmcsn.simulation.simulator.statistics.BetweenRunCollectorStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Application {

    private final static Logger LOG = LogManager.getRootLogger();
    private final static SimulationConfig SIMULATION_CONFIG = SimulationConfig.getInstance();

    private static void loadConfigFrom(@Nonnull String[] args) {
        Preconditions.checkNotNull(args, "No args received (current: %s)", (Object) args);

        try {
            if (args.length != 0) SIMULATION_CONFIG.load(args[0]);
            else SIMULATION_CONFIG.load();
        } catch (IllegalArgumentException e) {
            LOG.fatal("Error reading configuration file: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            LOG.fatal("Error opening configuration file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            LOG.fatal("Unknown error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        LOG.debug("Loading configuration");
        loadConfigFrom(args);
        LOG.debug('\n' + SIMULATION_CONFIG.toString());

        Simulator simulator = new Simulator(SIMULATION_CONFIG);
        LOG.info("*START* simulation, please wait");
        simulator.start();
        LOG.info("*STOP* simulation");
        LOG.info('\n' + simulator.getStatistics().toString());

        // to collect between-the-run statistics uncomment following line
        //  and comment previous ones
//        new BetweenRunCollectorStatistics(100, new Object[] {SIMULATION_CONFIG}).collect();
    }

}
