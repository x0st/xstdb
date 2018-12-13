package database;

import java.io.File;

import database.contract.HasTableName;

public class TableFactory {
    private final File mDirPath;

    public TableFactory(String path) {
        mDirPath = new File(path);
    }

    public Table make(HasTableName v) {
        File data = new File(mDirPath, v.getTableName());
        File definition = new File(mDirPath, v.getTableName() + "_definition");

        return new Table(data, definition);
    }
}
