#pragma once

#include <cstddef>
#include <cstdint>
#include <algorithm>

template <size_t N>
class CNROM
{
public:
    CNROM()
    {
        for(size_t i = 0; i < N; i++)
            rom[i] = 0;
    }

    CNROM(const uint8_t* data, size_t dataSize)
    {
        for(size_t i = 0; i < N; i++)
            rom[i] = 0;

        size_t numBytesToRead = std::min(dataSize, N);
        for(size_t i = 0; i < numBytesToRead; i++)
            rom[i] = data[i];
    }

    CNROM(CNROM&& other)
    {
        *this = std::move(other);
    }

    CNROM& operator=(CNROM&& other)
    {
        for(size_t i = 0; i < N; i++)
            rom[i] = other.rom[i];
        return *this;
    }

    size_t size() { return N; }
    uint8_t* data() { return rom; }

    uint8_t read(uint16_t address) const
    {
        return address < N ? rom[address] : 0;
    }
    
    void write(uint16_t address, uint8_t value)
    {
        if(writeProtect)
            return;

        if(address < N)
            rom[address] = value;
    }

    void setWriteProtect(bool writeProtect) { this->writeProtect = writeProtect; }
    bool isWriteProtected() const { return writeProtect; }
private:
    uint8_t rom[N];
    bool writeProtect = true;
};