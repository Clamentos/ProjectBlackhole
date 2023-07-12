package io.github.clamentos.blackhole.common.configuration;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * Global configuration constants fetched from the {@code Application.properties}
 * file located in {@code /resources}.
*/
public class ConfigurationProvider {
    
    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();

    private final Logger LOGGER = Logger.getInstance();
    
    private Map<Constants, Object> constants;

    //____________________________________________________________________________________________________________________________________

    private ConfigurationProvider() {

        Properties props = new Properties();
        constants = new HashMap<>();

        try {

            props.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        catch(InvalidPathException | IOException exc) {

            LOGGER.log(
                    
                "ConfigurationProvider.new > Could not initialize, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage() +
                " Defaults will be used.",
                LogLevel.NOTE
            );
        }

        for(Constants constant : Constants.values()) {

            try {

                constants.put(
                    
                    constant,
                    constant.getClass().cast(props.getOrDefault(constant.name(), constant.getValue()))
                );
            }

            catch(ClassCastException exc) {

                LOGGER.log(
                    
                    "ConfigurationProvider.new > Could not initialize, ClassCastException: " +
                    exc.getMessage() + " Skipping this property (" + constant.name() + ").",
                    LogLevel.ERROR
                );
            }

            // just for aligning the prints... 21 is the longest property name
            int amt = 21 - constant.name().length();
            String padding = " ".repeat(amt);

            LOGGER.log(
                    
                "Property: " + constant.name() + padding + "    Value: " + constants.get(constant).toString(),
                LogLevel.INFO
            );
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Get the {@link ConfigurationProvider} instance. If none is available, create it.
     * When creating the instace, this method will read the {@code Application.properties}
     * file located in {@code /resources}. If such file doesn't exist,
     * then the defaults for all properties will be used.
     * If a particular property isn't defined in the file, then the default (for that
     * particular variable) will be used.
     * @return The {@link ConfigurationProvider} instance.
    */
    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the value of the constant already casted.
     * @param <T> The of the result.
     * @param constant : The desired {@link Constant}. 
     * @param type : The type to cast to.
     * @return The value of the associated constant.
     * @throws ClassCastException If the cast failed.
    */
    public <T> T getConstant(Constants constant, Class<T> type) throws ClassCastException {

        return(type.cast(constants.get(constant)));
    }

    //____________________________________________________________________________________________________________________________________
}
