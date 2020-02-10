package it.uniroma2.pmcsn.simulation.simulator.event;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.MobileSet;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.simulator.model.Context;
import it.uniroma2.pmcsn.simulation.simulator.statistics.Statistics;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;
import it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet;
import it.uniroma2.pmcsn.simulation.system.cloudlet.access_control.Action;

import javax.annotation.Nonnull;

/**
 * Represent a {@link Task}'s offloading request arriving to the system (consisting of a
 * {@link Cloudlet} component and a {@link Cloud} component).
 * The request may be processed on the cloudlet or the cloud based on
 * {@link it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState}.
 * The {@link Task} associated with the offloading request, is received, executed
 * and associated with a completion time {@link Task#setCompletion(double)} based on
 * service time on the execution layer.
 */
public class OffloadRequestEvent implements IEvent {

    @Override
    public void simulate(@Nonnull Context context) {
        Preconditions.checkNotNull(context, "Context can not be null (current: %s)", context);
        Preconditions.checkState(
                context.getClock().getNextEvent() != null,
                "Processing offloading request of a null task (current: %s)",
                context.getClock().getNextEvent()
        );

        MobileSet mobileSet = context.getMobileSet();
        Cloudlet cloudlet = context.getCloudlet();
        Cloud cloud = context.getCloud();
        Task task = context.getClock().getNextEvent();
        Statistics statistics = context.getStatistics();

        // simulate task arrival on cloudlet or cloud
        Action action = cloudlet.accept(task);
        switch (action) {
            case SEND_CLOUDLET:
                // simulate task execution on cloudlet (completion time is assigned to task)
                cloudlet.execute(task);
                break;

            case SEND_CLOUDLET_PREEMPTION_CLASS2:
                Task taskInterruptedClass2 = cloud.getCloudState().getNextInterruptedClass2();
                Preconditions.checkState(taskInterruptedClass2 != null, "No interrupted class 2 task found to execute");

                double taskInterruptedClass2Arrival = taskInterruptedClass2.getArrival();
                // set arrival of interrupted class 2 task to current time instant
                //  because the cloud executor, to compute cloud completion time, need to know
                //  current time instant (so need to know arrival time in the cloud)
                taskInterruptedClass2.setArrival(task.getArrival());

                // simulate task execution on cloudlet (completion time is assigned to task)
                cloudlet.execute(task);
                // simulate task execution on cloud (completion time is assigned to task)
                cloud.execute(taskInterruptedClass2);

                // statistics update
                statistics.updateCletInterrupted(
                        task.getArrival() - taskInterruptedClass2Arrival,
                        taskInterruptedClass2.getCompletion() - taskInterruptedClass2Arrival
                );
                break;

            case SEND_CLOUD:
                // simulate task execution on cloud (completion time is assigned to task)
                cloud.execute(task);
                break;

            default:
                throw new TypeNotPresentException(
                        action.name(),
                        new Throwable("Impossible simulate offload request event for this execution layer")
                );
        }

        // for the simulation purpose it is assumed that the task is completed, so remove it from mobile set
        mobileSet.receive(task);
        // generate new task of the same class as the one just processed
        mobileSet.generate(task.getTaskClass(), task.getArrival());
    }

}
