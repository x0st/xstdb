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
import database.contract.Query;
import database.contract.QueryExecutor;
import database.contract.QueryTransformer;
import database.io.IOFacilityFactory;
import database.query.Parser;
import database.query.entity.InsertRowsQuery;
import database.query.executor.CreateTableQueryExecutor;
import database.query.executor.DescribeTableQueryExecutor;
import database.query.executor.InsertRowsQueryExecutor;
import database.query.executor.SelectRowsQueryExecutor;
import database.query.transformer.CreateTableQueryTransformer;
import database.query.transformer.DescribeTableQueryTransformer;
import database.query.transformer.InsertRowsQueryTransformer;
import database.query.transformer.SelectRowsQueryTransformer;

@Module
public class AppModule {
    private final Properties mProperties;

    public AppModule(Properties properties) {
        mProperties = properties;
    }

    @Singleton
    @Provides
    Parser provideParser() {
        return new Parser();
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
    List<QueryTransformer> providePoolOfQueryTransformers() {
        List<QueryTransformer> poolOfTransformers = new ArrayList<>(4);

        poolOfTransformers.add(new CreateTableQueryTransformer());
        poolOfTransformers.add(new DescribeTableQueryTransformer());
        poolOfTransformers.add(new InsertRowsQueryTransformer());
        poolOfTransformers.add(new SelectRowsQueryTransformer());

        return poolOfTransformers;
    }

    @Singleton
    @Provides
    CreateTableQueryTransformer provideCreateTableQueryTransformer() {
        return new CreateTableQueryTransformer();
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
