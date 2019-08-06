package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class represents the world as it exists per the contents of the user-selected
 * <code>.txt</code> file. It accepts <code>Scanner</code> data and sorts through it, assembling
 * proper instances of the applicable classes and moving them into the proper <code>ArrayList</code>
 * or field. It also hosts a global <code>allThings</code> <code>ArrayList</code> that contains
 * a complete listing of all <code>Thing</code>-based objects for ease of searching/sorting.
 */

final class World extends Thing {

    // Rubric-required fields
    private ArrayList<Thing> allThings;
    private ArrayList<SeaPort> ports;
    private PortTime time;

    /**
     * Parameterized constructor
     * @param scannerContents Content of the <code>.txt</code> file
     */
    protected World(Scanner scannerContents) {
        super(scannerContents);
        this.setAllThings(new ArrayList<>());
        this.setPorts(new ArrayList<>());
        this.process(scannerContents);
    }

    // Setters

    private void setAllThings(ArrayList<Thing> allThings) {
        this.allThings = allThings;
    }

    private void setPorts(ArrayList<SeaPort> ports) {
        this.ports = ports;
    }

    private void setTime(PortTime time) {
        this.time = time;
    }

    // Getters

    protected ArrayList<Thing> getAllThings() {
        return this.allThings;
    }

    protected ArrayList<SeaPort> getPorts() {
        return this.ports;
    }

    protected PortTime getTime() {
        return this.time;
    }

    // Handlers

    /**
     * This is the main method of the <code>World</code> class, invoked after the definitions of the
     * <code>ArrayList</code>s <code>allThings</code> and <code>ports</code> in the body of the main
     * constructor.
     * 
     * @return void
     */
    private void process(Scanner scannerContents) {

        // Declarations
        String lineString;
        Scanner lineContents;
        HashMap<Integer, SeaPort> portsMap;
        HashMap<Integer, Dock> docksMap;
        HashMap<Integer, Ship> shipsMap;

        // Localized HashMap definitions in Scanner method, per Project 2 rubric
        portsMap = new HashMap<>();
        docksMap = new HashMap<>();
        shipsMap = new HashMap<>();

        while (scannerContents.hasNextLine()) {
            lineString = scannerContents.nextLine().trim(); // Remove spaces

            // Avoid evaluating any blank lines if exist
            if (lineString.length() == 0) {
                continue;
            }

            // Scanner for each line's contents
            lineContents = new Scanner(lineString);

            if (lineContents.hasNext()) {

                /**
                 * Builds <code>Thing</code> objects & stuff, passing them to the appropriate adder
                 * method. 
                 */
                switch(lineContents.next().trim()) {
                    case "port":
                        SeaPort newSeaPort = new SeaPort(lineContents);
                        this.getAllThings().add(newSeaPort);
                        this.getPorts().add(newSeaPort);
                        portsMap.put(newSeaPort.getIndex(), newSeaPort);
                        break;
                    case "dock":
                        Dock newDock = new Dock(lineContents);
                        this.getAllThings().add(newDock);
                        this.addThingToList(portsMap, newDock, "getDocks");
                        docksMap.put(newDock.getIndex(), newDock);
                        break;
                    case "pship":
                        PassengerShip newPassengerShip = new PassengerShip(lineContents, docksMap,
                            portsMap);
                        this.getAllThings().add(newPassengerShip);
                        this.addShipToParent(newPassengerShip, docksMap, portsMap);
                        shipsMap.put(newPassengerShip.getIndex(), newPassengerShip);
                        break;
                    case "cship":
                        CargoShip newCargoShip = new CargoShip(lineContents, docksMap, portsMap);
                        this.getAllThings().add(newCargoShip);
                        this.addShipToParent(newCargoShip, docksMap, portsMap);
                        shipsMap.put(newCargoShip.getIndex(), newCargoShip);
                        break;
                    case "person":
                        Person newPerson = new Person(lineContents);
                        this.getAllThings().add(newPerson);
                        this.addThingToList(portsMap, newPerson, "getPersons");
                        break;
                    case "job":
                        Job newJob = new Job(lineContents, shipsMap);
                        this.getAllThings().add(newJob);
                        this.addJobToShip(newJob, shipsMap, docksMap);
                        break;
                    default: // Added because required by Google styleguide
                        break;
                }
            }
        }
    }

