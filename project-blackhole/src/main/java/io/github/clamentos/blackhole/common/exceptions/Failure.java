package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Regular class.</b></p>
 * Simple wrapper around the {@link Failures} enum, in order to make it {@link Throwable}.
*/
public class Failure extends Throwable {

    private Failures error;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link Failure} object.
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
     * @return The never {@code null} {@link Failures} constant.
    */
    public Failures getError() {

        return(error);
    }

    //____________________________________________________________________________________________________________________________________
}
