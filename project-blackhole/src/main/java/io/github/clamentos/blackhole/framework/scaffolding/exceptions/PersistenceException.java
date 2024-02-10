package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
import java.sql.SQLException;

///
/**
 * <h3>Persistence Exception</h3>
 * Runtime exception indicating an error while comunicating with the database.
 * @see GenericException
*/
public final class PersistenceException extends GenericException {
    
    ///
    /**
     * Instantiates a new {@link PersistenceException} object.
     * @param message : The internal exception detail message.
     * @param failure_message : The message to insert into a potential client response.
    */
    public PersistenceException(String message, String failure_message) {

        super(message, failure_message);
    }

    ///..
    /**
     * Instantiates a new {@link PersistenceException} object.
     * @param exc : The caught sql exception.
    */
    public PersistenceException(SQLException exc) {

        super(decodeMessage(exc), (exc == null) ? null : exc.getMessage());
    }

    ///
    // Decodes the raw SQL state messages into client-safe messages.
    // list of all possible codes: https://www.postgresql.org/docs/current/errcodes-appendix.html
    private static String decodeMessage(SQLException exc) {

        if(exc != null) {

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

        else {

            return(null);
        }
    }

    ///
}
