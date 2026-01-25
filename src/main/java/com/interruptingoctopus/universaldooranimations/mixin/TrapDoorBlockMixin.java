package com.interruptingoctopus.universaldooranimations.mixin;

import com.interruptingoctopus.universaldooranimations.common.block.entity.AnimatedDoorBlockEntity;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin to attach a Block Entity to the vanilla TrapDoorBlock.
 */
@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixin implements EntityBlock {

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnimatedDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide()) {
            if (blockEntityType == ModBlockEntities.DOOR_ANIMATION_BE.get()) {
                return (level1, pos, state1, be) -> {
                    if (be instanceof AnimatedDoorBlockEntity animatedDoor) {
                        animatedDoor.clientTick();
                    }
                };
            }
        }
        return null;
    }
}
