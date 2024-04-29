
package com.elmfer.cnmcu.network;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Packet {

    private static final Map<Class<? extends Packet>, Identifier> CHANNEL_MAP = new ConcurrentHashMap<>();

    protected PacketByteBuf buffer;
    
    public Packet(PacketByteBuf buffer) {
        this.buffer = buffer;
    }

    protected static void setChannel(Class<? extends Packet> clazz, String channelName) {
        CHANNEL_MAP.put(clazz, CodeNodeMicrocontrollers.id(channelName));
    }

    protected static Identifier getChannel(Class<? extends Packet> clazz) {
        return CHANNEL_MAP.get(clazz);
    }
    
    protected Identifier getChannel() {
        return CHANNEL_MAP.get(this.getClass());
    }

    public static abstract class S2C extends Packet {

        public S2C(PacketByteBuf buffer) {
            super(buffer);
        }

        protected static ClientPlayNetworking.PlayChannelHandler getHandler(Class<? extends Packet.S2C> clazz) {
            try {
                Method method = clazz.getMethod("recieve", MinecraftClient.class, ClientPlayNetworkHandler.class,
                        PacketByteBuf.class, PacketSender.class);

                return (client, handler, buf, responseSender) -> {
                    try {
                        method.invoke(null, client, handler, buf, responseSender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public void send(ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, getChannel(), buffer);
        }
    }

    public static abstract class C2S extends Packet {

        public C2S(PacketByteBuf buffer) {
            super(buffer);
        }

        protected static ServerPlayNetworking.PlayChannelHandler getHandler(Class<? extends Packet.C2S> clazz) {
            try {
                Method method = clazz.getMethod("recieve", MinecraftServer.class, ServerPlayerEntity.class,
                        ServerPlayNetworkHandler.class, PacketByteBuf.class, PacketSender.class);

                return (server, player, handler, buf, responseSender) -> {
                    try {
                        method.invoke(null, server, player, handler, buf, responseSender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public void send() {
            ClientPlayNetworking.send(getChannel(), buffer);
        }
    }
    
    public static abstract class C2S2C extends Packet {

        static final Map<Integer, C2S2C> TRANSACTIONS = new ConcurrentHashMap<>();
        
        static int nextTransactionId = 0;
        
        private int transactionId;
        private boolean ready = false;
        
        public C2S2C(PacketByteBuf buffer) {
            super(buffer);
            
            transactionId = nextTransactionId++;
            buffer.writeInt(transactionId);
        }
        
        public C2S2C(PacketByteBuf responseBuffer, PacketByteBuf requestBuffer) {
            super(responseBuffer);

            this.transactionId = requestBuffer.readInt();
            buffer.writeInt(transactionId);
        }

        protected static ServerPlayNetworking.PlayChannelHandler getServerHandler(Class<? extends Packet.C2S2C> clazz) {
            try {
                Method method = clazz.getMethod("recieveRequest", MinecraftServer.class, ServerPlayerEntity.class,
                        ServerPlayNetworkHandler.class, PacketByteBuf.class, PacketSender.class);

                return (server, player, handler, buf, responseSender) -> {
                    try {
                        method.invoke(null, server, player, handler, buf, responseSender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        
        protected static ClientPlayNetworking.PlayChannelHandler getClientHandler(Class<? extends Packet.C2S2C> clazz) {
            try {
                Method method = clazz.getMethod("recieveResponse", MinecraftClient.class, ClientPlayNetworkHandler.class,
                        PacketByteBuf.class, PacketSender.class, clazz);

                return (client, handler, buf, responseSender) -> {
                    try {
                        C2S2C transaction = TRANSACTIONS.get(buf.readInt());
                        transaction.ready = true;
                        TRANSACTIONS.remove(transaction.transactionId);
                        method.invoke(null, client, handler, buf, responseSender, transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected static Identifier getServerChannel(Class<? extends C2S2C> clazz) {
            return getChannel(clazz).withPath("server");
        }
        
        protected static Identifier getClientChannel(Class<? extends C2S2C> clazz) {
            return getChannel(clazz).withPath("client");
        }
        
        protected Identifier getServerChannel() {
            return getChannel().withPath("server");
        }
        
        protected Identifier getClientChannel() {
            return getChannel().withPath("client");
        }
        
        public void respond(ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, getClientChannel(), buffer);
        }

        public void request() {
            if (TRANSACTIONS.containsKey(transactionId))
                return;
            
            TRANSACTIONS.put(transactionId, this);
            ClientPlayNetworking.send(getServerChannel(), buffer);
        }
        
        public boolean isReady() {
            return ready;
        }
    }
}
