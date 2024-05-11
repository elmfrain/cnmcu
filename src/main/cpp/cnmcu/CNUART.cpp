#include "CNUART.hpp"

#include "Nano.hpp"

#include <cstring>

CNUART::CNUART()
{
    reset();
}

void CNUART::reset()
{
    memset(registers, 0, sizeof(registers));
    registers[STATUS_REG] = 1 << TX_BUFFER_EMPTY;
    registers[CONTROL_REG] = 0b01110011; // 1 stop bit, 8 bites, 10 baud
    registers[RX_PIN_REG] = 1;
    txOuput = 0;
    rxInput = 0;
    cycles = 0;
    cyclesTarget = 0;
}

void CNUART::tick()
{
    int baud = baudRate();

    while(cycles - cyclesTarget > 0)
    {
        doTx();
        doRx();

        cyclesTarget += INTERNAL_CLOCK / baud;
    }

    cycles += INTERNAL_CLOCK / GAME_TICK_RATE;
}

bool CNUART::shouldInterrupt() const
{
    return registers[STATUS_REG] & (1 << IRQ);
}

int CNUART::txPin() const
{
    if(!(registers[COMMAND_REG] & (1 << TX_PIN_ENABLE)))
        return -1;

    return registers[TX_PIN_REG];
}

int CNUART::rxPin() const
{
    if(!(registers[COMMAND_REG] & (1 << RX_PIN_ENABLE)))
        return -1;

    return registers[RX_PIN_REG];
}

uint8_t CNUART::txOut() const
{
    return txOuput;
}

void CNUART::rxIn(uint8_t value)
{
    rxInput = value;
}

uint8_t* CNUART::registerData()
{
    return registers;
}

void CNUART::write(uint16_t address, uint8_t value)
{
    if(size() <= address)
        return;

    // Writing to status register resets the module
    if (address == STATUS_REG)
    {
        reset();
        return;
    }

    // Writing to the tx data register triggers a transmission
    // If tx buffer is not empty, it's an overrun error
    if (address == TX_DATA_REG)
    {
        bool isTxEmpty = registers[STATUS_REG] & (1 << TX_BUFFER_EMPTY);
        if (!isTxEmpty)
            registers[STATUS_REG] |= 1 << OVERRUN_ERROR;
        else
        {
            registers[STATUS_REG] &= ~(1 << OVERRUN_ERROR);
            txIndexInc();
        }
    }

    // You won't be able to write to the internal register
    if (address == INTERNAL_REG)
        return;

    registers[address] = value;
}

uint8_t CNUART::read(uint16_t address)
{
    if(size() <= address)
        return 0;

    // Reading the status register clears the IRQ flag
    if (address == STATUS_REG)
    {
        uint8_t statusReg = registers[STATUS_REG];
        registers[STATUS_REG] &= ~(1 << IRQ);
        return statusReg;
    }

    // Reading the rx data register clears the RX buffer full flag
    if (address == RX_DATA_REG)
    {
        registers[STATUS_REG] &= ~(1 << RX_BUFFER_FULL);
        registers[STATUS_REG] &= ~(1 << OVERRUN_ERROR);
        registers[STATUS_REG] &= ~(1 << FRAME_ERROR);
        registers[STATUS_REG] &= ~(1 << PARITY_ERROR);
    }

    return registers[address];
}

int CNUART::stopBits() const
{
    return ((registers[CONTROL_REG] & 0b10000000) >> 7) + 1;
}

int CNUART::wordLength() const
{
    return ((registers[CONTROL_REG] & 0b01110000) >> 4) + 1;
}

int CNUART::baudRate() const
{
    const static int baudRates[] =
    {
        1, 2, 5, 10, 20, 50, 150, 300, 600, 1200, 1800, 2400, 3600, 4800, 7200, 9600
    };

    uint8_t selection = registers[CONTROL_REG] & 0b00001111;

    return baudRates[selection];
}

int CNUART::frameLength() const
{
    uint8_t parityBit = parityEnabled() ? 1 : 0;

    return 1 + wordLength() + parityBit + stopBits();
}

bool CNUART::rxInterruptEnabled() const
{
    return registers[COMMAND_REG] & (1 << RX_INTERRUPT_ENABLE);
}

bool CNUART::txInterruptEnabled() const
{
    return registers[COMMAND_REG] & (1 << TX_INTERRUPT_ENABLE);
}

