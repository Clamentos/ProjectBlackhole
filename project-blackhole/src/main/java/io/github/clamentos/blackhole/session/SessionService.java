package io.github.clamentos.blackhole.session;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

public class SessionService {
    
    private static final SessionService INSTANCE = new SessionService();

    private ConfigurationProvider configuration_provider;

    private ConcurrentHashMap<byte[], Session> sessions;
    private SecureRandom id_generator;

    private SessionService() {

        sessions = new ConcurrentHashMap<>();
        id_generator = new SecureRandom();
        configuration_provider = ConfigurationProvider.getInstance();
    }

    public static SessionService getInstance() {

        return(INSTANCE);
    }

    public Session findSession(byte[] session_id) {

        return(sessions.get(session_id));
    }

    public byte[] insertSession(int user_id /* other params */) {

        byte[] session_id = new byte[32];

        id_generator.nextBytes(session_id);
        sessions.put(

            session_id,
            new Session(user_id, System.currentTimeMillis() + configuration_provider.SESSION_DURATION)
        );

        return(session_id);
    }

    public void removeSession(byte[] session_id) {

        sessions.remove(session_id);
    }
}
