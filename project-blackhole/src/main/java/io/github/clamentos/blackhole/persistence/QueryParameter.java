package io.github.clamentos.blackhole.persistence;

public record QueryParameter(

    Object parameter,
    SqlTypes type

) {}
