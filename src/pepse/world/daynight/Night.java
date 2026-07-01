package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Represents the night darkness overlay
 */
public class Night {
    private static final String NIGHT_TAG = "night";
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float INITIAL_OPACITY = 0f;

    /**
     * Creates a night overlay GameObject
     * @param windowDimensions Size of the window
     * @param cycleLength Length of full day-night cycle in seconds
     * @return GameObject representing night darkness
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(Color.BLACK)
        );
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);

        night.renderer().setOpaqueness(INITIAL_OPACITY);
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                INITIAL_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
        return night;
    }
}