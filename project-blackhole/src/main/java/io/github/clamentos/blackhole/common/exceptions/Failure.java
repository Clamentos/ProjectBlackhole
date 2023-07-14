package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * Simple wrapper for the {@link Failures} enum.
*/
public class Failure extends Throwable {
    
    private Failures error;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link Failure}.
     * @param error : The specific error constant from the {@link Failures} enum.
     * @throws IllegalArgumentException If {@code error} is {@code null}.
    */
    public Failure(Failures error) throws IllegalArgumentException {

        if(error == null) throw new IllegalArgumentException();
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
