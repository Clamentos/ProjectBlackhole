package io.github.clamentos.blackhole.common.configuration;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

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
 * <p>Repository of the configuration constants found in {@link Constants}.</p>
 * <p>This class will use the {@code Application.properties} file located in {@code /resources}.</p>
 * The constructor method will read the configuration file and if such file doesn't exist,
 * then the defaults for all properties will be used. If a particular property
 * isn't defined in the file, then the default (for that particular constant) will be used.
*/

// TODO: watchout for illegal values... example: negative log file size
// it's better to do the checks here than each time on the objects.
public class ConfigurationProvider {
    
    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();
    private Map<Constants, Object> constants;

    //____________________________________________________________________________________________________________________________________

    private ConfigurationProvider() {

        Properties props = new Properties();
        constants = new HashMap<>();

        try {

            props.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        catch(InvalidPathException | IOException exc) {

            LogPrinter.printToConsole(new Log(
                    
                "ConfigurationProvider.new 1 > Could not initialize, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage() +
                " Defaults will be used.",
                LogLevel.NOTE
            ));
        }

        for(Constants constant : Constants.values()) {

            try {

                Class<?> type = constant.getType();

                constants.put(
                    
                    constant,
                    type.cast(props.getOrDefault(constant.name(), constant.getValue()))
                );
            }

            catch(ClassCastException exc) {

                LogPrinter.printToConsole(new Log(
                    
                    "ConfigurationProvider.new 2 > Could not initialize, ClassCastException: " +
                    exc.getMessage() + " Skipping this property (" + constant.name() + ").",
                    LogLevel.ERROR
                ));
            }

            // just for aligning the prints... 24 is the longest property name
            int amt = 24 - constant.name().length();
            String padding = " ".repeat(amt);

            LogPrinter.printToConsole(new Log(
                    
                "Property: " + constant.name() + padding + "    Value: " + constants.get(constant).toString(),
                LogLevel.INFO
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link ConfigurationProvider} instance created during class loading.
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
     * @throws NullPointerException If the {@code constant} or {@code type} are {@code null}.
    */
    public <T> T getConstant(Constants constant, Class<T> type) throws ClassCastException, NullPointerException {

        return(type.cast(constants.get(constant)));
    }

    //____________________________________________________________________________________________________________________________________
}
