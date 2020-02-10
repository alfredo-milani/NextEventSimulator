package it.uniroma2.pmcsn.simulation.simulator.event;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.simulator.model.Context;
import it.uniroma2.pmcsn.simulation.simulator.statistics.Statistics;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;

import javax.annotation.Nonnull;

/**
 * Completion of most imminent {@link Task}'s offloading request on {@link Cloud}.
 */
public class CloudCompletionEvent implements IEvent {

    @Override
    public void simulate(@Nonnull Context context) {
        Preconditions.checkNotNull(context, "Context can not be null (current: %s)", context);
        Preconditions.checkNotNull(context.getClock().getNextEvent(), "Task can not be null (current: %s)", context.getClock().getNextEvent());

        Cloud cloud = context.getCloud();
        Task task = context.getClock().getNextEvent();
        Statistics statistics = context.getStatistics();

        cloud.result(task);

        // statistics update
        statistics.updateCloudCompletion(task);
    }

}
