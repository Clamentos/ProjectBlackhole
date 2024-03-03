package io.github.clamentos.blackhole.framework.scaffolding.network.validation;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ValidationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Methods;

///
/**
 * <h3>Validator</h3>
 * Specifies that the implementing class can validate incoming deserializable objects.
*/
@FunctionalInterface
public interface Validator {

    ///
    /**
     * Validates the provided deserializable object.
     * @param dto : The incoming provided deserializable object.
     * @param method : The request method.
     * @throws ValidationException If any validation error occurs.
    */
    void validate(Deserializable dto, Methods method) throws ValidationException;

    ///
}
