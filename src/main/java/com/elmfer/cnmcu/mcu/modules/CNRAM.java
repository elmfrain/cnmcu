package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

import net.minecraft.nbt.NbtCompound;

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
    
    public void writeNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound ramNbt = new NbtCompound();
        
        ByteBuffer buffer = getData();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        ramNbt.putByteArray("data", data);
        
        nbt.put("ram", ramNbt);
    }
    
    public void readNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound ramNbt = nbt.getCompound("ram");

        ByteBuffer buffer = getData();
        byte[] data = ramNbt.getByteArray("data");
        buffer.put(data);
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
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = reinterpret_cast<CNRAM<CodeNodeNano::RAM_SIZE>*>(ptr);
        return env->NewDirectByteBuffer(ram->data(), CodeNodeNano::RAM_SIZE);
    */
    
    private static native byte read(long ptr, int address); /*
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = reinterpret_cast<CNRAM<CodeNodeNano::RAM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(ram->read(addr));
    */
    
    private static native void write(long ptr, int address, byte value); /*
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = reinterpret_cast<CNRAM<CodeNodeNano::RAM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        ram->write(addr, val);
    */
}
