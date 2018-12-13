package database.query.entity;

import database.contract.HasTableName;
import database.contract.Query;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;

public class CreateTableQuery implements Query, HasTableName {
    private TableScheme tableScheme;

    public CreateTableQuery(char[] table, ColumnScheme[] columnSchemeList) {
        tableScheme = new TableScheme(table, 0, columnSchemeList);
    }

    public TableScheme getTableScheme() {
        return tableScheme;
    }

    @Override
    public String getTableName() {
        return String.valueOf(tableScheme.getName());
    }
}