package com.interruptingoctopus.universaldooranimations.init;

import com.interruptingoctopus.universaldooranimations.common.block.entity.AnimatedDoorBlockEntity;
import com.interruptingoctopus.universaldooranimations.UniversalDoorAnimations;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A registry for all Block Entity Types in the mod.
 */
public class ModBlockEntities {
    /**
     * The DeferredRegister for Block Entity Types.
     */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, UniversalDoorAnimations.MODID);

    /**
     * The Block Entity Type for the animated door.
     */
    public static final Supplier<BlockEntityType<AnimatedDoorBlockEntity>> DOOR_ANIMATION_BE =
            BLOCK_ENTITIES.register("door_animation_be", () ->
                    new BlockEntityType<>(AnimatedDoorBlockEntity::new, Set.of()));

    /**
     * Registers the Block Entity Types to the mod event bus.
     * @param eventBus The mod event bus.
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
