package io.github.clamentos.blackhole.common.exceptions;

public class Error extends Throwable {
    
    private Errors error;

    public Error(Errors error) {

        this.error = error;
    }

    public Errors getError() {

        return(error);
    }
}
