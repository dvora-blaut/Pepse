package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating and managing the terrain
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final String GROUND_TAG = "ground";
    private static final float GROUND_HEIGHT_FACTOR = 2f / 3f;
    private static final int NOISE_FACTOR = 7;

    private final NoiseGenerator noiseGenerator;
    private final float groundHeightAtX0;

    /**
     * Constructor for Terrain
     * @param windowDimensions Size of the game window
     * @param seed Seed for terrain generation
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT_FACTOR;
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
    }

    /**
     * Returns the ground height at a given x coordinate
     * @param x The x coordinate
     * @return The ground height at x
     */
    public float groundHeightAt(float x) {
        double noise = noiseGenerator.noise(x, Block.SIZE * NOISE_FACTOR);
        return groundHeightAtX0 + (float)noise;
    }

    /**
     * Creates terrain blocks in a given x range
     * @param minX Minimum x coordinate
     * @param maxX Maximum x coordinate
     * @return List of terrain blocks
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        minX = (int)(Math.floor((float)minX / Block.SIZE) * Block.SIZE);
        maxX = (int)(Math.floor((float)maxX / Block.SIZE) * Block.SIZE);

        for (int x = minX; x <= maxX; x += Block.SIZE) {
            float groundHeight = groundHeightAt(x);
            int groundY = (int)(Math.floor(groundHeight / Block.SIZE) * Block.SIZE);

            for (int depth = 0; depth < TERRAIN_DEPTH; depth++) {
                int y = groundY + (depth * Block.SIZE);

                Block block = new Block(
                        new Vector2(x, y),
                        new RectangleRenderable(
                                ColorSupplier.approximateColor(BASE_GROUND_COLOR)
                        )
                );
                block.setTag(GROUND_TAG);
                blocks.add(block);
            }
        }

        return blocks;
    }
}