package io.github.clamentos.blackhole.framework.implementation.utility;

///
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.NoSuchElementException;

///
/**
 * <h3>Stream utils</h3>
 * Provides static utility methods for reading and writing data from and to streams.
*/
public final class StreamUtils {

    ///
    /**
     * Reads a single byte from the stream blocking until input data is available or the end of stream is reached.
     * @param in : The source stream.
     * @return : The byte red.
     * @throws IOException If any IO error occurs.
     * @throws NoSuchElementException If the end of stream is reached.
    */
    public static byte readByte(InputStream in) throws IOException, NoSuchElementException {

        int value = in.read();

        if(value == -1) {

            throw new NoSuchElementException("End of stream reached");
        }

        return((byte)value);
    }

    /**
     * Reads {@code length} bytes from the stream blocking until input data is available or the end of stream is reached.
     * @param in : The source stream.
     * @param length : The amount of bytes to read.
     * @return : The array of bytes red.
     * @throws IOException If any IO error occurs.
     * @throws NoSuchElementException If the end of stream is reached.
    */
    public static byte[] readBytes(InputStream in, int length) throws IOException, NoSuchElementException {

        byte[] result = in.readNBytes(length);

        if(result.length < length) {

            throw new NoSuchElementException(
                
                "End of stream reached. Only able to read " + result.length + " bytes out of " + length
            );
        }

        return(result);
    }

    public static void writeNumber(OutputStream out, long number, int length) throws IOException, IllegalArgumentException {

        if(length < 1 || length > 8) throw new IllegalArgumentException("The length must be between 1 and 8 (both inclusive)");

        for(int i = 0; i < length; i++) {

            out.write((byte)((number | (0x00000000000000FF << (i * 8))) >> (i * 8)));
        }
    }

    public static long readNumber(InputStream in, int length) throws IOException, NoSuchElementException, IllegalArgumentException {

        if(length < 1 || length > 8) throw new IllegalArgumentException("The length must be between 1 and 8 (both inclusive)");

        long result = 0;
        
        for(int i = 0; i < length; i++) {

            result |= (readByte(in) << (i * 8));
        }

        return(result);
    }

    ///
}
