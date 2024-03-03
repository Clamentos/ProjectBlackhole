package io.github.clamentos.blackhole.framework.scaffolding.network.security;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.AuthorizationException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.SessionExpiredException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.SessionNotFoundException;

///.
import java.util.Set;

///
/**
 * <h3>Session Service</h3>
 * Specifies that the implementing class can manage client network sessions.
*/
public interface SessionService {

    ///
    /**
     * Authenticates and authorizes the client network session associated to the provided session id.
     * @param session_id : The session id.
     * @param roles : The set of user roles to check against.
     * @throws IllegalArgumentException If either {@code session_id} or {@code roles} are {@code null}.
     * @throws AuthorizationException If the session doesn't have enough privileges to pass the check.
     * @throws SessionExpiredException If the session is expired.
     * @throws SessionNotFoundException If {@code session_id} doesn't map to any currently existing session.
     * @throws SecurityException If any other security error eccurs.
    */
    void authorize(byte[] session_id, Set<Role<?>> roles)
    throws IllegalArgumentException, AuthorizationException, SessionExpiredException, SessionNotFoundException, SecurityException;

    ///..
    /**
     * Creates a new session object with the given role.
     * @param role : The user role.
     * @param user_id : The associated user id.
     * @return the never {@code null} cryptographically random session id associated to the new object.
     * @throws IllegalArgumentException If {@code role} is {@code null}.
     * @throws SecurityException If any security error eccurs.
    */
    byte[] create(Role<?> role, long user_id) throws IllegalArgumentException, SecurityException;

    ///..
    /**
     * Gets the user role from the session object.
     * @param session_id : The session id.
     * @return The never {@code null} user role.
     * @throws SessionNotFoundException If {@code session_id} doesn't map to any currently existing session.
    */
    Role<?> getRole(byte[] session_id) throws SessionNotFoundException;

    ///..
    /**
     * Deletes the specified session.
     * @param session_id : The session id.
     * @throws SessionNotFoundException If {@code session_id} doesn't map to any currently existing session.
    */
    void delete(byte[] session_id) throws SessionNotFoundException;

    ///
}
