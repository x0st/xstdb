package database.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Writer {
    private BufferedOutputStream mOutputStream;

    public Writer(File file, int bufferSize) throws FileNotFoundException {
        mOutputStream = new BufferedOutputStream(new FileOutputStream(file, true), bufferSize);
    }

    public void writeCharSequence(char[] chars) throws IOException {
        byte[] buffer = new byte[chars.length];

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte)chars[i];
        }

        writeInt(buffer.length);
        mOutputStream.write(buffer);
    }

    public void writeCharSequence(char[] chars, int skip) throws IOException {
        int i = 0;
        byte[] buffer = new byte[skip];

        for (; i < chars.length; i++) {
            buffer[i] = (byte)chars[i];
        }

        for (; i < skip; i++) {
            buffer[i] = '\0';
        }

        writeInt(chars.length);
        mOutputStream.write(buffer);
    }

    public void writeInt(int value) throws IOException {
        mOutputStream.write(Util.integerIntoBytes(value));
    }

    public void writeByte(byte value) throws IOException {
        mOutputStream.write(new byte[] {value});
    }

    public void writeShort(short value) throws IOException {
        mOutputStream.write(Util.shortIntoBytes(value));
    }

    public void flush() {
        try {
            mOutputStream.flush();
        } catch (IOException ignore) { }
    }

    public void close() {
        try {
            mOutputStream.close();
        } catch (IOException ignore) { }
    }
}
