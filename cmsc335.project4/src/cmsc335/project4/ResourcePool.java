package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class was added last minute by the author for the 2nd Project 4 submission. Originally, the
 * author believed that the <code>SeaPort.persons</code> <code>ArrayList</code> was the titular
 * resource pool through which <code>Job</code>s would have to thrift to find workers who could
 * complete their tasks, though he eventually came to read the vague rubric language differently.
 */

final class ResourcePool {

    // Pool-specific fields to keep things organized
    private ArrayList<Person> personsInPool;
    private int availablePersons, totalPersons;
    private String skill, parentPort;

    // GUI elements
    private JPanel rowPanel;
    private JLabel portLabel, skillLabel, countLabel, totalLabel;

    /**
     * Parameterized constructor
     * @param personsInPool <code>ArrayList</code>
     * @param skill <code>String</code> value of <code>Person.getSkill()</code>
     * @param parentPort <code>String</code> value of <code>SeaPort.getName()</code>s
     */
    protected ResourcePool(ArrayList<Person> personsInPool, String skill, String parentPort) {
        this.setPersonsInPool(personsInPool);
        this.setTotalPersons(this.getPersonsInPool().size());
        this.setAvailablePersons(this.getPersonsInPool().size());
        this.setSkill(skill);
        this.setParentPort(parentPort);
    }

    // Setters

    private void setPersonsInPool(ArrayList<Person> personsInPool) {
        this.personsInPool = personsInPool;
    }

    private void setTotalPersons(int totalPersons) {
        this.totalPersons = totalPersons;
    }

    private void setAvailablePersons(int availablePersons) {
        this.availablePersons = availablePersons;
    }

    private void setSkill(String skill) {
        this.skill = skill;
    }

    private void setParentPort(String parentPort) {
        this.parentPort = parentPort;
    }

    // Getters

    protected ArrayList<Person> getPersonsInPool() {
        return this.personsInPool;
    }

    protected int getTotalPersons() {
        return this.totalPersons;
    }

    protected int getAvailablePersons() {
        return this.availablePersons;
    }

    protected String getSkill() {
        return this.skill;
    }

    protected String getParentPort() {
        return this.parentPort;
    }

    // Utility methods

    /**
     * Much like the similarly named <code>Job.getJobAsPanel</code> method that inspired its
     * creation, this method returns a <code>JPanel</code> "row" for a <code>JTable</code>-esque
     * pseudo-table to be constructed by the GUI class, <code>SeaPortProgram</code>. Each row
     * includes relevant information pertaining to the pool and its contents, from the number of
     * available persons to the port location and name of the skill.
     * 
     * @return rowPanel <code>JPanel</code>
     */
    protected JPanel getPoolAsPanel() {

        String job = this.getSkill().substring(0, 1).toUpperCase() + this.getSkill().substring(1);

        // Definitions
        this.rowPanel = new JPanel(new GridLayout(1, 3));
        this.skillLabel = new JLabel(job, JLabel.CENTER);
        this.portLabel = new JLabel(this.getParentPort(), JLabel.CENTER);
        this.countLabel = new JLabel("Available: "
            + String.valueOf(this.getAvailablePersons()), JLabel.CENTER);
        this.totalLabel = new JLabel("Total: "
            + String.valueOf(this.getTotalPersons()), JLabel.CENTER);

        // Allow for colorization of JLabel backgrounds
        this.skillLabel.setOpaque(true);
        this.portLabel.setOpaque(true);
        this.countLabel.setOpaque(true);
        this.totalLabel.setOpaque(true);

        // Add background colorization
        this.skillLabel.setBackground(Color.WHITE);
        this.portLabel.setBackground(Color.WHITE);
        this.countLabel.setBackground(Color.WHITE);
        this.totalLabel.setBackground(Color.WHITE);

        // Add JTable-esque borders
        this.skillLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.portLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.countLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.totalLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Add elements to the pseudo-row
        this.rowPanel.add(this.skillLabel);
        this.rowPanel.add(this.portLabel);
        this.rowPanel.add(this.countLabel);
        this.rowPanel.add(this.totalLabel);

        return this.rowPanel;
    }

    /**
     * This utility method was added to help add instances to the <code>ArrayList</code> for
     * relevant workers while simultaneously incrementing the number of available/total workers
     * accordingly. It is called within the body of <code>SeaPort.divideWorkersBySkill</code> only
     * and is only used in the assembly of resource pools.
     *
     * @return void
     */
    protected void addPerson(Person person) {
        this.getPersonsInPool().add(person);
        this.setAvailablePersons(this.getPersonsInPool().size());
        this.setTotalPersons(this.getPersonsInPool().size());
    }

    /**
     * Though this method and that following it could easily be combined into a single method that
     * employs reflection, the author elected to keep them separate to ensure readability is
     * preserved in the body of the <code>SeaPort</code> methods that invoke these methods.

* 
     * @return void
     */
    protected void reservePerson(Person person) {
        person.setIsWorking(true);
        this.setAvailablePersons(this.getAvailablePersons() - 1);
        this.countLabel.setText("Available: " + String.valueOf(this.getAvailablePersons()));
    }

    /**
     * Though this method and that following it could easily be combined into a single method that
     * employs reflection, the author elected to keep them separate to ensure readability is
     * preserved in the body of the <code>SeaPort</code> methods that invoke these methods.
     * 
     * @return void
     */
    protected void returnPerson(Person person) {
        person.setIsWorking(false);
        this.setAvailablePersons(this.getAvailablePersons() + 1);
        this.countLabel.setText("Available: " + String.valueOf(this.getAvailablePersons()));
    }
}
