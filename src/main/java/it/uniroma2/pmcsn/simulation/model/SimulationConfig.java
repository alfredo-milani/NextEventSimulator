package it.uniroma2.pmcsn.simulation.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.AccessControlFactory;
import it.uniroma2.pmcsn.simulation.system.cloudlet.task_interrupt.TaskInterruptFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public final class SimulationConfig extends HashMap<String, Object> {

    // Properties filename
    public static final String CONFIGURATION_FILE = "config-filename";
    public static final String DEFAULT_CONFIGURATION_FILE = "config.properties";

    // Max number of tasks in the cloudlet (N)
    public static final String THRESHOLD_N = "N";
    public static final int DEFAULT_THRESHOLD_N = 20;

    // Threshold for class2 tasks on cloudlet (S)
    public static final String THRESHOLD_S = "S";
    public static final int DEFAULT_THRESHOLD_S = DEFAULT_THRESHOLD_N;

    // Initial simulation instant
    public static final String START = "start";
    public static final double DEFAULT_START = 0.0;

    // Terminal simulation instant
    public static final String STOP = "stop";
    public static final double DEFAULT_STOP = 100000.0;

    // Class1 tasks arrival rate in the cloudlet controller
    public static final String LAMBDA_1 = "lambda-1";
    public static final double DEFAULT_LAMBDA_1 = 6.0;

    // Class2 tasks arrival rate in the cloudlet controller
    public static final String LAMBDA_2 = "lambda-2";
    public static final double DEFAULT_LAMBDA_2 = 6.25;

    // Class1 tasks service rate in the cloudlet
    public static final String MU_1_CLOUDLET = "mu-1-cloudlet";
    public static final double DEFAULT_MU_1_CLOUDLET = 0.45;

    // Class2 tasks service rate in the cloudlet
    public static final String MU_2_CLOUDLET = "mu-2-cloudlet";
    public static final double DEFAULT_MU_2_CLOUDLET = 0.27;

    // Class1 tasks service rate in the cloud
    public static final String MU_1_CLOUD = "mu-1-cloud";
    public static final double DEFAULT_MU_1_CLOUD = 0.25;

    // Class2 tasks service rate in the cloud
    public static final String MU_2_CLOUD = "mu-2-cloud";
    public static final double DEFAULT_MU_2_CLOUD = 0.22;

    // Mean setup time for tasks sent to the cloud
    public static final String MEAN_SETUP_TIME = "mean-setup";
    public static final double DEFAULT_MEAN_SETUP_TIME = 0.8;

    // Cloudlet controller access control algorithm
    public static final String CLOUDLET_ACCESS_CONTROL_ALGORITHM = "cloudlet-ac";
    public static final String DEFAULT_CLOUDLET_ACCESS_CONTROL_ALGORITHM = AccessControlFactory.Algorithm.ALGORITHM1.name();

    // Cloudlet task interrupt algorithm
    public static final String CLOUDLET_TASK_INTERRUPT_ALGORITHM = "cloudlet-ti";
    public static final String DEFAULT_CLOUDLET_TASK_INTERRUPT_ALGORITHM = TaskInterruptFactory.Algorithm.MIN_ARRIVAL_TIME.name();

    // Seed for Rngs
    public static final String RNGS_SEED = "rngs-seed";
    public static final Long DEFAULT_RNGS_SEED = 123456789L;

    // Compute statistics using batch means
    public static final String BATCH_MEANS = "batch-means";
    public static final boolean DEFAULT_BATCH_MEANS = true;

    // Batch size
    public static final String BATCH_SIZE = "batch-size";
    public static final long DEFAULT_BATCH_SIZE = 10000;

    // Alpha value for level of confidence
    public static final String LEVEL_OF_CONFIDENCE = "loc";
    public static final double DEFAULT_LEVEL_OF_CONFIDENCE = 0.95;



    // Singleton with static initialization is thread-safe iff there is one
    //  JVM and one Class Loader
    private static class SingletonContainer {
        private final static SimulationConfig INSTANCE = new SimulationConfig();
    }

    protected SimulationConfig() {
        put(CONFIGURATION_FILE, DEFAULT_CONFIGURATION_FILE);

        put(THRESHOLD_N, DEFAULT_THRESHOLD_N);
        put(THRESHOLD_S, DEFAULT_THRESHOLD_S);
        put(START, DEFAULT_START);
        put(STOP, DEFAULT_STOP);
        put(LAMBDA_1, DEFAULT_LAMBDA_1);
        put(LAMBDA_2, DEFAULT_LAMBDA_2);
        put(MU_1_CLOUDLET, DEFAULT_MU_1_CLOUDLET);
        put(MU_2_CLOUDLET, DEFAULT_MU_2_CLOUDLET);
        put(MU_1_CLOUD, DEFAULT_MU_1_CLOUD);
        put(MU_2_CLOUD, DEFAULT_MU_2_CLOUD);
        put(MEAN_SETUP_TIME, DEFAULT_MEAN_SETUP_TIME);
        put(CLOUDLET_ACCESS_CONTROL_ALGORITHM, DEFAULT_CLOUDLET_ACCESS_CONTROL_ALGORITHM);
        put(CLOUDLET_TASK_INTERRUPT_ALGORITHM, DEFAULT_CLOUDLET_TASK_INTERRUPT_ALGORITHM);
        put(RNGS_SEED, DEFAULT_RNGS_SEED);
        put(BATCH_MEANS, DEFAULT_BATCH_MEANS);
        put(BATCH_SIZE, DEFAULT_BATCH_SIZE);
        put(LEVEL_OF_CONFIDENCE, DEFAULT_LEVEL_OF_CONFIDENCE);
    }

    public static SimulationConfig getInstance() {
        return SingletonContainer.INSTANCE;
    }

    public void load() throws IOException {
        InputStream configInputStream = SimulationConfig.class
                .getClassLoader()
                .getResourceAsStream(getConfigurationFilename());
        if (configInputStream == null) {
            throw new FileNotFoundException("Default configuration file not found");
        }

        load(configInputStream);
    }

    public void load(@Nonnull String configurationFilename) throws IOException {
        Preconditions.checkNotNull(
                configurationFilename,
                "ConfigurationFilename can not be null (current: %s)",
                configurationFilename
        );

        setConfigurationFilename(configurationFilename);
        load(new FileInputStream(configurationFilename));
    }

    public void load(@Nonnull InputStream configInputStream) throws IOException {
        Preconditions.checkNotNull(
                configInputStream,
                "ConfigInputStream can not be null (current: %s)",
                configInputStream
        );

        Properties properties = new Properties();

        try (BufferedInputStream configBufferedInputStream = new BufferedInputStream(configInputStream)) {
            properties.load(configBufferedInputStream);
        }

        putInteger(THRESHOLD_N, properties.get(THRESHOLD_N), null);
        putInteger(THRESHOLD_S, properties.get(THRESHOLD_S), getThresholdN());
        putDouble(START, properties.get(START), DEFAULT_START);
        putDouble(STOP, properties.get(STOP), DEFAULT_STOP);
        putDouble(LAMBDA_1, properties.get(LAMBDA_1), null);
        putDouble(LAMBDA_2, properties.get(LAMBDA_2), null);
        putDouble(MU_1_CLOUDLET, properties.get(MU_1_CLOUDLET), null);
        putDouble(MU_2_CLOUDLET, properties.get(MU_2_CLOUDLET), null);
        putDouble(MU_1_CLOUD, properties.get(MU_1_CLOUD), null);
        putDouble(MU_2_CLOUD, properties.get(MU_2_CLOUD), null);
        putDouble(MEAN_SETUP_TIME, properties.get(MEAN_SETUP_TIME), null);
        putString(CLOUDLET_ACCESS_CONTROL_ALGORITHM, properties.get(CLOUDLET_ACCESS_CONTROL_ALGORITHM), null);
        putString(CLOUDLET_TASK_INTERRUPT_ALGORITHM, properties.get(CLOUDLET_TASK_INTERRUPT_ALGORITHM), null);
        putLong(RNGS_SEED, properties.get(RNGS_SEED), DEFAULT_RNGS_SEED);
        putBoolean(BATCH_MEANS, properties.get(BATCH_MEANS), DEFAULT_BATCH_MEANS);
        putLong(BATCH_SIZE, properties.get(BATCH_SIZE), DEFAULT_BATCH_SIZE);
        putDouble(LEVEL_OF_CONFIDENCE, properties.get(LEVEL_OF_CONFIDENCE), DEFAULT_LEVEL_OF_CONFIDENCE);
    }

    private void putObject(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        if (value != null) {
            put(key, value);
        } else {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putString(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        if (value != null) {
            put(key, String.valueOf(value));
        } else {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putInteger(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        try {
            put(key, Integer.valueOf((String) value));
        } catch (NumberFormatException e) {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putLong(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        try {
            put(key, Long.valueOf((String) value));
        } catch (NumberFormatException e) {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putFloat(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        try {
            put(key, Integer.valueOf((String) value));
        } catch (NumberFormatException e) {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putDouble(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        try {
            put(key, Double.valueOf((String) value));
        } catch (NumberFormatException e) {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    private void putBoolean(@Nonnull String key, @Nullable Object value, @Nullable Object defaultValue) {
        if (value != null) {
            put(key, Boolean.valueOf((String) value));
        } else {
            if (defaultValue != null) {
                put(key, defaultValue);
            } else {
                throw new IllegalArgumentException(String.format("Missing value for key '%s'", key));
            }
        }
    }

    public void setConfigurationFilename(@Nonnull String configurationFile) {
        Preconditions.checkNotNull(
                configurationFile,
                "ConfigurationFile can not be null (current: %s)",
                configurationFile
        );

        put(CONFIGURATION_FILE, configurationFile);
    }

    public void setSeed(@Nonnegative long seed) {
        Preconditions.checkArgument(seed >= 0, "The seed must be >= 0 (current: %s)", seed);

        put(RNGS_SEED, seed);
    }

    public String getConfigurationFilename() {
        return (String) get(CONFIGURATION_FILE);
    }

    public int getThresholdN() {
        return (int) get(THRESHOLD_N);
    }

    public int getThresholdS() {
        return (int) get(THRESHOLD_S);
    }

    public double getStart() {
        return (double) get(START);
    }

    public double getStop() {
        return (double) get(STOP);
    }

    public double getLambda1() {
        return (double) get(LAMBDA_1);
    }

    public double getLambda2() {
        return (double) get(LAMBDA_2);
    }

    public double getMu1Cloudlet() {
        return (double) get(MU_1_CLOUDLET);
    }

    public double getMu2Cloudlet() {
        return (double) get(MU_2_CLOUDLET);
    }

    public double getMu1Cloud() {
        return (double) get(MU_1_CLOUD);
    }

    public double getMu2Cloud() {
        return (double) get(MU_2_CLOUD);
    }

    public double getMeanSetupTime() {
        return (double) get(MEAN_SETUP_TIME);
    }

    public String getCloudletAccessControlAlgorithm() {
        return (String) get(CLOUDLET_ACCESS_CONTROL_ALGORITHM);
    }

    public String getCloudletTaskInterruptAlgorithm() {
        return (String) get(CLOUDLET_TASK_INTERRUPT_ALGORITHM);
    }

    public long getSeed() {
        return (long) get(RNGS_SEED);
    }

    public boolean getBatchMeans() {
        return (boolean) get(BATCH_MEANS);
    }

    public long getBatchSize() {
        return (long) get(BATCH_SIZE);
    }

    public double getLevelOfConfidence() {
        return (double) get(LEVEL_OF_CONFIDENCE);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Properties for %s:", this.getClass().getSimpleName()));
        keySet().forEach(k -> stringBuilder.append(String.format("\n\t> %s = %s", k, get(k))));
        return stringBuilder.toString();
    }

}
