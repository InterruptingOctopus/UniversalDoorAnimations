package com.interruptingoctopus.universaldooranimations.mixin;

import com.interruptingoctopus.universaldooranimations.client.renderer.IDoorAnimationRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    private void getBlockModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        // Check if the block is a door and if our renderer is NOT asking for the model.
        if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock) {
            if (!IDoorAnimationRenderer.isRendering.get()) {
                // If the chunk renderer or particle engine is asking, replace the model with an empty one.
                cir.setReturnValue(((BlockModelShaper)(Object)this).getBlockModel(Blocks.AIR.defaultBlockState()));
            }
            // Otherwise (if our renderer is asking), let the original method run to get the real model.
        }
    }
}
