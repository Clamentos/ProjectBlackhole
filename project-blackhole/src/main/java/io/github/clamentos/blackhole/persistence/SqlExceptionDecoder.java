package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;

import java.sql.SQLException;

//________________________________________________________________________________________________________________________________________

public class SqlExceptionDecoder {

    //____________________________________________________________________________________________________________________________________
    
    public static void decode(SQLException exc) throws PersistenceException {

        Failure cause = new Failure(Failures.DATABASE_ERROR);

        switch(exc.getSQLState().substring(0, 1)) {

            case "08": throw new PersistenceException("Database connection error", exc.getMessage(), cause);
            case "22": throw new PersistenceException("Database data error", exc.getMessage(), cause);
            case "23": throw new PersistenceException("Database integrity error", exc.getMessage(), cause);
            case "42": throw new PersistenceException("Query syntax error or database access rule violation", exc.getMessage(), cause);

            default: throw new PersistenceException("Uncategorized database error", exc.getMessage(), cause);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
