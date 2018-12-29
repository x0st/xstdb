package database;

import java.util.concurrent.TimeUnit;

import database.query.entity.InsertRowsQuery;
import database.query.entity.SelectRowsQuery;
import database.query.maker.CreateTableQueryMaker;
import database.query.maker.InsertRowsQueryMaker;
import database.query.maker.SelectRowsQueryMaker;
import database.query.parser.Lexer;

public class Main {
    public static void main(String[] args) throws Exception {
        CreateTableQueryMaker ctqm = new CreateTableQueryMaker();
        SelectRowsQueryMaker srqm = new SelectRowsQueryMaker();
        InsertRowsQueryMaker irqm = new InsertRowsQueryMaker();

        while (true) {
            long t = System.currentTimeMillis();
            Lexer l = new Lexer("get users [name <> :name & id > :id] [:name 23 :id 5]".toCharArray());
            l.next();
            SelectRowsQuery srq = srqm.make(l);
            System.out.println(System.currentTimeMillis() - t);
            TimeUnit.SECONDS.sleep(2);
        }
//        while (l.next()) {
//            System.out.println("lexeme:  "  + String.valueOf(l.lexeme()));
//        }
    }
    /*
    TableFactory tableFactory = new TableFactory("/Users/pavel/java-pr/database");

    IOFacilityFactory ioFacilityFactory = new IOFacilityFactory();

    QueryExecutor<TableScheme, DescribeTableQuery> describeTableQueryExecutor = new DescribeTableQueryExecutor(ioFacilityFactory, tableFactory);
    QueryExecutor<Void, InsertRowsQuery> insertMultipleRowsQueryExecutor = new InsertRowsQueryExecutor(ioFacilityFactory, tableFactory, describeTableQueryExecutor);
    QueryExecutor<Void, CreateTableQuery> createTableQueryExecutor = new CreateTableQueryExecutor(ioFacilityFactory, tableFactory);
    QueryExecutor<SelectRowsQueryOutput, SelectRowsQuery> selectRowsQueryExecutor = new SelectRowsQueryExecutor(ioFacilityFactory, tableFactory, describeTableQueryExecutor);

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        long m = System.currentTimeMillis();

        Properties pp = new Properties();
//        pp.load();
//        main.createTable();
//        main.describeTable();
        main.selectRows();

//        main.insertMultipleRows(main.generateRecords());

        System.out.println(System.currentTimeMillis() - m);
    }

    private void selectRows() throws BadQueryException, IOException {
        PlaceholderObject attributes = new PlaceholderObject(1);
        attributes.set(":name", new Value("pavl".toCharArray(), DataType.STRING));

        SelectRowsQuery query = new SelectRowsQuery("users", attributes, "name = :name");

        System.out.println(selectRowsQueryExecutor.execute(query).getRecords().size());
    }

    private List<ExternalRow> generateRecords() {
        List<ExternalRow> records = new java.util.LinkedList<>();

        for (int i = 0; i < 100000; i++) {
            ExternalRow row = new ExternalRow(2);

            row.add(new Value("pavlo".toCharArray(), DataType.STRING));
            row.add(new Value(25, DataType.INTEGER));

            records.add(row);
        }

        return records;
    }

    private void insertMultipleRows(List<ExternalRow> records) throws BadQueryException, IOException {
        List<String> columnOrder = new ArrayList<>();
        columnOrder.add("name");
        columnOrder.add("id");

        InsertRowsQuery query = new InsertRowsQuery("users", columnOrder, records);

        insertMultipleRowsQueryExecutor.execute(query);
    }

    private void describeTable() throws BadQueryException, IOException {
        DescribeTableQuery query = new DescribeTableQuery("users");

        TableScheme tableScheme = describeTableQueryExecutor.execute(query);

        System.out.println("Name: " + Arrays.toString(tableScheme.getName()));
        System.out.println("Number of rows: " + tableScheme.getRowsCount());
        System.out.println("Number of columns: " + tableScheme.getColumns().length);

        System.out.println("");

        for (ColumnScheme columnScheme : tableScheme.getColumns()) {
            System.out.println("Column name: " + Arrays.toString(columnScheme.getName()));
            System.out.println("Column type: " + columnScheme.getType());
            System.out.println("Column size: " + columnScheme.getSize());
            System.out.println("");
        }
    }

    private void createTable() throws BadQueryException, IOException {
        ColumnScheme[] columns = new ColumnScheme[2];

        columns[0] = new ColumnScheme("id".toCharArray(), DataType.INTEGER, 0);
        columns[1] = new ColumnScheme("name".toCharArray(), DataType.STRING, 0);

        CreateTableQuery query = new CreateTableQuery("users".toCharArray(), columns);

        createTableQueryExecutor.execute(query);
    }
    */
}
