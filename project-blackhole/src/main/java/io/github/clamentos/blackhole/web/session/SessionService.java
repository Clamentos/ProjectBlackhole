package io.github.clamentos.blackhole.web.session;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

// must be singleton
public class SessionService {
    
    private SecureRandom rng;
    private HashMap<byte[], UserSession> user_sessions;

    public SessionService() throws NoSuchAlgorithmException {

        rng = SecureRandom.getInstance("SHA1PRNG");
        user_sessions = new HashMap<>();
    }

    public UserSession findSession(byte[] session_id) {

        return(user_sessions.get(session_id));
    }

    // user_data is a dummy value for now...
    public byte[] insertSession(String user_data) {

        byte[] id = new byte[32];

        rng.nextBytes(id);
        user_sessions.put(id, new UserSession(id, user_data));

        return(id);
    }
}
