package io.github.clamentos.blackhole.web.session;

//________________________________________________________________________________________________________________________________________

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class SessionService {

    private static volatile SessionService INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();
    
    private SecureRandom rng;
    private ConcurrentHashMap<byte[], UserSession> user_sessions;

    //____________________________________________________________________________________________________________________________________

    private SessionService() throws NoSuchAlgorithmException {

        rng = SecureRandom.getInstance("SHA1PRNG");
        user_sessions = new ConcurrentHashMap<>();
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

    public UserSession findSession(byte[] session_id) {

        return(user_sessions.get(session_id));
    }

    public byte[] insertSession(UserSession session) {

        byte[] id = new byte[32];

        rng.nextBytes(id);
        user_sessions.put(id, session);

        return(id);
    }

    public void removeSession(byte[] session_id) {

        user_sessions.remove(session_id);
    }

    //____________________________________________________________________________________________________________________________________
}
