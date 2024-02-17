package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.utility.SqlExceptionDecoder;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.PersistenceException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.QueryBindingException;
///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.QueryBinder;

///.
import java.io.InputStream;

///..
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;

///
/**
 * <h3>Sql Query Binder</h3>
 * ...
 * @see QueryBinder
*/
public final class SqlQueryBinder implements QueryBinder {

    ///
    /** The associated statement to bind the query parameters on. */
    private PreparedStatement statement;

    /** The current column index. */
    private int index;

    ///
    /** Instantiates a new {@link SqlQueryBinder} object. */
    protected SqlQueryBinder() {

        this.index = 1;
    }

    ///
    public void setStatement(PreparedStatement statement) throws IllegalArgumentException {

        if(statement == null) {

            throw new IllegalArgumentException("(SqlQueryBinder.setStatement) -> Parameter \"statement\" cannot be null");
        }

        this.statement = statement;
    }

    ///..
    public void resetIndex() {

        index = 1;
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindBoolean(boolean value) throws IllegalStateException, PersistenceException {

        bind(value, JDBCType.BOOLEAN, "bindBoolean");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindBoolean(Boolean value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BOOLEAN, "bindBoolean");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindByte(byte value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.TINYINT, "bindByte");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindByte(Byte value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.CHAR, "bindByte");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindShort(short value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.SMALLINT, "bindShort");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindShort(Short value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.SMALLINT, "bindShort");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindInt(int value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.INTEGER, "bindInt");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindInt(Integer value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.INTEGER, "bindInt");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindLong(long value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BIGINT, "bindLong");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindLong(Long value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.BIGINT, "bindLong");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindFloat(float value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.FLOAT, "bindFloat");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindFloat(Float value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.FLOAT, "bindFloat");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindDouble(double value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.DOUBLE, "bindDouble");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindDouble(Double value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.DOUBLE, "bindDouble");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindString(String value) throws IllegalStateException, PersistenceException {
        
        bind(value, JDBCType.VARCHAR, "bindString");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindRaw(InputStream stream) throws IllegalStateException, PersistenceException {

        bind(stream, JDBCType.BLOB, "bindRaw");
    }

    ///.
    private void bind(Object value, JDBCType type, String caller_name) throws IllegalStateException, PersistenceException {

        if(statement == null) throw new IllegalStateException("(SqlQueryBinder." + caller_name + ") -> Cannot bind: null statement");

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

                    "(SqlQueryBinder." + caller_name + ") -> Cannot bind: Unknown type " + type.getName()
                );
            }

            index++;
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("(SqlQueryBinder." + caller_name + ") -> Cannot bind: ", exc);
        }
    }

    ///
}
