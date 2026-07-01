package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a leaf on a tree.
 */
public class Leaf extends GameObject {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final float WIND_ANGLE_RANGE = 10f;
    private static final float WIND_SIZE_CHANGE = 0.1f;
    private static final float WIND_CYCLE_TIME = 2f;
    private static final float MAX_DELAY = 1f;
    private static final String LEAF_TAG = "leaf";

    private final Vector2 originalDimensions;
    private final Random random;

    /**
     * Constructor for Leaf.
     *
     * @param topLeftCorner Position of the leaf
     * @param dimensions Size of the leaf
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions,
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));

        this.originalDimensions = dimensions;
        this.random = new Random();
        this.setTag(LEAF_TAG);

        startAnimation();
    }

    // Schedules the wind animation to start after a random delay.
    private void startAnimation() {
        float delay = random.nextFloat() * MAX_DELAY;

        new ScheduledTask(
                this,
                delay,
                false,
                () -> {
                    animateAngle();
                    animateSize();
                }
        );
    }

    // Returns a randomized cycle time for transitions.
    private float randomTime() {
        return WIND_CYCLE_TIME * (0.8f + random.nextFloat() * 0.4f);
    }

    // Creates a transition for leaf angle.
    private void animateAngle() {
        float angle = WIND_ANGLE_RANGE * (0.5f + random.nextFloat() * 0.5f);

        new Transition<>(
                this,
                a -> this.renderer().setRenderableAngle(a),
                -angle,
                angle,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                randomTime(),
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    // Creates a transition for leaf size.
    private void animateSize() {
        float multiplier = 1 - WIND_SIZE_CHANGE + (random.nextFloat() * WIND_SIZE_CHANGE);
        Vector2 minSize = originalDimensions.mult(multiplier);
        Vector2 maxSize = originalDimensions.mult(1 + WIND_SIZE_CHANGE - multiplier);

        new Transition<>(
                this,
                this::setDimensions,
                minSize,
                maxSize,
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                randomTime(),
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return false;
    }
}