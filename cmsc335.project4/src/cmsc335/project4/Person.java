package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.Scanner;

/**
 * A subclass of <code>Thing</code>, the <code>Person</code> class represents dock workers located
 * at specific <code>SeaPort</code>s. Each worker has a specific profession, notated in the class
 * itself as <code>skill</code>, and is placed in a <code>SeaPort</code>'s <code>persons</code>
 * <code>ArrayList</code>. Class contains setter/getter and the overridden <code>toString()</code>
 * method as per the rubric.
 */

final class Person extends Thing {

    // Rubric-required field
    private String skill;

    // Additional fields
    private volatile boolean isWorking; // Should be volatile?

    /**
     * Parameterized constructor
     * @param scannerContents Contents of the <code>.txt</code> file
     */
    protected Person(Scanner scannerContents) {
        super(scannerContents);

        if (scannerContents.hasNext()) {
            this.setSkill(scannerContents.next());
        } else {
            this.setSkill("Error");
        }

        this.setIsWorking(false);
    }

    // Setters

    private void setSkill(String skill) {
        this.skill = skill;
    }

    protected void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    // Getters

    protected String getSkill() {
        return this.skill;
    }

    protected boolean getIsWorking() {
        return this.isWorking;
    }

    // Overridden method

    @Override
    public String toString() {
        return "Person: " + super.toString() + " " + this.getSkill();
    }
}
