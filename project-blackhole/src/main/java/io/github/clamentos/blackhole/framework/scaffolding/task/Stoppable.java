package io.github.clamentos.blackhole.framework.scaffolding.task;

///
/**
 * <h3>Stoppable</h3>
 * Specifies that the implementing class is a runnable that can be stopped.
*/
public interface Stoppable extends Runnable {

    ///
    /**
     * Signals {@code this} runnable to start the halting procedure.
     * @apiNote It's not guaranteed that {@code this} runnable will be halted before the termination of this method.
     * Such situation can be possible for continuous / batch type tasks where they normally check for the stopped flag only at the beginning
     * of an endless loop. In short, the effects of this method should be considered <b>eventually consistent</b>.
    */
    void stop();

    ///
}
