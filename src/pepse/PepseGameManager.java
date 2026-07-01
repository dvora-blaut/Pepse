package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.avatar.Avatar;
import pepse.world.avatar.EnergyDisplay;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;

import java.util.List;

/**
 * Main game manager for PEPSE simulation.
 */
public class PepseGameManager extends GameManager {
    private static final int SEED = 42;
    private static final float CYCLE_LENGTH = 30f;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int HALO_LAYER = Layer.BACKGROUND + 10;
    private static final float AVATAR_Y_OFFSET = 100f;
    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS;
    private static final int LEAF_LAYER = Layer.BACKGROUND + 5;
    private static final int FRUIT_LAYER = Layer.DEFAULT;

    private Avatar avatar;
    private InfiniteWorldManager worldManager;
    private Vector2 windowDimensions;

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();

        createSkyAndCycle();

        Terrain terrain = new Terrain(windowDimensions, SEED);

        createAvatar(inputListener, imageReader, terrain);

        Flora flora = createFlora(terrain);

        setupWorld(terrain, flora);
        setupCamera();
    }

    // Creates sky and day/night cycle
    private void createSkyAndCycle() {
        gameObjects().addGameObject(Sky.create(windowDimensions), Layer.BACKGROUND);
        gameObjects().addGameObject(Night.create(windowDimensions, CYCLE_LENGTH), Layer.FOREGROUND);

        GameObject sun = Sun.create(windowDimensions, CYCLE_LENGTH);
        gameObjects().addGameObject(sun, SUN_LAYER);
        gameObjects().addGameObject(SunHalo.create(sun), HALO_LAYER);
    }

    // Creates avatar
    private void createAvatar(UserInputListener inputListener,
                              ImageReader imageReader,
                              Terrain terrain) {
        float initialX = windowDimensions.x() / 2;
        float groundY = terrain.groundHeightAt(initialX);

        this.avatar = new Avatar(
                new Vector2(initialX, groundY - AVATAR_Y_OFFSET),
                inputListener,
                imageReader
        );

        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        new EnergyDisplay(
                gameObjects(),
                Layer.UI,
                avatar::getEnergyPercentage
        );
    }

    // Creates flora manager
    private Flora createFlora(Terrain terrain) {
        return new Flora(
                gameObjects(),
                terrain::groundHeightAt,
                TRUNK_LAYER,
                LEAF_LAYER,
                FRUIT_LAYER,
                SEED,
                avatar::addEnergy
        );
    }

    // Sets up infinite world
    private void setupWorld(Terrain terrain, Flora flora) {
        float initialX = windowDimensions.x() / 2;
        int minX = (int) (initialX - windowDimensions.x());
        int maxX = (int) (initialX + windowDimensions.x() * 2);

        this.worldManager = new InfiniteWorldManager(
                gameObjects(),
                terrain,
                flora,
                Layer.STATIC_OBJECTS,
                minX,
                maxX
        );

        createInitialWorld(terrain, flora, minX, maxX);
    }

    // Creates the initial world
    private void createInitialWorld(Terrain terrain, Flora flora, int minX, int maxX) {
        List<Block> blocks = terrain.createInRange(minX, maxX);
        for (Block block : blocks) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }

        flora.createInRange(minX, maxX);
    }

    // Sets up camera to follow avatar
    private void setupCamera() {
        setCamera(new Camera(
                avatar,
                Vector2.ZERO,
                windowDimensions,
                windowDimensions
        ));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (avatar != null && worldManager != null) {
            worldManager.updateWorld(avatar.getCenter().x(), windowDimensions.x());
        }
    }

    /**
     * Main method to run the PEPSE game
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}