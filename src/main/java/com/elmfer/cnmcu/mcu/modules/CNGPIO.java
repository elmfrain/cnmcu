package com.elmfer.cnmcu.mcu.modules;

import java.nio.ByteBuffer;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

import net.minecraft.nbt.NbtCompound;

/**
 * Reference to a CNGPIO object
 * 
 * it is a weak reference, so it will be invalidated if the native
 * object is deleted.
 */
public class CNGPIO extends WeakNativeObject {

    private final long size;
    
    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected CNGPIO(long ptr) {
        setNativePtr(ptr);
        
        size = size(getNativePtr());
    }
    
    public long getSize() {
        assert isNativeObjectValid();

        return size;
    }
    
    public ByteBuffer getPVFrontData() {
        assert isNativeObjectValid();

        return pvFrontData(getNativePtr());
    }
    
    public ByteBuffer getPVBackData() {
        assert isNativeObjectValid();

        return pvBackData(getNativePtr());
    }
    
    public ByteBuffer getDirData() {
        assert isNativeObjectValid();

        return dirData(getNativePtr());
    }
    
    public ByteBuffer getIntData() {
        assert isNativeObjectValid();

        return intData(getNativePtr());
    }
    
    public ByteBuffer getIFLData() {
        assert isNativeObjectValid();

        return iflData(getNativePtr());
    }
    
    public int read(int address) {
        assert isNativeObjectValid();

        return read(getNativePtr(), address);
    }
    
    public void write(int address, int value) {
        assert isNativeObjectValid();

        write(getNativePtr(), address, value);
    }
    
    public boolean shouldInterrupt() {
        assert isNativeObjectValid();

        return shouldInterrupt(getNativePtr());
    }
    
    public void writeNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound gpioNbt = new NbtCompound();

        ByteBuffer buffer = getPVFrontData();
        byte[] pvFrontData = new byte[buffer.remaining()];
        buffer.get(pvFrontData);
        gpioNbt.putByteArray("pvFrontData", pvFrontData);

        buffer = getPVBackData();
        byte[] pvBackData = new byte[buffer.remaining()];
        buffer.get(pvBackData);
        gpioNbt.putByteArray("pvBackData", pvBackData);

        buffer = getDirData();
        byte[] dirData = new byte[buffer.remaining()];
        buffer.get(dirData);
        gpioNbt.putByteArray("dirData", dirData);

        buffer = getIntData();
        byte[] intData = new byte[buffer.remaining()];
        buffer.get(intData);
        gpioNbt.putByteArray("intData", intData);

        buffer = getIFLData();
        byte[] iflData = new byte[buffer.remaining()];
        buffer.get(iflData);
        gpioNbt.putByteArray("iflData", iflData);

        nbt.put("gpio", gpioNbt);
    }
    
    public void readNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound gpioNbt = nbt.getCompound("gpio");

        ByteBuffer buffer = getPVFrontData();
        byte[] pvFrontData = gpioNbt.getByteArray("pvFrontData");
        buffer.put(pvFrontData);

        buffer = getPVBackData();
        byte[] pvBackData = gpioNbt.getByteArray("pvBackData");
        buffer.put(pvBackData);

        buffer = getDirData();
        byte[] dirData = gpioNbt.getByteArray("dirData");
        buffer.put(dirData);

        buffer = getIntData();
        byte[] intData = gpioNbt.getByteArray("intData");
        buffer.put(intData);

        buffer = getIFLData();
        byte[] iflData = gpioNbt.getByteArray("iflData");
        buffer.put(iflData);
    }
    
    // @formatter:off
    
    /*JNI
        #include "cnmcuJava.h"
        #include "Nano.hpp"
    */
    
    private static native long size(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return static_cast<jlong>(gpio->size());
    */
    
    private static native ByteBuffer pvFrontData(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return env->NewDirectByteBuffer(gpio->pvFrontData(), CodeNodeNano::GPIO_NUM_PINS);
    */
    
    private static native ByteBuffer pvBackData(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return env->NewDirectByteBuffer(gpio->pvBackData(), CodeNodeNano::GPIO_NUM_PINS);
    */
    
    private static native ByteBuffer dirData(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return env->NewDirectByteBuffer(gpio->dirData(), CodeNodeNano::GPIO_NUM_PINS / 8);
    */
    
    private static native ByteBuffer intData(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return env->NewDirectByteBuffer(gpio->intData(), CodeNodeNano::GPIO_NUM_PINS / 2);
    */
    
    private static native ByteBuffer iflData(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return env->NewDirectByteBuffer(gpio->iflData(), CodeNodeNano::GPIO_NUM_PINS / 8);
    */
    
    private static native int read(long ptr, int address); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = gpio->read(address);    
        return static_cast<jint>(val);
    */
    
    private static native void write(long ptr, int address, int value); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        gpio->write(addr, val);
    */
    
    private static native boolean shouldInterrupt(long ptr); /*
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = reinterpret_cast<CNGPIO<CodeNodeNano::GPIO_NUM_PINS>*>(ptr);
        return static_cast<jboolean>(gpio->shouldInterrupt());
    */
}
