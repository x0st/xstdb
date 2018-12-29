package database.query.executor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import database.DataType;
import database.Table;
import database.TableFactory;
import database.contract.Query;
import database.contract.QueryExecutor;
import database.contract.Record;
import database.contract.ValueHolder;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.query.entity.DescribeTableQuery;
import database.query.entity.InsertRowsQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;
import database.io.RAF;
import database.io.Writer;

public class InsertRowsQueryExecutor implements QueryExecutor<Void, InsertRowsQuery> {
    private final TableFactory mTableFactory;
    private final IOFacilityFactory mIOFacilityFactory;
    private final QueryExecutor<TableScheme, DescribeTableQuery> mDescribeTableQueryExecutor;

    public InsertRowsQueryExecutor(IOFacilityFactory ioFacilityFactory, TableFactory tableFactory, QueryExecutor<TableScheme, DescribeTableQuery> describeTableQueryExecutor) {
        mIOFacilityFactory = ioFacilityFactory;
        mTableFactory = tableFactory;
        mDescribeTableQueryExecutor = describeTableQueryExecutor;
    }

    @Override
    public Void execute(InsertRowsQuery query) throws BadQueryException, IOException {
        Table table;
        TableScheme tableScheme;

        int[] columnOrderMap;

        table = mTableFactory.make(query);
        tableScheme = mDescribeTableQueryExecutor.execute(new DescribeTableQuery(query.getTableName()));
        columnOrderMap = makeColumnOrderMap(query, tableScheme);

        insertRows(query, tableScheme, table, columnOrderMap);
        updateRowsCount(tableScheme, table, query.getData().size());

        return null;
    }

    private int[] makeColumnOrderMap(InsertRowsQuery query, TableScheme tableScheme) {
        List<String> desiredColumnOrder;
        ColumnScheme[] columnSchemes;

        int[] map;

        desiredColumnOrder = query.getColumnOrder();
        columnSchemes = tableScheme.getColumns();

        map = new int[columnSchemes.length];

        for (int i = 0; i < desiredColumnOrder.size(); i++) {
            for (int j = 0; j < columnSchemes.length; j++) {
                if (desiredColumnOrder.get(i).hashCode() == columnSchemes[j].getNameHash()) {
                    map[j] = i;
                }
            }
        }

        return map;
    }

    private void updateRowsCount(TableScheme tableScheme, Table table, int add) throws IOException {
        RAF randomAccessFile = mIOFacilityFactory.randomAccessFile(table.getDefinitionFile());

        randomAccessFile.move(2);
        randomAccessFile.writeInteger(tableScheme.getRowsCount() + add);
        randomAccessFile.close();
    }

    private void insertRows(InsertRowsQuery query, TableScheme tableScheme, Table table, int[] columnOrderMap) throws IOException {
        Writer writer;
        ValueHolder cellValue;
        ColumnScheme[] columns;
        Record externalRow;
        Iterator<Record> rowsIterator;

        writer = mIOFacilityFactory.writer(table.getDataFile());
        columns = tableScheme.getColumns();
        rowsIterator = query.getData().iterator();

        while (rowsIterator.hasNext()) {
            externalRow = rowsIterator.next();

            for (int i = 0; i < columns.length; i++) {
                cellValue = externalRow.get(columnOrderMap[i]);

                if (columns[i].getType() == DataType.INTEGER) {
                    writer.writeInt(cellValue.getIntegerValue());
                } else if (columns[i].getType() == DataType.STRING) {
                    writer.writeInt(cellValue.hash());
                    writer.writeCharSequence(cellValue.getCharArrayValue(), DataType.STRING.getSize());
                }
            }
        }

        writer.flush();
        writer.close();
    }

    @Override
    public boolean executes(Query query) {
        return query instanceof InsertRowsQuery;
    }
}
