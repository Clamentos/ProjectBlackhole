package io.github.clamentos.blackhole.persistence;

// Enumeration of SLL possible queries
public enum Queries {
    
    INSERT_TAG("INSERT INTO tags(name, creation_date) VALUES(?, ?)"),
    SELECT_TAG_0("SELECT * FROM tags WHERE id ANY (?)");

    private String query;

    private Queries(String query) {

        this.query = query;
    }

    public String getQuery() {

        return(query);
    }
}
