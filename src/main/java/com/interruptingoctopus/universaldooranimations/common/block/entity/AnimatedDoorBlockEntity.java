package com.interruptingoctopus.universaldooranimations.common.block.entity;

import com.interruptingoctopus.universaldooranimations.api.animation.DoorHingeComponent;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

/**
 * A Block Entity that holds animation data for doors and trapdoors.
 * It is attached to vanilla door blocks via mixins to enable custom rendering.
 */
public class AnimatedDoorBlockEntity extends BlockEntity {
    private final DoorHingeComponent hinge = new DoorHingeComponent(90f, 0.30f);
    private boolean wasOpen = false;

    /**
     * Constructs a new AnimatedDoorBlockEntity.
     *
     * @param pos The position of the block in the world.
     * @param state The block state of the block.
     */
    public AnimatedDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOOR_ANIMATION_BE.get(), pos, state);
        // Initialize wasOpen to the initial state of the door
        if (state.hasProperty(DoorBlock.OPEN)) {
            this.wasOpen = state.getValue(DoorBlock.OPEN);
        } else if (state.hasProperty(TrapDoorBlock.OPEN)) {
            this.wasOpen = state.getValue(TrapDoorBlock.OPEN);
        }
    }

    /**
     * The client-side tick method for this Block Entity.
     * It updates the hinge animation and synchronizes with its other half if it's a double door.
     */
    public void clientTick() {
        if (level == null) return;

        BlockState state = this.getBlockState();
        boolean isOpen = state.hasProperty(DoorBlock.OPEN) ? state.getValue(DoorBlock.OPEN) :
                         state.hasProperty(TrapDoorBlock.OPEN) && state.getValue(TrapDoorBlock.OPEN);

        // If the door's state has just changed, find the other half and sync it.
        if (isOpen != this.wasOpen) {
            this.wasOpen = isOpen;
            syncPartner(level, state, this.getBlockPos(), isOpen);
        }

        this.hinge.tick(isOpen);
    }

    /**
     * Forces this block entity to start animating towards a new state.
     * This is called by the other half of a double door to ensure perfect synchronization.
     * @param isOpen The target state to animate towards.
     */
    private void forceAnimation(boolean isOpen) {
        if (this.wasOpen != isOpen) {
            this.wasOpen = isOpen;
            this.hinge.tick(isOpen);
        }
    }

    /**
     * Finds and synchronizes the other half of a double door.
     */
    private static void syncPartner(Level level, BlockState state, BlockPos pos, boolean isOpen) {
        if (!(state.getBlock() instanceof DoorBlock)) return;

        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);

        BlockPos partnerPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState partnerState = level.getBlockState(partnerPos);

        // Check if the partner block is the other half of this double door
        if (partnerState.is(state.getBlock()) && partnerState.getValue(DoorBlock.HINGE) != hinge) {
            if (level.getBlockEntity(partnerPos) instanceof AnimatedDoorBlockEntity partnerBe) {
                partnerBe.forceAnimation(isOpen);
            }
        }
    }

    /**
     * Gets the hinge component that manages the door's animation state.
     *
     * @return The {@link DoorHingeComponent} for this block entity.
     */
    public DoorHingeComponent getHinge() {
        return hinge;
    }
}
