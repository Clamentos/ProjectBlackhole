package io.github.clamentos.blackhole.persistence;

///
// Enumeration of ALL possible queries (still have to write them all)
public enum Queries {

    ///
    INSERT_TAGS("INSERT INTO Tags(name, creation_date) VALUES(?, ?)"),
    SELECT_TAGS_BY_IDS("SELECT * FROM Tags WHERE id = ANY ?"),    // use sql Array type. same thing as "id IN (?,?,?...)"
    SELECT_TAGS_BY_DATE("SELECT * FROM Tags WHERE id >= ? AND creation_date BETWEEN ? AND ? LIMIT ?"),
    SELECT_TAGS_BY_NAME("SELECT * FROM Tags WHERE id >= ? AND name LIKE ? LIMIT ?"),
    SELECT_TAGS_BY_NAME_DATE("SELECT * FROM Tags WHERE id >= ? AND name LIKE ? AND creation_date BETWEEN ? AND ? LIMIT ?"),
    COUNT_TAGS("COUNT(*) FROM Tags"),
    UPDATE_TAGS("UPDATE Tags SET name = ? WHERE id = ?"),
    DELETE_TAGS("DELETE FROM Tags WHERE id = ?");

    ///
    // others...

    ///
    private String query;

    ///
    private Queries(String query) {

        this.query = query;
    }

    ///
    public String getQuery() {

        return(query);
    }

    ///
}
