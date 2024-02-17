package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.implementation.logging.Log;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTask;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.QueryBinder;

///
/**
 * <h3>Log Entity</h3>
 * Database log entity. Corresponds to the {@code Logs} table.
 * @see Log
 * @see MetricsTask
*/
public final record LogEntity(

    ///
    /** The primary key of {@code this}. */
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
    /** {@inheritDoc} */
    @Override
    public void bindForInsert(QueryBinder query_binder) {

        query_binder.bindLong(log_id);
        query_binder.bindLong(creation_date);
        query_binder.bindString(log_level);
        query_binder.bindString(message);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindForUpdate(QueryBinder query_binder, long fields) {

        if((fields & 0b000010) > 0) query_binder.bindLong(log_id);
        if((fields & 0b000100) > 0) query_binder.bindLong(creation_date);
        if((fields & 0b001000) > 0) query_binder.bindString(log_level);
        if((fields & 0b010000) > 0) query_binder.bindString(message);

        query_binder.bindLong(id);
    }

    ///
}
