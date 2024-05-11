package com.elmfer.cnmcu.mcu;

import com.elmfer.cnmcu.cpp.StrongNativeObject;
import com.elmfer.cnmcu.mcu.cpu.MOS6502;
import com.elmfer.cnmcu.mcu.modules.CNEL;
import com.elmfer.cnmcu.mcu.modules.CNEL.EventType;
import com.elmfer.cnmcu.mcu.modules.CNGPIO;
import com.elmfer.cnmcu.mcu.modules.CNRAM;
import com.elmfer.cnmcu.mcu.modules.CNROM;
import com.elmfer.cnmcu.mcu.modules.CNUART;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

public class NanoMCU extends StrongNativeObject {
    public int frontInput, rightInput, backInput, leftInput;
    public int frontOutput, rightOutput, backOutput, leftOutput;

    private int[] inputs = new int[4];
    private int[] outputs = new int[4];
    private boolean frontOutputChanged, rightOutputChanged, backOutputChanged, leftOutputChanged;

    private MOS6502 cpu;
    private CNGPIO gpio;
    private CNRAM ram;
    private CNROM rom;
    private CNEL el;
    private CNUART uart;

    public NanoMCU() {
        setNativePtr(createMCU());

        cpu = CPU(getNativePtr());
        gpio = GPIO(getNativePtr());
        ram = RAM(getNativePtr());
        rom = ROM(getNativePtr());
        el = EL(getNativePtr());
        uart = UART(getNativePtr());
    }

    public void tick() {
        inputs[0] = frontInput;
        inputs[1] = rightInput;
        inputs[2] = backInput;
        inputs[3] = leftInput;

        el.triggerEvent(EventType.GAME_TICK);
        tick(getNativePtr(), inputs, outputs);

        frontOutputChanged = frontOutput != outputs[0];
        rightOutputChanged = rightOutput != outputs[1];
        backOutputChanged = backOutput != outputs[2];
        leftOutputChanged = leftOutput != outputs[3];

        frontOutput = outputs[0];
        rightOutput = outputs[1];
        backOutput = outputs[2];
        leftOutput = outputs[3];
    }

    public boolean outputHasChanged(Direction direction) {
        switch (direction) {
        case NORTH:
            return frontOutputChanged;
        case EAST:
            return rightOutputChanged;
        case SOUTH:
            return backOutputChanged;
        case WEST:
            return leftOutputChanged;
        default:
            return false;
        }
    }

    public void cycle() {
        cycle(getNativePtr());
    }

    public void reset() {
        frontInput = rightInput = backInput = leftInput = 0;
        reset(getNativePtr());
    }

    public void setPowered(boolean powered) {
        setPowered(getNativePtr(), powered);
    }

    public boolean isPowered() {
        return isPowered(getNativePtr());
    }

    public void setClockPause(boolean paused) {
        setClockPause(getNativePtr(), paused);
    }

    public boolean isClockPaused() {
        return isClockPaused(getNativePtr());
    }

    public long numCycles() {
        return numCycles(getNativePtr());
    }

    public int busAddress() {
        return busAddress(getNativePtr());
    }

    public int busData() {
        return busData(getNativePtr());
    }

    public boolean busRW() {
        return busRW(getNativePtr());
    }

    public void deleteNative() {
        cpu.invalidateNativeObject();
        gpio.invalidateNativeObject();
        ram.invalidateNativeObject();
        rom.invalidateNativeObject();
        el.invalidateNativeObject();
        uart.invalidateNativeObject();

        deleteMCU(getNativePtr());
    }

    /*
     * Get the CPU of the MCU which is a MOS6502
     */
    public MOS6502 getCPU() {
        return cpu;
    }

    /*
     * Get the GPIO of the MCU.
     * 
     * General Purpose Input/Output
     */
    public CNGPIO getGPIO() {
        return gpio;
    }

    /*
     * Get the RAM of the MCU.
     * 
     * Random Access Memory
     */
    public CNRAM getRAM() {
        return ram;
    }

    /*
     * Get the ROM of the MCU.
     * 
     * Read Only Memory
     */
    public CNROM getROM() {
        return rom;
    }

    /*
     * Get the EL of the MCU.
     * 
     * Event Listener
     */
    public CNEL getEL() {
        return el;
    }

