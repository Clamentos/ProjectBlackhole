package io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization;

///
/**
 * <h3>Data Provider</h3>
 * Specifies that the implementing class can provide data to other classes.
*/
@FunctionalInterface
public interface DataProvider {

    ///
    /**
     * <p>Fills the provided buffer with data, blocking if necessary for more to become available.</p>
     * <b>NOTE: This method only modifies the specified section of the provided buffer and leaves the remaining untouched.</b>
     * @param chunk : The input buffer to be filled.
     * @param starting_position : The starting position of the buffer.
     * @param amount : The amount of data to transfer.
     * @return The number of bytes red or {@code 0} if there isn't any.
    */
    int fill(byte[] chunk, int starting_position, int amount);

    ///
}
