package database.io;

import java.io.File;
import java.io.FileNotFoundException;

public class IOFacilityFactory {
    private int bufferSize = 1024 * 64;

    public Reader reader(File file) throws FileNotFoundException {
        return new Reader(file, bufferSize);
    }

    public Writer writer(File file) throws FileNotFoundException {
        return new Writer(file, bufferSize);
    }

    public RAF randomAccessFile(File file) throws FileNotFoundException {
        return new RAF(file);
    }
}
