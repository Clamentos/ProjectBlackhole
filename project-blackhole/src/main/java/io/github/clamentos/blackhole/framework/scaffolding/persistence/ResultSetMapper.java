package io.github.clamentos.blackhole.framework.scaffolding.persistence;

import java.sql.ResultSet;
import java.util.List;

import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ResultSetMappingException;

@FunctionalInterface
public interface ResultSetMapper<T extends Entity> {

    List<T> map(ResultSet result_set) throws ResultSetMappingException;
}
