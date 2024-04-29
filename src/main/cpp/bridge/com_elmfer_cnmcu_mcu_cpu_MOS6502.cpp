#include <com_elmfer_cnmcu_mcu_cpu_MOS6502.h>

//@line:103

        #include "mos6502.h"
    JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_NMI(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:107

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->NMI();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_IRQ(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:112

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->IRQ();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_Reset(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:117

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->Reset();
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetPC(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:122

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetPC());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetS(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:127

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetS());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetP(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:132

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetP());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetA(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:137

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetA());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetX(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:142

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetX());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetY(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:147

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        return static_cast<jint>(cpu->GetY());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_SetState(JNIEnv* env, jclass clazz, jlong ptr, jintArray obj_state) {
	int* state = (int*)env->GetPrimitiveArrayCritical(obj_state, 0);


//@line:152

        mos6502* cpu = reinterpret_cast<mos6502*>(ptr);
        cpu->SetState(state);
    
	env->ReleasePrimitiveArrayCritical(obj_state, state, 0);

}

