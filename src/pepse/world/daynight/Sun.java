package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Represents the sun moving in an ellipse across the sky
 */
public class Sun {
    private static final String SUN_TAG = "sun";
    private static final float SUN_SIZE = 80f;
    private static final float GROUND_HEIGHT_FACTOR = 2f / 3f;
    private static final float SUN_VERTICAL_OFFSET = 0.4f;

    /**
     * Creates a sun GameObject that moves in a circular path
     * @param windowDimensions Size of the window
     * @param cycleLength Length of full day-night cycle in seconds
     * @return GameObject representing the sun
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(
                Vector2.ZERO,
                Vector2.ONES.mult(SUN_SIZE),
                new OvalRenderable(Color.YELLOW)
        );
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        Vector2 cycleCenter = new Vector2(
                windowDimensions.x() / 2,
                windowDimensions.y() * GROUND_HEIGHT_FACTOR
        );

        Vector2 initialSunCenter = cycleCenter.subtract(
                new Vector2(0, windowDimensions.y() * SUN_VERTICAL_OFFSET)
        );

        new Transition<>(
                sun,
                (Float angle) -> {
                    Vector2 sunPosition = initialSunCenter.subtract(cycleCenter)
                            .rotated(angle)
                            .add(cycleCenter);
                    sun.setCenter(sunPosition);
                },
                0f,
                360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return sun;
    }
}