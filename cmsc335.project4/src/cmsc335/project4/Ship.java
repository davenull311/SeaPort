package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class represents general ship objects to be stored in queues or docks in port objects. The
 * class serves as the progenitor parent of two subclasses, namely <code>PassengerShip</code> and
 * <code>CargoShip</code>, and contains fields and <code>ArrayList</code>s related to the specific
 * physical properties of the vessel in question. However, like many of the world objects, the class
 * contains mainly setters and getters, the required <code>Scanner</code> based constructor, and the
 * overridden <code>toString()</code> method.
 */

class Ship extends Thing {

    // Rubric-required fields
    private PortTime arrivalTime, dockTime;
    private double draft, length, weight, width;
    private ArrayList<Job> jobs;

    // Project 3 & 4 fields, useful for Jobs
    private SeaPort port;
    private Dock dock;
    private HashMap<Integer, Dock> docksMap;

    /**
     * Parameterized constructor
     * @param scannerContents Contents of <code>.txt</code> file
     * @param docksMap <code>HashMap</code> of <code>Dock</code>s, from <code>World</code>
     * @param portsMap <code>HashMap</code> of <code>SeaPort</code>s, from <code>World</code>
     */
    protected Ship(Scanner scannerContents, HashMap<Integer, Dock> docksMap,
            HashMap<Integer, SeaPort> portsMap) {

        super(scannerContents);

        if (scannerContents.hasNextDouble()) {
            this.setWeight(scannerContents.nextDouble());
        }

        if (scannerContents.hasNextDouble()) {
            this.setLength(scannerContents.nextDouble());
        }

        if (scannerContents.hasNextDouble()) {
            this.setWidth(scannerContents.nextDouble());
        }

        if (scannerContents.hasNextDouble()) {
            this.setDraft(scannerContents.nextDouble());
        }

        this.setJobs(new ArrayList<>());
        this.setPort(docksMap, portsMap);
        this.setDocksMap(docksMap);
        this.setDock();
    }

    // Setters

    private void setArrivalTime(PortTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    private void setDockTime(PortTime dockTime) {
        this.dockTime = dockTime;
    }

    private void setWeight(double weight) {
        this.weight = weight;
    }

    private void setLength(double length) {
        this.length = length;
    }

    private void setWidth(double width) {
        this.width = width;
    }

    private void setDraft(double draft) {
        this.draft = draft;
    }

    private void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    private void setPort(HashMap<Integer, Dock> docksMap, HashMap<Integer, SeaPort> portsMap) {
        this.port = portsMap.get(this.getParent());

        if (this.port == null) {
            Dock pier = docksMap.get(this.getParent());
            this.port = portsMap.get(pier.getParent());
        }
    }

    private void setDocksMap(HashMap<Integer, Dock> docksMap) {
        this.docksMap = docksMap;
    }

    private void setDock() {
        if (this.getDocksMap().containsKey(this.getParent())) {
            this.dock = this.getDocksMap().get(this.getParent());
        } else {
            this.dock = null;
        }
    }

    protected void setDock(Dock dock) {
        this.dock = dock;
    }

    // Getters

    protected PortTime getArrivalTime() {
        return this.arrivalTime;
    }

    protected PortTime getDockTime() {
        return this.dockTime;
    }

    protected double getWeight() {
        return this.weight;
    }

    protected double getLength() {
        return this.length;
    }

    protected double getWidth() {
        return this.width;
    }

    protected double getDraft() {
        return this.draft;
    }

    protected ArrayList<Job> getJobs() {
        return this.jobs;
    }

    protected SeaPort getPort() {
        return this.port;
    }

    private HashMap<Integer, Dock> getDocksMap() {
        return this.docksMap;
    }

    protected Dock getDock() {
        return this.dock;
    }

    // Overriden methods

    /**
     * @return stringOutput <code>String</code>
     */
    @Override
    public String toString() {
        String stringOutput;

        stringOutput = super.toString() + "\n\tWeight: " + this.getWeight() + "\n\tLength: "
            + this.getLength() + "\n\tWidth: " + this.getWidth() + "\n\tDraft: " + this.getDraft()
            + "\n\tJobs:";

        if (this.getJobs().isEmpty()){
            stringOutput += " None";
        } else {
            for (Job newJob : this.getJobs()) {
                stringOutput += "\n" + newJob.toString();
            }
        }

        return stringOutput;
    }
}
