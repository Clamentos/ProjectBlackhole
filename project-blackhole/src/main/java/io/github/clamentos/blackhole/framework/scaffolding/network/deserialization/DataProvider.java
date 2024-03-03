package io.github.clamentos.blackhole.framework.scaffolding.network.deserialization;

///
/**
 * <h3>Data Provider</h3>
 * Specifies that the implementing class can provide raw data to other classes.
*/
@FunctionalInterface
public interface DataProvider {

    ///
    /**
     * Fills the provided buffer with data, blocking if necessary for more to become available.
     * @param chunk : The buffer to be filled.
     * @param offset : The buffer starting position.
     * @param amount : The amount of data to transfer.
     * @return The number of bytes red or {@code 0} if none was red. This value will always be less than or equal to {@code amount}.
     * @apiNote This method only modifies the specified section of the provided buffer and leaves the remaining untouched.
    */
    int fill(byte[] chunk, int offset, int amount);

    ///
}
