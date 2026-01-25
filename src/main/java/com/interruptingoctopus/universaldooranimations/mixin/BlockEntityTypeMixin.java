package com.interruptingoctopus.universaldooranimations.mixin;

import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == ModBlockEntities.DOOR_ANIMATION_BE.get()) {
            if (state.getBlock() instanceof DoorBlock || 
                state.getBlock() instanceof TrapDoorBlock) {
                cir.setReturnValue(true);
            }
        }
    }
}
