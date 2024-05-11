#pragma once

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <algorithm>

template <size_t N>
class CNGPIO
{
public:
    enum IRQType : uint8_t
    {
        NO_INTERRUPT = 0x0,
        LOW = 0x1,
        HIGH = 0x2,
        RISING = 0x3,
        FALLING = 0x4,
        CHANGE = 0x5,
        ANALOG_CHANGE = 0x6,
        ANALOG_RISING = 0x7,
        ANALOG_FALLING = 0x8,
        NO_CHANGE = 0x9
    };
private:
    constexpr static int GPIOPV = 0;
    constexpr static int GPIODIR = 1;
    constexpr static int GPIOINT = 2;
    constexpr static int GPIOIFL = 3;

    int mapAddress(uint16_t* address) const
    {
        if(*address < N)
            return GPIOPV;// 0x0000
        *address -= N;
        if(*address < N / 8) // 0x0040
            return GPIODIR;
        *address -= N / 8;
        if(*address < N / 2) // 0x0048
            return GPIOINT;
        *address -= N / 2;
        if(*address < N / 8) // 0x0068
            return GPIOIFL;
        *address -= N / 8;
        return -1;
    }
    
    bool triggersInterrupt(IRQType type, uint8_t pv, uint8_t pvOld) const
    {
        pv &= 0xF;
        pvOld &= 0xF;

        switch(type)
        {
            case LOW:
                return pv == 0;
            case HIGH:
                return pv > 0;
            case RISING:
                return (pv > 0) && (pvOld == 0);
            case FALLING:
                return (pv == 0) && (pvOld > 0);
            case CHANGE:
                return (pv > 0) != (pvOld > 0);
            case ANALOG_CHANGE:
                return pv != pvOld;
            case ANALOG_RISING:
                return pv > pvOld;
            case ANALOG_FALLING:
                return pv < pvOld;
            case NO_CHANGE:
                return pv == pvOld;
            default:
                return false;
        }
    }

    uint8_t gpiopvFront[N];
    uint8_t gpiopvBack[N];

    uint8_t gpiodir[N / 8];
    uint8_t gpioint[N / 2];
    uint8_t gpioifl[N / 8];
public:
    void reset()
    {
        memset(gpiopvFront, 0, N);
        memset(gpiopvBack, 0, N);
        memset(gpiodir, 0, N / 8);
        memset(gpioint, 0, N / 2);
        memset(gpioifl, 0, N / 8);
    }

    CNGPIO() { reset(); }
    CNGPIO(CNGPIO&& other) { *this = std::move(other); }
    CNGPIO& operator=(CNGPIO&& other)
    {
        memcpy(gpiopvFront, other.gpiopvFront, N);
        memcpy(gpiopvBack, other.gpiopvBack, N);
        memcpy(gpiodir, other.gpiodir, N / 8);
        memcpy(gpioint, other.gpioint, N / 2);
        memcpy(gpioifl, other.gpioifl, N / 8);
        return *this;
    }

    size_t size() const
    {
        return N + N / 8 + N / 2 + N / 8;
    }

    uint8_t* pvFrontData() { return gpiopvFront; }
    uint8_t* pvBackData() { return gpiopvBack; }
    uint8_t* dirData() { return gpiodir; }
    uint8_t* intData() { return gpioint; }
    uint8_t* iflData() { return gpioifl; }

    bool isInput(size_t pin) const
    {
        if(pin >= N || pin < 0)
            return false;

        return (gpiodir[pin / 8] & (1 << (pin % 8))) == 0;
    }

    uint8_t read(uint16_t address) const
    {
        int buffer = mapAddress(&address);

        switch(buffer)
        {
            case GPIOPV:
                return gpiopvFront[address];
            case GPIODIR:
                return gpiodir[address];
            case GPIOINT:
                return gpioint[address];
            case GPIOIFL:
                return gpioifl[address];
            default:
                return 0;
        }
    }

    void write(uint16_t address, uint8_t value)
    {
        int bufferID = mapAddress(&address);

        bool isInput;

        switch(bufferID)
        {
            case GPIOPV:
                isInput = (gpiodir[address / 8] & (1 << (address % 8))) == 0;
                if(isInput)
                    return;
                gpiopvFront[address] = value;
                return;
            case GPIODIR:
                gpiodir[address] = value;
                return;
            case GPIOINT:
                gpioint[address] = value;
                return;
            case GPIOIFL:
                for(int i = 0; i < 8; i++)
                {
                    if((value & (1 << i)) == 0) continue;

                    IRQType irqType = static_cast<IRQType>((gpioint[address * 4 + i / 2] >> ((i % 2) * 4)) & 0xF);

                    if(irqType == LOW || irqType == HIGH)
                    {
                        uint8_t pv = gpiopvBack[address * 8 + i] & 0xF;
                        if(irqType == LOW && pv == 0)
                            continue;
                        else if(irqType == HIGH && pv > 0)
                            continue;
                    }

                    gpioifl[address] &= ~(1 << i);
                }
                return;
        }
    }

    void tickInterrupts()
    {
        // Handle input interrupts
        for(size_t i = 0; i < N; i++)
        {
            bool isInput = (gpiodir[i / 8] & (1 << (i % 8))) == 0;
            if(!isInput)
            {
                gpioifl[i / 8] &= ~(1 << (i % 8)); // Clear interrupt flag when pin is not an input
                continue;
            }
            IRQType irqType = static_cast<IRQType>((gpioint[i / 2] >> ((i % 2) * 4)) & 0xF);
            if(irqType == NO_INTERRUPT)
            {
                gpioifl[i / 8] &= ~(1 << (i % 8)); // Clear interrupt flag when pin is not an input
                continue;
            }

            uint8_t pv = gpiopvFront[i];
            uint8_t pvOld = gpiopvBack[i];
            bool triggers = triggersInterrupt(irqType, pv, pvOld);
            if(triggers)
                gpioifl[i / 8] |= (1 << (i % 8));
        }

        // Trigger interrupt if any of the interrupt flags are set
    }

    void copyBuffers()
    {
        memcpy(gpiopvBack, gpiopvFront, N);
    }

    bool shouldInterrupt() const
    {
        // Trigger interrupt if any of the interrupt flags are set
        for(size_t i = 0; i < N / 8; i++)
        {
            if(gpioifl[i] != 0)
                return true;
        }

        return false;
    }
};