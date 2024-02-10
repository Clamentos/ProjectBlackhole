package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Result set Mapping Exception</h3>
 * Runtime exception indicating an error during the result set to entity mapping process.
 * @see GenericException
*/
public class ResultSetMappingException extends GenericException {

    ///
    /**
     * Instantiates a new {@link ResultSetMappingException} object.
     * @param message : The internal exception detail message.
     * @param failure_message : The message to insert into a potential client response.
    */
    public ResultSetMappingException(String message, String failure_message) {

        super(message, failure_message);
    }

    ///
}
