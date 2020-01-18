package database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import database.contract.Query;
import database.contract.QueryExecutor;
import database.contract.Tube;
import database.contract.Worker;
import database.io.IOFacilityFactory;
import database.query.executor.CreateTableQueryExecutor;
import database.query.executor.DeleteRowsQueryExecutor;
import database.query.executor.DescribeTableQueryExecutor;
import database.query.executor.InsertRowsQueryExecutor;
import database.query.executor.SelectRowsQueryExecutor;
import io.mappedbus.MappedBusReader;

public class QueryRunnerWorker implements Worker {
    private IOFacilityFactory mIOFacilityFactory;
    private TableFactory mTableFactory;
    private Tube mTube;

    private ArrayList<QueryExecutor> mQueryExecutors;

    private QueryRunnerWorker(String databasePath, Integer bufferSize) throws IOException {
        mTableFactory = new TableFactory(databasePath, new FileFactory());
        mIOFacilityFactory = new IOFacilityFactory(bufferSize);

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

                for (QueryExecutor mQueryExecutor : mQueryExecutors) {
                    if (mQueryExecutor.executes(query)) {

                        mQueryExecutor.execute(query);
                    }
                }
            }
        }
    }
}
