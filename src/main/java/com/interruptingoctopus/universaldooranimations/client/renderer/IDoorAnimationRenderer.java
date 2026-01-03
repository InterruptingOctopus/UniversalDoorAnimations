package com.interruptingoctopus.universaldooranimations.client.renderer;

/**
 * An interface containing a ThreadLocal flag to signal when the door animation renderer is active.
 * This is used by the BlockModelShaperMixin to avoid replacing the model when our own renderer needs it.
 */
public interface IDoorAnimationRenderer {
    /**
     * A ThreadLocal boolean that is true only when the DoorAnimationRenderer is currently getting a model.
     */
    ThreadLocal<Boolean> isRendering = ThreadLocal.withInitial(() -> false);
}
