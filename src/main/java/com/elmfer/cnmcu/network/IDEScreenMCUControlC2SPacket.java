package com.elmfer.cnmcu.network;

import java.util.UUID;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class IDEScreenMCUControlC2SPacket extends Packet.C2S {
    
    public IDEScreenMCUControlC2SPacket(UUID mcuId, Control control) {
        super(PacketByteBufs.create());
        
        buffer.writeUuid(mcuId);
        buffer.writeInt(control.getId());
    }

    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID mcuId = buf.readUuid();
        
        if (!CNnanoBlockEntity.SCREEN_UPDATES.containsKey(mcuId))
            return;
        
        Control control = Control.values()[buf.readInt()];
        CNnanoBlockEntity entity = CNnanoBlockEntity.SCREEN_UPDATES.get(mcuId).getEntity();
        
        switch (control) {
        case POWER_ON:
            entity.mcu.setPowered(true);
            break;
        case POWER_OFF:
            entity.mcu.setPowered(false);
            break;
        case RESET:
            entity.mcu.reset();
            break;
        case PAUSE_CLOCK:
            if (entity.mcu.isPowered())
                entity.mcu.setClockPause(true);
            break;
        case RESUME_CLOCK:
            if (entity.mcu.isPowered())
                entity.mcu.setClockPause(false);
            break;
        case CYCLE:
            if (entity.mcu.isClockPaused())
                entity.mcu.cycle();
            break;
        }
    }
    
    public static enum Control {
        POWER_ON(0),
        POWER_OFF(1),
        RESET(2),
        PAUSE_CLOCK(3),
        RESUME_CLOCK(4),
        CYCLE(5);
        
        private int id;
        
        private Control(int id) {
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
    }
}
