package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RandomAccessFile extends java.io.RandomAccessFile {
    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public void writeIntegerAt(long pos, Integer number) throws IOException {
        seek(pos);

        writeInt(number);
    }

    public void writeASCII(String string) throws IOException {
        for (byte b : string.getBytes()) {
            write(b);
        }
    }

    public void writeASCIIAt(long pos, String string) throws IOException {
        seek(pos);

        for (byte b : string.getBytes()) {
            write(b);
        }
    }
}
