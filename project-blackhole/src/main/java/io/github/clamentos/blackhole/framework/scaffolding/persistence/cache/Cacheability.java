package io.github.clamentos.blackhole.framework.scaffolding.persistence.cache;

///
/**
 * <h3>Cacheability</h3>
 * Specifies the cacheability level of a database entity.
 * <ol>
 *     <li>{@code ALWAYS}: The cache will allways cache new entities and never perform replacement.</li>
 *     <li>{@code SIZE_LIMITED}: The cache will perform replacement once the specified maximum is reached.</li>
 *     <li>{@code NEVER}: The entity will never be cached.</li>
 * </ol>
*/
public enum Cacheability {

    ///
    ALWAYS,
    SIZE_LIMITED,
    NEVER;

    ///
}
