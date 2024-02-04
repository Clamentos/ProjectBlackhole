package io.github.clamentos.blackhole.framework.scaffolding.cache;

///
/**
 * <h3>Cacheability</h3>
 * Specifies the cacheability of a given entity.
*/
public enum Cacheability {

    ///
    /** The entity will always be cached. */
    ALWAYS,

    /** The entity will only be cached if the size is less than the specified maximum. */
    SIZE_LIMITED,

    /** The entity will never be cached. */
    NEVER;

    ///
}
