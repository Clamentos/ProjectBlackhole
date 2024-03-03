package io.github.clamentos.blackhole.framework.implementation.network.security;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.AuthorizationException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.SessionExpiredException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.SessionNotFoundException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.security.Role;
import io.github.clamentos.blackhole.framework.scaffolding.network.security.SessionService;

///.
import java.security.SecureRandom;

///..
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

///
public final class NetworkSessionService implements SessionService {

    ///
    private static final NetworkSessionService INSTANCE = new NetworkSessionService();

    ///.
    private final Logger logger;

    ///..
    private final SecureRandom id_generator;
    private final ConcurrentHashMap<byte[], Session> session_map;
    private final ConcurrentHashMap<Long, Set<byte[]>> user_id_map;

    ///
    private NetworkSessionService() {

        logger = Logger.getInstance();

        id_generator = new SecureRandom();
        session_map = new ConcurrentHashMap<>();
        user_id_map = new ConcurrentHashMap<>();

        logger.log("NetworkSessionService.new => Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    public static NetworkSessionService getInstance() {

        return(INSTANCE);
    }

    ///
    @Override
    public void authorize(byte[] session_id, Set<Role<?>> roles)
    throws IllegalArgumentException, AuthorizationException, SessionExpiredException, SessionNotFoundException, SecurityException {

        if(session_id == null || roles == null) {

            throw new IllegalArgumentException("NetworkSessionService.authorize -> The input arguments cannot be null");
        }
        
        Session session = session_map.get(session_id);

        if(session == null) {

            throw new SessionNotFoundException("NetworkSessionService.authorize -> Session not found");
        }

        if(session.expiration() <= System.currentTimeMillis()) {

            throw new SessionExpiredException("NetworkSessionService.authorize -> Session expired");
        }

        if(roles.contains(session.role()) == false) {

            throw new AuthorizationException("NetworkSessionService.authorize -> Not enough permissions");
        }
    }

    ///..
    @Override
    public byte[] create(Role<?> role, long user_id) throws IllegalArgumentException, SecurityException {

        if(role == null) {

            throw new IllegalArgumentException("NetworkSessionService.create -> The input argument \"role\" cannot be null");
        }

        Set<byte[]> mappings = user_id_map.get(user_id);

        if(mappings != null && mappings.size() >= ConfigurationProvider.getInstance().MAX_USER_SESSIONS) {

            throw new SecurityException("NetworkSessionService.create -> Too many sessions for user: " + user_id);
        }

        byte[] session_id = new byte[32];

        id_generator.nextBytes(session_id);

        session_map.put(

            session_id, new Session(System.currentTimeMillis() + ConfigurationProvider.getInstance().SESSION_DURATION, user_id, role)
        );

        if(mappings != null) {

            mappings.add(session_id);
        }

        else {

            Set<byte[]> session_ids = new CopyOnWriteArraySet<>();
            session_ids.add(session_id);
            user_id_map.put(user_id, session_ids);
        }

        return(session_id);
    }

    ///..
    @Override
    public Role<?> getRole(byte[] session_id) throws SessionNotFoundException {

        Session session = session_map.get(session_id);

        if(session == null) {

            throw new SessionNotFoundException("NetworkSessionService.getRole -> Session not found");
        }

        return(session.role());
    }

    ///..
    @Override
    public void delete(byte[] session_id) throws SessionNotFoundException {

        Session session = session_map.get(session_id);

        if(session == null) {

            throw new SessionNotFoundException("NetworkSessionService.delete -> Session not found");
        }

        session_map.remove(session_id);
        user_id_map.get(session.user_id()).remove(session_id);
    }

    ///
}
