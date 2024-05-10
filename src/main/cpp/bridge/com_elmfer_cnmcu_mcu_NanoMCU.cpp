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
        
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>& gpio = nano->GPIO();
        uint8_t* pvFront = gpio.pvFrontData();
        uint8_t* outputPinDrivers = nano->pinOutputDrivers();
        bool frontPinIsInput = gpio.isInput(0);
        bool rightPinIsInput = gpio.isInput(1);
        bool backPinIsInput  = gpio.isInput(2);
        bool leftPinIsInput  = gpio.isInput(3);
        
        pvFront[0] = frontPinIsInput ? static_cast<uint8_t>(inputs[0]) : pvFront[0];
        pvFront[1] = rightPinIsInput ? static_cast<uint8_t>(inputs[1]) : pvFront[1];
        pvFront[2] = backPinIsInput  ? static_cast<uint8_t>(inputs[2]) : pvFront[2];
        pvFront[3] = leftPinIsInput  ? static_cast<uint8_t>(inputs[3]) : pvFront[3];
        
        nano->tick();
        
        outputs[0] = static_cast<jint>(frontPinIsInput ? 0 : outputPinDrivers[0]);
        outputs[1] = static_cast<jint>(rightPinIsInput ? 0 : outputPinDrivers[1]);
        outputs[2] = static_cast<jint>(backPinIsInput  ? 0 : outputPinDrivers[2]);
        outputs[3] = static_cast<jint>(leftPinIsInput  ? 0 : outputPinDrivers[3]);
    
	env->ReleasePrimitiveArrayCritical(obj_inputs, inputs, 0);
	env->ReleasePrimitiveArrayCritical(obj_outputs, outputs, 0);

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_cycle(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:245

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->cycle();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_reset(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:250

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->reset();
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setPowered(JNIEnv* env, jclass clazz, jlong ptr, jboolean powered) {


//@line:255

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(powered)
            nano->powerOn();
        else
            nano->powerOff();
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_isPowered(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:263

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isPoweredOn());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setClockPause(JNIEnv* env, jclass clazz, jlong ptr, jboolean paused) {


//@line:267

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(paused)
            nano->pauseClock();
        else
            nano->resumeClock();
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_isClockPaused(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:275

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isClockPaused());
    

}

JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_numCycles(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:280

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jlong>(nano->numCycles());
    

}

JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_setNumCycles(JNIEnv* env, jclass clazz, jlong ptr, jlong cycles) {


//@line:285

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        uint64_t numCycles = static_cast<uint64_t>(cycles);
        nano->setNumCycles(numCycles);
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busAddress(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:291

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busAddress());
    

}

JNIEXPORT jint JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busData(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:296

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busData());
    

}

JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_busRW(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:301

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->busRw());
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_CPU(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:306

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        mos6502* cpu = &nano->CPU();
        jobject cpuObj = env->NewObject(cnmcuJava::MOS6502, cnmcuJava::MOS6502_init, reinterpret_cast<jlong>(cpu));
        return cpuObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_GPIO(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:313

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = &nano->GPIO();
        jobject gpioObj = env->NewObject(cnmcuJava::CNGPIO, cnmcuJava::CNGPIO_init, reinterpret_cast<jlong>(gpio));
        return gpioObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_RAM(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:320

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = &nano->RAM();
        jobject ramObj = env->NewObject(cnmcuJava::CNRAM, cnmcuJava::CNRAM_init, reinterpret_cast<jlong>(ram));
        return ramObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_ROM(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:326

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>* rom = &nano->ROM();
        jobject romObj = env->NewObject(cnmcuJava::CNROM, cnmcuJava::CNROM_init, reinterpret_cast<jlong>(rom));
        return romObj;
    

}

JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_NanoMCU_EL(JNIEnv* env, jclass clazz, jlong ptr) {


//@line:333

        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNEL<CodeNodeNano::EL_SIZE>* el = &nano->EL();
        jobject elObj = env->NewObject(cnmcuJava::CNEL, cnmcuJava::CNEL_init, reinterpret_cast<jlong>(el));
        return elObj;
    

}

