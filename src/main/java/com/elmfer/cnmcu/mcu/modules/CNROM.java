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
    
    // @formatter:off
    
    /*JNI
        #include "cnmcuJava.h"
        #include "Nano.hpp"
     */
    
    private static native long size(long ptr); /*
        return static_cast<jlong>(CodeNodeNano::ROM_SIZE);
    */
    
    private static native ByteBuffer data(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        return env->NewDirectByteBuffer(rom.data(), CodeNodeNano::ROM_SIZE);
    */
    
    private static native byte read(long ptr, int address); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(rom.read(addr));
    */
    
    private static native void write(long ptr, int address, byte value); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        rom.write(addr, val);
    */
    
    private static native boolean isWriteProtected(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        return static_cast<jboolean>(rom.isWriteProtected());
    */
    private static native void setWriteProtected(long ptr, boolean writeProtected); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        rom.setWriteProtect(static_cast<bool>(writeProtected));
    */
}
