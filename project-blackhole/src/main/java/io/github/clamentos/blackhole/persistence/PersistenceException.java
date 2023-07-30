package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.Failure;

//________________________________________________________________________________________________________________________________________

public class PersistenceException extends Exception {

    private String generic_message;

    //____________________________________________________________________________________________________________________________________
    
    public PersistenceException(String generic_message, String original_message, Failure cause) {

        super(original_message, cause);
        this.generic_message = generic_message;
    }

    //____________________________________________________________________________________________________________________________________

    public String getGenericMessage() {

        return(generic_message);
    }

    //____________________________________________________________________________________________________________________________________
}
