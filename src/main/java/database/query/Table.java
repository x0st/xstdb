package database.query;

import java.io.File;

public class Table {
    private final File mDataFile;
    private final File mDefinitionFile;

    public Table(File dataFile, File definitionFile) {
        mDataFile = dataFile;
        mDefinitionFile = definitionFile;
    }

    public File getDataFile() {
        return mDataFile;
    }

    public File getDefinitionFile() {
        return mDefinitionFile;
    }
}
