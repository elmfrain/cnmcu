package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

import net.minecraft.nbt.NbtCompound;

/**
 * Reference to a CNEL object
 * 
 * it is a weak reference, so it will be invalidated if the native
 * object is deleted.
 */
public class CNEL extends WeakNativeObject {
    
    public static enum EventType {
        GAME_TICK(0);

        private final int value;

        private EventType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    private final long size;
    
    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected CNEL(long ptr) {
        setNativePtr(ptr);
        
        size = size(getNativePtr());
    }
    
    public long getSize() {
        assert isNativeObjectValid();

        return size;
    }
    
    public ByteBuffer getICLRegistersData() {
        assert isNativeObjectValid();

        return iclRegistersData(getNativePtr());
    }
    
    public ByteBuffer getIFLRegistersData() {
        assert isNativeObjectValid();

        return iflRegistersData(getNativePtr());
    }
    
    public void triggerEvent(EventType event) {
        assert isNativeObjectValid();

        triggerEvent(getNativePtr(), event.value);
    }
    
    public boolean shouldInterrupt() {
        assert isNativeObjectValid();

        return shouldInterrupt(getNativePtr());
    }
    
    public int read(int address) {
        assert isNativeObjectValid();

        return read(address);
    }
    
    public void write(int address, int data) {
        assert isNativeObjectValid();

        write(address, data);
    }
    
    public void writeNbt(NbtCompound nbt) {
        assert isNativeObjectValid();
        
        NbtCompound elNbt = new NbtCompound();
        
        ByteBuffer iclRegistersData = getICLRegistersData();
        ByteBuffer iflRegistersData = getIFLRegistersData();
        
        byte[] iclData = new byte[iclRegistersData.remaining()];
        byte[] iflData = new byte[iflRegistersData.remaining()];
        
        iclRegistersData.get(iclData);
        iflRegistersData.get(iflData);
        
        elNbt.putByteArray("iclRegistersData", iclData);
        elNbt.putByteArray("iflRegistersData", iflData);
        
        nbt.put("el", elNbt);
    }
    
    public void readNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound elNbt = nbt.getCompound("el");
        
        ByteBuffer iclRegistersData = getICLRegistersData();
        ByteBuffer iflRegistersData = getIFLRegistersData();

        byte[] iclData = elNbt.getByteArray("iclRegistersData");
        byte[] iflData = elNbt.getByteArray("iflRegistersData");

        iclRegistersData.put(iclData);
        iflRegistersData.put(iflData);
    }
    
    // @formatter:off
    
    /*JNI
         #include "cnmcuJava.h"
         #include "Nano.hpp"
     */
    
    private static native long size(long ptr); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        return cnel->size();
    */
    
    private static native ByteBuffer iclRegistersData(long ptr); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        return env->NewDirectByteBuffer(cnel->iclRegistersData(), CodeNodeNano::EL_SIZE);
    */
    
    private static native ByteBuffer iflRegistersData(long ptr); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        return env->NewDirectByteBuffer(cnel->iflRegistersData(), CodeNodeNano::EL_SIZE);
    */
    
    private static native void triggerEvent(long ptr, int event); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        cnel->triggerEvent(event);
    */
    
    private static native boolean shouldInterrupt(long ptr); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        return cnel->shouldInterrupt();
    */
    
    private static native int read(long ptr, int address); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        return static_cast<jint>(cnel->read(address));
    */
    
    private static native void write(long ptr, int address, int data); /*
        CNEL<CodeNodeNano::EL_SIZE>* cnel = reinterpret_cast<CNEL<CodeNodeNano::EL_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t dat = static_cast<uint8_t>(data);
        cnel->write(addr, dat);
    */
}
