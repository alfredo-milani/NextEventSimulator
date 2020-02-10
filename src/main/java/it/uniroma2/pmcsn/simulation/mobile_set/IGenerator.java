package it.uniroma2.pmcsn.simulation.mobile_set;

import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface IGenerator {

    void generate(@Nonnull Task.Class taskClass);

    void generate(@Nonnull Task.Class taskClass, @Nonnegative double timeOffset);

    void receive(@Nonnull Task taskResult);

}
