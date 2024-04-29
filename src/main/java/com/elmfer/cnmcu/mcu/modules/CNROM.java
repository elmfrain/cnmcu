package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

import net.minecraft.nbt.NbtCompound;

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
        writeProtected = isWriteProtected(getNativePtr());
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
    
    public void writeNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound romNbt = new NbtCompound();
        ByteBuffer data = getData();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        romNbt.putByteArray("data", bytes);
        romNbt.putBoolean("writeProtected", isWriteProtected());
        
        nbt.put("rom", romNbt);
    }
    
    public void readNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound romNbt = nbt.getCompound("rom");
        byte[] bytes = romNbt.getByteArray("data");
        ByteBuffer data = getData();
        data.put(bytes);
        setWriteProtected(romNbt.getBoolean("writeProtected"));
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
        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        return env->NewDirectByteBuffer(rom->data(), CodeNodeNano::ROM_SIZE);
    */
    
    private static native byte read(long ptr, int address); /*
        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(rom->read(addr));
    */
    
    private static native void write(long ptr, int address, byte value); /*
        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        rom->write(addr, val);
    */
    
    private static native boolean isWriteProtected(long ptr); /*
        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        return static_cast<jboolean>(rom->isWriteProtected());
    */
    private static native void setWriteProtected(long ptr, boolean writeProtected); /*
        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        rom->setWriteProtect(static_cast<bool>(writeProtected));
    */
}
