package it.uniroma2.pmcsn.simulation.simulator;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetState;
import it.uniroma2.pmcsn.simulation.model.IState;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.simulator.event.*;
import it.uniroma2.pmcsn.simulation.simulator.model.Context;
import it.uniroma2.pmcsn.simulation.simulator.statistics.Statistics;
import it.uniroma2.pmcsn.simulation.system.cloud.model.CloudState;
import it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Event {

    private static final Logger LOG = LogManager.getLogger(Event.class.getCanonicalName());

    public enum Type {

        NULL,
        START,
        OFFLOAD_REQUEST,
        CLOUDLET_COMPLETION,
        CLOUD_COMPLETION,
        STOP

    }

    // Used to map event type with the respective class
    private final Map<Type, IEvent> events = new HashMap<>() {{
        put(Type.NULL, new NullEvent());
        put(Type.START, new StartEvent());
        put(Type.OFFLOAD_REQUEST, new OffloadRequestEvent());
        put(Type.CLOUDLET_COMPLETION, new CloudletCompletionEvent());
        put(Type.CLOUD_COMPLETION, new CloudCompletionEvent());
        put(Type.STOP, new StopEvent());
    }};
    // Current event type
    private Type eventType = Type.STOP;

    // State-machine context
    private final Context context;
    private final MobileSetState mobileSetState;
    private final CloudletState cloudletState;
    private final CloudState cloudState;

    private final Clock clock;

    private final Statistics statistics;

    public Event(@Nonnull Context context) {
        Preconditions.checkNotNull(context, "Context must be not null (current: %s)", context);

        this.context = context;
        this.mobileSetState = context.getMobileSet().getMobileSetState();
        this.cloudletState = context.getCloudlet().getCloudletState();
        this.cloudState = context.getCloud().getCloudState();

        this.clock = context.getClock();
        this.statistics = context.getStatistics();
    }

    public @Nonnull Type getEventType() {
        return eventType;
    }

    public @Nonnull Clock getClock() {
        return clock;
    }

    public @Nonnull Statistics getStatistics() {
        return statistics;
    }

    /**
     * Initialize {@link Event}.
     */
    public void init() {
        // schedule initial event
        next();
        // generate initial event
        simulate();
    }

    /**
     * Method used to choose next event based on {@link MobileSetState}, {@link CloudletState} and
     * {@link CloudState}.
     */
    public void next() {
        if (areSatesEmpty()) {
            eventType = eventType == Type.STOP ? Type.START : Type.STOP;
            return;
        }

        // retrieve most imminent event from all entities (mobile devices, cloudlet and cloud)
        Task mobileSetNextTask = getNextTaskFrom(mobileSetState);
        Task cloudletNextTask = getNextTaskFrom(cloudletState);
        Task cloudNextTask = getNextTaskFrom(cloudState);
        // set next event to schedule
        clock.update(mobileSetNextTask, cloudletNextTask, cloudNextTask);
        LOG.debug(clock.toString());

        Task nextTask = clock.getNextEvent();
        if (nextTask == null) {
            eventType = Type.NULL;
            throw new IllegalStateException(String.format("The next scheduled task is invalid (current: %s)", nextTask));
        }
        if (nextTask.equals(mobileSetNextTask)) {
            eventType = Type.OFFLOAD_REQUEST;
        } else if (nextTask.equals(cloudletNextTask)) {
            eventType = Type.CLOUDLET_COMPLETION;
        } else if (nextTask.equals(cloudNextTask)) {
            eventType = Type.CLOUD_COMPLETION;
        } else {
            eventType = Type.NULL;
            throw new IllegalStateException("NextTask in not null but not belongs to system or mobile set");
        }
    }

    private Task getNextTaskFrom(IState state) {
        if (state instanceof MobileSetState) {
            Task mobileSetTask = mobileSetState.getNextEvent();
            // if most imminent task generated from mobile set overcomes stop time simulation,
            //  mark it as null
            if (mobileSetTask != null && mobileSetTask.getArrival() > context.getStop()) {
                mobileSetState.remove(mobileSetTask);
                mobileSetTask = null;
            }
            return mobileSetTask;
        } else if (state instanceof CloudletState) {
            return cloudletState.getNextEvent();
        } else if (state instanceof CloudState) {
            return cloudState.getNextEvent();
        } else {
            throw new TypeNotPresentException(
                    state.getClass().getSimpleName(),
                    new Throwable("Impossible to get task from selected state")
            );
        }
    }

    /**
     * Trigger current event based on {@link Event#eventType}.
     */
    public void simulate() {
        LOG.debug(String.format("Simulating %s", eventType.name()));
        events.get(eventType).simulate(context);
    }

    /**
     * Update {@link Statistics}.
     */
    public void updateStatistics() {
        // statistics update
        statistics.updateArea(
                clock.getNextInstant() - clock.getPreviousInstant(),
                cloudletState.getSizeTasksClass1(),
                cloudletState.getSizeTasksClass2(),
                cloudState.getSizeTasksClass1(),
                cloudState.getSizeTasksClass2()
        );
        statistics.updateTime(clock.getNextInstant() - clock.getPreviousInstant());
    }

    private boolean areSatesEmpty() {
        return mobileSetState.isEmpty() && cloudletState.isEmpty() && cloudState.isEmpty();
    }

}
