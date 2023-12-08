package io.github.clamentos.blackhole.business.session;

///
/**
 * <h3>Session<h3>
 * This simple record class is used to store the session of each logged user.
*/
public final record Session(

    ///
    byte[] id,
    int user_id,
    long flags,
    long flags_others,
    long valid_to

    ///
) {

    ///
    /**
     * Instantiates a new {@link Session} object.
     * @param id : The randomly generated session id.
     * @param user_id : The id of the target user.
     * @param flags : The permission flags.
     * @param valid_to : The expiration timestamp.
    */
    public Session(byte[] id, int user_id, PermissionFlags flags, long valid_to) {

        this(id, user_id, flags.extractFlags(), flags.extractFlagsOthers(), valid_to);
    }

    ///
}