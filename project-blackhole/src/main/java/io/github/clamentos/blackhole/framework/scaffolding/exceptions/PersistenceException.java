package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
import java.sql.SQLException;

///
/**
 * <h3>Persistence exception</h3>
 * This exception indicates a problem with the persistence layer.
*/
public final class PersistenceException extends RuntimeException {

    ///
    /** The original SQL state message returned by the wrapped {@link SQLException}. This message should be considered as client-unsafe. */
    private final String original_sql_message;
    
    ///
    /**
     * Instantiates a new {@link PersistenceException} object decoding the specified sql exception.
     * @param exc : The caught sql exception.
    */
    public PersistenceException(SQLException exc) {

        super(decodeMessage(exc));
        original_sql_message = exc.getMessage();
    }

    ///
    /** 
     * @return The original SQL state message returned by the wrapped {@link SQLException}.
     * This message should be considered as client-unsafe.
    */
    public String getOriginalSqlMessage() {

        return(original_sql_message);
    }

    ///.
    // Decodes the raw SQL state messages into client-safe messages.
    // list of all possible codes: https://www.postgresql.org/docs/current/errcodes-appendix.html
    private static String decodeMessage(SQLException exc) {

        switch(exc.getSQLState().substring(0, 2)) {

            case "08": // Connection errors.

                switch(exc.getSQLState().substring(2, 4)) {

                    case "001": return("Database unreachable.");
                    case "004": return("Connection refused by the database.");

                    default: return("Database connection error: " + exc.getSQLState());
                }

            case "23": // Integrity violation errors.

                switch(exc.getSQLState().substring(2, 4)) {

                    case "502": return("Not NULL constraint violated.");
                    case "503": return("Foreign key constraint violated.");
                    case "505": return("Unique constraint violated.");

                    default: return("Integrity constraint violated: " + exc.getSQLState());
                }

            default: return("Unknown SQL state: " + exc.getSQLState());
        }
    }

    ///
}
