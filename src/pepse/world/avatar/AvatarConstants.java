package pepse.world.avatar;

/**
 * Shared constants for Avatar and its states.
 */
public class AvatarConstants {
    public static final float VELOCITY_X = 400;
    public static final float VELOCITY_Y = -650;
    public static final float GRAVITY = 600;

    public static final float MAX_ENERGY = 100;
    public static final float RUN_ENERGY_COST = 2f;
    public static final float JUMP_ENERGY_COST = 20f;
    public static final float DOUBLE_JUMP_ENERGY_COST = 50f;
    public static final float ENERGY_GAIN = 1f;

    public static final String IDLE_ANIMATION_PATH = "assets\\idle_%d.png";
    public static final String RUN_ANIMATION_PATH = "assets\\run_%d.png";
    public static final String JUMP_ANIMATION_PATH = "assets\\jump_%d.png";
    public static final int IDLE_FRAME_COUNT = 4;
    public static final int RUN_FRAME_COUNT = 6;
    public static final int JUMP_FRAME_COUNT = 4;
    public static final double ANIMATION_TIME = 0.1;

    public static final String AVATAR_TAG = "avatar";
    public static final String GROUND_TAG = "ground";
}