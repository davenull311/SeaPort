package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.Scanner;

/**
 * This class is the progenitor of all <code>World</code>-based objects, from ship types to docks
 * and ports. As per the rubric, it implements the interface <code>Comparable</code>, which sees no
 * use during the first project but is included anyway.
 */

class Thing implements Comparable<Thing> {

    // Rubric-required variables in use in all objects
    private int index, parent;
    private String name;

    /**
     * Parameterized constructor, accepts <code>Scanner</code> contents as per rubric
     * @param scannerContents Content of the <code>.txt</code> file
     */
    protected Thing(Scanner scannerContents) {
        if (scannerContents.hasNext()) {
            this.setName(scannerContents.next());
        }

        if (scannerContents.hasNextInt()) {
            this.setIndex(scannerContents.nextInt());
        }

        if (scannerContents.hasNextInt()) {
            this.setParent(scannerContents.nextInt());
        }
    }

    // Setters

    private void setIndex(int index) {
        this.index = index;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setParent(int parent) {
        this.parent = parent;
    }

    // Getters

    protected int getIndex() {
        return this.index;
    }

    protected String getName() {
        return this.name;
    }

    protected int getParent() {
        return this.parent;
    }

    // Overridden methods

    /**
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        // It didn't make sense to include parent, since things are organized by parent anyway
        return this.getName() + " " + this.getIndex();
    }

    /**
     * @param thingInstance <code>Thing</code>
     * @return <code>boolean</code>
     */
    @Override
    public int compareTo(Thing thingInstance) {
        if (
            (thingInstance.getIndex() == this.getIndex()) &&
            (thingInstance.getName().equals(this.getName())) &&
            (thingInstance.getParent() == this.getParent())
        ) {
            return 1;
        } else {
            return 0;
        }
    }
}
