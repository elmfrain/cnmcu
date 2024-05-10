#pragma once

#include <cstdint>
#include <cstddef>

class CNUART
{
private:
    uint8_t registers[8];
public:
    enum ParityType : uint8_t
    {
        ODD  = 0,
        EVEN = 1
    };

    enum StatusFlags : uint8_t
    {
        PARITY_ERROR    = 0,
        FRAME_ERROR     = 1,
        OVERRUN_ERROR   = 2,
        RX_BUFFER_FULL  = 3,
        TX_BUFFER_EMPTY = 4,
        IRQ             = 7
    };

    constexpr static int STATUS_REG = 0;
    constexpr static int CONTROL_REG = 1;
    constexpr static int COMMAND_REG = 2;
    constexpr static int TX_DATA_REG = 3;
    constexpr static int RX_DATA_REG = 4;
    constexpr static int TX_PIN_REG = 5;
    constexpr static int RX_PIN_REG = 6;
    constexpr static int INTERNAL_REG = 7;

    constexpr static int INTERNAL_CLOCK = 115200; // Hz

    CNUART();

    constexpr size_t size() const { return sizeof(registers); }

    void reset();
    void tick();

    bool shouldInterrupt() const;

    int txPin() const;
    int rxPin() const;

    uint8_t txOut() const;
    void rxIn(uint8_t value);

    uint8_t* registerData();

    void write(uint16_t address, uint8_t value);
    uint8_t read(uint16_t address);
private:
    uint8_t txOuput;
    uint8_t rxInput;
    int64_t cycles;
    int64_t cyclesTarget;

    int stopBits() const;
    int wordLength() const;
    int baudRate() const;
    int frameLength() const;

    bool rxInterruptEnabled() const;
    bool txInterruptEnabled() const;
    bool echoEnabled() const;
    bool parityEnabled() const;
    ParityType parityType() const;

    int txIndex();
    void txIndexInc();
    int rxIndex();
    void rxIndexInc();

    bool getParity(uint8_t value) const;
    void doTx();
    void doRx();
};