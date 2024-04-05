package com.elmfer.cnmcu.mcu;

import com.elmfer.cnmcu.cpp.StrongNativeObject;
import com.elmfer.cnmcu.mcu.cpu.MOS6502;
import com.elmfer.cnmcu.mcu.modules.CNGPIO;
import com.elmfer.cnmcu.mcu.modules.CNRAM;
import com.elmfer.cnmcu.mcu.modules.CNROM;

public class NanoMCU extends StrongNativeObject {
    
    public int frontInput, rightInput, backInput, leftInput;
    public int frontOutput, rightOutput, backOutput, leftOutput;
    
    private int[] inputs = new int[4];
    private int[] outputs = new int[4];
    
    private MOS6502 cpu;
    private CNGPIO gpio;
    private CNRAM ram;
    private CNROM rom;
    
    public NanoMCU() {
        setNativePtr(createMCU());
        
        cpu = CPU(getNativePtr());
        gpio = GPIO(getNativePtr());
        ram = RAM(getNativePtr());
        rom = ROM(getNativePtr());
    }
    
    public void tick() {
        inputs[0] = frontInput;
        inputs[1] = rightInput;
        inputs[2] = backInput;
        inputs[3] = leftInput;
        
        tick(getNativePtr(), inputs, outputs);
        
        frontOutput = outputs[0];
        rightOutput = outputs[1];
        backOutput = outputs[2];
        leftOutput = outputs[3];
    }
    
    public void cycle() {
        cycle(getNativePtr());
    }
    
    public void reset() {
        reset(getNativePtr());
    }
    
    public void setPowered(boolean powered) {
        setPowered(getNativePtr(), powered);
    }
    
    public boolean isPowered() {
        return isPowered(getNativePtr());
    }
    
    public void setClockPause(boolean paused) {
        setClockPause(getNativePtr(), paused);
    }
    
    public boolean isClockPaused() {
        return isClockPaused(getNativePtr());
    }
    
    public long numCycles() {
        return numCycles(getNativePtr());
    }
    
    public int busAddress() {
        return busAddress(getNativePtr());
    }
    
    public int busData() {
        return busData(getNativePtr());
    }
    
    public boolean busRW() {
        return busRW(getNativePtr());
    }
    
    public void deleteNative() {
        deleteMCU(getNativePtr());
        
        cpu.invalidateNativeObject();
        gpio.invalidateNativeObject();
        ram.invalidateNativeObject();
        rom.invalidateNativeObject();
    }
    
    private static native long createMCU();
    private static native void deleteMCU(long ptr);
    private static native void tick(long ptr, int[] inputs, int[] outputs);
    private static native void cycle(long ptr);
    private static native void reset(long ptr);
    
    private static native void setPowered(long ptr, boolean powered);
    private static native boolean isPowered(long ptr);
    private static native void setClockPause(long ptr, boolean paused);
    private static native boolean isClockPaused(long ptr);
    private static native long numCycles(long ptr);
    
    private static native int busAddress(long ptr);
    private static native int busData(long ptr);
    private static native boolean busRW(long ptr);
    
    private static native MOS6502 CPU(long ptr);
    private static native CNGPIO GPIO(long ptr);
    private static native CNRAM RAM(long ptr);
    private static native CNROM ROM(long ptr);
}
