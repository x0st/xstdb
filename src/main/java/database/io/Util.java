package database.io;

public class Util {
    public static int integerOutOfBytes(byte[] buffer) {
        return buffer[0] << 24 | (buffer[1] & 0xFF) << 16 | (buffer[2] & 0xFF) << 8 | (buffer[3] & 0xFF);
    }

    public static short shortOutOfBytes(byte[] buffer) {
        return (short) ((buffer[0]) << 8 | (buffer[1] & 0xFF));
    }

    public static char[] charSequenceOutOfBytes(byte[] buffer) {
        char[] chars = new char[buffer.length];

        for (int i = 0; i < buffer.length; i++) {
            chars[i] = (char) buffer[i];
        }

        return chars;
    }

    public static String stringOutOfBytes(byte[] buffer) {
        return String.valueOf(Util.charSequenceOutOfBytes(buffer));
    }

    public static byte[] integerIntoBytes(int value) {
        return new byte[]{
                (byte) ((value & 0xFF000000) >> 24),
                (byte) ((value & 0x00FF0000) >> 16),
                (byte) ((value & 0x0000FF00) >> 8),
                (byte) ((value & 0x000000FF) >> 0)
        };
    }

    public static byte[] shortIntoBytes(short value) {
        return new byte[]{
                (byte) ((value & 0x0000FF00) >> 8),
                (byte) ((value & 0x000000FF) >> 0)
        };
    }
}
