package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class represents ports in the world, containing a series of <code>Thing</code> subclass
 * <code>ArrayList</code>s that hold docks, queued ships, all ships, and persons present at the
 * location.
 */

final class SeaPort extends Thing {

    // Rubric-required <code>ArrayList</code>s
    private ArrayList<Dock> docks;
    private ArrayList<Ship> que, ships;
    private ArrayList<Person> persons;

    // Divide Persons by skill, where name of skill is key
    private HashMap<String, ResourcePool> resourcePools;

    /**
     * Parameterized constructor
     * @param scannerContents - Contents of <code>.txt</code> file
     */
    protected SeaPort(Scanner scannerContents) {
        super(scannerContents);
        this.setDocks(new ArrayList<>());
        this.setQue(new ArrayList<>());
        this.setShips(new ArrayList<>());
        this.setPersons(new ArrayList<>());
        this.setResourcePools(new HashMap<>());
    }

    // Setters

    private void setDocks(ArrayList<Dock> docks) {
        this.docks = docks;
    }

    private void setQue(ArrayList<Ship> que) {
        this.que = que;
    }

    private void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    private void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    private void setResourcePools(HashMap<String, ResourcePool> resourcePools) {
        this.resourcePools = resourcePools;
    }

    // Getters

    protected ArrayList<Dock> getDocks() {
        return this.docks;
    }

    protected ArrayList<Ship> getQue() {
        return this.que;
    }

    protected ArrayList<Ship> getShips() {
        return this.ships;
    }

    protected ArrayList<Person> getPersons() {
        return this.persons;
    }

    protected HashMap<String, ResourcePool> getResourcePools() {
        return this.resourcePools;
    }

    // Utility methods

    /**
     * This method assembles workers from its <code>ArrayList</code> of <code>Person</code>s,
     * <code>this.persons</code>, that match the skills required by the <code>Job</code>s related to
     * <code>Ship</code>s moored at one of this port's <code>Dock</code>s. Those that match the
     * requirements are flagged as working and passed to the job in question for use therein.
     * 
     * @return candidates <code>Person</code> <code>ArrayList</code>
     */
    protected synchronized ArrayList<Person> getResources(Job job) {

        // Declarations
        ResourcePool skillGroup;
        ArrayList<Person> candidates;
        boolean areAllRequirementsMet;
        String workerLogLine;
        Person worker;
        HashMap<String, Integer> mapOfNeededSkills;

        // Definitions
        candidates = new ArrayList<>();
        areAllRequirementsMet = true;
        workerLogLine = "";
        mapOfNeededSkills = new HashMap<>();

        /**
         * Implementation suggested <a href="https://stackoverflow.com/questions/81346">here</a> as
         * an ideal Java 8 solution. Basically, to more easily keep track of duplicate skills needed
         * (i.e. two cooks for a job), we create a <code>HashMap</code> entry of that skill to
         * <code>mapOfNeededSkills</code> where the value is the number of workers with that skill
         * that are required to complete the job.
         */
        job.getRequirements().forEach((String skill) -> {
            mapOfNeededSkills.merge(skill, 1, Integer::sum);
        });

        outerLoop:
        for (String skill : job.getRequirements()) {

            // Grab the resource pool possessing all the workers who have this skill in the port
            skillGroup = this.getResourcePools().get(skill);

            // If no workers exist in the port with this specific skill...
            if (skillGroup == null) {
                job.getStatusLog().append("No qualified workers found for " + job.getName()
                    + " (" + job.getParentShip().getName() + ")\n");

                // Release the chopstick, Socrates
                this.returnResources(candidates);
                job.endJob();
                return new ArrayList<>();

            // If the total number of people with this skill is smaller than the needed number...
            } else if (skillGroup.getPersonsInPool().size() < mapOfNeededSkills.get(skill)) {
                job.getStatusLog().append("Not enough qualified workers found for " + job.getName()
                    + " (" + job.getParentShip().getName() + ")\n");

                // Gimme the fork, Epicurus
                this.returnResources(candidates);
                job.endJob();
                return new ArrayList<>();

            // Otherwise...
            } else {

                // For all workers with the required skill
                for (Person person : skillGroup.getPersonsInPool()) {

                    // If this individual is not employed
                    if (!person.getIsWorking()) {
                        skillGroup.reservePerson(person);
                        candidates.add(person);
                        continue outerLoop;
                    }
                }

                // If no available workers are present, we have to keep waiting
                areAllRequirementsMet = false;
                break;
            }
        } // end outerLoop

        // Basically a case of logical conjunction; we only return workers if all cases are true
        if (areAllRequirementsMet) {
            workerLogLine += job.getName() + " (" + job.getParentShip().getName() + ") reserving";

            for (int i = 0; i < candidates.size(); i++) {
                worker = candidates.get(i);

                if (i == 0) {
                    workerLogLine += " ";
                } else if (i < candidates.size() - 1) {
                    workerLogLine += ", ";
                } else {
                    workerLogLine += " & ";
                }

                workerLogLine += worker.getName();
            }
            job.getStatusLog().append(workerLogLine + "\n");

            return candidates;
        } else {

            this.returnResources(candidates);
            return null;
        }
    }

