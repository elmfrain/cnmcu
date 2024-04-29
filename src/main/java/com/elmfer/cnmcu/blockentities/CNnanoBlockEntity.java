package com.elmfer.cnmcu.blockentities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.elmfer.cnmcu.blocks.CNnanoBlock;
import com.elmfer.cnmcu.mcu.NanoMCU;
import com.elmfer.cnmcu.network.IDEScreenSyncS2CPacket;
import com.elmfer.cnmcu.ui.handler.IDEScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CNnanoBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    public static final Map<UUID, ScreenUpdates> SCREEN_UPDATES = new HashMap<>();
    
    private static final Direction HORIZONTALS[] = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private static int inputs[] = new int[4];
    
    public NanoMCU mcu;
    private UUID uuid;
    private boolean hasInit = false;
    private boolean forceUpdate = false;
    
    private String code = "";
    
    public CNnanoBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CN_NANO, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CNnanoBlockEntity blockEntity) {
        if(!blockEntity.hasInit)
            blockEntity.init();
        
        Direction blockDir = state.get(CNnanoBlock.FACING);
        
        int i = 0;
        for (Direction horizontal : HORIZONTALS) {
            Direction globalDir = CNnanoBlock.getGlobalDirection(blockDir, horizontal);
            inputs[i] = world.getStrongRedstonePower(pos.offset(globalDir), globalDir.getOpposite());
            inputs[i++] |= world.getEmittedRedstonePower(pos.offset(globalDir), globalDir.getOpposite());
        }
        
        blockEntity.mcu.frontInput = inputs[0];
        blockEntity.mcu.rightInput = inputs[1];
        blockEntity.mcu.backInput = inputs[2];
        blockEntity.mcu.leftInput = inputs[3];
        
        blockEntity.mcu.tick();
        if(blockEntity.mcu.isPowered())
            blockEntity.markDirty();
        
        SCREEN_UPDATES.get(blockEntity.uuid).handleScreenListeners();
        
        if(blockEntity.forceUpdate) {
            blockEntity.forceUpdate = false;
            world.updateNeighborsAlways(pos, state.getBlock());
            return;
        }
        
        for (Direction horizontal : HORIZONTALS) {
            Direction globalDir = CNnanoBlock.getGlobalDirection(blockDir, horizontal);
            if(blockEntity.mcu.outputHasChanged(horizontal))
                world.updateNeighbor(pos.offset(globalDir), state.getBlock(), pos);
        }
    }
    
    protected void init() {
        if(hasInit)
            return;
        
        if (mcu == null)
            mcu = new NanoMCU();
        uuid = UUID.randomUUID();
        SCREEN_UPDATES.put(uuid, new ScreenUpdates(this));
        
        hasInit = true;
    }
    
    public UUID getUUID() {
        return uuid;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        if (!hasInit)
            init();
        
        if(!nbt.contains("code"))
            return;
        
        mcu.readNbt(nbt);
        code = nbt.getString("code");
        
        forceUpdate = true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        if (!hasInit)
            init();
        
        mcu.writeNbt(nbt);
        nbt.putString("code", code);
    }

   @Override
   public void markRemoved() {
       super.markRemoved();
       
       if(mcu != null) {
           mcu.deleteNativeObject();
           mcu = null;
       }
       
       SCREEN_UPDATES.remove(uuid);
   }
    
    @Override
    public Text getDisplayName() {
        // TODO Auto-generated method stub
        return Text.of("Code Node Nano");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        SCREEN_UPDATES.get(uuid).addListener((ServerPlayerEntity) player);
        return new IDEScreenHandler(syncId, uuid);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeUuid(uuid);
        
        byte[] codeBytes = code.getBytes();
        
        buf.writeInt(codeBytes.length);
        buf.writeBytes(codeBytes);
    }
    
    public static class Listener {
        ServerPlayerEntity player;
        int ticksSinceLastHeartbeat = 0;
        boolean shouldRemove = false;
        
        public Listener(ServerPlayerEntity player) {
            this.player = player;
        }
        
        public void update(IDEScreenSyncS2CPacket syncPacket) {
            
            if (player.isDisconnected()) {
                shouldRemove = true;
                return;
            }
            
            syncPacket.send(player);
            
            if (ticksSinceLastHeartbeat >= ScreenUpdates.NEXT_HEARTBEAT_EXPECTATION) {
                shouldRemove = true;
                return;
            }
            
            ticksSinceLastHeartbeat++;
        }
    }
    
    public static class ScreenUpdates {
        static final int NEXT_HEARTBEAT_EXPECTATION = 100;
        
        Map<UUID, Listener> listeners = new HashMap<>();
        CNnanoBlockEntity entity;
        
        public ScreenUpdates(CNnanoBlockEntity entity) {
            this.entity = entity;
            
        }
        
        public void addListener(ServerPlayerEntity player) {
            if(listeners.containsKey(player.getUuid()))
                return;
            
            listeners.put(player.getUuid(), new Listener(player));
        }
        
        public void removeListener(UUID playerUuid) {
            listeners.remove(playerUuid);
        }
        
        public void heartBeat(UUID playerUuid) {
            if (!listeners.containsKey(playerUuid))
                return;
            
            listeners.get(playerUuid).ticksSinceLastHeartbeat = 0;
        }
        
        public CNnanoBlockEntity getEntity() {
            return entity;
        }
        
        public void handleScreenListeners() {
            IDEScreenSyncS2CPacket syncPacket = new IDEScreenSyncS2CPacket(entity);
            
            listeners.entrySet().removeIf(entry -> {
                Listener listener = entry.getValue();
                listener.update(syncPacket);
                
                if (listener.shouldRemove)
                    listener.player.closeHandledScreen();
                
                return listener.shouldRemove;
            });
        }
    }
}
