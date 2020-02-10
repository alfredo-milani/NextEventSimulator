package it.uniroma2.pmcsn.simulation.simulator.statistics;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.util.random.Rvms;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BatchMeansStatistics extends Statistics {

    public static final String P_TEMPLATE = "\n\t\t- %s: %s +/- %s [%s]";

    public static final long BATCH_SIZE = 10000;
    public static final double LEVEL_OF_CONFIDENCE = 0.95;

    // Library to evaluate pdf, cdf and idf for a variety of discrete and
    //  continuous random variables
    private final Rvms rvms;
    // Batch size
    private final long batchSize;
    // Alpha value for level of confidence
    private final double levelOfConfidence;

    // Batch counter
    private long batchCounter = 1;

    // Batch values
    private final List<Double> systemResponseTime = new ArrayList<>();
    private final List<Double> systemC1ResponseTime = new ArrayList<>();
    private final List<Double> systemC2ResponseTime = new ArrayList<>();
    private final List<Double> cletResponseTime = new ArrayList<>();
    private final List<Double> cletC1ResponseTime = new ArrayList<>();
    private final List<Double> cletC2ResponseTime = new ArrayList<>();
    private final List<Double> cloudResponseTime = new ArrayList<>();
    private final List<Double> cloudC1ResponseTime = new ArrayList<>();
    private final List<Double> cloudC2ResponseTime = new ArrayList<>();

    private final List<Double> systemThroughput = new ArrayList<>();
    private final List<Double> systemC1Throughput = new ArrayList<>();
    private final List<Double> systemC2Throughput = new ArrayList<>();
    private final List<Double> cletThroughput = new ArrayList<>();
    private final List<Double> cletC1Throughput = new ArrayList<>();
    private final List<Double> cletC2Throughput = new ArrayList<>();
    private final List<Double> cloudThroughput = new ArrayList<>();
    private final List<Double> cloudC1Throughput = new ArrayList<>();
    private final List<Double> cloudC2Throughput = new ArrayList<>();

    private final List<Double> systemPopulation = new ArrayList<>();
    private final List<Double> systemC1Population = new ArrayList<>();
    private final List<Double> systemC2Population = new ArrayList<>();
    private final List<Double> cletPopulation = new ArrayList<>();
    private final List<Double> cletC1Population = new ArrayList<>();
    private final List<Double> cletC2Population = new ArrayList<>();
    private final List<Double> cloudPopulation = new ArrayList<>();
    private final List<Double> cloudC1Population = new ArrayList<>();
    private final List<Double> cloudC2Population = new ArrayList<>();

    private final List<Double> c2InterruptedResponseTime = new ArrayList<>();
    private final List<Double> c2InterruptedFraction = new ArrayList<>();

    public BatchMeansStatistics() {
        this(BATCH_SIZE, LEVEL_OF_CONFIDENCE);
    }

    public BatchMeansStatistics(long batchSize, double levelOfConfidence) {
        this(batchSize, levelOfConfidence, new Rvms());
    }

    public BatchMeansStatistics(long batchSize, double levelOfConfidence, Rvms rvms) {
        super();

        Preconditions.checkArgument(batchSize > 0, "BatchSize must be > 0 (current: %s)", batchSize);
        Preconditions.checkArgument(
                levelOfConfidence > 0.0 && levelOfConfidence < 1.0,
                "LevelOfConfidence must be in range (0.0, 1.0) (current: %s)",
                levelOfConfidence
        );

        this.batchSize = batchSize;
        this.levelOfConfidence = levelOfConfidence;
        this.rvms = rvms;
    }

    @Override
    public void updateTime(@Nonnegative double increment) {
        super.updateTime(increment);

        // update batchCounter and check if the maximum threshold
        //   for batch means processing has been reached
        // note that, if we have the sequence: x1, x2, ..., xn then,
        //   the number of batches is k = Math.floor(n / b) and if b
        //   is not a divisor of n, last n mod b data points will not be considered
        if (batchCounter++ == batchSize) {
            // reset batchCounter
            batchCounter = 1;
            // compute statistics for current batch mean
            addBatch();
            // reset variables in order to compute new batch mean
            resetStatistics();
        }
    }

    private void addBatch() {
        // Update system response time
        systemResponseTime.add(getSystemResponseTime());
        systemC1ResponseTime.add(getSystemC1ResponseTime());
        systemC2ResponseTime.add(getSystemC2ResponseTime());

        // Update cloudlet response time
        cletResponseTime.add(getCletResponseTime());
        cletC1ResponseTime.add(getCletC1ResponseTime());
        cletC2ResponseTime.add(getCletC2ResponseTime());

        // Update cloud response time
        cloudResponseTime.add(getCloudResponseTime());
        cloudC1ResponseTime.add(getCloudC1ResponseTime());
        cloudC2ResponseTime.add(getCloudC2ResponseTime());

        // Update system throughput
        systemThroughput.add(getSystemThroughput());
        systemC1Throughput.add(getSystemC1Throughput());
        systemC2Throughput.add(getSystemC2Throughput());

        // Update cloudlet throughput
        cletThroughput.add(getCletThroughput());
        cletC1Throughput.add(getCletC1Throughput());
        cletC2Throughput.add(getCletC2Throughput());

        // Update cloud throughput
        cloudThroughput.add(getCloudThroughput());
        cloudC1Throughput.add(getCloudC1Throughput());
        cloudC2Throughput.add(getCloudC2Throughput());

        // Update system population
        systemPopulation.add(getSystemPopulation());
        systemC1Population.add(getSystemC1Population());
        systemC2Population.add(getSystemC2Population());

        // Update cloudlet population
        cletPopulation.add(getCletPopulation());
        cletC1Population.add(getCletC1Population());
        cletC2Population.add(getCletC2Population());

        // Update cloud population
        cloudPopulation.add(getCloudPopulation());
        cloudC1Population.add(getCloudC1Population());
        cloudC2Population.add(getCloudC2Population());

        // Update class 2 interrupted tasks response time
        c2InterruptedResponseTime.add(getC2InterruptedResponseTime());
        // Update percentage interrupted class 2 tasks
        c2InterruptedFraction.add(getC2InterruptedFraction());
    }

    private @Nonnull double[] computeBatchMeansFrom(@Nonnull List<Double> batchList) {
        Preconditions.checkNotNull(batchList, "BatchList can not be null (current: %s)", batchList);

        // Calculate the mean and the standard deviation of all the batch means
        // One pass Welford algorithm is used
        double mean = 0.0;
        double standardDeviation;
        double difference;
        double sum = 0.0;
        int index = 0;

        for (Double value : batchList) {
            ++index;
            difference = value - mean;
            sum += difference * difference * (index - 1.0) / index;
            mean += difference / index;
        }
        standardDeviation = Math.sqrt(sum / index);

        // level of confidence obtained from configuration file

        // compute critical value and the interval endpoints
        // interval parameter: 1 - 1/2 * alpha, with alpha = 1.0 - levelOfConfidence
        double u = 1.0 - 1.0 / 2.0 * (1.0 - levelOfConfidence);
        // compute critical value of T
        double criticalValue = rvms.idfStudent(batchList.size() - 1, u);
        // interval half width
        double intervalWidth = criticalValue * standardDeviation / Math.sqrt(batchList.size() - 1);

        return new double[]{mean, intervalWidth};
    }

    @Override
    public String toString() {
        double[] systemResponseTime = computeBatchMeansFrom(this.systemResponseTime);
        double[] systemC1ResponseTime = computeBatchMeansFrom(this.systemC1ResponseTime);
        double[] systemC2ResponseTime = computeBatchMeansFrom(this.systemC2ResponseTime);
        double[] cletResponseTime = computeBatchMeansFrom(this.cletResponseTime);
        double[] cletC1ResponseTime = computeBatchMeansFrom(this.cletC1ResponseTime);
        double[] cletC2ResponseTime = computeBatchMeansFrom(this.cletC2ResponseTime);
        double[] cloudResponseTime = computeBatchMeansFrom(this.cloudResponseTime);
        double[] cloudC1ResponseTime = computeBatchMeansFrom(this.cloudC1ResponseTime);
        double[] cloudC2ResponseTime = computeBatchMeansFrom(this.cloudC2ResponseTime);

        double[] systemThroughput = computeBatchMeansFrom(this.systemThroughput);
        double[] systemC1Throughput = computeBatchMeansFrom(this.systemC1Throughput);
        double[] systemC2Throughput = computeBatchMeansFrom(this.systemC2Throughput);
        double[] cletThroughput = computeBatchMeansFrom(this.cletThroughput);
        double[] cletC1Throughput = computeBatchMeansFrom(this.cletC1Throughput);
        double[] cletC2Throughput = computeBatchMeansFrom(this.cletC2Throughput);
        double[] cloudThroughput = computeBatchMeansFrom(this.cloudThroughput);
        double[] cloudC1Throughput = computeBatchMeansFrom(this.cloudC1Throughput);
        double[] cloudC2Throughput = computeBatchMeansFrom(this.cloudC2Throughput);

        double[] systemPopulation = computeBatchMeansFrom(this.systemPopulation);
        double[] systemC1Population = computeBatchMeansFrom(this.systemC1Population);
        double[] systemC2Population = computeBatchMeansFrom(this.systemC2Population);
        double[] cletPopulation = computeBatchMeansFrom(this.cletPopulation);
        double[] cletC1Population = computeBatchMeansFrom(this.cletC1Population);
        double[] cletC2Population = computeBatchMeansFrom(this.cletC2Population);
        double[] cloudPopulation = computeBatchMeansFrom(this.cloudPopulation);
        double[] cloudC1Population = computeBatchMeansFrom(this.cloudC1Population);
        double[] cloudC2Population = computeBatchMeansFrom(this.cloudC2Population);

        double[] c2InterruptedResponseTime = computeBatchMeansFrom(this.c2InterruptedResponseTime);
        double[] c2InterruptedPercentage = computeBatchMeansFrom(this.c2InterruptedFraction);

        return generateTitle("Simulation statistics", "#", 5, 3, 0) +
                String.format(H_TEMPLATE, "Values obtained using batch means method with level of confidence of " + PER.format(levelOfConfidence * 100) + " [%]") +
                NL +
                String.format(H_TEMPLATE, "Response time") +
                String.format(P_TEMPLATE, "Average global response time", DF.format(systemResponseTime[0]), DF.format(systemResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average global response time tasks class 1", DF.format(systemC1ResponseTime[0]), DF.format(systemC1ResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average global response time tasks class 2", DF.format(systemC2ResponseTime[0]), DF.format(systemC2ResponseTime[1]), "s") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet response time", DF.format(cletResponseTime[0]), DF.format(cletResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average cloudlet response time tasks class 1", DF.format(cletC1ResponseTime[0]), DF.format(cletC1ResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average cloudlet response time tasks class 2", DF.format(cletC2ResponseTime[0]), DF.format(cletC2ResponseTime[1]), "s") +
                NL +
                String.format(P_TEMPLATE, "Average cloud response time", DF.format(cloudResponseTime[0]), DF.format(cloudResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average cloud response time tasks class 1", DF.format(cloudC1ResponseTime[0]), DF.format(cloudC1ResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Average cloud response time tasks class 2", DF.format(cloudC2ResponseTime[0]), DF.format(cloudC2ResponseTime[1]), "s") +
                NL +
                String.format(H_TEMPLATE, "Throughput") +
                String.format(P_TEMPLATE, "Average global throughput", DF.format(systemThroughput[0]), DF.format(systemThroughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average global throughput tasks class 1", DF.format(systemC1Throughput[0]), DF.format(systemC1Throughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average global throughput tasks class 2", DF.format(systemC2Throughput[0]), DF.format(systemC2Throughput[1]), "task/s") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet throughput", DF.format(cletThroughput[0]), DF.format(cletThroughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average cloudlet throughput tasks class 1", DF.format(cletC1Throughput[0]), DF.format(cletC1Throughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average cloudlet throughput tasks class 2", DF.format(cletC2Throughput[0]), DF.format(cletC2Throughput[1]), "task/s") +
                NL +
                String.format(P_TEMPLATE, "Average cloud throughput", DF.format(cloudThroughput[0]), DF.format(cloudThroughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average cloud throughput tasks class 1", DF.format(cloudC1Throughput[0]), DF.format(cloudC1Throughput[1]), "task/s") +
                String.format(P_TEMPLATE, "Average cloud throughput tasks class 2", DF.format(cloudC2Throughput[0]), DF.format(cloudC2Throughput[1]), "task/s") +
                NL +
                String.format(H_TEMPLATE, "Population") +
                String.format(P_TEMPLATE, "Average global population", DF.format(systemPopulation[0]), DF.format(systemPopulation[1]), "task") +
                String.format(P_TEMPLATE, "Average global population tasks class 1", DF.format(systemC1Population[0]), DF.format(systemC1Population[1]), "task") +
                String.format(P_TEMPLATE, "Average global population tasks class 2", DF.format(systemC2Population[0]), DF.format(systemC2Population[1]), "task") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet population", DF.format(cletPopulation[0]), DF.format(cletPopulation[1]), "task") +
                String.format(P_TEMPLATE, "Average cloudlet population tasks class 1", DF.format(cletC1Population[0]), DF.format(cletC1Population[1]), "task") +
                String.format(P_TEMPLATE, "Average cloudlet population tasks class 2", DF.format(cletC2Population[0]), DF.format(cletC2Population[1]), "task") +
                NL +
                String.format(P_TEMPLATE, "Average cloud population", DF.format(cloudPopulation[0]), DF.format(cloudPopulation[1]), "task") +
                String.format(P_TEMPLATE, "Average cloud population tasks class 1", DF.format(cloudC1Population[0]), DF.format(cloudC1Population[1]), "task") +
                String.format(P_TEMPLATE, "Average cloud population tasks class 2", DF.format(cloudC2Population[0]), DF.format(cloudC2Population[1]), "task") +
                NL +
                String.format(H_TEMPLATE, "Others") +
                String.format(P_TEMPLATE, "Average response time interrupted tasks class 2", DF.format(c2InterruptedResponseTime[0]), DF.format(c2InterruptedResponseTime[1]), "s") +
                String.format(P_TEMPLATE, "Percentage of class 2 interrupted tasks", PER.format(getPercentageFrom(c2InterruptedPercentage[0])), PER.format(getPercentageFrom(c2InterruptedPercentage[1])), "%");
    }

}
