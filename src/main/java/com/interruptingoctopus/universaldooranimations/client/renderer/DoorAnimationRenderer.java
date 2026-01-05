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
import net.minecraft.client.renderer.texture.OverlayTexture;
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

        isRendering.set(true);
        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(visualState);
        isRendering.set(false);

        RenderType renderType = ItemBlockRenderTypes.getMovingBlockRenderType(visualState);

        nodeCollector.submitBlockModel(
                poseStack,
                renderType,
                model,
                1.0f, 1.0f, 1.0f,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0 // outlineColor
        );

        poseStack.popPose();
    }

    private void animateDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);

        float rotationAngle = hinge == DoorHingeSide.LEFT ? angle : -angle;

        float pivotX = 0.0f;
        float pivotZ = 0.0f;

        // 1. Calculate the base pivot point at the correct hinge corner
        switch (facing) {
            case NORTH:
                pivotZ = 1.0f;
                pivotX = hinge == DoorHingeSide.RIGHT ? 1.0f : 0.0f;
                break;
            case SOUTH:
                pivotZ = 0.0f;
                pivotX = hinge == DoorHingeSide.RIGHT ? 0.0f : 1.0f;
                break;
            case WEST:
                pivotX = 1.0f;
                pivotZ = hinge == DoorHingeSide.RIGHT ? 0.0f : 1.0f;
                break;
            case EAST:
                pivotX = 0.0f;
                pivotZ = hinge == DoorHingeSide.RIGHT ? 1.0f : 0.0f;
                break;
        }

        // 2. Define and apply the pixel offsets
        float forwardPixelOffset = 1.5f;
        float rightPixelOffset = 1.5f;
        float forwardOffset = forwardPixelOffset / 16.0f;
        float rightOffset = rightPixelOffset / 16.0f;

        Direction horizontalOffsetDir = hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();

        pivotX += facing.getStepX() * forwardOffset;
        pivotZ += facing.getStepZ() * forwardOffset;

        pivotX += horizontalOffsetDir.getStepX() * rightOffset;
        pivotZ += horizontalOffsetDir.getStepZ() * rightOffset;

        // 3. Apply transformation
        poseStack.translate(pivotX, 0, pivotZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        poseStack.translate(-pivotX, 0, -pivotZ);
    }

    private void animateTrapDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(TrapDoorBlock.FACING);
        Half half = state.getValue(TrapDoorBlock.HALF);

        if (half == Half.TOP) {
            angle = -angle;
        }

        float pivotY = (half == Half.BOTTOM) ? 0.0f : 1.0f;

        switch (facing) {
            case NORTH:
                poseStack.translate(0.5, pivotY, 1.0);
                poseStack.mulPose(Axis.XP.rotationDegrees(-angle));
                poseStack.translate(-0.5, -pivotY, -1.0);
                break;
            case SOUTH:
                poseStack.translate(0.5, pivotY, 0.0);
                poseStack.mulPose(Axis.XP.rotationDegrees(angle));
                poseStack.translate(-0.5, -pivotY, 0.0);
                break;
            case WEST:
                poseStack.translate(1.0, pivotY, 0.5);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));
                poseStack.translate(-1.0, -pivotY, -0.5);
                break;
            case EAST:
                poseStack.translate(0.0, pivotY, 0.5);
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
                poseStack.translate(0.0, -pivotY, -0.5);
                break;
        }
    }

    public static class DoorRenderState extends BlockEntityRenderState {
        @Nullable public Level level;
        @Nullable public BlockPos pos;
        @Nullable public BlockState blockState;
        public float angle;
    }
}
