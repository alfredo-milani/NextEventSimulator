package it.uniroma2.pmcsn.simulation.simulator.statistics;

public class WithinRunCollectorStatistics extends AbstractCollectorStatistics {

    public static final long DEFAULT_SIZE = 100;

    private long counter = 0;
    private final long size;

    public WithinRunCollectorStatistics() {
        this(DEFAULT_SIZE);
    }

    public WithinRunCollectorStatistics(long size) {
        this.size = size;
    }

    @Override
    public final void collect() {
        if (Math.floor(time / size) > counter) {
            ++counter;
            doCollect();
        }
    }

    @Override
    protected void doCollect() {
        System.out.println(String.format(
                "%s %s",
                getSystemResponseTime(),
                getSystemThroughput()
        ));
    }

}
