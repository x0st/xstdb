package database.dagger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import database.FileFactory;
import database.TableFactory;
import database.contract.QueryExecutor;
import database.contract.QueryMaker;
import database.io.IOFacilityFactory;
import database.query.QueryIdentifier;
import database.query.executor.CreateTableQueryExecutor;
import database.query.executor.DescribeTableQueryExecutor;
import database.query.executor.InsertRowsQueryExecutor;
import database.query.executor.SelectRowsQueryExecutor;
import database.query.maker.CreateTableQueryMaker;
import database.query.maker.DescribeTableQueryMaker;
import database.query.maker.InsertRowsQueryMaker;
import database.query.maker.SelectRowsQueryMaker;


@Module
public class AppModule {
    private final Properties mProperties;

    public AppModule(Properties properties) {
        mProperties = properties;
    }

    @Singleton
    @Provides
    TableFactory provideTableFactory(FileFactory fileFactory) {
        try {
            return new TableFactory(
                    mProperties.getProperty("dir"),
                    fileFactory
            );
        } catch (FileNotFoundException ignore) { return null; }
    }

    @Singleton
    @Provides
    QueryIdentifier provideQueryIdentifier() {
        return new QueryIdentifier();
    }

    @Singleton
    @Provides
    List<QueryMaker> providePoolOfQueryMakers() {
        List<QueryMaker> list = new ArrayList<>(4);

        list.add(new CreateTableQueryMaker());
        list.add(new InsertRowsQueryMaker());
        list.add(new DescribeTableQueryMaker());
        list.add(new SelectRowsQueryMaker());

        return list;
    }

    @Singleton
    @Provides
    List<QueryExecutor> providePoolOfQueryExecutors(
            CreateTableQueryExecutor createTableQueryExecutor,
            DescribeTableQueryExecutor describeTableQueryExecutor,
            InsertRowsQueryExecutor insertRowsQueryExecutor,
            SelectRowsQueryExecutor selectRowsQueryExecutor
    ) {
        List<QueryExecutor> poolOfExecutors = new ArrayList<>(4);

        poolOfExecutors.add(createTableQueryExecutor);
        poolOfExecutors.add(describeTableQueryExecutor);
        poolOfExecutors.add(insertRowsQueryExecutor);
        poolOfExecutors.add(selectRowsQueryExecutor);

        return poolOfExecutors;
    }

    @Singleton
    @Provides
    FileFactory provideFileFactory() {
        return new FileFactory();
    }

    @Singleton
    @Provides
    IOFacilityFactory provideIOFacilityFactory() {
        return new IOFacilityFactory(
                Integer.parseInt(mProperties.getProperty("buffer_size"))
        );
    }

    @Singleton
    @Provides
    DescribeTableQueryExecutor provideDescribeTableQueryExecutor(
            IOFacilityFactory ioFacilityFactory,
            TableFactory tableFactory
    ) {
        return new DescribeTableQueryExecutor(ioFacilityFactory, tableFactory);
    }

    @Singleton
    @Provides
    SelectRowsQueryExecutor provideSelectRowsQueryExecutor(
            IOFacilityFactory ioFacilityFactory,
            DescribeTableQueryExecutor describeTableQueryExecutor,
            TableFactory tableFactory
    ) {
        return new SelectRowsQueryExecutor(ioFacilityFactory, tableFactory, describeTableQueryExecutor);
    }

    @Singleton
    @Provides
    InsertRowsQueryExecutor provideInsertRowsQueryExecutor(
            IOFacilityFactory ioFacilityFactory,
            DescribeTableQueryExecutor describeTableQueryExecutor,
            TableFactory tableFactory
    ) {
        return new InsertRowsQueryExecutor(ioFacilityFactory, tableFactory, describeTableQueryExecutor);
    }

    @Singleton
    @Provides
    CreateTableQueryExecutor provideCreateTableQueryExecutor(
            IOFacilityFactory ioFacilityFactory,
            TableFactory tableFactory
    ) {
        return new CreateTableQueryExecutor(ioFacilityFactory, tableFactory);
    }
}
