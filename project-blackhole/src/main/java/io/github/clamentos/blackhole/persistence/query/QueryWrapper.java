package io.github.clamentos.blackhole.persistence.query;

//________________________________________________________________________________________________________________________________________

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Custom query object that acts as a bridge between the servlets and the query workers.</p>
 * The provided getter and setter methods are all standard and thread safe.
*/
public class QueryWrapper {

    private Boolean status;
    private String error_cause;
    private QueryType query_type;
    private String sql;
    private List<Object> parameters;
    private ResultSet result;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiate a new {@link QueryWrapper} with the given parameters.
     * @param query_type : The query type, from the {@link QueryType} enum.
     * @param sql : The actual SQL string. Use the '?' to denote a parameter.
     * @param parameters : The query parameter list. Parameters must be ordered correctly.
    */
    public QueryWrapper(QueryType query_type, String sql, List<Object> parameters) {

        status = null;
        error_cause = null;
        this.query_type = query_type;
        this.sql = sql;
        this.parameters = parameters;
        result = null;
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiate a new {@link QueryWrapper} with the given parameters.
     * @param query_type : The query type, from the {@link QueryType} enum.
     * @param sql : The actual SQL string. Use the '?' to denote a parameter.
     * @param parameter : The query parameter.
    */
    public QueryWrapper(QueryType query_type, String sql, Object parameter) {

        status = null;
        error_cause = null;
        this.query_type = query_type;
        this.sql = sql;
        parameters = new ArrayList<>();
        result = null;

        parameters.add(parameter);
    }

    //____________________________________________________________________________________________________________________________________

    public Boolean getStatus() {

        return(status);
    }

    public String getErrorCause() {

        return(error_cause);
    }
    
    public QueryType getQueryType() {

        return(query_type);
    }
    
    public String getSql() {

        return(sql);
    }

    public List<Object> getParameters() {

        return(parameters);
    }

    public ResultSet getResult() {

        return(result);
    }

    //____________________________________________________________________________________________________________________________________

    public void setStatus(Boolean status) {

        this.status = status;
    }

    public void setErrorCause(String error_cause) {

        this.error_cause = error_cause;
    }
    
    public void setResult(ResultSet result) {

        this.result = result;
    }

    //____________________________________________________________________________________________________________________________________
}
