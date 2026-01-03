package com.interruptingoctopus.universaldooranimations.client;

import com.interruptingoctopus.universaldooranimations.client.renderer.DoorAnimationRenderer;
import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import com.interruptingoctopus.universaldooranimations.UniversalDoorAnimations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = UniversalDoorAnimations.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DOOR_ANIMATION_BE.get(), DoorAnimationRenderer::new);
    }
}
