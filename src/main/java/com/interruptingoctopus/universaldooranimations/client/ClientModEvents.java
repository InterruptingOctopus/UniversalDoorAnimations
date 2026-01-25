package com.interruptingoctopus.universaldooranimations.client;

import com.interruptingoctopus.universaldooranimations.client.renderer.DoorAnimationRenderer;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import com.interruptingoctopus.universaldooranimations.UniversalDoorAnimations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Handles client-side mod events, such as renderer registration.
 * This class is automatically subscribed to the mod event bus.
 */
@EventBusSubscriber(modid = UniversalDoorAnimations.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    /**
     * Registers the Block Entity Renderers.
     * @param event The registration event.
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DOOR_ANIMATION_BE.get(), DoorAnimationRenderer::new);
    }
}
