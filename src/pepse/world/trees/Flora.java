package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Creates and manages trees in the world.
 * Each tree has a trunk, leaves (canopy), and fruits.
 */
public class Flora {
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final int MIN_TRUNK_HEIGHT = 100;
    private static final int MAX_TRUNK_HEIGHT = 200;
    private static final float TREE_PROBABILITY = 0.1f;
    private static final int LEAF_SIZE = Block.SIZE;
    private static final int CANOPY_RADIUS = 3;
    private static final float LEAF_PROBABILITY = 0.7f;
    private static final String GROUND_TAG = "ground";

    private final GameObjectCollection gameObjects;
    private final Function<Float, Float> groundHeightAt;
    private final Consumer<Float> onFruitEatenCallback;
    private final int trunkLayer;
    private final int leafLayer;
    private final int fruitLayer;
    private final int seed;


    /**
     * Creates tree manager that spawns trees in the world.
     * Uses the given seed for reproducible tree generation.
     */
    public Flora(GameObjectCollection gameObjects,
                 Function<Float, Float> groundHeightAt,
                 int trunkLayer,
                 int leafLayer,
                 int fruitLayer,
                 int seed,
                 Consumer<Float> onFruitEatenCallback) {
        this.gameObjects = gameObjects;
        this.groundHeightAt = groundHeightAt;
        this.trunkLayer = trunkLayer;
        this.leafLayer = leafLayer;
        this.fruitLayer = fruitLayer;
        this.seed = seed;
        this.onFruitEatenCallback = onFruitEatenCallback;
    }

    /**
     * Generates trees between minX and maxX.
     * Returns all tree parts (trunks, leaves, fruits) that were created.
     */
    public List<GameObject> createInRange(int minX, int maxX) {
        List<GameObject> allObjects = new ArrayList<>();

        int startX = (minX / Block.SIZE) * Block.SIZE;
        int endX = (maxX / Block.SIZE) * Block.SIZE;

        for (int x = startX; x <= endX; x += Block.SIZE) {
            Random columnRandom = new Random(Objects.hash(x, seed));

            if (columnRandom.nextFloat() < TREE_PROBABILITY) {
                List<GameObject> treeObjects = createTreeAt(x);
                allObjects.addAll(treeObjects);
            }
        }

        return allObjects;
    }

    // Creates a complete tree at the given position
    private List<GameObject> createTreeAt(int x) {
        List<GameObject> treeObjects = new ArrayList<>();
        Random treeRandom = new Random(Objects.hash(x, seed));

        float groundY = calculateGroundHeight(x);
        int trunkHeight = calculateTrunkHeight(treeRandom);

        GameObject trunk = buildTrunk(x, groundY, trunkHeight);
        gameObjects.addGameObject(trunk, trunkLayer);
        treeObjects.add(trunk);

        float canopyY = groundY - trunkHeight;
        treeObjects.addAll(buildCanopy(x, canopyY, treeRandom));
        treeObjects.addAll(buildFruits(x, canopyY, treeRandom));

        return treeObjects;
    }

    // Gets ground height at x position and rounds to block grid
    private float calculateGroundHeight(int x) {
        float groundY = groundHeightAt.apply((float) x);
        return Math.round(groundY / Block.SIZE) * Block.SIZE;
    }

    // Generates random trunk height between min and max
    private int calculateTrunkHeight(Random random) {
        int height = MIN_TRUNK_HEIGHT + random.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT);
        return (height / Block.SIZE) * Block.SIZE;
    }

    // Creates the tree trunk GameObject
    private GameObject buildTrunk(int x, float groundY, int height) {
        Vector2 position = new Vector2(x, groundY - height);
        Vector2 dimensions = new Vector2(Block.SIZE, height);

        RectangleRenderable renderable =
                new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));

        GameObject trunk = new GameObject(position, dimensions, renderable);
        trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        trunk.setTag(GROUND_TAG);

        return trunk;
    }

    // Creates leaves around the tree top
    private List<GameObject> buildCanopy(int centerX, float topY, Random treeRandom) {
        List<GameObject> leaves = new ArrayList<>();

        for (int dx = -CANOPY_RADIUS; dx <= CANOPY_RADIUS; dx++) {
            for (int dy = -CANOPY_RADIUS; dy <= CANOPY_RADIUS; dy++) {
                if (treeRandom.nextFloat() < LEAF_PROBABILITY) {
                    int leafX = centerX + (dx * LEAF_SIZE);
                    int leafY = (int) topY + (dy * LEAF_SIZE);

                    Leaf leaf = new Leaf(
                            new Vector2(leafX, leafY),
                            new Vector2(LEAF_SIZE, LEAF_SIZE)
                    );

                    gameObjects.addGameObject(leaf, leafLayer);
                    leaves.add(leaf);
                }
            }
        }
        return leaves;
    }

    // Adds fruits randomly in the canopy area
    private List<GameObject> buildFruits(int centerX, float topY, Random treeRandom) {
        List<GameObject> fruits = new ArrayList<>();

        int numFruits = 2 + treeRandom.nextInt(4);

        for (int i = 0; i < numFruits; i++) {
            int dx = -CANOPY_RADIUS + treeRandom.nextInt(CANOPY_RADIUS * 2 + 1);
            int dy = -CANOPY_RADIUS + treeRandom.nextInt(CANOPY_RADIUS + 1);

            float fruitX = centerX + (dx * LEAF_SIZE) + (LEAF_SIZE / 4.0f);
            float fruitY = topY + (dy * LEAF_SIZE) + (LEAF_SIZE / 4.0f);

            Fruit fruit = new Fruit(
                    new Vector2(fruitX, fruitY),
                    new Vector2(LEAF_SIZE / 2.0f, LEAF_SIZE / 2.0f),
                    onFruitEatenCallback
            );

            gameObjects.addGameObject(fruit, fruitLayer);
            fruits.add(fruit);
        }

        return fruits;
    }
}