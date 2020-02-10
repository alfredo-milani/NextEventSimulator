package it.uniroma2.pmcsn.simulation.system;

import it.uniroma2.pmcsn.simulation.model.Task;

import javax.annotation.Nonnull;

public interface IExecutor {

    /**
     * Simulates the arrival of a task in the execution node.
     *
     * @param task to receive
     */
    void receive(@Nonnull Task task);

    /**
     * Simulates the arrival of a task in the execution node with preemption.
     * When a task is received, a task of class {@param preemptionClass}
     * will be interrupted.
     *
     * @param task to receive
     * @param preemptionClass of the task to interrupt
     */
    void receive(@Nonnull Task task, @Nonnull Task.Class preemptionClass);

    /**
     * Simulates task execution.
     *
     * @param task to execute
     */
    void execute(@Nonnull Task task);

    /**
     * Simulates the sending of the results obtained from the processing of a task.
     *
     * @param task from which to get results
     */
    void result(@Nonnull Task task);

}
