package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Simple error wrapper class in order to make the {@link Error} enum extend {@link Throwable}.</p>
*/
public class ErrorWrapper extends Throwable {
    
    private Error error;

    //____________________________________________________________________________________________________________________________________

    public ErrorWrapper(Error error) {

        this.error = error;
    }

    //____________________________________________________________________________________________________________________________________

    public Error getError() {

        return(error);
    }

    //____________________________________________________________________________________________________________________________________
}
