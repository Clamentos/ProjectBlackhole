package io.github.clamentos.blackhole.common.configuration;

import java.util.Map;

// eager loading singleton
// thread safe
public class ConfigurationProvider {
    
    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();
    private Map<Constants, Object> constants;
    
    private ConfigurationProvider() {

        // TODO: read config file & create instance
        //...
    }

    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    public <T> T getConstant(Constants constant, Class<T> type) throws ClassCastException {

        return(type.cast(constants.get(constant)));
    }
}