bool CNUART::echoEnabled() const
{
    return registers[COMMAND_REG] & (1 << ECHO_ENABLE);
}

bool CNUART::parityEnabled() const
{
    return registers[COMMAND_REG] & (1 << PARITY_ENABLE);
}

CNUART::ParityType CNUART::parityType() const
{
    return (ParityType)((registers[COMMAND_REG] & (1 << PARITY_TYPE)) >> PARITY_TYPE);
}

int CNUART::txIndex()
{
    return registers[INTERNAL_REG] & 0b00001111;
}

void CNUART::txIndexInc()
{
    uint8_t txi = txIndex() + 1;

    if(txi > frameLength())
    {
        txi = 0;
        registers[STATUS_REG] |= 1 << TX_BUFFER_EMPTY;
        if(txInterruptEnabled())
            registers[STATUS_REG] |= 1 << IRQ;
    }
    else if(txi == 1)
        registers[STATUS_REG] &= ~(1 << TX_BUFFER_EMPTY);

    registers[INTERNAL_REG] = (registers[INTERNAL_REG] & 0b11110000) | (txi & 0b00001111);
}

int CNUART::rxIndex()
{
    return (registers[INTERNAL_REG] & 0b11110000) >> 4;
}

void CNUART::rxIndexInc()
{
    uint8_t rxi = rxIndex() + 1;

    if(rxi > frameLength())
    {
        rxi = 0;
        registers[STATUS_REG] |= 1 << RX_BUFFER_FULL;
        if(rxInterruptEnabled())
            registers[STATUS_REG] |= 1 << IRQ;
        
        if (echoEnabled())
        {
            bool isTxEmpty = registers[STATUS_REG] & (1 << TX_BUFFER_EMPTY);
            if (!isTxEmpty)
                registers[STATUS_REG] |= 1 << OVERRUN_ERROR;
            else
            {
                registers[STATUS_REG] &= ~(1 << OVERRUN_ERROR);
                txIndexInc();
            }
            registers[TX_DATA_REG] = registers[RX_DATA_REG];
        }
    }
    else if(rxi == 1)
        registers[STATUS_REG] &= ~(1 << RX_BUFFER_FULL);

    registers[INTERNAL_REG] = (registers[INTERNAL_REG] & 0b00001111) | (rxi << 4);
}

bool CNUART::getParity(uint8_t value) const
{
    int sum = 0;
    for(int i = 0; i < 8; i++)
        sum += (value >> i) & 1;

    bool even = sum % 2 == 0;

    return parityType() == EVEN ? even : !even;
}

void CNUART::doTx()
{
    uint8_t txi = txIndex();

    if(txi == 0)
        return;

    txi--;

    if(txi-- == 0) // Start bit
    {
        txOuput = 15;
        txIndexInc();
    }
    else if(txi < wordLength()) // Data bits
    {
        bool bitSet = (registers[TX_DATA_REG] >> txi) & 1;
        txOuput = bitSet ? 15 : 0;
        txIndexInc();
    }
    else // Parity and/or stop bits
    {
        if(parityEnabled() && txi == wordLength())
        {
            bool parityBit = getParity(registers[TX_DATA_REG]);

            txOuput = parityBit ? 15 : 0;
        }
        else
            txOuput = 0;

        txIndexInc();
    }
}

void CNUART::doRx()
{
    uint8_t rxi = rxIndex();

    if(rxi == 0 && rxInput == 0)
        return;
    else if(rxi == 0)
    {
        rxIndexInc();
        rxi = 1;
    }

    rxi--;

    if(rxi-- == 0) // Start bit
    {
        registers[RX_DATA_REG] = 0;
        rxIndexInc();
    }
    else if(rxi < wordLength()) // Data bits
    {
        bool bitSet = rxInput > 0;
        registers[RX_DATA_REG] |= bitSet << rxi;
        rxIndexInc();
    }
    else // Parity and/or stop bits
    {
        if(parityEnabled() && rxi == wordLength())
        {
            bool parityBit = rxInput > 0;
            bool correctParity = getParity(rxInput);

            if(parityBit != correctParity)
                registers[STATUS_REG] |= 1 << PARITY_ERROR;
        }
        else if(rxInput != 0) // Stop bit must be 0 or it's a frame error
            registers[STATUS_REG] |= 1 << FRAME_ERROR;

        rxIndexInc();
    }
}