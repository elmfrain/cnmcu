package com.elmfer.cnmcu.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Packets {

    public static void initClientPackets() {
        registerS2CPacket("ide_screen_sync", IDEScreenSyncS2CPacket.class);
        
        registerC2S2CClientPacket("upload_rom", UploadROMC2S2CPacket.class);
    }

    public static void initServerPackets() {
        registerC2SPacket("ide_screen_heartbeat", IDEScreenHeartbeatC2SPacket.class);
        registerC2SPacket("ide_screen_save_code", IDEScreenSaveCodeC2SPacket.class);
        registerC2SPacket("ide_screen_mcu_control", IDEScreenMCUControlC2SPacket.class);
        
        Packet.setChannel(IDEScreenSyncS2CPacket.class, "ide_screen_sync");
        registerC2S2CServerPacket("upload_rom", UploadROMC2S2CPacket.class);
    }

    private static void registerS2CPacket(String channelName, Class<? extends Packet.S2C> packetClass) {
        Packet.setChannel(packetClass, channelName);
        ClientPlayNetworking.registerGlobalReceiver(Packet.getChannel(packetClass), Packet.S2C.getHandler(packetClass));
    }

    private static void registerC2SPacket(String channelName, Class<? extends Packet.C2S> packetClass) {
        Packet.setChannel(packetClass, channelName);
        ServerPlayNetworking.registerGlobalReceiver(Packet.getChannel(packetClass), Packet.C2S.getHandler(packetClass));
    }

    private static void registerC2S2CServerPacket(String channelName, Class<? extends Packet.C2S2C> packetClass) {
        Packet.setChannel(packetClass, channelName);
        ServerPlayNetworking.registerGlobalReceiver(Packet.C2S2C.getServerChannel(packetClass),
                Packet.C2S2C.getServerHandler(packetClass));
    }

    private static void registerC2S2CClientPacket(String channelName, Class<? extends Packet.C2S2C> packetClass) {
        Packet.setChannel(packetClass, channelName);
        ClientPlayNetworking.registerGlobalReceiver(Packet.C2S2C.getClientChannel(packetClass),
                Packet.C2S2C.getClientHandler(packetClass));
    }
}
