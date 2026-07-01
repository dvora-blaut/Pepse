package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

import static pepse.world.avatar.AvatarConstants.*;

/**
 * Avatar is moving left or right on the ground.
 */
public class RunState implements AvatarState {
    private final AnimationRenderable animation;

    /**
     * Constructs a RunState with the given animation.
     * @param animation The animation to display when running
     */
    public RunState(AnimationRenderable animation) {
        this.animation = animation;
    }

    /**
     * Updates the run state behavior each frame.
     *
     * @param avatar The avatar instance
     * @param inputListener User input handler
     * @param deltaTime Time since last update
     */
    @Override
    public void update(Avatar avatar, UserInputListener inputListener, float deltaTime) {
        float xVel = handleRunning(avatar, inputListener);
        avatar.setHorizontalVelocity(xVel);

        if (xVel == 0) {
            avatar.transitionToIdle();
            return;
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
     * @return The run animation renderable
     */
    @Override
    public AnimationRenderable getAnimation() {
        return animation;
    }

    // Calculates horizontal velocity based on input.
    private float handleRunning(Avatar avatar, UserInputListener inputListener) {
        boolean leftPressed = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);

        if (leftPressed && !rightPressed) {
            if (avatar.hasEnoughEnergy(RUN_ENERGY_COST)) {
                avatar.setFlipped(true);
                avatar.consumeEnergy(RUN_ENERGY_COST);
                return -VELOCITY_X;
            }
        } else if (rightPressed && !leftPressed) {
            if (avatar.hasEnoughEnergy(RUN_ENERGY_COST)) {
                avatar.setFlipped(false);
                avatar.consumeEnergy(RUN_ENERGY_COST);
                return VELOCITY_X;
            }
        }
        return 0;
    }
}