package cmsc335.project4;

/**
 *
 * @author davenull
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * This class, extending <code>Thing</code>, is technically an optional class until Projects 3 & 4.
 * It represents the jobs available to be filled on board <code>Ship</code>s, both passenger liners
 * and cargo ships. It contains the appropriate setters/getters and <code>toString()</code> method
 * as per all <code>Thing</code> objects.
 */

final class Job extends Thing implements Runnable {

    // Rubric-required fields
    private double duration;
    private ArrayList<String> requirements;

    // Thread-related fields
    private enum Status {RUNNING, SUSPENDED, WAITING, DONE}
    private Ship parentShip;
    private SeaPort parentPort;
    private boolean isSuspended, isCancelled, isFinished, isEndex;
    private Status status;
    private Thread jobThread;
    private ArrayList<Person> workers;

    // GUI elements
    private JTextArea statusLog;
    private JButton cancelButton, suspendButton;
    private JProgressBar jobProgress;
    private JPanel rowPanel;
    private JLabel rowLabel;

    /**
     * Parameterized constructor
     * @param scannerContents Contents of <code>.txt</code> file
     * @param shipsMap The localized <code>HashMap</code> from <code>this.world</code>
     */
    protected Job(Scanner scannerContents, HashMap<Integer, Ship> shipsMap) {
        super(scannerContents);
        if (scannerContents.hasNextDouble()) {
            this.setDuration(scannerContents.nextDouble());
        }

        this.setRequirements(new ArrayList<>());
        while (scannerContents.hasNext()) {
            this.getRequirements().add(scannerContents.next());
        }

        this.setParentShip(shipsMap.get(this.getParent()));
        this.setParentPort(this.getParentShip().getPort());
        this.setIsSuspended(false);
        this.setIsCancelled(false);
        this.setIsFinished(false);
        this.setStatus(Status.SUSPENDED);
        this.setJobThread(new Thread(this, this.getName() + " (" + this.getParentShip().getName()
            + ")")); // Threads named for the fourth project, more easily tracked in JConsole
        this.setIsEndex(false);
    }

    // Setters

    private void setDuration(double duration) {
        this.duration = duration;
    }

    private void setRequirements(ArrayList<String> requirements) {
        this.requirements = requirements;
    }

    private void setParentShip(Ship parentShip) {
        this.parentShip = parentShip;
    }

    private void setParentPort(SeaPort parentPort) {
        this.parentPort = parentPort;
    }

    protected void setStatusLog(JTextArea statusLog) {
        this.statusLog = statusLog;
    }

    private void setWorkers(ArrayList<Person> workers) {
        this.workers = workers;
    }

    private void setIsSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    private void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    /**
     * Setter for <code>isFinished</code>
     * @param isFinished <code>boolean</code>
     * @return void
     */
    private void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    private void setJobThread(Thread jobThread) {
        this.jobThread = jobThread;
    }

    private void setIsEndex(boolean isEndex) {
        this.isEndex = isEndex;
    }

    // Getters

    protected double getDuration() {
        return this.duration;
    }

    protected ArrayList<String> getRequirements() {
        return this.requirements;
    }

    protected Ship getParentShip() {
        return this.parentShip;
    }

    protected SeaPort getParentPort() {
        return this.parentPort;
    }

    protected JTextArea getStatusLog() {
        return this.statusLog;
    }

    protected ArrayList<Person> getWorkers() {
        return this.workers;
    }

    protected boolean getIsSuspended() {
        return this.isSuspended;
    }

    protected boolean getIsCancelled() {
        return this.isCancelled;
    }

    protected boolean getIsFinished() {
        return this.isFinished;
    }

    protected Status getStatus() {
        return this.status;
    }

    protected Thread getJobThread() {
        return this.jobThread;
    }

    private boolean getIsEndex() {
        return this.isEndex;
    }

    // Utility methods

    /**
     * This method is a mini version of <code>SeaPortProgram.class.constructGUI()</code> and is
     * arranged in a similar manner to that method. It is used to set a "row" in the appropriate
     * <code>Job</code>s section of the GUI, and as such employs <code>GridLayout</code> to factor
     * the row into a pseudo-<code>JTable</code> row. It is invoked from within a
     * <code>Job</code>s for loop in <code>SeaPortProgram.startAllJobs</code>.
     *
     * @return rowPanel <code>JPanel</code> set with <code>GridLayout</code>
     */
    protected JPanel getJobAsPanel() {

        // Definitions
        this.rowPanel = new JPanel(new GridLayout(1, 4));

        // JLabel
        this.rowLabel = new JLabel(this.getName() + " (" + this.getParentShip().getName() + ")",
            JLabel.CENTER);
        this.rowLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // JProgressBar
        this.jobProgress = new JProgressBar();
        this.jobProgress.setStringPainted(true);

        // JButton definitions
        this.suspendButton = new JButton("Suspend");
        this.cancelButton = new JButton("Cancel");

        // Suspend button handler
        this.suspendButton.addActionListener((ActionEvent e) -> {
            this.toggleSuspendFlag();
        });

        // Cancel button handler
        this.cancelButton.addActionListener((ActionEvent e) -> {
            this.toggleCancelFlag();
        });

        // Add to row panel
        this.rowPanel.add(this.rowLabel);
        this.rowPanel.add(this.jobProgress);
        this.rowPanel.add(this.suspendButton);
        this.rowPanel.add(this.cancelButton);

        return this.rowPanel;
    }

