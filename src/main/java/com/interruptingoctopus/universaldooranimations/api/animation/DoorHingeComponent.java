package com.interruptingoctopus.universaldooranimations.api.animation;

import net.minecraft.util.Mth;

/**
 * A component that manages the state of a single-axis rotation animation, such as a door hinge.
 * It smoothly interpolates between a closed (0 degrees) and open state.
 */
public class DoorHingeComponent {
    private float currentAngle;
    private float prevAngle;
    private final float openAngle;
    private final float speed;

    /**
     * Constructs a new DoorHingeComponent.
     *
     * @param openAngle The angle, in degrees, representing the fully open state.
     * @param speed The speed of the animation, represented as a value between 0.0 and 1.0.
     *              This is the interpolation factor used each tick.
     */
    public DoorHingeComponent(float openAngle, float speed) {
        this.openAngle = openAngle;
        this.speed = speed;
    }

    /**
     * Ticks the animation logic, moving the current angle closer to the target state.
     *
     * @param isOpen True if the door should be moving towards its open state, false for closed.
     */
    public void tick(boolean isOpen) {
        this.prevAngle = this.currentAngle;
        float target = isOpen ? this.openAngle : 0.0F;
        this.currentAngle = Mth.lerp(this.speed, this.currentAngle, target);
    }

    /**
     * Gets the interpolated angle for smooth rendering between ticks.
     *
     * @param partialTick The fraction of the current tick that has passed.
     * @return The interpolated angle for rendering.
     */
    public float getInterpolatedAngle(float partialTick) {
        return Mth.lerp(partialTick, this.prevAngle, this.currentAngle);
    }
}