    /**
     * This method is used by both the <code>SeaPort</code> and <code>Job</code> classes to
     * symbolically return workers to their respective port resource pools at the conclusion of a
     * <code>Job</code> thread. Though before the method employed <code>ArrayList.addAll()</code>
     * to return the formerly removed <code>SeaPort.persons</code> entries to the listing, the
     * current incarnation more easily sets the <code>boolean</code> flag
     * <code>Person.isWorking</code> to false.
     * 
     * @return void
     */
    protected synchronized void returnResources(ArrayList<Person> resources) {
        resources.forEach((Person worker) -> {
            this.getResourcePools().get(worker.getSkill()).returnPerson(worker);
        });
    }

    /**
     * This method simply takes the complete contents of <code>SeaPort.persons</code>, the port's
     * listing of all dock workers, and divides its contents based upon each worker's occupation
     * (value of <code>Person.skill</code>). Each person is added to a newly-created
     * <code>ResourcePool</code> containing fields and methods related to that skill, with said new
     * class instances being created if not already existing. This definitely makes it easier to
     * check if jobs demand a skill that no workers possess; if there is no matching resource pool
     * in the port, no workers have the skill because the pool was never created by this method.
     *
     * @see project4.ResourcePool
     * @return void
     */
    protected void divideWorkersBySkill() {
        ResourcePool myResourcePool;

        for (Person person : this.getPersons()) {
            myResourcePool = this.getResourcePools().get(person.getSkill());

            // Create the pool if no other workers have yet been discovered with this skill
            if (myResourcePool == null) {
                myResourcePool = new ResourcePool(new ArrayList<>(), person.getSkill(),
                    this.getName());
                this.getResourcePools().put(person.getSkill(), myResourcePool);
            }

            myResourcePool.addPerson(person);
        }
    }

    // Overridden methods

    /**
     * @inheritdoc
     * @return stringOutput <code>String</code>
     */
    @Override
    public String toString() {
        String stringOutput;

        // A near-identical implementation of the method as denoted in the rubric
        stringOutput = "\n\nSeaPort: " + super.toString();
        stringOutput = this.getDocks().stream().map((dock) -> "\n> " + dock.toString()).reduce(stringOutput, String::concat);

        stringOutput += "\n\n --- List of all ships in que:";
        stringOutput = this.getQue().stream().map((shipQue) -> "\n> " + shipQue.toString()).reduce(stringOutput, String::concat);

        // Since the above output displays ship-related details, this one is just a quick summary
        stringOutput += "\n\n --- List of all ships:";
        stringOutput = this.getShips().stream().map((shipAll) -> "\n> " + shipAll.getName() + " " + shipAll.getIndex() + " ("
                + shipAll.getClass().getSimpleName() + ")").reduce(stringOutput, String::concat);

        stringOutput += "\n\n --- List of all persons:";
        stringOutput = this.getPersons().stream().map((person) -> "\n> " + person.toString()).reduce(stringOutput, String::concat);

        return stringOutput;
    }
}
