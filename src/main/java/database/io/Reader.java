package database.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader {
    private BufferedInputStream mInputStream;

    public Reader(File file, int bufferSize) throws FileNotFoundException {
        mInputStream = new BufferedInputStream(new FileInputStream(file), bufferSize);
    }

    public void skip(long skip) throws IOException {
        mInputStream.skip(skip);
    }

    public void skipShort() throws IOException {
        skip(2);
    }

    public int readBytes(byte buffer[]) throws IOException {
        return mInputStream.read(buffer);
    }

    public char[] readCharSequence(int skip) throws IOException {
        byte[] buffer = new byte[readInt()];

        mInputStream.read(buffer, 0, buffer.length);
        mInputStream.skip(skip - buffer.length);

        return Util.charSequenceOutOfBytes(buffer);
    }

    public char[] readCharSequence() throws IOException {
        byte[] buffer = new byte[readInt()];

        mInputStream.read(buffer, 0, buffer.length);

        return Util.charSequenceOutOfBytes(buffer);
    }

    public int readInt() throws IOException {
        byte[] buffer = new byte[4];

        mInputStream.read(buffer, 0, buffer.length);

        return Util.integerOutOfBytes(buffer);
    }

    public short readShort() throws IOException {
        byte[] buffer = new byte[2];

        mInputStream.read(buffer, 0, buffer.length);

        return Util.shortOutOfBytes(buffer);
    }

    public byte readByte() throws IOException {
        byte[] buffer = new byte[1];

        mInputStream.read(buffer, 0, buffer.length);

        return (byte)(buffer[0] & 0xFF);
    }

    public void close() {
        try {
            mInputStream.close();
        } catch (IOException ignore) { }
    }
}
