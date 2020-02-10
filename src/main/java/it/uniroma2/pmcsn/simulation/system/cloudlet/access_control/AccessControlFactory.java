package it.uniroma2.pmcsn.simulation.system.cloudlet.access_control;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class AccessControlFactory {

    public enum Algorithm {

        ALGORITHM1,
        ALGORITHM2;

        public static @Nonnull Algorithm from(@Nonnull String algorithm) {
            for (Algorithm a : Algorithm.values()) {
                if (algorithm.equalsIgnoreCase(a.name())) {
                    return a;
                }
            }
            throw new TypeNotPresentException(algorithm, new Throwable("Selected algorithm not yet implemented"));
        }

    }

    public static @Nonnull IAccessControl get(@Nonnull String algorithm) {
        Preconditions.checkNotNull(algorithm, "Algorithm can not be null");

        return get(Algorithm.from(algorithm));
    }

    public static @Nonnull IAccessControl get(@Nonnull Algorithm algorithm) {
        Preconditions.checkNotNull(algorithm, "Algorithm can not be null");

        switch (algorithm) {
            case ALGORITHM1:
                return new Algorithm1();

            case ALGORITHM2:
                return new Algorithm2();

            default:
                throw new TypeNotPresentException(algorithm.name(), new Throwable("Algorithm not yet implemented"));
        }
    }

}
