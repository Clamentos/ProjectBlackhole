package io.github.clamentos.blackhole.web.session;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.Error;
import io.github.clamentos.blackhole.common.exceptions.ErrorWrapper;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.components.Method;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>This class is a singleton.</b></p>
 * This class is responsible for managing the in-memory user sessions.
*/
public class SessionService {

    private static volatile SessionService INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;
    
    private SecureRandom rng;
    private ConcurrentHashMap<byte[], UserSession> user_sessions;

    //____________________________________________________________________________________________________________________________________

    private SessionService() throws NoSuchAlgorithmException {

        LOGGER = Logger.getInstance();
        rng = SecureRandom.getInstance("SHA1PRNG");
        user_sessions = new ConcurrentHashMap<>();
        LOGGER.log("Session service instantiated", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the SessionService instance.
     * If the instance doesn't exist, create it.
     * @return The SessionService instance.
    */
    public static SessionService getInstance() throws NoSuchAlgorithmException {

        SessionService temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new SessionService();
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Searches the {@link UserSession} associated to the {@code session_id}.
     * @param session_id : The key.
     * @return The found {@link UserSession} or {@code null} if there was no association.
    */
    public UserSession findSession(byte[] session_id) {

        return(user_sessions.get(session_id));
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Inserts the specified {@link UserSession} into the session pool.
     * @param session : The {@link UserSession}.
     * @return The random {@code session_id} associated with this {@link UserSession}.
    */
    public byte[] insertSession(UserSession session) {

        byte[] id = new byte[32];

        rng.nextBytes(id);
        user_sessions.put(id, session);

        return(id);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deletes the specified {@link UserSession} from the session pool.
     * @param session_id : The key.
    */
    public void removeSession(byte[] session_id) {

        user_sessions.remove(session_id);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Checks if the specified user is allowed to use the specified method.
     * @param session_id : The user {@code session_id}.
     * @param method : The request method.
     * @throws SecurityException If the associated {@link UserSession} doesn't specify
     *                           enough privileges.
     */
    public void checkSessionTag(byte[] session_id, Method method) throws SecurityException {

        boolean check;
        
        switch(method) {

            case CREATE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_CREATE; break;
            case READ: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_READ; break;
            case UPDATE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_UPDATE; break;
            case DELETE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_DELETE; break;

            default: check = false; break;
        }

        checkSession(session_id, check);
    }

    // TODO: other session checks...

    //____________________________________________________________________________________________________________________________________

    private void checkSession(byte[] session_id, boolean check) throws SecurityException {

        UserSession session;

        if(check == true) {

            session = findSession(session_id);

            if(session == null) {

                throw new SecurityException("No session found", new ErrorWrapper(Error.SESSION_NOT_FOUND));
            }

            if(session.valid_to() < System.currentTimeMillis()) {

                removeSession(session_id);
                throw new SecurityException("Expired session", new ErrorWrapper(Error.SESSION_EXPIRED));
            }

            if((session.post_permissions() & 0b0100) == 0) {

                throw new SecurityException("Not enough privileges", new ErrorWrapper(Error.NOT_ENOUGH_PRIVILEGES));
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
