package cmsc335.project4;

/**
 *
 * @author davenull
 */

/**
 * As of Project 4, this class sits unused, but is designed to represent the specific times related
 * to the entry of ships in to <code>SeaPort</code> locales, presumably. As it is not an extended
 * subclass of <code>Thing</code>, it does not possess a <code>Scanner</code>-based constructor, but
 * rather accepts a value in a more conventional constructor design.
 */

final class PortTime {

    // Rubric-required field
    private int time;

    /**
     * Parameterized constructor
     * @param time <code>int</code>
     */
    protected PortTime(int time) {
        this.setTime(time);
    }

    // Setter

    private void setTime(int time) {
        this.time = time;
    }

    // Getter

    protected int getTime() {
        return this.time;
    }

    // Overriden method

    /**
     * @inheritdoc
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "Time: " + this.getTime();
    }
}
