package database;

import java.io.File;

import database.contract.HasTableName;

public class TableFactory {
    private final File mDirPath;

    public TableFactory(String path) {
        mDirPath = new File(path);
    }

    public Table make(HasTableName v) {
        File data = new File(mDirPath, v.getTableName() + ".dat");
        File definition = new File(mDirPath, v.getTableName() + ".def");

        return new Table(data, definition);
    }
}
