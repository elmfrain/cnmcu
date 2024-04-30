package com.elmfer.cnmcu.network;

import java.util.UUID;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class IDEScreenHeartbeatC2SPacket extends Packet.C2S {
    
    public IDEScreenHeartbeatC2SPacket (UUID mcuId) {
        super(PacketByteBufs.create());
        
        buffer.writeUuid(mcuId);
    }
    
    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID mcuId = buf.readUuid();
        
        server.execute(() -> {
            if (CNnanoBlockEntity.SCREEN_UPDATES.containsKey(mcuId))
                CNnanoBlockEntity.SCREEN_UPDATES.get(mcuId).heartBeat(player.getUuid());
        });
    }
}
