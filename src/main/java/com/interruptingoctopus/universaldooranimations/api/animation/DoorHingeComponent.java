package com.interruptingoctopus.universaldooranimations.api.animation;

import net.minecraft.util.Mth;

/**
 * A component that manages the state of a hinge-based animation.
 * It tracks the current and previous angle and smoothly interpolates between them.
 */
public class DoorHingeComponent {
    private float currentAngle;
    private float prevAngle;
    private final float openAngle;
    private final float speed;

    public DoorHingeComponent(float openAngle, float speed) {
        this.openAngle = openAngle;
        this.speed = speed;
    }

    /**
     * Ticks the component, updating the angle towards the target.
     * @param isOpen True if the door should be open, false otherwise.
     */
    public void tick(boolean isOpen) {
        this.prevAngle = this.currentAngle;
        float target = isOpen ? this.openAngle : 0.0F;
        this.currentAngle = Mth.lerp(this.speed, this.currentAngle, target);
    }

    /**
     * Gets the smoothed angle for rendering.
     * @param partialTick The partial tick time.
     * @return The interpolated angle for rendering.
     */
    public float getInterpolatedAngle(float partialTick) {
        return Mth.lerp(partialTick, this.prevAngle, this.currentAngle);
    }
}
