package io.github.clamentos.blackhole.framework.scaffolding.cache;

///
/**
 * <h3>Cacheability</h3>
 * Specifies the cacheability level of a given entity.
 * <ol>
 *     <li>{@code ALWAYS}: The entity will always be considered eligible for caching.</li>
 *     <li>{@code SIZE_LIMITED}: The entity will only be considered eligible for caching if the size is less than or equal to the specified
 *                               entity maximum.</li>
 *     <li>{@code NEVER}: The entity will never be considered eligible for caching.</li>
 * </ol>
*/
public enum Cacheability {

    ///
    ALWAYS,
    SIZE_LIMITED,
    NEVER;

    ///
}
