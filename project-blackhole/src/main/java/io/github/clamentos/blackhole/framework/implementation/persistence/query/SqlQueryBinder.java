package io.github.clamentos.blackhole.framework.implementation.persistence.query;

///
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.SqlExceptionDecoder;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.PersistenceException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.QueryBindingException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.QueryBinder;

///.
import java.io.InputStream;

///..
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;

///
/**
 * <h3>Sql Query Binder</h3>
 * Wrapper for binding parameters to JDBC statements.
*/
public final class SqlQueryBinder implements QueryBinder {

    ///
    /** The associated statement to bind the query parameters on. */
    private PreparedStatement statement;

    /** The current statement column index. */
    private int index;

    ///
    /** Instantiates a new {@link SqlQueryBinder} object. */
    public SqlQueryBinder() {

        this.index = 1;
    }

    ///
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindBoolean(boolean value) throws IllegalStateException, PersistenceException {

        bind(value, JDBCType.BOOLEAN, "bindBoolean");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindBoolean(Boolean value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BOOLEAN, "bindBoolean");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindByte(byte value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.TINYINT, "bindByte");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindByte(Byte value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.CHAR, "bindByte");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindShort(short value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.SMALLINT, "bindShort");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindShort(Short value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.SMALLINT, "bindShort");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindInt(int value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.INTEGER, "bindInt");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindInt(Integer value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.INTEGER, "bindInt");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindLong(long value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BIGINT, "bindLong");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindLong(Long value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BIGINT, "bindLong");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindFloat(float value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.FLOAT, "bindFloat");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindFloat(Float value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.FLOAT, "bindFloat");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindDouble(double value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.DOUBLE, "bindDouble");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindDouble(Double value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.DOUBLE, "bindDouble");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindString(String value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.VARCHAR, "bindString");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    @Override
    public void bindRaw(InputStream stream) throws IllegalStateException, PersistenceException {

        bind(stream, JDBCType.BLOB, "bindRaw");
    }

    ///.
    /**
     * Sets the internal JDBC statement to the provided one.
     * @param statement : The provided JDBC statement.
    */
    protected void setStatement(PreparedStatement statement) {

        this.statement = statement;
    }

    ///..
    /** Resets the satement column index to the starting position. */
    protected void resetIndex() {

        index = 1;
    }

    ///.
    /**
     * Binds the provided parameter.
     * @param value : The parameter to bind
     * @param type : The type of the parameter.
     * @param caller_name : The name of the caller, used for logging.
     * @throws IllegalStateException If the internal JDBC statement is {@code null}.
     * @throws PersistenceException If any database access error occurs.
    */
    private void bind(Object value, JDBCType type, String caller_name) throws IllegalStateException, PersistenceException {

        if(statement == null) throw new IllegalStateException("SqlQueryBinder." + caller_name + " -> Cannot bind: null statement");

        try {

            switch(type) {

                case BOOLEAN: statement.setBoolean(index, (boolean)value); break;
                case TINYINT: statement.setByte(index, (byte)value); break;
                case SMALLINT: statement.setShort(index, (short)value); break;
                case INTEGER: statement.setInt(index, (int)value); break;
                case BIGINT: statement.setLong(index, (long)value); break;
                case FLOAT: statement.setFloat(index, (float)value); break;
                case DOUBLE: statement.setDouble(index, (double)value); break;
                case VARCHAR: statement.setString(index, (String)value); break;
                case BLOB: statement.setBlob(index, (InputStream)value); break;

                default: throw new QueryBindingException(

                    "SqlQueryBinder." + caller_name + " -> Cannot bind: Unknown type " + type.getName()
                );
            }

            index++;
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("SqlQueryBinder." + caller_name + " -> Cannot bind: ", exc);
        }
    }

    ///
}
