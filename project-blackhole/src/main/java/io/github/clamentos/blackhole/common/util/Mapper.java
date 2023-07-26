package io.github.clamentos.blackhole.common.util;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.web.request.components.DataEntry;
import io.github.clamentos.blackhole.framework.web.request.components.Types;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Static.</b></p>
 * <p>Global utility class.</p>
 * This class holds a variety of static utility methods publicly accessible.
*/
public class Mapper {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</b></p>
     * <p>Converts the given {@link DataEntry} to {@link String}.</p>
     * The method also checks if the input conforms to the specified boundaries.
     * @param entry : The input data entry.
     * @param regex : The pattern used to check if {@code entry} is acceptable.
     * @param nullable : Specifies if {@ null} is allowed.
     * @return : The converted {@code entry}.
     * @throws IllegalArgumentException If {@code regex} doesn't match or if {@code entry} was null
     *                                  and {@code nullable} was {@code false}.
     * @throws PatternSyntaxException If {@code regex} is not a valid regular expression.
    */
    public static String entryAsString(DataEntry entry, String regex, boolean nullable) throws IllegalArgumentException, PatternSyntaxException {

        String str;

        if(entry.data_type().equals(Types.NULL) == false) {

            if(entry.data_type().equals(Types.STRING) == false) {

                throw new IllegalArgumentException("Input must be a STRING, got: " + entry.data_type().name());
            }

            str = (String)entry.data();
            
            if(Pattern.matches(regex, str) == true) {

                return(str);
            }

            throw new IllegalArgumentException("Input STRING doesn't conform");
        }

        if(nullable == false) {

            throw new IllegalArgumentException("Input STRING cannot be null");
        }

        return(null);
    }

    //____________________________________________________________________________________________________________________________________
}
