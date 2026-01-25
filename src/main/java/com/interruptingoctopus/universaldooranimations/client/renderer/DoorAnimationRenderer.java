package com.interruptingoctopus.universaldooranimations.client.renderer;

import com.interruptingoctopus.universaldooranimations.common.block.entity.AnimatedDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
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
import org.joml.Vector3f;

/**
 * A Block Entity Renderer for {@link AnimatedDoorBlockEntity}.
 * This class is responsible for rendering the door model with a rotation
 * based on the animation state provided by the block entity.
 */
public class DoorAnimationRenderer implements BlockEntityRenderer<AnimatedDoorBlockEntity, DoorAnimationRenderer.DoorRenderState> {

    private final BlockRenderDispatcher blockRenderer;

    /**
     * Constructs a new DoorAnimationRenderer.
     * @param context The context provided by the BlockEntityRendererProvider.
     */
    public DoorAnimationRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.blockRenderDispatcher();
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
        BlockState doorState = renderState.blockState;
        if (doorState == null) {
            return;
        }

        poseStack.pushPose();

        if (doorState.getBlock() instanceof DoorBlock) {
            animateDoor(poseStack, doorState, renderState.angle);
        } else if (doorState.getBlock() instanceof TrapDoorBlock) {
            animateTrapDoor(poseStack, doorState, renderState.angle);
        }

        BlockState renderModel = doorState;
        if (renderModel.hasProperty(DoorBlock.OPEN)) {
            renderModel = renderModel.setValue(DoorBlock.OPEN, false);
        } else if (renderModel.hasProperty(TrapDoorBlock.OPEN)) {
            renderModel = renderModel.setValue(TrapDoorBlock.OPEN, false);
        }
        
        BlockStateModel model = this.blockRenderer.getBlockModel(renderModel);
        RenderType renderType = RenderTypes.cutoutMovingBlock();

        nodeCollector.submitBlockModel(
                poseStack,
                renderType,
                model,
                1.0f, 1.0f, 1.0f,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }

    /**
     * Applies the appropriate transformations to the PoseStack for a standard door.
     * @param poseStack The PoseStack to transform.
     * @param state The block state of the door.
     * @param angle The current animation angle.
     */
    private void animateDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);

        // Define offsets in pixels. Positive values are East(X), Up(Y), South(Z).
        float xPixelOffset = -1.5f;
        float yPixelOffset = 0.0f;
        float zPixelOffset = 1.5f;

        // Convert to block units
        float xOffset = xPixelOffset / 16.0f;
        float yOffset = yPixelOffset / 16.0f;
        float zOffset = zPixelOffset / 16.0f;

        float rotationAngle = hinge == DoorHingeSide.RIGHT ? -angle : angle;
        float pivotX = (hinge == DoorHingeSide.RIGHT ? 0.0f : 1.0f) + xOffset;
        float pivotY = 0.0f + yOffset;
        float pivotZ = 0.0f + zOffset;

        // First, orient the door to face the correct direction
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5, -0.5, -0.5);

        // Then, translate to the pivot, rotate, and translate back
        poseStack.translate(pivotX, pivotY, pivotZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        poseStack.translate(-pivotX, -pivotY, -pivotZ);
    }

    /**
     * Applies the appropriate transformations to the PoseStack for a trapdoor.
     * @param poseStack The PoseStack to transform.
     * @param state The block state of the trapdoor.
     * @param angle The current animation angle.
     */
    private void animateTrapDoor(PoseStack poseStack, BlockState state, float angle) {
        Direction facing = state.getValue(TrapDoorBlock.FACING);
        Half half = state.getValue(TrapDoorBlock.HALF);

        // Define offsets in pixels. Positive values are East(X), Up(Y), South(Z).
        float xPixelOffset = 0.0f;
        float yPixelOffset = -1.5f;
        float zPixelOffset = -1.5f;

        // Convert to block units
        float xOffset = xPixelOffset / 16.0f;
        float yOffset = yPixelOffset / 16.0f;
        float zOffset = zPixelOffset / 16.0f;

        if (half == Half.TOP) {
            angle = -angle;
        }

        float pivotY = ((half == Half.BOTTOM) ? 0.0f : 1.0f) + yOffset;
        
        Vector3f pivot = new Vector3f(0.5f, pivotY, 0.5f);
        Axis axis = Axis.XP;
        float rotationAngle = angle;

        switch (facing) {
            case NORTH: // Hinge is on the SOUTH edge
                pivot.z = 1.0f + zOffset;
                break;
            case SOUTH: // Hinge is on the NORTH edge
                pivot.z = 0.0f + zOffset;
                rotationAngle = -angle;
                break;
            case WEST: // Hinge is on the EAST edge
                pivot.x = 1.0f + xOffset;
                axis = Axis.ZP;
                break;
            case EAST: // Hinge is on the WEST edge
                pivot.x = 0.0f + xOffset;
                axis = Axis.ZP;
                rotationAngle = -angle;
                break;
        }

        poseStack.translate(pivot.x, pivot.y, pivot.z);
        poseStack.mulPose(axis.rotationDegrees(rotationAngle));
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
    }

    /**
     * A state object holding data needed for rendering.
     */
    public static class DoorRenderState extends BlockEntityRenderState {
        /** The level of the block entity. */
        @Nullable public Level level;
        /** The position of the block entity. */
        @Nullable public BlockPos pos;
        /** The state of the block entity. */
        @Nullable public BlockState blockState;
        /** The current animation angle. */
        public float angle;
    }
}
