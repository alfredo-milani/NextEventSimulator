package it.uniroma2.pmcsn.simulation.simulator.statistics;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class Statistics {

    public static final String H_TEMPLATE = "\n\t> %s";
    public static final String P_TEMPLATE = "\n\t\t- %s: %s [%s]";
    public static final char NL = '\n';
    public static final DecimalFormat DF = new DecimalFormat("###0.00000");
    public static final DecimalFormat PER = new DecimalFormat("###0.00");
    public static final double DEFAULT_VALUE_SAFE_DIVISION = 0.0;

    // Final simulation time
    protected double time;

    // Time-integrated class 1 tasks on cloudlet
    protected double tasksC1CletArea;
    // Time-integrated class 2 tasks on cloudlet
    protected double tasksC2CletArea;
    // Time-integrated class 1 tasks on cloud
    protected double tasksC1CloudArea;
    // Time-integrated class 2 tasks on cloud
    protected double tasksC2CloudArea;

    // Completion class 1 tasks on cloudlet
    protected long tasksC1CletCompletion;
    // Completion class 1 tasks on cloudlet
    protected long tasksC2CletCompletion;
    // Completion class 1 tasks on cloudlet
    protected long tasksC1CloudCompletion;
    // Completion class 1 tasks on cloudlet
    protected long tasksC2CloudCompletion;

    // Total class 2 interrupted tasks response time
    protected double tasksC2InterruptedResponseTime;
    // Number of class 2 interrupted tasks on cloudlet
    protected long tasksC2CletInterrupted;

    public Statistics() {
        resetStatistics();
    }

    public void resetStatistics() {
        time = 0.0;

        tasksC1CletArea = 0.0;
        tasksC2CletArea = 0.0;
        tasksC1CloudArea = 0.0;
        tasksC2CloudArea = 0.0;

        tasksC1CletCompletion = 0;
        tasksC2CletCompletion = 0;
        tasksC1CloudCompletion = 0;
        tasksC2CloudCompletion = 0;

        tasksC2InterruptedResponseTime = 0.0;
        tasksC2CletInterrupted = 0;
    }

    public void updateTime(@Nonnegative double increment) {
        Preconditions.checkArgument(increment >= 0, "Increment must be >= 0 (current: %s)", increment);

        time += increment;
    }

    public void updateArea(@Nonnegative double deltaTime, @Nonnegative long c1CletPopulation,
                           @Nonnegative long c2CletPopulation, @Nonnegative long c1CloudPopulation,
                           @Nonnegative long c2CloudPopulation) {
        Preconditions.checkArgument(deltaTime >= 0, "Increment must be >= 0 (current: %s)", deltaTime);
        Preconditions.checkArgument(c1CletPopulation >= 0, "C1CletPopulation must be >= 0 (current: %s)", c1CletPopulation);
        Preconditions.checkArgument(c2CletPopulation >= 0, "C2CletPopulation must be >= 0 (current: %s)", c2CletPopulation);
        Preconditions.checkArgument(c1CloudPopulation >= 0, "C1CloudPopulation must be >= 0 (current: %s)", c1CloudPopulation);
        Preconditions.checkArgument(c2CloudPopulation >= 0, "C2CloudPopulation must be >= 0 (current: %s)", c2CloudPopulation);

        updateC1CletArea(deltaTime, c1CletPopulation);
        updateC2CletArea(deltaTime, c2CletPopulation);
        updateC1CloudArea(deltaTime, c1CloudPopulation);
        updateC2CloudArea(deltaTime, c2CloudPopulation);
    }

    public void updateCletCompletion(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        Task.Class taskClass = task.getTaskClass();
        switch (task.getTaskClass()) {
            case CLASS1:
                this.tasksC1CletCompletion += 1;
                break;

            case CLASS2:
                this.tasksC2CletCompletion += 1;
                break;

            default:
                throw new TypeNotPresentException(
                    taskClass.name(),
                    new Throwable("Type not supported for statistical purpose")
            );
        }
    }

    public void updateCloudCompletion(@Nonnull Task task) {
        Preconditions.checkNotNull(task, "Task can not be null (current: %s)", task);

        Task.Class taskClass = task.getTaskClass();
        switch (taskClass) {
            case CLASS1:
                this.tasksC1CloudCompletion += 1;
                break;

            case CLASS2:
                this.tasksC2CloudCompletion += 1;
                break;

            default:
                throw new TypeNotPresentException(
                        taskClass.name(),
                        new Throwable("Type not supported for statistical purpose")
                );
        }
    }

    /**
     * Update {@link it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState} statistics.
     *
     * Param {@param deltaArea} is necessary to get effective class 2 tasks' statistics, so we subtract
     * interrupted task time-integrated.
     *
     * Param {@param deltaResponseTime} is necessary to get response time of class 2 interrupted tasks.
     *
     * @param deltaArea class 2 tasks time-integrated area to subtract from current
     *                  class 2 tasks area for completed tasks
     * @param deltaResponseTime response time of class 2 interrupted task
     */
    public void updateCletInterrupted(@Nonnegative double deltaArea, @Nonnegative double deltaResponseTime) {
        Preconditions.checkArgument(deltaArea >= 0, "DeltaArea must be >= 0 (current: %s)", deltaArea);
        Preconditions.checkArgument(deltaResponseTime >= 0, "DeltaResponseTime must be >= 0 (current: %s)", deltaResponseTime);

        tasksC2CletInterrupted += 1;
        tasksC2CletArea -= deltaArea;
        tasksC2InterruptedResponseTime += deltaResponseTime;
    }

    protected void updateC1CletArea(double deltaTime, long currentPopulation) {
        tasksC1CletArea += deltaTime * currentPopulation;
    }

    protected void updateC2CletArea(double deltaTime, long currentPopulation) {
        tasksC2CletArea += deltaTime * currentPopulation;
    }

    protected void updateC1CloudArea(double deltaTime, long currentPopulation) {
        tasksC1CloudArea += deltaTime * currentPopulation;
    }

    protected void updateC2CloudArea(double deltaTime, long currentPopulation) {
        tasksC2CloudArea += deltaTime * currentPopulation;
    }

    protected double getPercentageFrom(double fraction) {
        return fraction * 100;
    }

    protected double safeDivision(double numerator, double divider, double defaultValue) {
        return divider != 0 ? numerator / divider : defaultValue;
    }

    public long getSystemCompletion() {
        return tasksC1CletCompletion + tasksC2CletCompletion +
                tasksC1CloudCompletion + tasksC2CloudCompletion;
    }

    public double getSystemResponseTime() {
        return safeDivision(
                tasksC1CletArea + tasksC2CletArea + tasksC1CloudArea + tasksC2CloudArea,
                getSystemCompletion(),
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getSystemC1ResponseTime() {
        return safeDivision(
                tasksC1CletArea + tasksC1CloudArea,
                tasksC1CletCompletion + tasksC1CloudCompletion,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getSystemC2ResponseTime() {
        return safeDivision(
                tasksC2CletArea + tasksC2CloudArea,
                tasksC2CletCompletion + tasksC2CloudCompletion,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCletResponseTime() {
        return safeDivision(
                tasksC1CletArea + tasksC2CletArea,
                tasksC1CletCompletion + tasksC2CletCompletion,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCletC1ResponseTime() {
        return safeDivision(tasksC1CletArea, tasksC1CletCompletion, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCletC2ResponseTime() {
        return safeDivision(tasksC2CletArea, tasksC2CletCompletion, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudResponseTime() {
        return safeDivision(
                tasksC1CloudArea + tasksC2CloudArea,
                tasksC1CloudCompletion + tasksC2CloudCompletion,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCloudC1ResponseTime() {
        return safeDivision(tasksC1CloudArea, tasksC1CloudCompletion, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudC2ResponseTime() {
        return safeDivision(tasksC2CloudArea, tasksC2CloudCompletion, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getSystemThroughput() {
        return safeDivision(getSystemCompletion(), time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getSystemC1Throughput() {
        return safeDivision(
                tasksC1CletCompletion + tasksC1CloudCompletion,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getSystemC2Throughput() {
        return safeDivision(
                tasksC2CletCompletion + tasksC2CloudCompletion,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCletThroughput() {
        return getCletC1Throughput() + getCletC2Throughput();
    }

    public double getCletC1Throughput() {
        return safeDivision(tasksC1CletCompletion, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCletC2Throughput() {
        return safeDivision(tasksC2CletCompletion, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudThroughput() {
        return getCloudC1Throughput() + getCloudC2Throughput();
    }

    public double getCloudC1Throughput() {
        return safeDivision(tasksC1CloudCompletion, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudC2Throughput() {
        return safeDivision(tasksC2CloudCompletion, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getSystemPopulation() {
        return safeDivision(
                tasksC1CletArea + tasksC2CletArea + tasksC1CloudArea + tasksC2CloudArea,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getSystemC1Population() {
        return safeDivision(
                tasksC1CletArea + tasksC1CloudArea,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getSystemC2Population() {
        return safeDivision(
                tasksC2CletArea + tasksC2CloudArea,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCletPopulation() {
        return safeDivision(
                tasksC1CletArea + tasksC2CletArea,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCletC1Population() {
        return safeDivision(tasksC1CletArea, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCletC2Population() {
        return safeDivision(tasksC2CletArea, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudPopulation() {
        return safeDivision(
                tasksC1CloudArea + tasksC2CloudArea,
                time,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    public double getCloudC1Population() {
        return safeDivision(tasksC1CloudArea, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getCloudC2Population() {
        return safeDivision(tasksC2CloudArea, time, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getC2InterruptedResponseTime() {
        return safeDivision(tasksC2InterruptedResponseTime, tasksC2CletInterrupted, DEFAULT_VALUE_SAFE_DIVISION);
    }

    public double getC2InterruptedFraction() {
        return safeDivision(
                (double) tasksC2CletInterrupted,
                tasksC2CletCompletion + tasksC2CloudCompletion,
                DEFAULT_VALUE_SAFE_DIVISION
        );
    }

    protected String generateTitle(@Nonnull String title, @Nonnull String cchar,
                                int nCChar, int nSpaces, int initialSpaces) {
        final String frame = cchar.repeat(title.length() + 2 * (nCChar + nSpaces));
        return " ".repeat(initialSpaces) + frame +
                NL +
                " ".repeat(initialSpaces) +
                cchar.repeat(nCChar) + " ".repeat(nSpaces) + title + " ".repeat(nSpaces) + cchar.repeat(nCChar) +
                NL +
                " ".repeat(initialSpaces) + frame;
    }

    @Override
    public String toString()  {
        return generateTitle("Simulation statistics", "#", 5, 3, 0) +
                String.format(H_TEMPLATE, "Response time") +
                String.format(P_TEMPLATE, "Average global response time", DF.format(getSystemResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average global response time tasks class 1", DF.format(getSystemC1ResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average global response time tasks class 2", DF.format(getSystemC2ResponseTime()), "s") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet response time", DF.format(getCletResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average cloudlet response time tasks class 1", DF.format(getCletC1ResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average cloudlet response time tasks class 2", DF.format(getCletC2ResponseTime()), "s") +
                NL +
                String.format(P_TEMPLATE, "Average cloud response time", DF.format(getCloudResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average cloud response time tasks class 1", DF.format(getCloudC1ResponseTime()), "s") +
                String.format(P_TEMPLATE, "Average cloud response time tasks class 2", DF.format(getCloudC2ResponseTime()), "s") +
                NL +
                String.format(H_TEMPLATE, "Throughput") +
                String.format(P_TEMPLATE, "Average global throughput", DF.format(getSystemThroughput()), "task/s") +
                String.format(P_TEMPLATE, "Average global throughput tasks class 1", DF.format(getSystemC1Throughput()), "task/s") +
                String.format(P_TEMPLATE, "Average global throughput tasks class 2", DF.format(getSystemC2Throughput()), "task/s") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet throughput", DF.format(getCletThroughput()), "task/s") +
                String.format(P_TEMPLATE, "Average cloudlet throughput tasks class 1", DF.format(getCletC1Throughput()), "task/s") +
                String.format(P_TEMPLATE, "Average cloudlet throughput tasks class 2", DF.format(getCletC2Throughput()), "task/s") +
                NL +
                String.format(P_TEMPLATE, "Average cloud throughput", DF.format(getCloudThroughput()), "task/s") +
                String.format(P_TEMPLATE, "Average cloud throughput tasks class 1", DF.format(getCloudC1Throughput()), "task/s") +
                String.format(P_TEMPLATE, "Average cloud throughput tasks class 2", DF.format(getCloudC2Throughput()), "task/s") +
                NL +
                String.format(H_TEMPLATE, "Population") +
                String.format(P_TEMPLATE, "Average global population", DF.format(getSystemPopulation()), "task") +
                String.format(P_TEMPLATE, "Average global population tasks class 1", DF.format(getSystemC1Population()), "task") +
                String.format(P_TEMPLATE, "Average global population tasks class 2", DF.format(getSystemC2Population()), "task") +
                NL +
                String.format(P_TEMPLATE, "Average cloudlet population", DF.format(getCletPopulation()), "task") +
                String.format(P_TEMPLATE, "Average cloudlet population tasks class 1", DF.format(getCletC1Population()), "task") +
                String.format(P_TEMPLATE, "Average cloudlet population tasks class 2", DF.format(getCletC2Population()), "task") +
                NL +
                String.format(P_TEMPLATE, "Average cloud population", DF.format(getCloudPopulation()), "task") +
                String.format(P_TEMPLATE, "Average cloud population tasks class 1", DF.format(getCloudC1Population()), "task") +
                String.format(P_TEMPLATE, "Average cloud population tasks class 2", DF.format(getCloudC2Population()), "task") +
                NL +
                String.format(H_TEMPLATE, "Others") +
                String.format(P_TEMPLATE, "Total processed tasks", getSystemCompletion(), "task") +
                String.format(P_TEMPLATE, "Average response time interrupted tasks class 2", DF.format(getC2InterruptedResponseTime()), "s") +
                String.format(P_TEMPLATE, "Percentage of class 2 interrupted tasks", PER.format(getPercentageFrom(getC2InterruptedFraction())), "%");
    }

}
