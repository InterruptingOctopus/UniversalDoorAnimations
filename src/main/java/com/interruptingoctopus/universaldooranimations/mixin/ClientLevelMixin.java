package com.interruptingoctopus.universaldooranimations.mixin;

import com.interruptingoctopus.universaldooranimations.client.renderer.IDoorAnimationRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    /**
     * Injects before and after the addDestroyBlockEffect method in ClientLevel.
     * This is where the game creates block-breaking particles.
     * We set our ThreadLocal flag to true for the duration of this method,
     * which signals our BlockModelShaperMixin to provide the REAL model
     * instead of the AIR model, allowing correct particles to be generated.
     */
    @Inject(method = "addDestroyBlockEffect", at = @At("HEAD"))
    private void beforeAddDestroyEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock) {
            IDoorAnimationRenderer.isRendering.set(true);
        }
    }

    @Inject(method = "addDestroyBlockEffect", at = @At("TAIL"))
    private void afterAddDestroyEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock) {
            IDoorAnimationRenderer.isRendering.set(false);
        }
    }
}
