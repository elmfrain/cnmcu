package com.elmfer.cnmcu.cpp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elmfer.cnmcu.mcu.NanoMCU;

/**
 * This class is used to store the native pointer of a native object.
 * It is used to track the native object's life cycle. Java code is
 * responsible for the life cycle of the native object.
 */
public class StrongNativeObject {

    private static final Map<Class<? extends StrongNativeObject>, Method[]> FINALIZERS = new ConcurrentHashMap<>();

    static {
        FINALIZERS.put(NanoMCU.class, getFinalizers(NanoMCU.class));
    }

    private long nativePtr = 0;

    protected StrongNativeObject() {
    }

    protected long getNativePtr() {
        return nativePtr;
    }

    protected void setNativePtr(long ptr) {
        nativePtr = ptr;
    }
    
    public void deleteNativeObject() {
        if (nativePtr == 0) {
            return;
        }
        
        Method[] finalizers = FINALIZERS.get(getClass());
        
        if (finalizers == null)
            throw new RuntimeException("No finalizers found for class " + getClass().getName());
        
        for (Method finalizer : finalizers) {
            try {
                finalizer.invoke(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        nativePtr = 0;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nativePtr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StrongNativeObject) {
            StrongNativeObject other = (StrongNativeObject) obj;
            return nativePtr == other.nativePtr;
        }
        return false;
    }

    /**
     * Collect all the finalizer methods for a given class with the help of
     * reflection.
     */
    private static Method[] getFinalizers(Class<? extends StrongNativeObject> clazz) {
        ArrayList<Method> finalizers = new ArrayList<>();
        for (Class<?> c = clazz; c != StrongNativeObject.class; c = c.getSuperclass()) {
            try {
                Method finalizer = c.getDeclaredMethod("deleteNative");
                finalizer.setAccessible(true);
                finalizers.add(finalizer);
            } catch (IllegalArgumentException | NoClassDefFoundError | SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
            }
        }

        int numFinalizers = finalizers.size();
        Method[] finalizerArray = new Method[numFinalizers];
        finalizers.toArray(finalizerArray);

        return finalizerArray;
    }
}
