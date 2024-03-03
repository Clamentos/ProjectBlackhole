package io.github.clamentos.blackhole.framework.scaffolding.network.validation;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;

///
/**
 * <h3>Validator Provider</h3>
 * Specifies that the implementing class can provide validators to other classes.
*/
@FunctionalInterface
public interface ValidatorProvider {

    ///
    /**
     * Gets the validator associated to the provided data-transfer-object.
     * @param dto_class : The class of the data-transfer-object to be validated.
     * @return the never {@code null} validator associated to {@code dto_class}.
    */
    Validator getValidator(Class<? extends Deserializable> dto_class);

    ///
}
