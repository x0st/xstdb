package database;

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

    public String readASCII() throws IOException {
        int numberOfBytes = read4BytesNumber();
        byte[] stringAsBytes = new byte[numberOfBytes];

        read(stringAsBytes, 0, numberOfBytes);

        return new String(stringAsBytes, StandardCharsets.UTF_8);
    }

    public String readASCII(int length) throws IOException {
        int numberOfBytes = read4BytesNumber();
        byte[] stringAsBytes = new byte[numberOfBytes];

        read(stringAsBytes, 0, numberOfBytes);

        seek(getFilePointer() + length - numberOfBytes);

        return new String(stringAsBytes, StandardCharsets.UTF_8);
    }

    public void writeASCII(String string) throws IOException {
        byte[] bytes = string.getBytes();

        write4BytesNumber(bytes.length);

        for (byte b : bytes) {
            write(b);
        }
    }

    public void writeASCII(String string, int length) throws IOException {
        int i = 0;
        byte[] bytes = string.getBytes();

        write4BytesNumber(bytes.length);

        for (byte b : bytes) {
            write(b);
            i++;

            if (i >= length) {
                break;
            }
        }

        for (; i < length; i++) {
            write(0);
        }
    }
}
