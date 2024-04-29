package com.elmfer.cnmcu.network;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;
import com.elmfer.cnmcu.mcu.NanoMCU;
import com.elmfer.cnmcu.mcu.cpu.MOS6502;
import com.elmfer.cnmcu.ui.IDEScreen;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class IDEScreenSyncS2CPacket extends Packet.S2C {
    
    public IDEScreenSyncS2CPacket(CNnanoBlockEntity blockEntity) {
        super(PacketByteBufs.create());
        
        NanoMCU mcu = blockEntity.mcu;
        MOS6502 cpu = mcu.getCPU();
        
        buffer.writeBoolean(mcu.isPowered());
        buffer.writeBoolean(mcu.isClockPaused());
        
        buffer.writeInt(cpu.getA());
        buffer.writeInt(cpu.getX());
        buffer.writeInt(cpu.getY());
        buffer.writeInt(cpu.getPC());
        buffer.writeInt(cpu.getS());
        buffer.writeInt(cpu.getP());
        buffer.writeLong(mcu.numCycles());
        
        buffer.writeInt(mcu.busAddress());
        buffer.writeInt(mcu.busData());
        buffer.writeBoolean(mcu.busRW());
        
        byte zeroPage[] = new byte[256];
        blockEntity.mcu.getRAM().getData().get(zeroPage);
        buffer.writeByteArray(zeroPage);
    }
    
    public static void recieve(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(!(client.currentScreen instanceof IDEScreen))
            return;
        
        IDEScreen screen = (IDEScreen) client.currentScreen;
        
        CPUStatus cpuStatus = new CPUStatus();
        BusStatus busStatus = new BusStatus();
        
        screen.isPowered = buf.readBoolean();
        screen.isClockPaused = buf.readBoolean();
        
        cpuStatus.A = buf.readInt();
        cpuStatus.X = buf.readInt();
        cpuStatus.Y = buf.readInt();
        cpuStatus.PC = buf.readInt();
        cpuStatus.SP = buf.readInt();
        cpuStatus.Flags = buf.readInt();
        cpuStatus.Cycles = buf.readLong();
        
        busStatus.Address = buf.readInt();
        busStatus.Data = buf.readInt();
        busStatus.RW = buf.readBoolean();
        
        screen.cpuStatus = cpuStatus;
        screen.busStatus = busStatus;
        screen.zeroPage.clear();
        buf.readVarInt();
        buf.readBytes(screen.zeroPage);
    }
    
    public static class CPUStatus {
        public int A;
        public int X;
        public int Y;
        public int PC;
        public int SP;
        public int Flags;
        public long Cycles;
    }
    
    public static class BusStatus {
        public int Address;
        public int Data;
        public boolean RW;
    }
}
