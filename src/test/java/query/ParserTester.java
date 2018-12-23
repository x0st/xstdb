package query;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import database.query.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTester {
    @Test
    public void parsesJSONCorrectly() {
        Parser parser = new Parser();

        assertEquals("describe_table", parser.fromString("{\"type\": \"describe_table\",\"name\": \"users\"}").get("type"));
        assertEquals("users", parser.fromString("{\"type\": \"describe_table\",\"name\": \"users\"}").get("name"));

        assertEquals("insert_rows", parser.fromString("{\"type\": \"insert_rows\",\"name\": \"users\"}").get("type"));
        assertEquals("users", parser.fromString("{\"type\": \"insert_rows\",\"name\": \"users\"}").get("name"));
    }

    @Test
    public void throwsExceptionOnInvalidJSON() {
        Parser parser = new Parser();

        assertThrows(ValidationException.class, () -> parser.fromString("{\"typ\": \"insert_rows\",\"name\": \"users\"}"));
        assertThrows(ValidationException.class, () -> parser.fromString("{\"type\": true,\"name\": \"users\"}"));
        assertThrows(ValidationException.class, () -> parser.fromString("{\"type\": 5,\"name\": 1}"));
        assertThrows(ValidationException.class, () -> parser.fromString("{\"type\": \"select_rows\",\"name\": 1}"));
        assertThrows(ValidationException.class, () -> parser.fromString("{\"type\": \"insert_rows\",\"name123\": \"users\"}"));
        assertThrows(JSONException.class, () -> parser.fromString("{\"type\": \"insert_rows\",\"name123\""));
        assertThrows(JSONException.class, () -> parser.fromString("{\"type\": \"i"));
    }
}