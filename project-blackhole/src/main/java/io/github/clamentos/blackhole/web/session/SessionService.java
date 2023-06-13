package io.github.clamentos.blackhole.web.session;

import io.github.clamentos.blackhole.persistence.entities.EndpointPermission;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// must be singleton
public class SessionService {

    private static volatile SessionService INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();
    
    private SecureRandom rng;
    private ConcurrentHashMap<byte[], UserSession> user_sessions;

    private SessionService() throws NoSuchAlgorithmException {

        rng = SecureRandom.getInstance("SHA1PRNG");
        user_sessions = new ConcurrentHashMap<>();
    }

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

    public UserSession findSession(byte[] session_id) {

        return(user_sessions.get(session_id));
    }

    public byte[] insertSession(List<EndpointPermission> permissions) {

        byte[] id = new byte[32];

        rng.nextBytes(id);
        user_sessions.put(id, new UserSession(id, permissions));

        return(id);
    }
}
