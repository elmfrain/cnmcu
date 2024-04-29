package com.elmfer.cnmcu.network;

import java.util.UUID;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;
import com.elmfer.cnmcu.mcu.NanoMCU;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class UploadROMC2S2CPacket extends Packet.C2S2C {

    public int bytesUploaded;
    public String message = "";
    
    /**
     * Constructor for client to server packet
     * 
     * @param buffer
     */
    public UploadROMC2S2CPacket(UUID mcuId, byte[] rom) {
        super(PacketByteBufs.create());
        
        buffer.writeUuid(mcuId);
        buffer.writeInt(rom.length);
        buffer.writeBytes(rom);
    }

    /**
     * Constructor for server to client packet
     * 
     * @param buffer
     */
    public UploadROMC2S2CPacket(PacketByteBuf requestBuffer) {
        super(PacketByteBufs.create(), requestBuffer);
    }

    public static void recieveRequest(MinecraftServer server, ServerPlayerEntity player,
            ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UploadROMC2S2CPacket packet = new UploadROMC2S2CPacket(buf);
        UUID mcuId = buf.readUuid();
        
        if (!CNnanoBlockEntity.SCREEN_UPDATES.containsKey(mcuId)) {
            packet.buffer.writeInt(0);
            packet.buffer.writeString("MCU not found.");
            packet.respond(player);
            return;
        }
        
        int binarySize = buf.readInt();
        NanoMCU mcu = CNnanoBlockEntity.SCREEN_UPDATES.get(mcuId).getEntity().mcu;
        
        if (mcu.getROM().getSize() != binarySize) {
            String message = String.format("Binary size mismatch. Expected: %d, Got: %d", mcu.getROM().getSize(), binarySize);
            packet.buffer.writeInt(0);
            packet.buffer.writeString(message);
            packet.respond(player);
            return;
        }
        
        server.execute(() -> {
            mcu.setPowered(false);
            
            byte[] rom = new byte[binarySize];
            buf.readBytes(rom);
            
            mcu.getROM().getData().put(rom);
            
            mcu.setPowered(true);
        });
        
        String message = String.format("Uploaded %d bytes.", binarySize);
        packet.buffer.writeInt(binarySize);
        packet.buffer.writeString(message);
        packet.respond(player);
    }

    @Environment(value=EnvType.CLIENT)
    public static void recieveResponse(MinecraftClient server, ClientPlayNetworkHandler handler, PacketByteBuf buf,
            PacketSender responseSender, UploadROMC2S2CPacket requestPacket) {
        
        requestPacket.bytesUploaded = buf.readInt();
        requestPacket.message = buf.readString();
    }
}
