#include <com_elmfer_cnmcu_mcu_cpu_MOS6502.h>

//@line:77

        #include "mos6502.h"
    JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_NMI(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:81

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->NMI();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_IRQ(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:86

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->IRQ();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_Reset(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:91

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->Reset();
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetPC(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:96

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetPC());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetS(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:101

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetS());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetP(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:106

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetP());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetA(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:111

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetA());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetX(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:116

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetX());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetY(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:121

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetY());
    

}

