package it.uniroma2.pmcsn.simulation.simulator.event;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.mobile_set.MobileSet;
import it.uniroma2.pmcsn.simulation.model.Task;
import it.uniroma2.pmcsn.simulation.simulator.model.Context;

import javax.annotation.Nonnull;

/**
 * Class representing initial {@link IEvent}.
 * The state machine should be in this state iif there are no {@link Task}s in
 * {@link it.uniroma2.pmcsn.simulation.mobile_set.model.MobileSetState},
 * {@link it.uniroma2.pmcsn.simulation.system.cloudlet.model.CloudletState} and
 * {@link it.uniroma2.pmcsn.simulation.system.cloud.model.CloudState}.
 */
public class StartEvent implements IEvent {

    @Override
    public void simulate(@Nonnull Context context) {
        Preconditions.checkNotNull(context, "Context can not be null (current: %s)", context);

        MobileSet mobileSet = context.getMobileSet();

        mobileSet.generate(Task.Class.CLASS1, context.getStart());
        mobileSet.generate(Task.Class.CLASS2, context.getStart());
    }

}
