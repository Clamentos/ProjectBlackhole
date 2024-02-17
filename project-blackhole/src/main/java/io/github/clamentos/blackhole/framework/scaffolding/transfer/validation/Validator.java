package io.github.clamentos.blackhole.framework.scaffolding.transfer.validation;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ValidationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;

///
/**
 * <h3>Validator</h3>
 * Specifies that the implementing class can validate incoming deserializable objects.
 * @see Deserializable
*/
@FunctionalInterface
public interface Validator {

    ///
    /**
     * Validates the provided deserializable object.
     * @param obj : The incoming provided deserializable object.
     * @param request_method : The associated request method.
     * @throws ValidationException If any validation error occurs.
     * @see Deserializable
     * @see Methods
    */
    void validate(Deserializable obj, Methods request_method) throws ValidationException;

    ///
}
