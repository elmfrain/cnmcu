#include <com_elmfer_cnmcu_mcu_modules_CNROM.h>

//@line:67

        #include "cnmcuJava.h"
        #include "Nano.hpp"
     JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_size(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:72

        return static_cast<jlong>(CodeNodeNano::ROM_SIZE);
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_data(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:76

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        return env->NewDirectByteBuffer(rom.data(), CodeNodeNano::ROM_SIZE);
    

}

JNIEXPORT jbyte JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_read(JNIEnv* env, jclass clazz, jlong ptr, jint address) {


//@line:82

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(rom.read(addr));
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_write(JNIEnv* env, jclass clazz, jlong ptr, jint address, jbyte value) {


//@line:89

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        rom.write(addr, val);
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_isWriteProtected(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:97

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        return static_cast<jboolean>(rom.isWriteProtected());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_setWriteProtected(JNIEnv* env, jclass clazz, jlong ptr, jboolean writeProtected) {


//@line:102

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
        rom.setWriteProtect(static_cast<bool>(writeProtected));
    

}

