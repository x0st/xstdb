package database.query.executor;

import java.io.IOException;

import database.Table;
import database.TableFactory;
import database.contract.Query;
import database.contract.QueryExecutor;
import database.exception.TableAlreadyExistsException;
import database.io.IOFacilityFactory;
import database.io.RAF;

import database.query.entity.CreateTableQuery;
import database.scheme.ColumnScheme;

public class CreateTableQueryExecutor implements QueryExecutor<Void, CreateTableQuery> {
    private final TableFactory mTableFactory;
    private final IOFacilityFactory mIOFacilityFactory;

    public CreateTableQueryExecutor(IOFacilityFactory ioFacilityFactory, TableFactory factory) {
        mIOFacilityFactory = ioFacilityFactory;
        mTableFactory = factory;
    }

    public Void execute(CreateTableQuery query) throws IOException, TableAlreadyExistsException {
        Table table = mTableFactory.make(query);

        createFiles(table);

        RAF randomAccessFile = mIOFacilityFactory.randomAccessFile(table.getDefinitionFile());

        ColumnScheme[] columns = query.getTableScheme().getColumns();

        randomAccessFile.writeShort((short)0); // number of bytes taken for schema definition
        randomAccessFile.writeInteger(0); // rows count
        randomAccessFile.writeCharSequence(query.getTableScheme().getName()); // table name
        randomAccessFile.writeByte((byte)columns.length); // number of columns

        for (ColumnScheme columnScheme : columns) {
            randomAccessFile.writeCharSequence(columnScheme.getName());
            randomAccessFile.writeByte((byte) columnScheme.getType().getType());
            randomAccessFile.writeInteger(columnScheme.getSize());
        }

        randomAccessFile.move(0);
        randomAccessFile.writeShort((short)randomAccessFile.size());
        randomAccessFile.close();

        return null;
    }

    @Override
    public boolean executes(Query query) {
        return query instanceof CreateTableQuery;
    }

    public void createFiles(Table table) throws IOException, TableAlreadyExistsException {
        if (!table.getDefinitionFile().createNewFile() || !table.getDataFile().createNewFile()) {
            throw new TableAlreadyExistsException("A table with this name already exists.");
        }
    }
}
