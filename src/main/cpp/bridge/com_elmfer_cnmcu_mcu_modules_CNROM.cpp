#include <com_elmfer_cnmcu_mcu_modules_CNROM.h>

//@line:92

        #include "cnmcuJava.h"
        #include "Nano.hpp"
     JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_size(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:97

        return static_cast<jlong>(CodeNodeNano::ROM_SIZE);
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_data(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:101

        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        return env->NewDirectByteBuffer(rom->data(), CodeNodeNano::ROM_SIZE);
    

}

JNIEXPORT jbyte JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_read(JNIEnv* env, jclass clazz, jlong ptr, jint address) {


//@line:106

        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        return static_cast<jbyte>(rom->read(addr));
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_write(JNIEnv* env, jclass clazz, jlong ptr, jint address, jbyte value) {


//@line:112

        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        uint16_t addr = static_cast<uint16_t>(address);
        uint8_t val = static_cast<uint8_t>(value);
        rom->write(addr, val);
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_isWriteProtected(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:119

        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        return static_cast<jboolean>(rom->isWriteProtected());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_setWriteProtected(JNIEnv* env, jclass clazz, jlong ptr, jboolean writeProtected) {


//@line:123

        CNROM<CodeNodeNano::ROM_SIZE>* rom = reinterpret_cast<CNROM<CodeNodeNano::ROM_SIZE>*>(ptr);
        rom->setWriteProtect(static_cast<bool>(writeProtected));
    

}

