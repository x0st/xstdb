package database.query.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import database.contract.HasTableName;
import database.contract.Query;
import database.exception.BuilderException;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private char[] table = null;
        private List<ColumnScheme> columnSchemeList = new ArrayList<>(10);

        public Builder table(char[] value) {
            table = value;
            return this;
        }

        public Builder column(ColumnScheme value) {
            columnSchemeList.add(value);
            return this;
        }

        public CreateTableQuery build() throws BuilderException {
            if (table == null || columnSchemeList.size() == 0) {
                throw new BuilderException();
            }

            ColumnScheme[] columnSchemes = new ColumnScheme[columnSchemeList.size()];

            for (int i = 0; i < columnSchemeList.size(); i++) {
                columnSchemes[i] = columnSchemeList.get(i);
            }

            return new CreateTableQuery(table, columnSchemes);
        }
    }
}
