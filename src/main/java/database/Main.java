package database;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import database.contract.Record;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.query.QueryIdentifier;
import database.query.SelectRowsQueryOutput;
import database.query.executor.CreateTableQueryExecutor;
import database.query.executor.DeleteRowsQueryExecutor;
import database.query.executor.DescribeTableQueryExecutor;
import database.query.executor.InsertRowsQueryExecutor;
import database.query.executor.SelectRowsQueryExecutor;
import database.query.assember.CreateTableQueryAssembler;
import database.query.assember.DeleteRowsQueryAssembler;
import database.query.assember.InsertRowsQueryAssembler;
import database.query.assember.SelectRowsQueryAssembler;
import database.rawquery.parser.Lexer;

public class Main {
    private QueryIdentifier mQueryIdentifier = new QueryIdentifier();
    private IOFacilityFactory mIOFacilityFactory = new IOFacilityFactory(35000);
    private TableFactory mTableFactory = new TableFactory("/Users/pavel/xstdb", new FileFactory());

    public Main() throws FileNotFoundException {
    }

    public static void main(String[] args) throws IOException, BadQueryException {
        Main m = new Main();

//        m.createTableTest();
//        m.insertRowsTest();
//        m.selectRowsTest();
//        m.deleteRowsTest();

        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/pavel/xstdb/file", true));

        Thread thread1 = new Thread(() -> {
            try {
                writer.write("thread1\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                writer.write("thread2\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread3 = new Thread(() -> {
            try {
                writer.write("thread3\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        int i = 0;

        while (i != 3) {
            if (thread1.getState() == Thread.State.TERMINATED) {
                i++;
                System.out.println("thread1 - done");
            }
            if (thread2.getState() == Thread.State.TERMINATED) {
                i++;
                System.out.println("thread2 - done");
            }
            if (thread3.getState() == Thread.State.TERMINATED) {
                i++;
                System.out.println("thread3 - done");
            }
        }
    }

    public void deleteRowsTest() throws BadQueryException, IOException {
        Lexer lexer = new Lexer();
        lexer.setInput("delete users [id = :id] where [:id = 1]".toCharArray());

        DeleteRowsQueryExecutor executor = new DeleteRowsQueryExecutor(
                mTableFactory,
                mIOFacilityFactory,
                new DescribeTableQueryExecutor(mIOFacilityFactory, mTableFactory)
        );

        mQueryIdentifier.identify(lexer);
        executor.execute(new DeleteRowsQueryAssembler().assemble(lexer));
    }

    public void createTableTest() throws BadQueryException, IOException {
        Lexer lexer = new Lexer();
        lexer.setInput("add users [id INTEGER] [name STRING]".toCharArray());

        CreateTableQueryExecutor executor = new CreateTableQueryExecutor(mIOFacilityFactory, mTableFactory);

        mQueryIdentifier.identify(lexer);
        executor.execute(new CreateTableQueryAssembler().assemble(lexer));
    }

    public void insertRowsTest() throws BadQueryException, IOException {
        Lexer lexer = new Lexer();
        lexer.setInput("put users [id name] [1 alex] [2 pavel] [3 jimmy] [4 evan]".toCharArray());

        InsertRowsQueryExecutor executor = new InsertRowsQueryExecutor(
                mIOFacilityFactory,
                mTableFactory,
                new DescribeTableQueryExecutor(mIOFacilityFactory, mTableFactory)
        );

        mQueryIdentifier.identify(lexer);
        executor.execute(new InsertRowsQueryAssembler().assemble(lexer));
    }

    public void selectRowsTest() throws BadQueryException, IOException {
        Lexer lexer = new Lexer();
        lexer.setInput("get users [name = :name | id = :id] where [:name = pavel & :id = 4]".toCharArray());
//        lexer.setInput("get users".toCharArray());

        SelectRowsQueryExecutor executor = new SelectRowsQueryExecutor(
                mIOFacilityFactory,
                mTableFactory,
                new DescribeTableQueryExecutor(mIOFacilityFactory, mTableFactory)
        );

        mQueryIdentifier.identify(lexer);
        SelectRowsQueryOutput output = executor.execute(new SelectRowsQueryAssembler().assemble(lexer));

        LinkedList<Record> records = output.getRecords();

        for (Record record : records) {
            for (int i = 0; i < record.size(); i++) {
                if (record.get(i).getDataType() == DataType.STRING) {
                    System.out.print(record.get(i).getCharArrayValue());
                } else if (record.get(i).getDataType() == DataType.INTEGER) {
                    System.out.print(record.get(i).getIntegerValue());
                }

                System.out.print(" ");
            }

            System.out.print("\n");
        }
    }
}
