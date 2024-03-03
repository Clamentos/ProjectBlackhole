package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.QueryBinder;

///
/**
 * <h3>Log Entity</h3>
 * Database log entity. Corresponds to the {@code Logs} table.
*/
public final record LogEntity(

    ///
    /** The primary key. */
    long id,

    /** The log object identifier. */
    long log_id,

    /** The instantiation timestamp of the log object. */
    long creation_date,

    /** The severity of the log object. */
    String log_level,

    /** The message of the log object. */
    String message

    ///
) implements Entity {

    ///
    /**
     * Instantiates a new {@link LogEntity} object.
     * @param id : The primary key.
     * @param log_id : The log object identifier.
     * @param creation_date : The instantiation timestamp of the log object.
     * @param log_level : The severity of the log object.
     * @param message : The message of the log object.
    */
    public LogEntity {

        if(log_level == null || message == null) {

            throw new IllegalArgumentException("LogEntity.new -> The input arguments cannot be null");
        }
    }

    ///
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code query_binder} is {@code null}.
    */
    @Override
    public void bindForInsert(QueryBinder query_binder) throws NullPointerException {

        query_binder.bindLong(log_id);
        query_binder.bindLong(creation_date);
        query_binder.bindString(log_level);
        query_binder.bindString(message);
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code query_binder} is {@code null}.
    */
    @Override
    public void bindForUpdate(QueryBinder query_binder, long fields) throws NullPointerException {

        if((fields & 0b000010) > 0) query_binder.bindLong(log_id);
        if((fields & 0b000100) > 0) query_binder.bindLong(creation_date);
        if((fields & 0b001000) > 0) query_binder.bindString(log_level);
        if((fields & 0b010000) > 0) query_binder.bindString(message);

        query_binder.bindLong(id);
    }

    ///
}
