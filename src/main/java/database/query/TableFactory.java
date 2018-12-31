package database.query;

import java.io.File;
import java.io.FileNotFoundException;

import database.contract.HasTableName;

public class TableFactory {
    private final File mDirPath;
    private final FileFactory mFileFactory;

    public TableFactory(String databaseFolder, FileFactory fileFactory) throws FileNotFoundException {
        mFileFactory = fileFactory;
        mDirPath = mFileFactory.make(databaseFolder);

        if (!mDirPath.exists()) {
            throw new FileNotFoundException("Invalid file path");
        }
    }

    public Table make(HasTableName v) {
        File data = mFileFactory.make(mDirPath, v.getTableName() + ".dat");
        File definition = mFileFactory.make(mDirPath, v.getTableName() + ".def");

        return new Table(data, definition);
    }
}
