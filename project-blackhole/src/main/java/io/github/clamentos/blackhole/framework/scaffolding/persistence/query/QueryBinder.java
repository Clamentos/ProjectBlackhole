package io.github.clamentos.blackhole.framework.scaffolding.persistence.query;

///
import java.io.InputStream;

///
/**
 * <h3>Query Binder</h3>
 * Specifies that the implement class can bind entity parameters to a JDBC statement.
 * @apiNote Binding order is important and must reflect the order of the database columns.
*/
public interface QueryBinder {

    ///
    /**
     * Binds the provided boolean to {@code this}.
     * @param value : The value to be binded.
    */
    void bindBoolean(boolean value);

    ///..
    /**
     * Binds the provided Boolean to {@code this}.
     * @param value : The value to be binded.
    */
    void bindBoolean(Boolean value);
    
    ///..
    /**
     * Binds the provided byte to {@code this}.
     * @param value : The value to be binded.
    */
    void bindByte(byte value);

    ///..
    /**
     * Binds the provided Byte to {@code this}.
     * @param value : The value to be binded.
    */
    void bindByte(Byte value);

    ///..
    /**
     * Binds the provided short to {@code this}.
     * @param value : The value to be binded.
    */
    void bindShort(short value);

    ///..
    /**
     * Binds the provided Short to {@code this}.
     * @param value : The value to be binded.
    */
    void bindShort(Short value);

    ///..
    /**
     * Binds the provided int to {@code this}.
     * @param value : The value to be binded.
    */
    void bindInt(int value);

    ///..
    /**
     * Binds the provided Integer to {@code this}.
     * @param value : The value to be binded.
    */
    void bindInt(Integer value);

    ///..
    /**
     * Binds the provided long to {@code this}.
     * @param value : The value to be binded.
    */
    void bindLong(long value);

    ///..
    /**
     * Binds the provided Long to {@code this}.
     * @param value : The value to be binded.
    */
    void bindLong(Long value);

    ///..
    /**
     * Binds the provided float to {@code this}.
     * @param value : The value to be binded.
    */
    void bindFloat(float value);

    ///..
    /**
     * Binds the provided Float to {@code this}.
     * @param value : The value to be binded.
    */
    void bindFloat(Float value);

    ///..
    /**
     * Binds the provided double to {@code this}.
     * @param value : The value to be binded.
    */
    void bindDouble(double value);

    ///..
    /**
     * Binds the provided Double to {@code this}.
     * @param value : The value to be binded.
    */
    void bindDouble(Double value);

    ///..
    /**
     * Binds the provided string to {@code this}.
     * @param value : The value to be binded.
    */
    void bindString(String value);

    ///..
    /**
     * Binds the provided stream to {@code this}.
     * @param stream : The stream to be binded.
    */
    void bindRaw(InputStream stream);

    ///
}
