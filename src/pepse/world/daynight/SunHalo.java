package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Represents a glowing halo around the sun
 */
public class SunHalo {
    private static final String HALO_TAG = "halo";
    private static final float HALO_SIZE_MULTIPLIER = 1.5f;
    private static final int HALO_ALPHA = 20;

    /**
     * Creates a halo around the sun
     * @param sun The sun GameObject to follow
     * @return GameObject representing the halo
     */
    public static GameObject create(GameObject sun) {
        Vector2 haloSize = sun.getDimensions().mult(HALO_SIZE_MULTIPLIER);
        GameObject sunHalo = new GameObject(
                Vector2.ZERO,
                haloSize,
                new OvalRenderable(new Color(255, 255, 0, HALO_ALPHA))
        );
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(HALO_TAG);

        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}