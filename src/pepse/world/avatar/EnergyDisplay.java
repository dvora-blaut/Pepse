package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.function.Supplier;

/**
 * Displays the avatar's energy as a numeric percentage on screen.
 */
public class EnergyDisplay {
    private static final String TAG = "energyDisplay";
    private static final Vector2 SIZE = new Vector2(100, 50);
    private static final Vector2 POSITION = new Vector2(20, 20);
    private static final Color TEXT_COLOR = Color.WHITE;

    private final GameObject displayObject;
    private float lastValue = -1;

    /**
     * Creates an energy display .
     *
     * @param gameObjects Collection to add the display to
     * @param layer Layer for the display
     * @param energySupplier Supplier function that returns current energy percentage
     */
    public EnergyDisplay(GameObjectCollection gameObjects,
                         int layer,
                         Supplier<Float> energySupplier) {

        this.displayObject = createDisplayObject();
        update(energySupplier);
        gameObjects.addGameObject(displayObject, layer);

        showEnergy(energySupplier.get());
    }

    // Creates the text display object
    private GameObject createDisplayObject() {
        TextRenderable text = new TextRenderable("");
        text.setColor(TEXT_COLOR);

        GameObject obj = new GameObject(POSITION, SIZE, text);
        obj.setTag(TAG);
        obj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        return obj;
    }

    // Sets up automatic energy updates each frame
    private void update(Supplier<Float> energySupplier) {
        displayObject.addComponent(deltaTime -> {
            float current = energySupplier.get();

            if (current != lastValue) {
                showEnergy(current);
                lastValue = current;
            }
        });
    }

    // Updates the displayed energy text
    private void showEnergy(float energyPercentage) {
        int percent = Math.round(energyPercentage * 100);
        ((TextRenderable) displayObject.renderer().getRenderable())
                .setString(String.format("Energy: %d%%", percent));
    }
}