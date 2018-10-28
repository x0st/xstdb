package core;

import java.io.File;

public class DatabaseDir {
    private File directory;

    public DatabaseDir(File path) {
        this.directory = path;
    }

    public File getDirectory() {
        return directory;
    }
}
