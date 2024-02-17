package io.github.clamentos.blackhole.framework.scaffolding.persistence;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ResultSetMappingException;

///.
import java.sql.ResultSet;

///..
import java.util.List;

///
/**
 * <h3>Result Set Mapper</h3>
 * Specifies that the implementing class can transfer a result set into a list of entities.
*/
@FunctionalInterface
public interface ResultSetMapper<T extends Entity> {

    ///
    /**
     * Maps the given result set into a list of corresponding entities.
     * @param result_set : The input result set.
     * @return The never {@code null} list of mapped entities.
     * If no entity was mapped due to an empty result set, the returned list must be empty.
     * @throws ResultSetMappingException If any error occurs during the mapping process.
    */
    List<T> map(ResultSet result_set) throws ResultSetMappingException;

    ///
}
