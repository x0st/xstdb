package database.query;

import java.util.List;

import database.contract.Query;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;

public class CreateTableQuery implements Query {
    private TableScheme tableScheme;

    public CreateTableQuery(String table, List<ColumnScheme> columnSchemeList) {
        tableScheme = new TableScheme(table, 0, columnSchemeList);
    }

    public TableScheme getTableScheme() {
        return tableScheme;
    }
}
