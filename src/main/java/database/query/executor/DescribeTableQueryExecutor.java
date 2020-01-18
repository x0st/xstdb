package database.query.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import database.DataType;
import database.Table;
import database.TableFactory;
import database.contract.Query;
import database.contract.QueryExecutor;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.query.entity.DescribeTableQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;
import database.io.Reader;

public class DescribeTableQueryExecutor implements QueryExecutor<TableScheme, DescribeTableQuery> {
    private final TableFactory mTableFactory;
    private final IOFacilityFactory mIOFacilityFactory;

    public DescribeTableQueryExecutor(IOFacilityFactory ioFacilityFactory, TableFactory fac) {
        mTableFactory = fac;
        mIOFacilityFactory = ioFacilityFactory;
    }

    public TableScheme execute(DescribeTableQuery query) throws IOException, BadQueryException {
        try {
            return execute0(query);
        } catch (FileNotFoundException e) {
            throw BadQueryException.tableNotFound();
        }
    }

    private TableScheme execute0(DescribeTableQuery query) throws IOException {
        Table table = mTableFactory.make(query);
        Reader reader = mIOFacilityFactory.reader(table.getDefinitionFile());

        // the first 2 bytes are reserved for the integer
        // that represents the number of bytes allocated for table scheme
        reader.skipShort();

        int rowsCount = reader.readInt();
        char[] tableName = reader.readCharSequence();
        int columnsCount = reader.readByte();

        ColumnScheme[] columns = new ColumnScheme[columnsCount];

        for (int i = 0; i < columnsCount; i++) {
            columns[i] = new ColumnScheme(reader.readCharSequence(), DataType.valueOf(reader.readByte()), reader.readInt());
        }

        reader.close();

        return new TableScheme(tableName, rowsCount, columns);
    }

    @Override
    public boolean executes(Query query) {
        return query instanceof DescribeTableQuery;
    }
}
