package database.contract;

import java.io.IOException;

public interface Tube {
    public String push(Query query) throws IOException;
    public Query pop() throws IOException;
    public boolean has();
}
