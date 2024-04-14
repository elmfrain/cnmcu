package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

/**
 * Reference to a CNRAM object
 * it is a weak reference, so it will be invalidated if the native
 * object is deleted.
 */
public class CNRAM extends WeakNativeObject {
    
    private final long size;
    
    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected CNRAM(long ptr) {
        setNativePtr(ptr);
        
        size = size(getNativePtr());
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
    
    // @formatter:off
    
    /*JNI
         #include "cnmcuJava.h"
         #include "Nano.hpp"
     */
    
    private static native long size(long ptr); /*
        return static_cast<jlong>(CodeNodeNano::RAM_SIZE);
    */
    
    private static native ByteBuffer data(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
        return env->NewDirectByteBuffer(ram.data(), CodeNodeNano::RAM_SIZE);
    */
    
    private static native byte read(long ptr, int address); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(ram.read(addr));
    */
    
    private static native void write(long ptr, int address, byte value); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        ram.write(addr, val);
    */
}
