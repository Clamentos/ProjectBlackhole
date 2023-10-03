package io.github.clamentos.blackhole.session;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.exceptions.FailuresWrapper;

import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

///
/**
 * <h3>Session managing service</h3>
 * 
 * This class manages the sessions for each logged user. The class offers methods
 * to create, destroy and perform permission checks on sessions.
*/
public class SessionService {
    
    private static final SessionService INSTANCE = new SessionService();

    private final int MAX_USER_SESSIONS;
    private final long SESSION_DURATION;

    private ConcurrentHashMap<byte[], Session> sessions;
    private ConcurrentHashMap<Integer, ArrayList<Session>> mappings;
    private SecureRandom id_generator;

    ///
    private SessionService() {

        sessions = new ConcurrentHashMap<>();
        mappings = new ConcurrentHashMap<>();
        id_generator = new SecureRandom();

        MAX_USER_SESSIONS = ConfigurationProvider.getInstance().MAX_USER_SESSIONS;
        SESSION_DURATION = ConfigurationProvider.getInstance().SESSION_DURATION;
    }

    ///
    /** @return The {@link SessionService} instance created during class loading. */
    public static SessionService getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Checks the permissions of a given session against the provided flags.
     * 
     * @param session_id : The session identifier.
     * @param flags : The flags to check against.
     * @throws SecurityException If no session is found, is expired or the permissions check fails.
    */
    public void checkPermissions(byte[] session_id, int flags) throws SecurityException {

        Session session = sessions.get(session_id);

        if(session == null) {

            throw new SecurityException(
                
                "No session found",
                new FailuresWrapper(Failures.SESSION_NOT_FOUND)
            );
        }

        if(session.valid_to() <= System.currentTimeMillis()) {

            removeSession(session_id);

            throw new SecurityException(
                
                "The session was expired",
                new FailuresWrapper(Failures.SESSION_EXPIRED)
            );
        }

        if((session.flags() & flags) == 0) {

            throw new SecurityException(
                
                "Permission denied",
                new FailuresWrapper(Failures.NOT_ENOUGH_PRIVILEGES)
            );
        }
    }

    /**
     * Creates a new session and inserts it into the buffers.
     * 
     * @param user_id : The user identifier for the new session.
     * @param flags : The user permission flags.
     * @return The new session object.
     * @throws IllegalStateException If the maximum number of user sessions is exceeded.
    */
    public byte[] createSession(int user_id, byte flags) throws IllegalStateException {

        byte[] session_id;
        ArrayList<Session> temp;
        Session session;

        temp = mappings.get(user_id);

        if(temp == null) {

            // Never seen that user OR all of its sessions have expired -> create mappings entry.
            mappings.put(user_id, new ArrayList<>());
        }

        else {

            if(temp.size() >= MAX_USER_SESSIONS) {

                throw new IllegalStateException(
                    
                    "Too many sessions",
                    new FailuresWrapper(Failures.TOO_MANY_SESSIONS)
                );
            }
        }

        // Create the new session.
        session_id = new byte[32];
        id_generator.nextBytes(session_id);
        session = new Session(session_id, user_id, flags, System.currentTimeMillis() + SESSION_DURATION);

        // Add it in both buffers.
        sessions.put(session_id, session);
        mappings.get(user_id).add(session);

        return(session_id);
    }

    /**
     * Removes the specified session from all the buffers.
     * @param session_id : The identifier of the session to remove.
    */
    public void removeSession(byte[] session_id) {

        Session session = sessions.get(session_id);
        ArrayList<Session> temp;

        sessions.remove(session_id);
        temp = mappings.get(session.user_id());

        if(temp.size() == 1) {

            mappings.remove(session.user_id());
        }

        else {

            temp.remove(session);
        }
    }

    ///
}
