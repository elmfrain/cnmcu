package com.elmfer.cnmcu.mcu.cpu;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

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
    
    private static native void NMI(long ptr);
    private static native void IRQ(long ptr);
    private static native void Reset(long ptr);
    
    private static native int GetPC(long ptr);
    private static native int GetS(long ptr);
    private static native int GetP(long ptr);
    private static native int GetA(long ptr);
    private static native int GetX(long ptr);
    private static native int GetY(long ptr);
}
