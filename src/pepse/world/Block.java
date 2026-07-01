package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single block in the world
 */
public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * Constructor for Block
     * @param topLeftCorner Position of top-left corner
     * @param renderable Visual representation
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        // Prevent other objects from passing through this block
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        // Make block immovable
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