    /**
     * This utility method is simply used to start the <code>Thread</code>, setting the associated
     * <code>boolean</code> flag <code>this.isFinished</code> to false from the outset. This method
     * is invoked from within a <code>for</code> loop within the body of the
     * <code>SeaPortProgram.class.updateJobs()</code> method.
     *
     * @return void
     */
    protected void startJob() {
        this.setIsFinished(false);
        this.getJobThread().start();
    }

    /**
     * This method provides a safe way of ending a thread, rather than relying on a testy method
     * like <code>Thread.stop()</code>. The <code>boolean</code> <code>this.isEndex</code> (a
     * reference to the old military code "end of exercise") is used as an emergency hard stop that
     * returns straight out of the <code>run()</code> method, thereby remedying the former bug that
     * saw the previous world threads continuing to post to the status log even after the
     * initialization of a new <code>World</code> instance by <code>SeaPortProgram.readFile</code>.
     *
     * @return void
     */
    protected void endJob() {
        this.toggleCancelFlag();
        this.setIsEndex(true);
    }

    /**
     * This method is first of two <code>JButton</code> click event handlers. This is used to toggle
     * each <code>Job</code>'s suspend process, setting the value of <code>this.isSuspended</code>
     * to the opposite value of its current value, as in <code>isSuspended = !isSuspended;</code>
     *
     * @return void
     */
    private void toggleSuspendFlag() {
        this.setIsSuspended(!this.getIsSuspended());
    }

    /**
     * This method is one of two <code>JButton</code> click handlers, used to toggle the
     * <code>Job</code> cancellation process. When clicked, the values of
     * <code>this.isCancelled</code> and <code>this.isFinished</code> are set to <code>true</code>,
     * naturally and gracefully bringing an end to the thread.
     *
     * @return void
     */
    private void toggleCancelFlag() {
        this.setIsCancelled(true);
        this.setIsFinished(true);
    }

    /**
     * This method switches the background color property and displayed text of the
     * <code>this.suspendButton</code> <code>JButton</code> based upon the <code>Status</code> enum
     * value.
     *
     * @param status <code>Status</code> enum value
     * @return void
     */
    private void showStatus(Status status) {
        switch(status){
            case RUNNING:
                this.suspendButton.setBackground(Color.GREEN);
                this.suspendButton.setText("Running");
                break;
            case SUSPENDED:
                this.suspendButton.setBackground(Color.YELLOW);
                this.suspendButton.setText("Suspended");
                break;
            case WAITING:
                this.suspendButton.setBackground(Color.ORANGE);
                this.suspendButton.setText("Waiting");
                break;
            case DONE:
                this.suspendButton.setBackground(Color.RED);
                this.suspendButton.setText("Done");
                break;
        }
    }

    /**
     * This method is called by each thread upon receiving a <code>notifyAll()</code> call centered
     * on the <code>this.getParentPort()</code> object. It checks to see if the <code>Ship</code> to
     * which the <code>Job</code> is attached has been moved from the queue to a pier. If it has
     * been moored, the method invokes <code>SeaPort.class.getResources</code> and provides it with
     * a list of required skills. The returned dock workers who match this skill are bundled and
     * assigned herein to the <code>this.workers</code> <code>ArrayList</code>.
     *
     * @return <code>boolean</code>
     */
    private synchronized boolean isWaiting() {

        // Declaration
        ArrayList<Person> dockWorkers;

        if (this.getParentPort().getQue().contains(this.getParentShip())) { // If in the queue
            return true; // Still waiting
        } else { // If not in queue (if docked)
            if (!this.getRequirements().isEmpty()) { // if ship requires workers (some don't)
                dockWorkers = this.getParentPort().getResources(this);
                if (dockWorkers == null) { // If not all needed workers are obtainable at the moment
                    return true; // we're still waiting
                } else { // if all requirements are met
                    this.setWorkers(dockWorkers); // start the workers on the job
                    return false; // We're no longer waiting
                }
            } else { // If the job requires no workers, we can simply proceed
                return false;
            }
        }
    }

    // Overriden methods

