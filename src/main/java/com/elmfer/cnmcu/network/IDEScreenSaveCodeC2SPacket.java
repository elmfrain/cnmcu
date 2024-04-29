package com.elmfer.cnmcu.network;

import java.util.UUID;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class IDEScreenSaveCodeC2SPacket extends Packet.C2S {

    public IDEScreenSaveCodeC2SPacket(String code, UUID mcuId) {
        super(PacketByteBufs.create());
        
        buffer.writeUuid(mcuId);
        buffer.writeInt(code.length());
        buffer.writeBytes(code.getBytes());
    }

    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID mcuId = buf.readUuid();

        byte[] code = new byte[buf.readInt()];
        buf.readBytes(code);

        String codeStr = new String(code);

        if (CNnanoBlockEntity.SCREEN_UPDATES.containsKey(mcuId)) {
            CNnanoBlockEntity entity = CNnanoBlockEntity.SCREEN_UPDATES.get(mcuId).getEntity();
            entity.setCode(codeStr);
            entity.markDirty();
        }
            
    }
}
