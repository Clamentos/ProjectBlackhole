package io.github.clamentos.blackhole.session;

///
/**
 * <h3>User session<h3>
 * This simple record class is used to store the session of each logged user.
*/
public record Session(

    byte[] id,
    int user_id,
    int flags,
    long valid_to

    ///
) {}