    /**
     * Generic addition method that replaces a fair bit of copy/pasta methods that were basically
     * identical. Accepts a new <code>Thing</code> subclass object and a <code>String</code>
     * representation of a declared method as parameters, allowing for the selection of the proper
     * <code>SeaPort</code> <code>ArrayList</code> getter method depending on input.
     * 
     * @return void
     */
    @SuppressWarnings("unchecked")
    private <T extends Thing> void addThingToList(HashMap<Integer, SeaPort> portsMap, T newThing,
            String methodName) {

        // Declarations
        SeaPort newPort;
        ArrayList<T> thingsList;
        Method getList;

        // Definition
        newPort = portsMap.get(newThing.getParent());

        try {
            // Either SeaPort.class.getPersons() or SeaPort.class.getDocks();
            getList = SeaPort.class.getDeclaredMethod(methodName);

            // See casting comment in above method
            thingsList = (ArrayList<T>) getList.invoke(newPort); // newPort.getList()

            if (newPort != null) {
                thingsList.add(newThing);
            }
        } catch (
            NoSuchMethodException |
            SecurityException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException ex
        ) {
            System.out.println("Error: " + ex);
        }
    }

    /**
     * This method was a tricky one, and is similar in scope to the method below. In some cases, the
     * value of <code>getParent</code> for the new <code>Job</code> instance will not match that of
     * an extant ship in any <code>SeaPort</code>'s <code>getShips()</code> <code>ArrayList</code>,
     * so the method must check each port's docks for moored ships, find the ship, and add the
     * new job to that ship.
     *
     * @param newJob <code>Job</code>
     * @param shipsMap <code>HashMap</code>
     * @param docksMap <code>HashMap</code>
     * @return void
     */
    private void addJobToShip(Job newJob, HashMap<Integer, Ship> shipsMap,
            HashMap<Integer, Dock> docksMap) {

        Dock newDock;
        Ship newShip = shipsMap.get(newJob.getParent());

        if (newShip != null) {
            newShip.getJobs().add(newJob);
        } else {
            newDock = docksMap.get(newJob.getParent());
            newDock.getShip().getJobs().add(newJob);
        }
    }

    /**
     * This method was tricky to get right. As not all <code>Ship</code> objects are moored at a
     * <code>Dock</code>, the value of <code>getParent()</code> may not correspond to any extant
     * <code>Dock</code> objects. If the ship's <code>parent</code> is not a dock (i.e. is
     * <code>null</code>), then we add it to the all ships <code>ArrayList</code> <code>ships</code>
     * and add it to the queue (<code>getQue()</code>). If the ship is moored, we add it to the all
     * ships listing and set the value of the specific <code>Dock</code>'s <code>getShip()</code> as
     * the ship.
     *
     * @param newship <code>Ship</code>
     * @param docksMap <code>HashMap</code>
     * @param portsMap <code>HashMap</code>
     * return void
     */
    private void addShipToParent(Ship newShip, HashMap<Integer, Dock> docksMap,
            HashMap<Integer, SeaPort> portsMap) {

        SeaPort myPort;
        Dock myDock = docksMap.get(newShip.getParent());

        if (myDock == null) {
            myPort = portsMap.get(newShip.getParent());
            myPort.getShips().add(newShip);
            myPort.getQue().add(newShip);
        } else {
            myPort = portsMap.get(myDock.getParent());
            myDock.setShip(newShip);
            myPort.getShips().add(newShip);
        }
    }