    /**
     * This method is the sole required method of the <code>Runnable</code> interface employed by
     * objects of the <code>Job</code> class to run threads denoting the job in question. At its
     * most elementary level, the method is divided into three major section based upon the
     * underlying ethos at play with all job threads, namely "retrieve worker(s) when available,
     * undertake jobs, return workers to pool." The organizational layout of this method was
     * inspired by the framework outlined in a page on the ancillary site linked in the Project 3
     * rubric, namely <a href="http://sandsduchon.org/duchon/2016/f/cs335/ideas.html">"Ideas"</a>.
     * 
     * @return void
     */
    @Override
    public void run() {

        // Declarations
        long time, startTime, stopTime;
        double timeNeeded;
        ArrayList<Boolean> statusList;
        Ship newShipToMoor;
        Dock parentDock;
        String workerLogLine;

        // Definitions
        time = System.currentTimeMillis();
        startTime = time;
        stopTime = time + 1000 * (long) this.getDuration();
        timeNeeded = stopTime - time;
        workerLogLine = "";

        /**
         * First <code>synchronized</code> block, employed to force threads to wait until acquiring
         * a place at a <code>Dock</code> to get resources and continue with their jobs. Every time
         * a ship's jobs are complete, the <code>notifyAll()</code> call provides the thread with
         * the ability to request resources anew until such time as the vessel is moored at a pier.
         */
        synchronized(this.getParentPort()) {
            while (this.isWaiting()) {
                try {
                    this.showStatus(Status.WAITING);
                    this.getParentPort().wait();
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e);
                }
            }
        }

        /**
         * The code contained between the two <code>synchronized</code> blocks does not require
         * exclusive access to the <code>SeaPort</code> resources, so it is not required to sit
         * within a block of its own. It progresses the state of the job in an attempt to duplicate
         * some CPU function, eventually changing the status to completed before entering the second
         * <code>synchronized</code> block.
         */
        while (time < stopTime && !this.getIsCancelled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }

            if (!this.getIsSuspended()) {
                this.showStatus(Status.RUNNING);
                time += 100;
                this.jobProgress.setValue((int) (((time - startTime) / timeNeeded) * 100));
            } else {
                this.showStatus(Status.SUSPENDED);
            }

            // Emergency hard stop forces old threads to stop before reaching status logging below
            if (this.getIsEndex()) {
                return;
            }
        }

        if (!this.getIsSuspended()) {
            this.jobProgress.setValue(100);
            this.showStatus(Status.DONE);
            this.setIsFinished(true);
        }

        /**
         * Second <code>synchronized</code> block latches back onto the <code>SeaPort</code> object
         * in order to return its dock workers to the port resource pool, move a new ship with jobs
         * to the pier on the completion of all <code>Job</code>s, and finally to notify all the
         * other waitlisted jobs of the thread's completion.
         */
        synchronized(this.getParentPort()) {

            if (!this.getRequirements().isEmpty() && !this.getWorkers().isEmpty()) {
                workerLogLine += this.getName() + " (" + this.getParentShip().getName()
                    + ") returning";

                for (int i = 0; i < this.getWorkers().size(); i++) {
                    if (i == 0) {
                        workerLogLine += " ";
                    } else if (i < this.getWorkers().size() - 1) {
                        workerLogLine += ", ";
                    } else {
                        workerLogLine += " & ";
                    }
                    workerLogLine += this.getWorkers().get(i).getName();
                }
                this.getStatusLog().append(workerLogLine + "\n");

                // Return workers at job's end
                this.getParentPort().returnResources(this.getWorkers());
            }


            statusList = new ArrayList<>();
            this.getParentShip().getJobs().forEach((job) -> {
                statusList.add(job.getIsFinished());
            });

            if (!statusList.contains(false)) {
                this.getStatusLog().append("Unmooring " + this.getParentShip().getName() + " from "
                    + this.getParentShip().getDock().getName() + " in "
                    + this.getParentPort().getName()+ "\n");

                while (!this.getParentPort().getQue().isEmpty()) {
                    newShipToMoor = this.getParentPort().getQue().remove(0);

                    if (!newShipToMoor.getJobs().isEmpty()) { // If this new vessel has jobs
                        parentDock = this.getParentShip().getDock(); // First dock is defined
                        parentDock.setShip(newShipToMoor); // Set the ship to the dock
                        newShipToMoor.setDock(parentDock); // Tell the ship of its new dock location

                        this.getStatusLog().append("Mooring " + newShipToMoor.getName() + " at "
                            + parentDock.getName() + " in " + this.getParentPort().getName()
                            + "\n");
                        break;
                    }
                }
            }

            // Relinquish access
            this.getParentPort().notifyAll();
        }
    }

    /**
     * @inheritdoc
     * @return stringOutput <code>String</code>
     */
    @Override
    public String toString() {
        String stringOutput;

        stringOutput = "\t\t" + super.toString() + "\n\t\tDuration: " + this.getDuration()
            + "\n\t\tRequirements:";

        if (this.getRequirements().isEmpty()) {
            stringOutput += "\n\t\t\t - None";
        } else {
            stringOutput = this.getRequirements().stream().map((requiredSkill) -> "\n\t\t\t - " + requiredSkill).reduce(stringOutput, String::concat);
        }

        return stringOutput;
    }
}
