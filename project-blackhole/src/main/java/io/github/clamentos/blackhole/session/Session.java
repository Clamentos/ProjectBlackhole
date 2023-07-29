package io.github.clamentos.blackhole.session;

public record Session(

    int user_id,
    // other...
    long valid_to
) {}
