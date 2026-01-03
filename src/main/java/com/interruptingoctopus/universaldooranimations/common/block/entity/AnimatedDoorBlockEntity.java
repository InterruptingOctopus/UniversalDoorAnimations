package com.interruptingoctopus.universaldooranimations.common.block.entity;

import com.interruptingoctopus.universaldooranimations.api.animation.DoorHingeComponent;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A BlockEntity that provides animation capabilities for doors and trapdoors.
 * It uses a {@link DoorHingeComponent} to manage the animation state.
 */
public class AnimatedDoorBlockEntity extends BlockEntity {
    private final DoorHingeComponent hinge = new DoorHingeComponent(90f, 0.15f);

    public AnimatedDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOOR_ANIMATION_BE.get(), pos, state);
    }

    /**
     * The server-side tick method for this BlockEntity.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, AnimatedDoorBlockEntity be) {
        boolean isOpen = false;
        if (state.hasProperty(DoorBlock.OPEN)) {
            isOpen = state.getValue(DoorBlock.OPEN);
        } else if (state.hasProperty(TrapDoorBlock.OPEN)) {
            isOpen = state.getValue(TrapDoorBlock.OPEN);
        }
        be.hinge.tick(isOpen);
    }

    /**
     * Gets the hinge component associated with this block entity.
     */
    public DoorHingeComponent getHinge() {
        return hinge;
    }
}
