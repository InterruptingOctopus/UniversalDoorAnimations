package com.interruptingoctopus.universaldooranimations.common.block.entity;

import com.interruptingoctopus.universaldooranimations.api.animation.DoorHingeComponent;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

/**
 * A BlockEntity that provides animation capabilities for doors and trapdoors.
 * It uses a {@link DoorHingeComponent} to manage the animation state.
 */
public class AnimatedDoorBlockEntity extends BlockEntity {
    public static final ModelProperty<Boolean> ANIMATING_PROPERTY = new ModelProperty<>();
    private final DoorHingeComponent hinge = new DoorHingeComponent(90f, 0.15f);
    private boolean wasAnimating = false;

    public AnimatedDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOOR_ANIMATION_BE.get(), pos, state);
    }

    /**
     * The server-side tick method for this BlockEntity.
     */
    public static void tick(BlockState state, AnimatedDoorBlockEntity be) {
        boolean isOpen = false;
        if (state.hasProperty(DoorBlock.OPEN)) {
            isOpen = state.getValue(DoorBlock.OPEN);
        } else if (state.hasProperty(TrapDoorBlock.OPEN)) {
            isOpen = state.getValue(TrapDoorBlock.OPEN);
        }

        boolean isCurrentlyAnimating = be.hinge.isAnimating();
        be.hinge.tick(isOpen);

        // If the animation has just stopped, trigger a chunk re-render.
        if (isCurrentlyAnimating && !be.hinge.isAnimating()) {
            if (be.level != null) {
                be.level.sendBlockUpdated(be.worldPosition, be.getBlockState(), be.getBlockState(), 3);
            }
        }

        be.requestModelDataUpdate();
    }

    /**
     * Gets the hinge component associated with this block entity.
     */
    public DoorHingeComponent getHinge() {
        return hinge;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ANIMATING_PROPERTY, hinge.isAnimating()).build();
    }
}
