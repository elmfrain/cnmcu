package com.elmfer.cnmcu.mcu.cpu;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

import net.minecraft.nbt.NbtCompound;

/**
 * Reference to a MOS 6502 CPU object
 * it is a weak reference, so it will be invalidated if the native
 * object is deleted.
 */
public class MOS6502 extends WeakNativeObject {

    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected MOS6502(long ptr) {
        setNativePtr(ptr);
    }
    
    public void NMI() {
        assert isNativeObjectValid();
        
        NMI(getNativePtr());
    }
    
    public void IRQ() {
        assert isNativeObjectValid();

        IRQ(getNativePtr());
    }
    
    public void Reset() {
        assert isNativeObjectValid();

        Reset(getNativePtr());
    }
    
    public int getPC() {
        assert isNativeObjectValid();

        return GetPC(getNativePtr());
    }
    
    public int getS() {
        assert isNativeObjectValid();

        return GetS(getNativePtr());
    }
    
    public int getP() {
        assert isNativeObjectValid();

        return GetP(getNativePtr());
    }
    
    public int getA() {
        assert isNativeObjectValid();

        return GetA(getNativePtr());
    }
    
    public int getX() {
        assert isNativeObjectValid();

        return GetX(getNativePtr());
    }
    
    public int getY() {
        assert isNativeObjectValid();

        return GetY(getNativePtr());
    }
    
    public void writeNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound cpuNbt = new NbtCompound();
        cpuNbt.putInt("pc", getPC());
        cpuNbt.putInt("s", getS());
        cpuNbt.putInt("p", getP());
        cpuNbt.putInt("a", getA());
        cpuNbt.putInt("x", getX());
        cpuNbt.putInt("y", getY());
        
        nbt.put("mos6502", cpuNbt);
    }
    
    public void readNbt(NbtCompound nbt) {
        assert isNativeObjectValid();

        NbtCompound state = nbt.getCompound("mos6502");
        int[] stateArray = new int[] { state.getInt("pc"), state.getInt("s"), state.getInt("p"), state.getInt("a"),
                state.getInt("x"), state.getInt("y") };

        SetState(getNativePtr(), stateArray);
    }
    
    // @formatter:off
    
    /*JNI
        #include "mos6502.h"
    */
    
    private static native void NMI(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->NMI();
    */
    
    private static native void IRQ(long ptr);/*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->IRQ();
    */
    
    private static native void Reset(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->Reset();
    */ 
    
    private static native int GetPC(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetPC());
    */
    
    private static native int GetS(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetS());
    */
    
    private static native int GetP(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetP());
    */
    
    private static native int GetA(long ptr);/*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetA());
    */

    private static native int GetX(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetX());
    */
    
    private static native int GetY(long ptr); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetY());
    */
    
    private static native void SetState(long ptr, int state[]); /*
        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->SetState(state);
    */
}