    /*
     * Get the UART of the MCU.
     * 
     * Universal Asynchronous Receiver-Transmitter
     */
    public CNUART getUART() {
        return uart;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound mcuNbt = new NbtCompound();

        mcuNbt.putInt("frontOutput", frontOutput);
        mcuNbt.putInt("rightOutput", rightOutput);
        mcuNbt.putInt("backOutput", backOutput);
        mcuNbt.putInt("leftOutput", leftOutput);
        mcuNbt.putBoolean("powered", isPowered());
        mcuNbt.putBoolean("clockPaused", isClockPaused());
        mcuNbt.putLong("numCycles", numCycles());
        rom.writeNbt(mcuNbt);
        ram.writeNbt(mcuNbt);
        gpio.writeNbt(mcuNbt);
        cpu.writeNbt(mcuNbt);
        el.writeNbt(mcuNbt);
        uart.writeNbt(mcuNbt);

        nbt.put("mcu", mcuNbt);
    }

    public void readNbt(NbtCompound nbt) {
        NbtCompound mcuNbt = nbt.getCompound("mcu");

        frontOutput = mcuNbt.getInt("frontOutput");
        rightOutput = mcuNbt.getInt("rightOutput");
        backOutput = mcuNbt.getInt("backOutput");
        leftOutput = mcuNbt.getInt("leftOutput");
        setPowered(mcuNbt.getBoolean("powered"));
        setClockPause(mcuNbt.getBoolean("clockPaused"));
        setNumCycles(getNativePtr(), mcuNbt.getLong("numCycles"));
        rom.readNbt(mcuNbt);
        ram.readNbt(mcuNbt);
        gpio.readNbt(mcuNbt);
        cpu.readNbt(mcuNbt);
        el.readNbt(mcuNbt);
        uart.readNbt(mcuNbt);
    }

    // @formatter:off
    
    /*JNI
        #include "cnmcuJava.h"
        #include "Nano.hpp"
     */ 
    
    private static native long createMCU(); /*
        cnmcuJava::init(env);
        CodeNodeNano* nano = new CodeNodeNano();
        return reinterpret_cast<jlong>(nano);
    */
    
    private static native void deleteMCU(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        delete nano;
    */
    
    private static native void tick(long ptr, int[] inputs, int[] outputs); /*
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
    */
    
    private static native void cycle(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->cycle();
    */
    
    private static native void reset(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        nano->reset();
    */
    
    private static native void setPowered(long ptr, boolean powered); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(powered)
            nano->powerOn();
        else
            nano->powerOff();
    */
    
    private static native boolean isPowered(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isPoweredOn());
    */
    private static native void setClockPause(long ptr, boolean paused); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        if(paused)
            nano->pauseClock();
        else
            nano->resumeClock();
    */
    
    private static native boolean isClockPaused(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->isClockPaused());
    */
    
    private static native long numCycles(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jlong>(nano->numCycles());
    */
    
    private static native void setNumCycles(long ptr, long cycles); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        uint64_t numCycles = static_cast<uint64_t>(cycles);
        nano->setNumCycles(numCycles);
    */
    
    private static native int busAddress(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busAddress());
    */
    
    private static native int busData(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jint>(nano->busData());
    */
    
    private static native boolean busRW(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        return static_cast<jboolean>(nano->busRw());
    */
    
    private static native MOS6502 CPU(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        mos6502* cpu = &nano->CPU();
        jobject cpuObj = env->NewObject(cnmcuJava::MOS6502, cnmcuJava::MOS6502_init, reinterpret_cast<jlong>(cpu));
        return cpuObj;
    */
    
    private static native CNGPIO GPIO(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNGPIO<CodeNodeNano::GPIO_NUM_PINS>* gpio = &nano->GPIO();
        jobject gpioObj = env->NewObject(cnmcuJava::CNGPIO, cnmcuJava::CNGPIO_init, reinterpret_cast<jlong>(gpio));
        return gpioObj;
    */
    
    private static native CNRAM RAM(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNRAM<CodeNodeNano::RAM_SIZE>* ram = &nano->RAM();
        jobject ramObj = env->NewObject(cnmcuJava::CNRAM, cnmcuJava::CNRAM_init, reinterpret_cast<jlong>(ram));
        return ramObj;
    */
    private static native CNROM ROM(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNROM<CodeNodeNano::ROM_SIZE>* rom = &nano->ROM();
        jobject romObj = env->NewObject(cnmcuJava::CNROM, cnmcuJava::CNROM_init, reinterpret_cast<jlong>(rom));
        return romObj;
    */
    
    private static native CNEL EL(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNEL<CodeNodeNano::EL_SIZE>* el = &nano->EL();
        jobject elObj = env->NewObject(cnmcuJava::CNEL, cnmcuJava::CNEL_init, reinterpret_cast<jlong>(el));
        return elObj;
    */
    
    private static native CNUART UART(long ptr); /*
        CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(ptr);
        CNUART* uart = &nano->UART();
        jobject uartObj = env->NewObject(cnmcuJava::CNUART, cnmcuJava::CNUART_init, reinterpret_cast<jlong>(uart));
        return uartObj;
    */
}
