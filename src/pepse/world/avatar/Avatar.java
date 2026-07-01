package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static pepse.world.avatar.AvatarConstants.*;

/**
 * Represents the player's avatar using State Pattern.
 */
public class Avatar extends GameObject {
    private final UserInputListener inputListener;
    private IdleState idleState;
    private RunState runState;
    private JumpState jumpState;
    private AvatarState currentState;
    private Energy energy;
    private boolean isOnGround;
    private boolean hasDoubleJumped;

    /**
     * Constructs a new Avatar.
     *
     * @param topLeftCorner The initial position of the avatar
     * @param inputListener Listener for user input
     * @param imageReader Reader for loading avatar animations
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, Vector2.ONES.mult(50),
                createAnimation(imageReader, IDLE_ANIMATION_PATH, IDLE_FRAME_COUNT));

        this.energy = new Energy();
        this.isOnGround = false;
        this.hasDoubleJumped = false;

        this.inputListener = inputListener;
        initializeStates(imageReader);

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        setTag(AVATAR_TAG);
    }

    /**
     * Updates the avatar's state and animation each frame.
     *
     * @param deltaTime Time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Cap falling velocity to prevent tunneling through terrain
        float maxFallingVelocity = 500f;
        if (getVelocity().y() > maxFallingVelocity) {
            transform().setVelocityY(maxFallingVelocity);
        }

        currentState.update(this, inputListener, deltaTime);
        renderer().setRenderable(currentState.getAnimation());
    }

    /**
     * Handles collision events when the avatar enters collision.
     *
     * @param other The object collided with
     * @param collision Collision details
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals(GROUND_TAG)) {
            // Stop falling
            if (getVelocity().y() > 0) {
                this.transform().setVelocityY(0);
            }
            isOnGround = true;
            hasDoubleJumped = false;
        }
    }

    /**
     * Handles collision events when the avatar exits collision.
     *
     * @param other The object no longer colliding with
     */
    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);

        if (other.getTag().equals(GROUND_TAG)) {
            isOnGround = false;
        }
    }

    /**
     * The avatar collides with any object the superclass would collide with.
     *
     * @param other The other game object
     * @return true if collision should occur, false otherwise
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other.getTag().equals(GROUND_TAG) || super.shouldCollideWith(other);
    }

    /**
     * Gets the avatar's current energy as a percentage.
     *
     * @return Energy percentage
     */
    public float getEnergyPercentage() {
        return energy.getPercentage();
    }

    /**
     * Adds energy to the avatar - maximum 100.
     *
     * @param amount Amount of energy to add
     */
    public void addEnergy(float amount) {
        energy.gain(amount);
    }


    // Returns whether the avatar is on the ground
    boolean isOnGround() {
        return isOnGround;
    }

    // Returns whether the avatar has enough energy for an action
    boolean hasEnoughEnergy(float cost) {
        return energy.hasEnough(cost);
    }

    // Consumes the specified amount of energy
    void consumeEnergy(float amount) {
        energy.consume(amount);
    }

    // Sets the horizontal velocity
    void setHorizontalVelocity(float velocity) {
        transform().setVelocityX(velocity);
    }

    // Flips the avatar's sprite horizontally
    void setFlipped(boolean flipped) {
        renderer().setIsFlippedHorizontally(flipped);
    }

    // Performs a regular jump
    void performJump() {
        transform().setVelocityY(VELOCITY_Y);
        consumeEnergy(JUMP_ENERGY_COST);
        isOnGround = false;
        hasDoubleJumped = false;
    }

    // Returns whether the avatar can perform a double jump
    boolean canDoubleJump() {
        return !hasDoubleJumped && getVelocity().y() > 0;
    }

    // Performs a double jump
    void performDoubleJump() {
        transform().setVelocityY(VELOCITY_Y);
        consumeEnergy(DOUBLE_JUMP_ENERGY_COST);
        hasDoubleJumped = true;
    }

    // Transitions the avatar to Idle state
    void transitionToIdle() {
        currentState = idleState;
    }

    // Transitions the avatar to Run state
    void transitionToRun() {
        currentState = runState;
    }

    // Transitions the avatar to Jump state
    void transitionToJump() {
        currentState = jumpState;
    }


    //Creates and initializes all avatar states with their animations
    private void initializeStates(ImageReader imageReader) {
        AnimationRenderable idleAnim = createAnimation(imageReader,
                IDLE_ANIMATION_PATH, IDLE_FRAME_COUNT);
        AnimationRenderable runAnim = createAnimation(imageReader,
                RUN_ANIMATION_PATH, RUN_FRAME_COUNT);
        AnimationRenderable jumpAnim = createAnimation(imageReader,
                JUMP_ANIMATION_PATH, JUMP_FRAME_COUNT);

        this.idleState = new IdleState(idleAnim);
        this.runState = new RunState(runAnim);
        this.jumpState = new JumpState(jumpAnim);

        this.currentState = idleState;
    }

    // Creates an animation from a sequence of images
    private static AnimationRenderable createAnimation(ImageReader imageReader,
                                                       String pathFormat,
                                                       int frameCount) {
        Renderable[] frames = new Renderable[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = imageReader.readImage(String.format(pathFormat, i), true);
        }
        return new AnimationRenderable(frames, ANIMATION_TIME);
    }
}