package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

import static pepse.world.avatar.AvatarConstants.*;

/**
 * Avatar is jumping.
 */
public class JumpState implements AvatarState {
    private final AnimationRenderable animation;

    /**
     * Constructor
     * @param animation Jump animation
     */
    public JumpState(AnimationRenderable animation) {
        this.animation = animation;
    }

    /**
     * Updates the jump state each frame.
     *
     * @param avatar The avatar instance
     * @param inputListener User input handler
     * @param deltaTime Time since last update
     */
    @Override
    public void update(Avatar avatar, UserInputListener inputListener, float deltaTime) {
        float xVel = airMovement(avatar, inputListener);
        avatar.setHorizontalVelocity(xVel);

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (avatar.canDoubleJump() && avatar.hasEnoughEnergy(DOUBLE_JUMP_ENERGY_COST)) {
                avatar.performDoubleJump();
            }
        }

        if (avatar.isOnGround()) {
            handleLanding(avatar, xVel);
        }
    }

    /**
     * Gets the animation for this state.
     * @return The jump animation renderable
     */
    @Override
    public AnimationRenderable getAnimation() {
        return animation;
    }

    // Calculates horizontal velocity based on input.
    private float airMovement(Avatar avatar, UserInputListener inputListener) {
        boolean leftPressed = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);

        if (leftPressed && !rightPressed) {
            avatar.setFlipped(true);
            return -VELOCITY_X;
        } else if (rightPressed && !leftPressed) {
            avatar.setFlipped(false);
            return VELOCITY_X;
        }
        return 0;
    }

    // Handles landing.
    private void handleLanding(Avatar avatar, float xVel) {
        if (xVel != 0) {
            avatar.transitionToRun();
        } else {
            avatar.transitionToIdle();
        }
    }
}