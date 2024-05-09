#pragma once

#include <cstring>

template<size_t N>
class CNEL
{
public:
    void reset()
    {
        memset(iclRegisters, 0, N);
        memset(iflRegisters, 0, N);
    }
private:
    uint8_t iclRegisters[N];
    uint8_t iflRegisters[N];
public:
    CNEL() { reset(); }

    size_t size() const { return N * 2; }

    uint8_t* iclRegistersData() { return iclRegisters; }
    uint8_t* iflRegistersData() { return iflRegisters; }

    void triggerEvent(int eventId)
    {
        if(N * 8 <= eventId)
            return;

        iflRegisters[eventId / 8] |= 1 << (eventId % 8);
    }

    bool shouldInterrupt() const
    {
        for(size_t i = 0; i < N; i++)
            if(iflRegisters[i] & iclRegisters[i])
                return true;

        return false;
    }

    uint8_t read(uint16_t address) const
    {
        if(N * 2 <= address)
            return 0;

        if(address < N)
            return iclRegisters[address];
        
        return iflRegisters[address - N];
    }

    void write(uint16_t address, uint8_t value)
    {
        if(N * 2 <= address)
            return;

        if(address < N)
            iclRegisters[address] = value;
        else
            iflRegisters[address - N] &= ~value;
    }
};