    /**
     * This method is called within the body of <code>SeaPortProgram</code>'s main assembly method,
     * <code>SeaPortProgram.class.readFileContents</code>, to provide the fully assembled
     * <code>DefaultMutableTreeNode</code> representing the complete <code>World</code> instance
     * for use in assembly of a <code>DefaultTreeModel</code>. The complete model is then displayed
     * in a <code>JTree</code> for a more user-friendly means of interacting with the contents of
     * the world.
     *
     * @see java.lang.reflect
     * @param <T> extends <code>Thing</code>
     * @return <code>mainNode</code> The complete <code>DefaultMutableTreeNode</code>
     */
    @SuppressWarnings("unchecked")
    protected <T extends Thing> DefaultMutableTreeNode toTree() {

        // Declarations
        DefaultMutableTreeNode mainNode, parentNode, childNode;
        Method getList;
        HashMap<String, String> classMethodMap;
        ArrayList<T> thingsList;

        // Definitions
        mainNode = new DefaultMutableTreeNode("World");
        classMethodMap = new HashMap<String, String>() {{
            put("Docks", "getDocks");
            put("Ships", "getShips");
            put("Que", "getQue");
            put("Persons", "getPersons");
        }};

        for (SeaPort newPort : this.getPorts()) {
            parentNode = new DefaultMutableTreeNode(newPort.getName() + " (" + newPort.getIndex()
                + ")");
            mainNode.add(parentNode);

            for (HashMap.Entry<String, String> pair : classMethodMap.entrySet()) {
                try {
                    // Reflection-related stuff
                    getList = SeaPort.class.getDeclaredMethod(pair.getValue());
                    thingsList = (ArrayList<T>) getList.invoke(newPort);

                    // Acquire each port's children and add them to the port parent
                    childNode = this.addBranch(pair.getKey(), thingsList);
                    parentNode.add(childNode);
                } catch (
                    NoSuchMethodException |
                    SecurityException |
                    IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException ex
                ) {
                    System.out.println("Error: " + ex);
                }
            }
        }

        return mainNode;
    }

    /**
     * This utility method is invoked via reflection by <code>World.class.toTree</code> to assemble
     * each sub-branch (i.e. persons, ships, que, and docks) of each <code>SeaPort</code>. Based on
     * the class type of each associated <code>ArrayList</code>, additional sub-sub branches may be
     * added as well, including moored ships at <code>Dock</code>s or <code>Job</code>s aboard
     * <code>Ship</code>s.
     *
     * @param <T> extends <code>Thing</code>
     * @param title The <code>String</code> value to be used as the title of the branch
     * @param thingsList Contents of an <code>ArrayList</code>
     * @return <code>parentNode</code> The complete <code>DefaultMutableTreeNode</code>
     */
    private <T extends Thing> DefaultMutableTreeNode addBranch(String title,
            ArrayList<T> thingsList) {

        // Declarations
        String newThingName, childTitle;
        DefaultMutableTreeNode parentNode, childNode;
        Dock thisDock;
        Ship mooredShip, newShip;

        // Definitions
        parentNode = new DefaultMutableTreeNode(title);

        for (T newThing : thingsList) {
            newThingName = newThing.getName() + " (" + newThing.getIndex() + ")";
            childNode = new DefaultMutableTreeNode(newThingName);

            /**
             * In cases of <code>Dock</code>s, the associated moored <code>Ship</code> (if present)
             * is displayed as well as a child node in its own right. Likewise, in cases of
             * <code>Ship</code>s, <code>Job</code>s are displayed as child nodes if they exist.
             */
            if (newThing instanceof Dock) { // Is Dock instance
                thisDock = (Dock) newThing;
                mooredShip = thisDock.getShip();

                if (thisDock.getShip() != null) {
                    childTitle = mooredShip.getName() + " (" + mooredShip.getIndex() + ")";
                    childNode.add(new DefaultMutableTreeNode(childTitle));
                }
            } else if (newThing instanceof Ship) {
                newShip = (Ship) newThing;

                if (!newShip.getJobs().isEmpty()) {
                    for (Job newJob : newShip.getJobs()) {
                        childTitle = newJob.getName();
                        childNode.add(new DefaultMutableTreeNode(childTitle));
                    }
                }
            }

            parentNode.add(childNode);
        }

        return parentNode;
    }

    // Overridden methods

    /**
     * @inheritdoc
     * @return <code>stringOutput</code>
     */
    @Override
    public String toString() {
        String stringOutput = ">>>>> The world:\n";

        for (SeaPort seaPort : this.getPorts()) {
            stringOutput += seaPort.toString() + "\n";
        }
        return stringOutput;
    }
}
