package database;

import java.io.IOException;
import java.util.ArrayList;

import database.contract.Query;
import database.contract.QueryExecutorInterface;
import database.contract.Tube;
import database.contract.Worker;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.query.QueryTransport;
import database.query.executor.CreateTableQueryExecutor;
import database.query.executor.DeleteRowsQueryExecutor;
import database.query.executor.DescribeTableQueryExecutor;
import database.query.executor.InsertRowsQueryExecutor;
import database.query.executor.SelectRowsQueryExecutor;

public class QueryRunnerWorker implements Worker {
    private Tube mTube;

    private ArrayList<QueryExecutorInterface> mQueryExecutors;

    private QueryRunnerWorker(String databasePath, Integer bufferSize) throws IOException {
        TableFactory mTableFactory = new TableFactory(databasePath, new FileFactory());
        IOFacilityFactory mIOFacilityFactory = new IOFacilityFactory(bufferSize);

        mTube = QueryTransport.Builder.forReading();

        mQueryExecutors = new ArrayList<>();

        mQueryExecutors.add(new DescribeTableQueryExecutor(mIOFacilityFactory, mTableFactory));
        mQueryExecutors.add(new CreateTableQueryExecutor(mIOFacilityFactory, mTableFactory));
        mQueryExecutors.add(new SelectRowsQueryExecutor(mIOFacilityFactory, mTableFactory, (DescribeTableQueryExecutor)mQueryExecutors.get(0)));
        mQueryExecutors.add(new InsertRowsQueryExecutor(mIOFacilityFactory, mTableFactory, (DescribeTableQueryExecutor)mQueryExecutors.get(0)));
        mQueryExecutors.add(new DeleteRowsQueryExecutor(mTableFactory, mIOFacilityFactory, (DescribeTableQueryExecutor)mQueryExecutors.get(0)));
    }

    public static void main(String[] args) throws Throwable {
        String databasePath;
        Integer bufferSize = 35000;

        if (0 == args.length) {
            throw new RuntimeException("A path to a directory must be specified.");
        }

        for (int i = 1; i < args.length; i++) {
            if ("--buffer".equals(args[i])) {
                bufferSize = Integer.valueOf(args[i + 1]);
            }
        }

        databasePath = args[0];

        new QueryRunnerWorker(databasePath, bufferSize).run();
    }

    @Override
    public void run() throws Throwable {
        Query query;

        while (true) {
            if (mTube.has()) {
                query = mTube.pop();

                for (QueryExecutorInterface queryExecutor : mQueryExecutors) {
                    if (queryExecutor.executes(query)) {
                        try {
                            queryExecutor.execute(query);

                            System.out.println(String.format("Processed a query"));
                        } catch (BadQueryException badQueryException) {
                            System.out.println(String.format("BadQueryException: %s", badQueryException.getMessage()));
                        } catch (IOException ioException) {
                            System.out.println(String.format("IOException: %s", ioException.getMessage()));
                        }
                    }
                }
            }
        }
    }
}
