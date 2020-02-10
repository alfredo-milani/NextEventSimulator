package it.uniroma2.pmcsn.simulation.util;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.util.random.Rngs;
import it.uniroma2.pmcsn.util.random.Rvgs;

import javax.annotation.Nonnegative;

public class Distribution {

    private final Rngs rngs;
    private final Rvgs rvgs;

    public Distribution() {
        this(0);
    }

    public Distribution(@Nonnegative long seed) {
        Preconditions.checkArgument(seed >= 0, "Rngs seed must be >= 0 (current: %s)", seed);
        if (seed > 0) {
            Preconditions.checkArgument(getDigitsOf(seed) <= 9, "Rngs seed must have at most 9 digits (current: %s)", seed);
        }

        this.rngs = new Rngs();
        this.rngs.plantSeeds(seed);
        this.rvgs = new Rvgs(this.rngs);
    }

    public Rngs getRngs() {
        return rngs;
    }

    public Rvgs getRvgs() {
        return rvgs;
    }

    public void selectRngStream(int stream) {
        rngs.selectStream(stream);
    }

    public double fromExponential(double value) {
        return fromExponential(0, value);
    }

    public double fromExponential(int stream, double value) {
        selectRngStream(stream);
        return rvgs.exponential(value);
    }

    public long fromBernoulli(double value) {
        return fromBernoulli(0, value);
    }

    public long fromBernoulli(int stream, double value) {
        selectRngStream(stream);
        return rvgs.bernoulli(value);
    }

    /**
     * Performance evaluation at https://www.baeldung.com/java-number-of-digits-in-int
     *
     * @param number to count digits
     * @return number of digits
     */
    public static int getDigitsOf(long number) {
        return (int) (Math.log10(number) + 1);
    }

}
