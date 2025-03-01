# SeaPort Simulation Project

This project simulates a busy SeaPort environment where ships (cargo and passenger) arrive at ports, dock, and execute jobs that require the skills of dock workers. The project was developed as part of my undergrad coursework for CMSC335 (Object Oriented and Concurrent Programming) and uses Java (version 8 or later) with a Swing GUI for real‑time monitoring of the simulation.

## Project Structure

The source code is organized into the following packages and classes:

- **cmsc335.project4**
  - **Thing.java** – The base class for all objects in the world.
  - **Ship.java** – Represents a general ship and is extended by CargoShip and PassengerShip.
  - **CargoShip.java** – Extends Ship to represent cargo vessels with cargo-specific data.
  - **PassengerShip.java** – Extends Ship to represent passenger vessels.
  - **Dock.java** – Models a dock where a ship can be moored.
  - **SeaPort.java** – Represents a port with docks, ships, queued ships, and dock workers.
  - **World.java** – Parses the input file and assembles the complete simulation world.
  - **Job.java** – Represents a job that ships must complete; each job runs in its own thread.
  - **ResourcePool.java** – Manages collections of workers by their skill.
  - **ThingComparator.java** – Used for sorting objects (e.g., by weight, length, etc.).
  - **SeaPortProgram.java** – The main GUI class that builds the Swing interface and starts job threads.

## Requirements

- Java 8 or later
- A properly formatted input text file (with tokens: `port`, `dock`, `pship`, `cship`, `person`, `job`)
- Files placed in a directory structure matching the package declaration (`cmsc335/project4`)

## How to Compile and Run

1. **Compile the Project:**
   Make sure your current directory is the root directory containing the `cmsc335` folder, then run:
   ```bash
   javac cmsc335/project4/*.java
   ```

2. **Run the Program:**
   Launch the application using:
   ```bash
   java cmsc335.project4.SeaPortProgram
   ```

   The program initializes the GUI and waits for you to load a world file via the "Read" button.

## Test Cases and Screenshots

Below are five test cases with corresponding screenshots:

### Test Case 1: No Error Message if No World Loaded
Before any world is loaded, performing certain actions (such as clicking on the search button) should not display an error message.  
<img width="539" alt="SeaPort_Case1" src="https://github.com/user-attachments/assets/a201fec9-9925-4ed1-b8a1-594e46ef2ba8" />

### Test Case 2: “Cancel” on Shoetrees Ship
After loading a world that includes a ship named "Shoetrees," clicking the "Cancel" button on that ship’s job should cancel the job without throwing exceptions.  
<img width="541" alt="SeaPort_Case2" src="https://github.com/user-attachments/assets/0f618833-98b9-4c69-88c0-2e916436cd25" />

### Test Case 3: "Search" with No Input
If you press the "Search" button without providing any input in the search field, the program should display an error message indicating that no search term was entered.  
<img width="539" alt="SeaPort_Case3" src="https://github.com/user-attachments/assets/48857100-23fb-4f21-9376-38f376d0020d" />

### Test Case 4: "Suspend" All Active Jobs
Clicking the "Suspend" button for active jobs should suspend them. The GUI should update to show the status as "Suspended" for all active job threads.  
<img width="539" alt="SeaPort_Case4" src="https://github.com/user-attachments/assets/a4f4dd3c-5655-4fb6-9a42-5fdba7a0a072" />

### Test Case 5: "Search" with No Input (Repeated)
Repeating the "Search" action with no input should consistently produce the same error message as in Test Case 3.  
<img width="539" alt="SeaPort_Case5" src="https://github.com/user-attachments/assets/2b9f1716-c94a-4dcd-bc6e-c61efcc1d914" />

## Troubleshooting Tips

- **Null Checks:** If a dock does not have a ship, ensure that your code (especially in the `startAllJobs()` method in SeaPortProgram) properly checks for `null` before calling methods on the ship.
- **Input File Format:** Verify that your input file is correctly formatted. A missing or extra token can cause the simulation to initialize incorrectly.
- **Threading Issues:** Ensure that all GUI-related code runs on the Event Dispatch Thread (EDT) by using `SwingUtilities.invokeLater` in your main method if necessary.
- **Sorting Behavior:** The `compareTo` method in Thing is nonstandard. While it won’t cause compile errors, it may lead to unpredictable sorting behavior.

By following the guidelines above and reviewing the test case screenshots, you should be able to diagnose and resolve the issues preventing the program from running as expected.

