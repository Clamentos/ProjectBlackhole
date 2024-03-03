/** This module specifies the API for the underlying framework. */
module framework {

    ///
    // Dependencies.
    requires java.base;
    requires java.management;
    requires transitive java.sql;

    ///.
    // Exports (implementation).
    exports io.github.clamentos.blackhole.framework.implementation.configuration;

    exports io.github.clamentos.blackhole.framework.implementation.logging.exportable;

    exports io.github.clamentos.blackhole.framework.implementation.network.transfer.components.exportable;

    exports io.github.clamentos.blackhole.framework.implementation.persistence.cache;
    exports io.github.clamentos.blackhole.framework.implementation.persistence.models;
    exports io.github.clamentos.blackhole.framework.implementation.persistence.pool;
    exports io.github.clamentos.blackhole.framework.implementation.persistence.query;

    exports io.github.clamentos.blackhole.framework.implementation.utility.exportable;

    ///..
    // Exports (scaffolding).
    exports io.github.clamentos.blackhole.framework.scaffolding;

    exports io.github.clamentos.blackhole.framework.scaffolding.exceptions;

    exports io.github.clamentos.blackhole.framework.scaffolding.network.controller;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.deserialization;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.security;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.serialization;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output;
    exports io.github.clamentos.blackhole.framework.scaffolding.network.validation;

    exports io.github.clamentos.blackhole.framework.scaffolding.persistence.cache;
    exports io.github.clamentos.blackhole.framework.scaffolding.persistence.model;
    exports io.github.clamentos.blackhole.framework.scaffolding.persistence.query;

    exports io.github.clamentos.blackhole.framework.scaffolding.tasks;

    ///
}
