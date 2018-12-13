package database.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RAF {
    private final RandomAccessFile mRandomAccessFile;

    public RAF(File file) throws FileNotFoundException {
        mRandomAccessFile = new RandomAccessFile(file, "rw");
    }

    public void writeCharSequence(char[] chars) throws IOException {
        byte[] buffer = new byte[chars.length];

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte)chars[i];
        }

        writeInteger(buffer.length);
        mRandomAccessFile.write(buffer);
    }

    public void writeInteger(int value) throws IOException {
        mRandomAccessFile.write(new byte[] {
                (byte) ((value & 0xFF000000) >> 24),
                (byte) ((value & 0x00FF0000) >> 16),
                (byte) ((value & 0x0000FF00) >> 8),
                (byte) ((value & 0x000000FF) >> 0)
        });
    }

    public void writeByte(byte value) throws IOException {
        mRandomAccessFile.write(new byte[] {(byte) ((value & 0x000000FF) >> 0)});
    }

    public void writeShort(short value) throws IOException {
        mRandomAccessFile.write(new byte[] {
                (byte) ((value & 0x0000FF00) >> 8),
                (byte) ((value & 0x000000FF) >> 0)
        });
    }

    public long size() throws IOException {
        return mRandomAccessFile.length();
    }

    public void move(long pointer) throws IOException {
        mRandomAccessFile.seek(pointer);
    }

    public void close() throws IOException {
        mRandomAccessFile.close();
    }
}
