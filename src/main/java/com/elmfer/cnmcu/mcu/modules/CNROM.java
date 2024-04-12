package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

/**
 * Reference to a CNROM object it is a weak reference, so it will be invalidated
 * if the native object is deleted.
 */
public class CNROM extends WeakNativeObject {
    
    private final long size;
    
    private boolean writeProtected;
    
    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected CNROM(long ptr) {
        setNativePtr(ptr);
        
        size = size(getNativePtr());
        writeProtected = false; //isWriteProtected(getNativePtr());
    }
    
    public long getSize() {
        assert isNativeObjectValid();

        return size;
    }
    
    public ByteBuffer getData() {
        assert isNativeObjectValid();

        return data(getNativePtr());
    }
    
    public byte read(int address) {
        assert isNativeObjectValid();

        return read(getNativePtr(), address);
    }
    
    public void write(int address, byte value) {
        assert isNativeObjectValid();

        write(getNativePtr(), address, value);
    }
    
    public void setWriteProtected(boolean writeProtected) {
        assert isNativeObjectValid();

        setWriteProtected(getNativePtr(), writeProtected);
        this.writeProtected = writeProtected;
    }
    
    public boolean isWriteProtected() {
        assert isNativeObjectValid();

        return writeProtected;
    }
    
    private static native long size(long ptr);
    private static native ByteBuffer data(long ptr);
    private static native byte read(long ptr, int address);
    private static native void write(long ptr, int address, byte value);
    private static native boolean isWriteProtected(long ptr);
    private static native void setWriteProtected(long ptr, boolean writeProtected);
}
