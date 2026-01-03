package com.interruptingoctopus.universaldooranimations.mixin;

import com.interruptingoctopus.universaldooranimations.common.block.entity.AnimatedDoorBlockEntity;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements EntityBlock {

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnimatedDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @Nullable Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        if (level != null && !level.isClientSide()) {
            if (blockEntityType == ModBlockEntities.DOOR_ANIMATION_BE.get()) {
                return (BlockEntityTicker<T>) (BlockEntityTicker<AnimatedDoorBlockEntity>) AnimatedDoorBlockEntity::tick;
            }
        }
        return null;
    }
}
