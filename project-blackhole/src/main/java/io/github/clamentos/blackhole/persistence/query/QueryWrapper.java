package io.github.clamentos.blackhole.persistence.query;

//________________________________________________________________________________________________________________________________________

import java.sql.ResultSet;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Custom query object that acts as a bridge between the servlets and the query workers.</p>
 * The provided getter and setter methods are all standard and thread safe.
*/
public class QueryWrapper {

    private AtomicInteger status;    // 0: not executed yet, 1: OK, -1: ERROR, exception will be set
    private Exception exception;
    private QueryType query_type;
    private String sql;
    private List<List<Object>> parameters;
    private ResultSet result;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiate a new {@link QueryWrapper} with the given parameters.
     * @param query_type : The query type, from the {@link QueryType} enum.
     * @param sql : The actual SQL string. Use the '?' to denote a parameter.
     * @param parameters : The query parameter list. Parameters must be ordered correctly.
    */
    public QueryWrapper(QueryType query_type, String sql, List<List<Object>> parameters) {

        status = new AtomicInteger(0);
        exception = null;
        this.query_type = query_type;
        this.sql = sql;
        this.parameters = parameters;
        result = null;
    }

    //____________________________________________________________________________________________________________________________________

    public int getStatus() {

        return(status.get());
    }

    public Exception getException() {

        return(exception);
    }
    
    public QueryType getQueryType() {

        return(query_type);
    }
    
    public String getSql() {

        return(sql);
    }

    public List<List<Object>> getParameters() {

        return(parameters);
    }

    public ResultSet getResult() {

        return(result);
    }

    //____________________________________________________________________________________________________________________________________

    public void setStatus(int status) {

        this.status.set(status);
    }

    public void setException(Exception exception) {

        this.exception = exception;
    }
    
    public void setResult(ResultSet result) {

        this.result = result;
    }

    //____________________________________________________________________________________________________________________________________
}
