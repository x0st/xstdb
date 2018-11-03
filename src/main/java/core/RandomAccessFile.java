package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RandomAccessFile extends java.io.RandomAccessFile {
    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public void write4BytesNumber(Integer number) throws IOException {
        write((number >>> 24) & 0xFF);
        write((number >>> 16) & 0xFF);
        write((number >>>  8) & 0xFF);
        write((number >>>  0) & 0xFF);
    }

    public void write1ByteNumber(int number) throws IOException {
        write((number >>>  0) & 0xFF);
    }

    public int read4BytesNumber() throws IOException {
        return readInt();
    }

    public int read1ByteNumber() throws IOException {
        return read();
    }

    public String readASCII(int length) throws IOException {
        int i;
        byte[] container = new byte[length];
        read(container, 0, length);

        for (i = 0; i < container.length; i++) {
            if (container[i] == 0) {
                break;
            }
        }

        byte[] a = new byte[i];

        for (int j = 0; j < i; j++) {
            a[j] = container[j];
        }

        return new String(a, StandardCharsets.UTF_8);
    }

    public void writeASCII(String string) throws IOException {
        for (byte b : string.getBytes()) {
            write(b);
        }
    }
}
