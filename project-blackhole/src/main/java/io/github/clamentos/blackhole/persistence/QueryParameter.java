package io.github.clamentos.blackhole.persistence;

import java.sql.JDBCType;

public record QueryParameter(

    JDBCType type,
    Object value

) {}
