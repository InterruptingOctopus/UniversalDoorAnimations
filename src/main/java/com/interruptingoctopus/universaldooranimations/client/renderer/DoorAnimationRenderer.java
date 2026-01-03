package com.interruptingoctopus.universaldooranimations.client.renderer;

import com.interruptingoctopus.universaldooranimations.common.block.entity.AnimatedDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DoorAnimationRenderer implements BlockEntityRenderer<AnimatedDoorBlockEntity, DoorAnimationRenderer.DoorRenderState>, IDoorAnimationRenderer {

    public DoorAnimationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public DoorRenderState createRenderState() {
        return new DoorRenderState();
    }

    @Override
    public void extractRenderState(
            AnimatedDoorBlockEntity blockEntity,
            DoorRenderState renderState,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.level = blockEntity.getLevel();
        renderState.pos = blockEntity.getBlockPos();
        renderState.blockState = blockEntity.getBlockState();
        renderState.angle = blockEntity.getHinge().getInterpolatedAngle(partialTick);
    }

    @Override
    public void submit(DoorRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        Level level = renderState.level;
        BlockState state = renderState.blockState;

        if (level == null || state == null) {
            return;
        }

        BlockState visualState = state;
        if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
            visualState = state.setValue(DoorBlock.OPEN, false);
        } else if (state.hasProperty(TrapDoorBlock.OPEN) && state.getValue(TrapDoorBlock.OPEN)) {
            visualState = state.setValue(TrapDoorBlock.OPEN, false);
        }

        poseStack.pushPose();

        if (state.getBlock() instanceof DoorBlock) {
            animateDoor(poseStack, state, renderState.angle);
        } else if (state.getBlock() instanceof TrapDoorBlock) {
            animateTrapDoor(poseStack, state, renderState.angle);
        }

        // Set the flag to true so the mixin knows not to replace the model
        isRendering.set(true);
        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(visualState);
        // Unset the flag immediately after getting the model
        isRendering.set(false);

        RenderType renderType = ItemBlockRenderTypes.getRenderType(visualState);

        nodeCollector.submitBlockModel(
                poseStack,
                renderType,
                model,
                1.0f, 1.0f, 1.0f,
                renderState.lightCoords,
                0,
                0
        );

        poseStack.popPose();
    }

    private void animateDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);

        if (hinge == DoorHingeSide.LEFT) {
            angle = -angle;
        }

        float pivotX = 0;
        float pivotZ = 0;

        switch (facing) {
            case NORTH:
                pivotZ = 1;
                pivotX = (hinge == DoorHingeSide.LEFT) ? 1 : 0;
                break;
            case SOUTH:
                pivotZ = 0;
                pivotX = (hinge == DoorHingeSide.LEFT) ? 0 : 1;
                break;
            case WEST:
                pivotX = 1;
                pivotZ = (hinge == DoorHingeSide.LEFT) ? 0 : 1;
                break;
            case EAST:
                pivotX = 0;
                pivotZ = (hinge == DoorHingeSide.LEFT) ? 1 : 0;
                break;
        }

        poseStack.translate(pivotX, 0, pivotZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(-pivotX, 0, -pivotZ);
    }

    private void animateTrapDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(TrapDoorBlock.FACING);
        Half half = state.getValue(TrapDoorBlock.HALF);

        float pivotX = 0.5f;
        float pivotY;
        float pivotZ = 0.5f;

        switch (facing) {
            case NORTH: pivotZ = 1; break;
            case SOUTH: pivotZ = 0; break;
            case WEST:  pivotX = 1; break;
            case EAST:  pivotX = 0; break;
        }
        
        if (half == Half.BOTTOM) pivotY = 0;
        else pivotY = 1;

        poseStack.translate(pivotX, pivotY, pivotZ);

        if (facing == Direction.NORTH) poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        else if (facing == Direction.SOUTH) poseStack.mulPose(Axis.XP.rotationDegrees(-angle));
        else if (facing == Direction.WEST)  poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));
        else if (facing == Direction.EAST)  poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

        poseStack.translate(-pivotX, -pivotY, -pivotZ);
    }

    public static class DoorRenderState extends BlockEntityRenderState {
        @Nullable public Level level;
        @Nullable public BlockPos pos;
        @Nullable public BlockState blockState;
        public float angle;
    }
}
