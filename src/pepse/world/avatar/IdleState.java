package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

import static pepse.world.avatar.AvatarConstants.*;

/**
 * Avatar is standing on the ground.
 */
public class IdleState implements AvatarState {
    private final AnimationRenderable animation;

    /**
     * Constructor
     * @param animation Idle animation
     */
    public IdleState(AnimationRenderable animation) {
        this.animation = animation;
    }

    /**
     * Updates the idle state behavior each frame.
     *
     * @param avatar The avatar instance
     * @param inputListener User input handler
     * @param deltaTime Time since last update
     */
    @Override
    public void update(Avatar avatar, UserInputListener inputListener, float deltaTime) {
        avatar.addEnergy(ENERGY_GAIN);

        if (isMoving(inputListener)) {
            if (avatar.hasEnoughEnergy(RUN_ENERGY_COST)) {
                avatar.transitionToRun();
                return;
            }
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (avatar.hasEnoughEnergy(JUMP_ENERGY_COST)) {
                avatar.performJump();
                avatar.transitionToJump();
            }
        }
    }

    /**
     * Gets the animation for this state.
     * @return The idle animation renderable
     */
    @Override
    public AnimationRenderable getAnimation() {
        return animation;
    }

    // Checks if movement is requested.
    private boolean isMoving(UserInputListener inputListener) {
        boolean leftPressed = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        return (leftPressed && !rightPressed) || (rightPressed && !leftPressed);
    }
}