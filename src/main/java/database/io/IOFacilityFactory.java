package database.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IOFacilityFactory {
    private final int mBufferSize;

    public IOFacilityFactory(int bufferSize) {
        mBufferSize = bufferSize;
    }

    public Reader reader(File file) throws FileNotFoundException {
        return new Reader(file, mBufferSize);
    }

    public Reader reader(InputStream stream) {
        return new Reader(stream, mBufferSize);
    }

    public Writer writer(File file) throws FileNotFoundException {
        return new Writer(file, mBufferSize);
    }

    public RAF randomAccessFile(File file) throws FileNotFoundException {
        return new RAF(file);
    }
}
