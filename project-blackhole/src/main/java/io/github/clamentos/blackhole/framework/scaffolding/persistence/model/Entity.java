package io.github.clamentos.blackhole.framework.scaffolding.persistence.model;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.QueryBinder;

///
/**
 * <h3>Entity</h3>
 * Specifies that the implementing class is a database entity.
*/
public interface Entity {

    ///
    /**
     * Binds the field values of {@code this} entity into the given statement for an {@code INSERT} query.
     * @param binder : The statement binder to bind the parameters on.
    */
    void bindForInsert(QueryBinder binder);

    ///..
    /**
     * Binds the field values of {@code this} entity into the given statement for an {@code UPDATE} query.
     * @param binder : The statement binder to bind the parameters on.
     * @param fields : The fields to consider. This parameter works as a checklist
     * starting from the first field which maps to least significant bit.
    */
    void bindForUpdate(QueryBinder binder, long fields);

    ///
}
