package com.elmfer.cnmcu.blocks;

import org.jetbrains.annotations.Nullable;

import com.elmfer.cnmcu.blockentities.BlockEntities;
import com.elmfer.cnmcu.blockentities.CNnanoBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CNnanoBlock extends BlockWithEntity {
    public static final MapCodec<CNnanoBlock> CODEC = createCodec(CNnanoBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected CNnanoBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<CNnanoBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CNnanoBlockEntity(pos, state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof CNnanoBlockEntity))
            return 0;

        CNnanoBlockEntity entity = (CNnanoBlockEntity) blockEntity;
        
        if (entity.mcu == null || !entity.mcu.isPowered())
            return 0;
        
        Direction blockDir = state.get(FACING);
        Direction localDir = getLocalDirection(blockDir, direction);

        switch (localDir) {
        case EAST:
            return entity.mcu.rightOutput;
        case SOUTH:
            return entity.mcu.backOutput;
        case WEST:
            return entity.mcu.leftOutput;
        case NORTH:
            return entity.mcu.frontOutput;
        default:
            return 0;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient)
            return null;

        return CNnanoBlock.validateTicker(type, BlockEntities.CN_NANO, world.isClient ? null : CNnanoBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null)
                player.openHandledScreen(screenHandlerFactory);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }
    
    public static Direction getGlobalDirection(Direction facing, Direction direction) {
        switch (facing) {
        case EAST:
            return direction.rotateYClockwise();
        case SOUTH:
            return direction.getOpposite();
        case WEST:
            return direction.rotateYCounterclockwise();
        default:
            return direction;
        }
    }
    
    public static Direction getLocalDirection(Direction facing, Direction direction) {
        switch (facing) {
        case WEST:
            return direction.rotateYCounterclockwise();
        case NORTH:
            return direction.getOpposite();
        case EAST:
            return direction.rotateYClockwise();
        default:
            return direction;
        }
    }
}
