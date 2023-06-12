package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import java.sql.ResultSet;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * Custom query object that acts as a bridge between the servlets and the query workers.
*/
// TODO: finish docs
public class QueryWrapper {

    private Boolean status;
    private QueryType query_type;
    private String sql;
    private List<Object> parameters;
    private ResultSet result;

    //____________________________________________________________________________________________________________________________________

    public QueryWrapper(QueryType query_type, String sql, List<Object> parameters) {

        status = null;
        this.query_type = query_type;
        this.sql = sql;
        this.parameters = parameters;
        result = null;
    }

    //____________________________________________________________________________________________________________________________________

    public Boolean getStatus() {

        return(status);
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
    
    public void setResult(ResultSet result) {

        this.result = result;
    }

    //____________________________________________________________________________________________________________________________________
}
