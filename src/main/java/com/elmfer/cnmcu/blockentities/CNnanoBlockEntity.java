package com.elmfer.cnmcu.blockentities;

import com.elmfer.cnmcu.mcu.NanoMCU;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CNnanoBlockEntity extends BlockEntity {

    public final NanoMCU mcu;

    public CNnanoBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CN_NANO, pos, state);

        mcu = new NanoMCU();
        mcu.setPowered(true);
        System.out.println("Created block entity");
    }

    public static void tick(World world, BlockPos pos, BlockState state, CNnanoBlockEntity blockEntity) {
        if (blockEntity.mcu != null && blockEntity.removed) {
            blockEntity.mcu.deleteNativeObject();
            System.out.println("Deleted block entity");
        }
        
        blockEntity.mcu.tick();
        world.updateNeighborsAlways(pos, state.getBlock());
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
