package database.query;

import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;

import database.contract.Query;
import database.contract.Tube;
import database.query.entity.CreateTableQuery;
import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;

public class QueryTransport implements Tube {
    private MappedBusReader mMappedBusReader;
    private MappedBusWriter mMappedBusWriter;

    private QueryTransport(MappedBusWriter mappedBusWriter) throws IOException {
        mMappedBusWriter = mappedBusWriter;
        mMappedBusWriter.open();
    }

    private QueryTransport(MappedBusReader mappedBusReader) throws IOException {
        mMappedBusReader = mappedBusReader;
        mMappedBusReader.open();
    }

    @Override
    public String push(Query query) throws IOException {
        query.setTrackingUID(UUID.randomUUID().toString());

        mMappedBusWriter.write(query);

        return query.getTrackingUID();
    }

    @Override
    public Query pop() {
        Query query = null;

        if (mMappedBusReader.readType() == QueryType.ADD.getType()) {
            query = CreateTableQuery.Factory.makeFromMemoryMappedFile(mMappedBusReader);
        }

        return query;
    }

    @Override
    public boolean has() {
        try {
            return mMappedBusReader.next();
        } catch (EOFException e) {
            return false;
        }
    }

    public static class Builder {
        public static Tube forWriting() throws IOException {
            return new QueryTransport(new MappedBusWriter("/tmp/xstdb", 10000L, 256));
        }

        public static Tube forReading() throws IOException {
            return new QueryTransport(new MappedBusReader("/tmp/xstdb", 10000L, 256));
        }
    }
}
