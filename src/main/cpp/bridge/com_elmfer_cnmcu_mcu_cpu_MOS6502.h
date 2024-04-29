/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_elmfer_cnmcu_mcu_cpu_MOS6502 */

#ifndef _Included_com_elmfer_cnmcu_mcu_cpu_MOS6502
#define _Included_com_elmfer_cnmcu_mcu_cpu_MOS6502
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    NMI
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_NMI
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    IRQ
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_IRQ
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    Reset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_Reset
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetPC
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetPC
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetS
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetS
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetP
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetP
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetA
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetA
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetX
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetX
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetY
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetY
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif