package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import pepse.world.trees.Flora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages infinite world
 */
public class InfiniteWorldManager {
    private static final int RENDER_BUFFER = 600;
    private static final int CHUNK_SIZE = Block.SIZE * 50;

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final Flora flora;
    private final int terrainLayer;
    private final Map<Integer, List<GameObject>> chunkObjects;

    private int currentMinX;
    private int currentMaxX;

    /**
     * Constructor for InfiniteWorldManager.
     */
    public InfiniteWorldManager(GameObjectCollection gameObjects,
                                Terrain terrain,
                                Flora flora,
                                int terrainLayer,
                                int initialMinX,
                                int initialMaxX) {
        this.gameObjects = gameObjects;
        this.terrain = terrain;
        this.flora = flora;
        this.terrainLayer = terrainLayer;
        this.currentMinX = initialMinX;
        this.currentMaxX = initialMaxX;
        this.chunkObjects = new HashMap<>();
    }

    /**
     * Updates the world based on avatar position.
     */
    public void updateWorld(float avatarX, float screenWidth) {
        int visibleMinX = calcVisibleMin(avatarX, screenWidth);
        int visibleMaxX = calcVisibleMax(avatarX, screenWidth);

        expandLeft(visibleMinX);
        expandRight(visibleMaxX);
        remove(avatarX, screenWidth);
    }

    // Calculates the minimum visible x coordinate with buffer
    private int calcVisibleMin(float avatarX, float screenWidth) {
        int minX = (int) (avatarX - screenWidth / 2 - RENDER_BUFFER);
        return (minX / Block.SIZE) * Block.SIZE;
    }

    // Calculates the maximum visible x coordinate with buffer
    private int calcVisibleMax(float avatarX, float screenWidth) {
        int maxX = (int) (avatarX + screenWidth / 2 + RENDER_BUFFER);
        return (maxX / Block.SIZE) * Block.SIZE;
    }

    // Expands world to the left if needed
    private void expandLeft(int visibleMinX) {
        if (visibleMinX < currentMinX) {
            createSegment(visibleMinX, currentMinX);
            currentMinX = visibleMinX;
        }
    }

    // Expands world to the right if needed
    private void expandRight(int visibleMaxX) {
        if (visibleMaxX > currentMaxX) {
            createSegment(currentMaxX, visibleMaxX);
            currentMaxX = visibleMaxX;
        }
    }

    // Creates terrain and trees for a segment
    private void createSegment(int minX, int maxX) {
        int startChunk = toChunkIndex(minX);
        int endChunk = toChunkIndex(maxX);

        for (int i = startChunk; i <= endChunk; i++) {
            if (!chunkObjects.containsKey(i)) {
                createChunk(i);
            }
        }
    }

    // Creates a single chunk with terrain and trees
    private void createChunk(int chunkIndex) {
        int chunkMinX = chunkIndex * CHUNK_SIZE;
        int chunkMaxX = chunkMinX + CHUNK_SIZE;

        List<GameObject> objects = new ArrayList<>();

        // Add terrain blocks
        List<Block> blocks = terrain.createInRange(chunkMinX, chunkMaxX);
        for (Block block : blocks) {
            gameObjects.addGameObject(block, terrainLayer);
            objects.add(block);
        }

        objects.addAll(flora.createInRange(chunkMinX, chunkMaxX));

        chunkObjects.put(chunkIndex, objects);
    }

    /**
     * Removes chunks that are more than 2 screen widths away from avatar.
     * This prevents memory buildup while keeping nearby terrain loaded.
     */
    private void remove(float avatarX, float screenWidth) {
        float maxDistance = screenWidth * 2f;
        List<Integer> toRemove = new ArrayList<>();

        for (int i : chunkObjects.keySet()) {
            int chunkCenter = i * CHUNK_SIZE + CHUNK_SIZE / 2;
            if (Math.abs(chunkCenter - avatarX) > maxDistance) {
                toRemove.add(i);
            }
        }

        for (int i : toRemove) {
            removeChunk(i);
        }
    }

    // Removes a single chunk and all its objects
    private void removeChunk(int chunkIndex) {
        List<GameObject> objects = chunkObjects.get(chunkIndex);
        if (objects != null) {
            for (GameObject obj : objects) {
                gameObjects.removeGameObject(obj);
            }
        }
        chunkObjects.remove(chunkIndex);
    }

    /**
     * Converts world x-coordinate to chunk index.
     * Example: x=1600 with CHUNK_SIZE=1500 → chunk 1
     *          x=-100 with CHUNK_SIZE=1500 → chunk -1
     * Using floor() handles negative coordinates correctly.
     */
    private int toChunkIndex(int x) {
        return (int) Math.floor((float) x / CHUNK_SIZE);
    }

}