package io.github.clamentos.blackhole.framework.scaffolding.persistence.query;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ResultSetMappingException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;

///.
import java.sql.ResultSet;

///..
import java.util.List;

///
/**
 * <h3>Result Set Mapper</h3>
 * Specifies that the implementing class can transform a result set into a list of database entities.
 * @param < E > E : The type of database entity.
*/
@FunctionalInterface
public interface ResultSetMapper<E extends Entity> {

    ///
    /**
     * Maps the given result set into a list of corresponding entities.
     * @param result_set : The result set.
     * @return The never {@code null} list of mapped entities.
     * If no entity was mapped due to an empty result set, the returned list will be empty.
     * @throws ResultSetMappingException If any error occurs during the mapping process.
    */
    List<E> map(ResultSet result_set) throws ResultSetMappingException;

    ///
}
