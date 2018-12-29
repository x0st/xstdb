package query.executor;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import database.DataType;
import database.FileFactory;
import database.Table;
import database.TableFactory;
import database.contract.HasTableName;
import database.exception.TableAlreadyExistsException;
import database.io.IOFacilityFactory;
import database.io.RAF;
import database.query.entity.CreateTableQuery;
import database.query.executor.CreateTableQueryExecutor;
import database.scheme.ColumnScheme;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateTableQueryExecutorTester {
    private TableFactory mockTableFactory(boolean createNewFile) throws IOException {
        TableFactory tableFactory = mock(TableFactory.class);
        Table table = mock(Table.class);
        File file = mock(File.class);

        when(file.createNewFile()).thenReturn(createNewFile);
        when(table.getDefinitionFile()).thenReturn(file);
        when(table.getDataFile()).thenReturn(file);
        when(tableFactory.make(any(HasTableName.class))).thenReturn(table);

        return tableFactory;
    }

    @Test
    public void throwsTableAlreadyExistsException() throws IOException {
        CreateTableQueryExecutor createTableQueryExecutor;
        IOFacilityFactory ioFacilityFactory;
        CreateTableQuery createTableQuery;
        TableFactory tableFactory;
        RAF raf;

        char[] tableName;

        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        tableName = "test_table".toCharArray();

        raf = mock(RAF.class);
        tableFactory = mockTableFactory(false);
        ioFacilityFactory = mock(IOFacilityFactory.class);

        when(ioFacilityFactory.randomAccessFile(any(File.class))).thenReturn(raf);

        columnSchemes[0] = new ColumnScheme("id".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("login".toCharArray(), DataType.STRING);

        createTableQuery = new CreateTableQuery(tableName, columnSchemes);
        createTableQueryExecutor = new CreateTableQueryExecutor(ioFacilityFactory, tableFactory);

        assertThrows(TableAlreadyExistsException.class, () -> createTableQueryExecutor.execute(createTableQuery));
    }

    @Test
    public void writesBytesCorrectly() throws IOException, TableAlreadyExistsException {
        CreateTableQueryExecutor createTableQueryExecutor;
        IOFacilityFactory ioFacilityFactory;
        CreateTableQuery createTableQuery;
        TableFactory tableFactory;
        InOrder inOrder;
        RAF raf;

        char[] tableName;

        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        tableName = "test_table".toCharArray();

        raf = mock(RAF.class);
        tableFactory = mockTableFactory(true);
        ioFacilityFactory = mock(IOFacilityFactory.class);
        inOrder = Mockito.inOrder(raf);

        when(ioFacilityFactory.randomAccessFile(any(File.class))).thenReturn(raf);

        columnSchemes[0] = new ColumnScheme("id".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("login".toCharArray(), DataType.STRING);

        createTableQuery = new CreateTableQuery(tableName, columnSchemes);
        createTableQueryExecutor = new CreateTableQueryExecutor(ioFacilityFactory, tableFactory);

        createTableQueryExecutor.execute(createTableQuery);

        inOrder.verify(raf).writeShort(eq((short)0));
        inOrder.verify(raf).writeInteger(eq(0));
        inOrder.verify(raf).writeCharSequence(eq(tableName));
        inOrder.verify(raf).writeByte(eq((byte)columnSchemes.length));

        for (ColumnScheme columnScheme : columnSchemes) {
            inOrder.verify(raf).writeCharSequence(eq(columnScheme.getName()));
            inOrder.verify(raf).writeByte(eq((byte)columnScheme.getType().getType()));
            inOrder.verify(raf).writeInteger(eq(columnScheme.getSize()));
        }

        inOrder.verify(raf).move(eq((long)0));
        inOrder.verify(raf).writeShort(eq((short)raf.size()));
        inOrder.verify(raf).close();
    }
}
