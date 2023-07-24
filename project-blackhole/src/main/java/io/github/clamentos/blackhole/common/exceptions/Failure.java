// OK
package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/** Simple wrapper around the {@link Failures} enum, in order to make them {@link Throwable}. */
public class Failure extends Throwable {
    
    private Failures error;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link Failure}.
     * @param error : The specific error constant from the {@link Failures} enum.
     * @throws IllegalArgumentException If {@code error} is {@code null}.
    */
    public Failure(Failures error) throws IllegalArgumentException {

        if(error == null) {

            throw new IllegalArgumentException();
        }

        this.error = error;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Simple getter for the actual error constant.
     * @return The never {@code null} {@link Failures} constant.
    */
    public Failures getError() {

        return(error);
    }

    //____________________________________________________________________________________________________________________________________
}
