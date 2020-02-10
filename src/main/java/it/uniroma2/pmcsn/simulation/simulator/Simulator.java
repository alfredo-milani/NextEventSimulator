package it.uniroma2.pmcsn.simulation.simulator;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.MobileSet;
import it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetConfig;
import it.uniroma2.pmcsn.simulation.model.SimulationConfig;
import it.uniroma2.pmcsn.simulation.simulator.model.Context;
import it.uniroma2.pmcsn.simulation.simulator.statistics.BatchMeansStatistics;
import it.uniroma2.pmcsn.simulation.simulator.statistics.Statistics;
import it.uniroma2.pmcsn.simulation.simulator.statistics.WithinRunCollectorStatistics;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;
import it.uniroma2.pmcsn.simulation.system.cloud.model.CloudConfig;
import it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.AccessControlFactory;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletConfig;
import it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt.TaskInterruptFactory;
import it.uniroma2.pmcsn.simulation.util.Distribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

import static it.uniroma2.pmcsn.simulation.simulator.Event.Type.STOP;

public class Simulator {

    private static final Logger LOG = LogManager.getLogger(Simulator.class.getCanonicalName());
    private static final DecimalFormat progressFormat = new DecimalFormat("###0.00");

    private final SimulationConfig simulationConfig;
    private final Event event;
    private final Statistics statistics;

    public Simulator(SimulationConfig simulationConfig) {
        // Config validation
        configValidation(simulationConfig);
        this.simulationConfig = simulationConfig;
        this.statistics = simulationConfig.getBatchMeans() ?
                new BatchMeansStatistics(simulationConfig.getBatchSize(), simulationConfig.getLevelOfConfidence()) :
                new Statistics();
//                new WithinRunCollectorStatistics();

        Distribution distribution = new Distribution(simulationConfig.getSeed());

        MobileSetConfig mobileSetConfig = MobileSetConfig.MobileSetConfigBuilder.builder()
                .distribution(distribution)
                .lambda1(simulationConfig.getLambda1())
                .lambda2(simulationConfig.getLambda2())
                .build();
        MobileSet mobileSet = new MobileSet(mobileSetConfig);

        CloudConfig cloudConfig = CloudConfig.CloudConfigBuilder.builder()
                .distribution(distribution)
                .mu1(simulationConfig.getMu1Cloud())
                .mu2(simulationConfig.getMu2Cloud())
                .meanSetupTime(simulationConfig.getMeanSetupTime())
                .build();
        Cloud cloud = new Cloud(cloudConfig);

        CloudletConfig cloudletConfig = CloudletConfig.CloudletConfigBuilder.builder()
                .cloud(cloud)
                .distribution(distribution)
                .mu1(simulationConfig.getMu1Cloudlet())
                .mu2(simulationConfig.getMu2Cloudlet())
                .thresholdN(simulationConfig.getThresholdN())
                .accessControlAlgorithm(AccessControlFactory.Algorithm.from(simulationConfig.getCloudletAccessControlAlgorithm()))
                .interruptTaskAlgorithm(TaskInterruptFactory.Algorithm.from(simulationConfig.getCloudletTaskInterruptAlgorithm()))
                .thresholdS(simulationConfig.getThresholdS())
                .build();
        Cloudlet cloudlet = new Cloudlet(cloudletConfig);

        Context context = Context.ContextBuilder.builder()
                .statistics(statistics)
                .start(simulationConfig.getStart())
                .stop(simulationConfig.getStop())
                .mobileSet(mobileSet)
                .cloudlet(cloudlet)
                .cloud(cloud)
                .clock(new Clock())
                .build();
        this.event = new Event(context);
    }

    private void configValidation(@Nonnull SimulationConfig simulationConfig) {
        Preconditions.checkNotNull(simulationConfig, "SimulationConfig must be not null (current: %s)", simulationConfig);

        Preconditions.checkArgument(
                simulationConfig.getThresholdS() <= simulationConfig.getThresholdN(),
                "Threshold parameter S can not be greater of max number of accepted tasks in the cloudlet (current: %s)",
                simulationConfig.getThresholdS()
        );
        Preconditions.checkArgument(
                simulationConfig.getStart() >= 0 && simulationConfig.getStart() < Double.MAX_VALUE,
                "Start simulation time must be in range [%s, %s) (current: %s)",
                0,
                Double.MAX_VALUE,
                simulationConfig.getStart()
        );
        Preconditions.checkArgument(
                simulationConfig.getStop() >= 1 && simulationConfig.getStop() <= Double.MAX_VALUE,
                "Stop simulation time must be in range [%s, %s] (current: %s)",
                1,
                Double.MAX_VALUE,
                simulationConfig.getStop()
        );
        Preconditions.checkArgument(
                simulationConfig.getStart() < simulationConfig.getStop(),
                "Start simulation time must be less than stop simulation time (current: %s, %s)",
                simulationConfig.getStart(),
                simulationConfig.getStop()
        );
    }

    private void showProgress(double current, double total) {
        System.out.print(String.format(
                "Progress: %s %%\r",
                progressFormat.format(current >= total ? 100.00 : current / total * 100)
        ));
    }

    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Start the simulation.
     */
    public final void start() {
        // initialize event
        event.init();
        do {
            beforeEvent();

            // schedule next event
            event.next();
            // execute next event
            event.simulate();
            // update statistics
            event.updateStatistics();

            afterEvent();
        } while (event.getEventType() != STOP);
    }

    protected void beforeEvent() {

    }

    protected void afterEvent() {
        // show progress
        showProgress(event.getClock().getNextInstant(), simulationConfig.getStop());

        // to collect within-the-run statistics replace class Statistics with WithinRunCollectorStatistics,
        //  comment showProgress() method and uncomment following line
        // make sure to disable batch-means method (config.properties)
//        ((WithinRunCollectorStatistics) statistics).collect();
    }

}
