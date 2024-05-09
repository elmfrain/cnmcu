#include <com_elmfer_cnmcu_mcu_NanoMCU.h>

//@line:205

        #include "cnmcuJava.h"
        #include "Nano.hpp"
     JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_createMCU(JNIEnv* env, jclass clazz) {


//@line:210

        cnmcuJava::init(env);
        CodeNodeNano* nano = new CodeNodeNano();
        return reinterpret_cast<jlong>(nano);
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_deleteMCU(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:216

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        delete nano;
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_tick(JNIEnv* env, jclass clazz, jlong ptr, jintArray obj_inputs, jintArray obj_outputs) {
	int* inputs = (int*)env->GetPrimitiveArrayCritical(obj_inputs, 0);
	int* outputs = (int*)env->GetPrimitiveArrayCritical(obj_outputs, 0);


//@line:221

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        
        uint8_t* pvFront = nano->GPIO().pvFrontData();
        uint8_t dir = *nano->GPIO().dirData();
        bool frontPinIsInput = (dir & 0b0001) == 0;
        bool rightPinIsInput = (dir & 0b0010) == 0;
        bool backPinIsInput  = (dir & 0b0100) == 0;
        bool leftPinIsInput  = (dir & 0b1000) == 0;
        
        pvFront[0] = frontPinIsInput ? static_cast<uint8_t>(inputs[0]) : pvFront[0];
        pvFront[1] = rightPinIsInput ? static_cast<uint8_t>(inputs[1]) : pvFront[1];
        pvFront[2] = backPinIsInput  ? static_cast<uint8_t>(inputs[2]) : pvFront[2];
        pvFront[3] = leftPinIsInput  ? static_cast<uint8_t>(inputs[3]) : pvFront[3];
        
        nano->tick();
        
        outputs[0] = static_cast<jint>(frontPinIsInput ? 0 : pvFront[0]);
        outputs[1] = static_cast<jint>(rightPinIsInput ? 0 : pvFront[1]);
        outputs[2] = static_cast<jint>(backPinIsInput  ? 0 : pvFront[2]);
        outputs[3] = static_cast<jint>(leftPinIsInput  ? 0 : pvFront[3]);
    
	env->ReleasePrimitiveArrayCritical(obj_inputs, inputs, 0);
	env->ReleasePrimitiveArrayCritical(obj_outputs, outputs, 0);

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_cycle(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:244

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->cycle();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_reset(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:249

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->reset();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setPowered(JNIEnv* env, jclass clazz, jlong ptr, jboolean powered) {


//@line:254

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(powered)
            nano->powerOn();
        else
            nano->powerOff();
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_isPowered(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:262

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isPoweredOn());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setClockPause(JNIEnv* env, jclass clazz, jlong ptr, jboolean paused) {


//@line:266

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(paused)
            nano->pauseClock();
        else
            nano->resumeClock();
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_isClockPaused(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:274

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isClockPaused());
    

}

JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_numCycles(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:279

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jlong>(nano->numCycles());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setNumCycles(JNIEnv* env, jclass clazz, jlong ptr, jlong cycles) {


//@line:284

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        uint64_t numCycles = static_cast<uint64_t>(cycles);
        nano->setNumCycles(numCycles);
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busAddress(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:290

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busAddress());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busData(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:295

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busData());
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busRW(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:300

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->busRw());
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_CPU(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:305

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        mos6502* cpu = &nano->CPU();
        jobject cpuObj = env->NewObject(cnmcuJava::MOS6502, cnmcuJava::MOS6502_init, reinterpret_cast<jlong>(cpu));
        return cpuObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_GPIO(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:312

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = &nano->GPIO();
        jobject gpioObj = env->NewObject(cnmcuJava::CNGPIO, cnmcuJava::CNGPIO_init, reinterpret_cast<jlong>(gpio));
        return gpioObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_RAM(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:319

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = &nano->RAM();
        jobject ramObj = env->NewObject(cnmcuJava::CNRAM, cnmcuJava::CNRAM_init, reinterpret_cast<jlong>(ram));
        return ramObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_ROM(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:325

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>* rom = &nano->ROM();
        jobject romObj = env->NewObject(cnmcuJava::CNROM, cnmcuJava::CNROM_init, reinterpret_cast<jlong>(rom));
        return romObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_EL(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:332

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNEL<CodeNodeNano::EL_SIZE>* el = &nano->EL();
        jobject elObj = env->NewObject(cnmcuJava::CNEL, cnmcuJava::CNEL_init, reinterpret_cast<jlong>(el));
        return elObj;
    

}

