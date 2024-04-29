package com.elmfer.cnmcu.ui.handler;

import java.util.UUID;

import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

public class IDEScreenHandler extends ScreenHandler {

    private UUID mcuID;
    private String code;
    
    public IDEScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(ScreenHandlers.IDE_SCREEN_HANDLER, syncId);
        
        mcuID = buf.readUuid();
        
        byte[] code = new byte[buf.readInt()];
        buf.readBytes(code);
        
        this.code = new String(code);
    }

    public IDEScreenHandler(int syncId, UUID mcuID) {
        super(ScreenHandlers.IDE_SCREEN_HANDLER, syncId);
        
        this.mcuID = mcuID;
    }

    public UUID getMcuID() {
        return mcuID;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public void onClosed(PlayerEntity player) {
        if (!player.getWorld().isClient() && CNnanoBlockEntity.SCREEN_UPDATES.containsKey(mcuID)) 
            CNnanoBlockEntity.SCREEN_UPDATES.get(mcuID).removeListener(player.getUuid());
    }
   
    @Override
    public ItemStack quickMove(PlayerEntity var1, int var2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity var1) {
        // TODO Auto-generated method stub
        return true;
    }
}
