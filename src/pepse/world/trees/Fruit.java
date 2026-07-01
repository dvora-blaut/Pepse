package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;

import java.awt.Color;
import java.util.function.Consumer;

/**
 * a fruit that can be collected by the avatar.
 */
public class Fruit extends GameObject {
    private static final Color FRUIT_COLOR = new Color(200, 30, 30);
    private static final int ENERGY_BONUS = 10;
    private static final float REAPPEAR_TIME = 30f;
    private static final String FRUIT_TAG = "fruit";
    private static final String AVATAR_TAG = "avatar";

    private boolean isEaten;
    private final Consumer<Float> onEatenCallback;

    /**
     * Constructor for Fruit.
     */
    public Fruit(Vector2 topLeftCorner,
                 Vector2 dimensions,
                 Consumer<Float> onEatenCallback) {
        super(topLeftCorner, dimensions,
                new OvalRenderable(ColorSupplier.approximateColor(FRUIT_COLOR)));

        this.isEaten = false;
        this.onEatenCallback = onEatenCallback;
        this.setTag(FRUIT_TAG);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        String otherTag = other.getTag();
        if (otherTag.equals(AVATAR_TAG) && !isEaten) {
            eatFruit();
        }
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        String otherTag = other.getTag();
        return otherTag.equals(AVATAR_TAG) && !isEaten;
    }

    private void eatFruit() {
        isEaten = true;
        renderer().setOpaqueness(0f);

        if (onEatenCallback != null) {
            onEatenCallback.accept((float)ENERGY_BONUS);
        }
        new ScheduledTask(this, REAPPEAR_TIME, false, this::reappear);
    }

    private void reappear() {
        isEaten = false;
        renderer().setOpaqueness(1f);
    }
}