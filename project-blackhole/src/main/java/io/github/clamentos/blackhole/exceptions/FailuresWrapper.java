package io.github.clamentos.blackhole.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Throwable failure</h3>
 * This class is a simple wrapper for the {@link Failures} enum, in order to make it usable in standard
 * language exceptions.
 * @apiNote This class is a <b>wrapper</b>.
*/
public class FailuresWrapper extends Throwable {
    
    private Failures failure;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new {@link FailuresWrapper} object.
     * @param failure : The failure reason.
     * @see {@link Failures}
    */
    public FailuresWrapper(Failures failure) {

        this.failure = failure;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * @return The underlying failure reason.
     * @see {@link Failures}
    */
    public Failures getFailure() {

        return(failure);
    }

    //____________________________________________________________________________________________________________________________________
}
