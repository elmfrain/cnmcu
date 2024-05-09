#pragma once

#include <cstddef>
#include <cstdint>
#include <algorithm>

template <size_t N>
class CNRAM
{
public:
    void reset()
    {
        for(size_t i = 0; i < N; i++)
            ram[i] = 0;
    }

    CNRAM()
    {
        reset();
    }

    CNRAM(const uint8_t* data, size_t dataSize)
    {
        size_t numBytesToRead = std::min(dataSize, N);
        for(size_t i = 0; i < N; i++)
            ram[i] = i < numBytesToRead ? data[i] : 0;
    }

    CNRAM(CNRAM&& other)
    {
        *this = std::move(other);
    }
    
    CNRAM& operator=(CNRAM&& other)
    {
        for(size_t i = 0; i < N; i++)
            ram[i] = other.ram[i];
        return *this;
    }

    size_t size() const { return N; }
    uint8_t* data() { return ram; }

    uint8_t read(uint16_t address) const { return address < N ? ram[address] : 0; }
    void write(uint16_t address, uint8_t value) { if(address < N) ram[address] = value; }
private:
    uint8_t ram[N];
};