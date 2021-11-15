package database.query.entity;

import database.contract.Query;
import database.io.Util;
import io.mappedbus.MemoryMappedFile;

abstract public class AbstractQuery implements Query {
    String mUID;

    @Override
    public String getTrackingUID() {
        return mUID;
    }

    @Override
    public void setTrackingUID(String uid) {
        mUID = uid;
    }

    @Override
    public void write(MemoryMappedFile mem, long pos) {
        // write down how many bytes the uid takes
        mem.putInt(pos, mUID.length());
        // write down the uid
        mem.setBytes(pos + 4L, mUID.getBytes(), 0, mUID.length());

        writeIntoMemoryMappedFile(mem, pos + 4L + mUID.length());
    }

    @Override
    public void read(MemoryMappedFile mem, long pos) {
        // indicates how many bytes a tracking UID takes
        int trackingUIDLength = mem.getInt(pos);
        // buffer for a tracking uid
        byte[] trackingUIDInBytes = new byte[trackingUIDLength];
        // read a tracking UID
        mem.getBytes(pos + 4L, trackingUIDInBytes, 0, trackingUIDLength);

        mUID = Util.stringOutOfBytes(trackingUIDInBytes);

        recreateFromMemoryMappedFile(mem, pos +  4L + trackingUIDLength);
    }

    abstract protected void writeIntoMemoryMappedFile(MemoryMappedFile mem, long pos);
    abstract protected void recreateFromMemoryMappedFile(MemoryMappedFile mem, long pos);
}
