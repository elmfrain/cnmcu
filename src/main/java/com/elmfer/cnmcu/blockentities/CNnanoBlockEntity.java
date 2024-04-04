package com.elmfer.cnmcu.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CNnanoBlockEntity extends BlockEntity {
    
    public CNnanoBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CN_NANO, pos, state);
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, CNnanoBlockEntity blockEntity) {
        
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }
}
