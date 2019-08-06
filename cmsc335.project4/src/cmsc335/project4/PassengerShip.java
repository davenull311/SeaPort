package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.HashMap;
import java.util.Scanner;

/**
 * Class for the <code>Ship</code> subclass <code>PassengerShip</code> which represents a passenger
 * ship. Contains data pertaining to rooms and passengers, set in the proper field by the required
 * <code>Scanner</code> constructor (per the rubric). Also contains the necessary overridden
 * <code>toString()</code> method.
 */

final class PassengerShip extends Ship {

    // Rubric-required fields
    private int numberOfOccupiedRooms, numberOfPassengers, numberOfRooms;

    /**
     * Parameterized constructor
     * @param scannerContents Contents of <code>.txt</code> file
     * @param docksMap <code>HashMap</code> of <code>Dock</code>s, from <code>World</code>
     * @param portsMap <code>HashMap</code> of <code>SeaPort</code>s, from <code>World</code>
     */
    protected PassengerShip(Scanner scannerContents, HashMap<Integer, Dock> docksMap,
            HashMap<Integer, SeaPort> portsMap) {

        super(scannerContents, docksMap, portsMap);

        if (scannerContents.hasNextInt()) {
            this.setNumberOfPassengers(scannerContents.nextInt());
        }

        if (scannerContents.hasNextInt()) {
            this.setNumberOfRooms(scannerContents.nextInt());
        }

        if (scannerContents.hasNextInt()) {
            this.setNumberOfOccupiedRooms(scannerContents.nextInt());
        }
    }

    // Setters

    private void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    private void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    private void setNumberOfOccupiedRooms(int numberOfOccupiedRooms) {
        this.numberOfOccupiedRooms = numberOfOccupiedRooms;
    }

    // Getters

    protected int getNumberOfPassengers() {
        return this.numberOfPassengers;
    }

    protected int getNumberOfRooms() {
        return this.numberOfRooms;
    }

    protected int getNumberOfOccupiedRooms() {
        return this.numberOfOccupiedRooms;
    }

    // Overriden methods

    @Override
    public String toString() {
        String stringOutput;

        stringOutput = "Passenger Ship: " + super.toString() + "\n\tPassengers: "
            + this.getNumberOfPassengers() + "\n\tRooms: " + this.getNumberOfRooms()
            + "\n\tOccupied Rooms: " + this.getNumberOfOccupiedRooms();

        return stringOutput;
    }
}
