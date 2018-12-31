package database.query;

import java.io.File;

public class FileFactory {
    public File make(String path) {
        return new File(path);
    }

    public File make(File parent, String child) {
        return new File(parent, child);
    }
}
