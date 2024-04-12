#include <com_elmfer_cnmcu_mcu_cpu_MOS6502.h>

#include <mos6502.h>

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    NMI
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_NMI
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    cpu->NMI();
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    IRQ
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_IRQ
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    cpu->IRQ();
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    Reset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_Reset
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    cpu->Reset();
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetPC
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetPC
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetPC());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetS
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetS
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetS());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetP
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetP
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetP());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetA
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetA
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetA());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetX
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetX
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetX());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_cpu_MOS6502
 * Method:    GetY
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_cpu_MOS6502_GetY
(JNIEnv*, jclass, jlong cpuId)
{
    mos6502* cpu = reinterpret_cast<mos6502*>(cpuId);
    return static_cast<jint>(cpu->GetY());
}