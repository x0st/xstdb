package database.scheme;

import java.util.List;

public class TableScheme {
    private String name;
    private Integer rowsCount;
    private List<ColumnScheme> columns;

    public TableScheme(String name, Integer rowsCount, List<ColumnScheme> columns) {
        this.name = name;
        this.columns = columns;
        this.rowsCount = rowsCount;
    }

    public String getName() {
        return name;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public List<ColumnScheme> getColumns() {
        return columns;
    }
}
