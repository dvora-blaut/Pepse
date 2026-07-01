package pepse.world.avatar;

import static pepse.world.avatar.AvatarConstants.*;

/**
 * Represents the avatar's energy resource.
 */
public class Energy {

    private float value;

    /**
     * Creates a new Energy instance initialized to maximum energy.
     */
    public Energy() {
        this.value = MAX_ENERGY;
    }

    /**
     * Returns the current energy as a percentage of the maximum.
     *
     * @return Energy percentage
     */
    public float getPercentage() {
        return value / MAX_ENERGY;
    }

    // Returns whether there is at least the given amount of energy
    boolean hasEnough(float amount) {
        return value >= amount;
    }

    // Adds the given amount of energy, capped at MAX_ENERGY
    void gain(float amount) {
        value = Math.min(MAX_ENERGY, value + amount);
    }

    // Subtracts the given amount of energy, floored at 0
    void consume(float amount) {
        value = Math.max(0, value - amount);
    }
}