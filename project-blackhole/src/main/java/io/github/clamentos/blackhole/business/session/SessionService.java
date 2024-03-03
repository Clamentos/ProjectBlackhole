package io.github.clamentos.blackhole.business.session;

import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

import java.math.BigInteger;

import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

///
/**
 * <h3>Session service</h3>
 * This class manages the sessions for each logged user via methods to create, destroy and perform permission checks.
*/
public class SessionService {
    
    ///
    private static final SessionService INSTANCE = new SessionService();

    private final Logger logger;

    private final SecureRandom id_generator;
    private final Map<byte[], Session> sessions;
    private final Map<Integer, ArrayList<Session>> mappings;

    ///
    private SessionService() {

        logger = Logger.getInstance();

        id_generator = new SecureRandom();
        sessions = new ConcurrentHashMap<>();
        mappings = new ConcurrentHashMap<>();

        logger.log("SessionService.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link SessionService} instance created during class loading. */
    public static SessionService getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Checks the permissions of a given session against the provided flags.
     * @param session_id : The session identifier.
     * @param flags : The flags to check against.
     * @throws SecurityException If no session is found, is expired or the permissions check fails.
     * @throws IllegalArgumentException If {@code flags} is {@code null}.
    */
    /*public void checkPermissions(byte[] session_id, PermissionFlags flags) throws SecurityException, IllegalArgumentException {

        if(flags == null) {

            throw new IllegalArgumentException("The permission flags cannot be null");
        }

        Session session = sessions.get(session_id);

        if(session == null) {

            throw new SecurityException(
                
                "No session found with id: " + new BigInteger(session_id).toString(16),
                new FailuresWrapper(Failures.SESSION_NOT_FOUND)
            );
        }

        if(session.valid_to() <= System.currentTimeMillis()) {

            removeSession(session_id);

            throw new SecurityException(

                "The session: " + new BigInteger(session_id).toString(16) + " is expired",
                new FailuresWrapper(Failures.SESSION_EXPIRED)
            );
        }

        if((session.flags() & flags.extractFlags()) == 0 && (session.flags_others() & flags.extractFlagsOthers()) == 0) {

            throw new SecurityException(

                "Not enough privileges for session: " + new BigInteger(session_id).toString(16),
                new FailuresWrapper(Failures.NOT_ENOUGH_PRIVILEGES)
            );
        }
    }*/

    /**
     * Creates a new session and inserts it into the buffers.
     * @param user_id : The user identifier for the new session.
     * @param flags : The user permission flags.
     * @return The new session object.
     * @throws IllegalStateException If the maximum number of user sessions is exceeded.
    */
    /*public byte[] createSession(int user_id, PermissionFlags flags) throws IllegalStateException {

        ArrayList<Session> temp = mappings.get(user_id);

        if(temp == null) {

            // Never seen that user OR all of its sessions have expired -> create new empty mappings entry.
            mappings.put(user_id, new ArrayList<>());
        }

        else {

            if(temp.size() >= ConfigurationProvider.getInstance().MAX_USER_SESSIONS) {

                throw new IllegalStateException(
                    
                    "User: " + user_id + "has too many sessions: " + temp.size(),
                    new FailuresWrapper(Failures.TOO_MANY_SESSIONS)
                );
            }
        }

        // Create the new session.
        byte[] session_id = new byte[32];
        id_generator.nextBytes(session_id);

        Session session = new Session(
            
            session_id, user_id, flags,
            System.currentTimeMillis() + ConfigurationProvider.getInstance().SESSION_DURATION
        );

        // Add it in both buffers.
        sessions.put(session_id, session);
        mappings.get(user_id).add(session);

        return(session_id);
    }*/

    /**
     * Removes the specified session from all the buffers.
     * @param session_id : The identifier of the session to remove.
    */
    /*public void removeSession(byte[] session_id) {

        Session session = sessions.get(session_id);
        ArrayList<Session> temp = mappings.get(session.user_id());

        sessions.remove(session_id);

        if(temp.size() == 1) {

            mappings.remove(session.user_id());
        }

        else {

            temp.remove(session);
        }
    }*/

    ///
}
