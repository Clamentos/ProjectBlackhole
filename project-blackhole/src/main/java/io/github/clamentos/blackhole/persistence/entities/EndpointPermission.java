package io.github.clamentos.blackhole.persistence.entities;

public record EndpointPermission(

    long id,
    int user_id,
    short resource, 
    short method

) {}

/*
 * resources:
 * 
 * 0: system
 * 1: users
 * 2: resources
 * 3: edges
 * 4: tags
 * 5: ...
*/