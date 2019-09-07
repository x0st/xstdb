package database;

public enum QueryType {
    // create a table
    ADD,
    // insert a record
    PUT,
    // show info about a table
    SHOW,
    // select records from a table
    GET,
    // delete records from a table
    DELETE
}
