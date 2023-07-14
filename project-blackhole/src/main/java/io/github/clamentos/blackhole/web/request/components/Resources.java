package io.github.clamentos.blackhole.web.request.components;

public enum Resources {
    
    SYSTEM,
    USER,
    TAG,
    RESOURCE,
    ECHO;

    public static Resources construct(byte resource_id) throws IllegalArgumentException {

        switch(resource_id) {

            case 0: return(Resources.SYSTEM);
            case 1: return(Resources.USER);
            case 2: return(Resources.TAG);
            case 3: return(Resources.RESOURCE);
            case 4: return(Resources.ECHO);

            default: throw new IllegalArgumentException("Unknown resource type");
        }
    }
}
