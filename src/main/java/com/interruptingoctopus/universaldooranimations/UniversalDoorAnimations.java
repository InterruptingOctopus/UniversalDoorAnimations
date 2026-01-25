package com.interruptingoctopus.universaldooranimations;

import com.interruptingoctopus.universaldooranimations.init.ModBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * The main mod class for Universal Door Animations.
 * This class is the entry point for the mod and is responsible for
 * registering deferred objects to the appropriate event buses.
 */
@Mod(UniversalDoorAnimations.MODID)
public class UniversalDoorAnimations {
    /**
     * The unique identifier for this mod.
     */
    public static final String MODID = "universaldooranimations";

    /**
     * Constructs the main mod class and registers mod components.
     * @param modEventBus The event bus for mod-specific events.
     */
    public UniversalDoorAnimations(IEventBus modEventBus) {
        ModBlockEntities.register(modEventBus);
    }
}
