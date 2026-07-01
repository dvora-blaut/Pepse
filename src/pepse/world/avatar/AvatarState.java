package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.UserInputListener;

/**
 * Interface for Avatar states.
 */
public interface AvatarState {
    /**
     * Updates the state's behavior each frame
     * @param avatar The Avatar instance
     * @param inputListener User input handler
     * @param deltaTime Time since last update
     */
    void update(Avatar avatar, UserInputListener inputListener, float deltaTime);

    /**
     * Gets the animation for this state
     * @return Animation renderable for the current state
     */
    AnimationRenderable getAnimation();

}