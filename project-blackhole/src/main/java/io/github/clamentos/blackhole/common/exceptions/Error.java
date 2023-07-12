package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * Simple wrapper for the {@link Errors} enum.
*/
public class Error extends Throwable {
    
    private Errors error;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link Error}.
     * @param error : The specific error constant from the {@link Errors} enum.
    */
    public Error(Errors error) {

        this.error = error;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Simple getter for the actual error constant.
     * @return The associated {@link Errors} constant.
    */
    public Errors getError() {

        return(error);
    }

    //____________________________________________________________________________________________________________________________________
}
