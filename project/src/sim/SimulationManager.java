package sim;


import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import conversions.CommunicationDataDecoded;
import conversions.Conversions;
import entities.*;
import location.Position;
import net.ClientTCP;
import net.NetUser;

/**
 * This class specifies the simulation manager that contains and handles all entities present in the simulation.
 * Does as well the communication with the server to receive and send animals.
 * 
 * @author MOREIRA Gabriel
 * 
 */
public class SimulationManager {
	
	SecureRandom rnd;
	
	/**
	 * Lists of the entities present in the simulation
	 * 
	 */
	private ArrayList<Predator> predators;
	private ArrayList<Prey> preys;
	private ArrayList<Plant> plants;	
	private ArrayList<Predator> predatorsDeathList;
	private ArrayList<Prey> preysDeathList;
	private ArrayList<Predator> predatorsBirthList;
	private ArrayList<Prey> preysBirthList;
	private ArrayList<Prey> exitingPreys;
	private ArrayList<Predator> exitingPredators;
	private ArrayList<Prey> incomingPreys;
	private ArrayList<Predator> incomingPredators;
	private CommunicationDataDecoded comDataReceived;
	boolean isTerminated;
	private int simulationID;
	

	/**
	 * Creates a new SimulationManager.
	 * @throws IOException if there is a problem communicating with the Server.
	 * 
	 */
	public SimulationManager() throws IOException {
		predators = new ArrayList<>();
		predatorsDeathList = new ArrayList<>();
		predatorsBirthList = new ArrayList<>();
		exitingPreys = new ArrayList<>();
		exitingPredators = new ArrayList<>();
		incomingPreys = new ArrayList<>();
		incomingPredators = new ArrayList<>();
		preys = new ArrayList<>();
		preysDeathList = new ArrayList<>();
		preysBirthList = new ArrayList<>();
		plants = new ArrayList<>();
		rnd = new SecureRandom();
		isTerminated = false;
		simulationID = (int)(rnd.nextDouble()*100);
		double[] initData = {simulationID, 24};
		ClientTCP.communicateWithServer(Conversions.doubleToByte(initData));
	}
	
	/**
	 * Creates random entities and adds them to the simulation.
	 * 
	 */
	public void randomStart() {	
		int numPredators = 26; 
		int numPreys = 28; 
		int numPlants = (int) (rnd.nextDouble()*10 + 80);
		
		for (int i = 0; i < numPreys; i++) {
			preys.add(Prey.randomPrey(this));
		}
		for (int i = 0; i < numPredators; i++) {
			predators.add(Predator.randomPredator(this));
		}
		for (int i = 0; i < numPlants; i++) {
			plants.add(Plant.randomPlant(this));
		}		
	}
		
	/** 
	 *  Adds a Plant to the simulation.
	 * @param plant is the Plant to be added.
	 * 
	 */
	public void addPlant(Plant plant) {
		plants.add(plant);
	}
	
	/** 
	 *  Adds a Prey to the simulation.
	 * @param prey is the Prey to be added.
	 * 
	 */
	public void addPrey(Prey prey) {
		preys.add(prey);
	}
	
	/**
	 * Adds a Predator to the Simulation.
	 * @param predator is the Predator to be added.
	 * 
	 */
	public void addPredator(Predator predator) {
		predators.add(predator);
	}
	
	/**
	 * Goes through every entity within the simulation and runs them once.
	 * @throws IOException if there's a problem communicating with server.
	 * 
	 */
	public void updateEntities() throws IOException {
		updateExitingPredators();
		updateExitingPreys();
		
		for(Predator p : predatorsDeathList) {
			predators.remove(p);
		}
		for(Prey p : preysDeathList) {
			preys.remove(p);
		}		
		for(Predator p : exitingPredators) {
			predators.remove(p);
		}
		for(Prey p : exitingPreys) {
			preys.remove(p);
		}
		for(Predator p : predatorsBirthList) {
			predators.add(p);
		}
		for(Prey p : preysBirthList) {
			preys.add(p);
		}
		for(Predator p : incomingPredators) {
			predators.add(p);
		}
		for(Prey p : incomingPreys) {
			preys.add(p);
		}
		
		incomingPredators.clear();
		incomingPreys.clear();
		predatorsDeathList.clear();
		predatorsBirthList.clear();
		preysDeathList.clear();
		preysBirthList.clear();
	
		for(Predator p : predators) {
			p.satisfyNeeds();
			p.growUp();
		}
		for(Prey p : preys) {
			p.satisfyNeeds();
			p.growUp();
		}
		
		/** adds some new plants if the number of plants in the simulation is low*/
		int numNewPlants;
		if (rnd.nextDouble() > 0.9) {
			if (plants.size() < 40)
				numNewPlants = 45;
			else
				numNewPlants = 16;
			for (int i = 0; i < numNewPlants; i++)
				plants.add(Plant.randomPlant(this));
		}
		
		/** sends and receives data from and to server */
		double[] val = ClientTCP.communicateWithServer(Conversions.encodeCommunicationData(simulationID, exitingPredators, exitingPreys));
		comDataReceived = Conversions.decodeCommunicationData(val, NetUser.CLIENT, this);
		incomingPredators = comDataReceived.getPredators();
		incomingPreys = comDataReceived.getPreys();		
		exitingPreys.clear();
		exitingPredators.clear();
	}
	
	
	/**
	 * Gets array of the predators exiting the domain.
	 * @return the array of exiting predators.
	 * 
	 */
	private void updateExitingPredators() {
		for(Predator p : predators) {
			if (p.getPosition().isOutOfBounds())
				exitingPredators.add(p);
		}
	}
	
	/**
	 * Gets array of the preys exiting the domain.
	 * @return the array of exiting preys.
	 * 
	 */
	private void updateExitingPreys() {
		for(Prey p : preys ) {
			if (p.getPosition().isOutOfBounds()) {
				exitingPreys.add(p);
			}
		}
	}
	
	/**
	 * Gives us all the predators the animal is able to see from his current position.
	 * @param p1 is the position of the observing animal.
	 * @param radius the animal's eyesight range.
	 * @return the list of predators the animal sees.
	 * 
	 */
	public ArrayList<Predator> getPredatorsAroundThis(Position p1, double radius) {
		ArrayList<Predator> output = new ArrayList<Predator>();
		for (Predator p : predators) {
			if (p.getPosition().distance(p1) <= radius) {
				if (output.isEmpty())
					output.add(p);
				else {
					for (int i = 0; i < output.size(); i++) {
						if (output.get(i).getPosition().distance(p1) >= p.getPosition().distance(p1)) {
							output.add(i, p);
							break;
						}
					}
				}
			}
		}
		return output;
	}
	
	/**
	 * Gives us all the preys the animal is able to see from his current position.
	 * @param p1 is the position of the observing animal.
	 * @param radius is the animal's eyesight range.
	 * @return the list of predators the animal sees.
	 * 
	 */
	public ArrayList<Prey> getPreysAroundThis(Position p1, double radius) {
		ArrayList<Prey> output = new ArrayList<Prey>();
		for (Prey p : preys) {
			if (p.getPosition().distance(p1) <= radius) {
				if (output.isEmpty())
					output.add(p);
				else {
					for (int i = 0; i < output.size(); i++) {
						if (output.get(i).getPosition().distance(p1) >= p.getPosition().distance(p1)) {
							output.add(i, p);
							break;
						}
					}
				}	
			}
		}	
		return output;
	}
	
	/**
	 * Gives us all the plants the animal is able to see from his current position.
	 * @param p1 is the position of the observing animal.
	 * @param radius is the animal's eyesight range.
	 * @return the list of plants the animal sees.
	 * 
	 */
	public ArrayList<Plant> getPlantsAroundThis(Position p1, double radius) {
		ArrayList<Plant> output = new ArrayList<Plant>();
		for (Plant p : plants) {
			if (p.getPosition().distance(p1) <= radius) {
				if(output.isEmpty())
					output.add(p);
				else {
					for (int i = 0; i < output.size(); i++) {
						if (output.get(i).getPosition().distance(p1) >= p.getPosition().distance(p1)) {
							output.add(i, p);
							break;
						}
					}
				}		
			}
		}
		return output;
	}
	
	/**
	 * Gives the list of predators inbound to this simulation.
	 * @return the list of incomingPredators.
	 * 
	 */
	public ArrayList<Predator> getIncomingPredators() {
		return incomingPredators;
	}
	
	/**
	 * Gives the list of preys inbound to this simulation.
	 * @return the list of incomingPreys.
	 * 
	 */
	public ArrayList<Prey> getIncomingPreys() {
		return incomingPreys;
	}
	
	/**
	 * Gives us the list of predators.
	 * @return the list of predators in the current simulation.
	 * 
	 */
	public ArrayList<Predator> getPredators() {
		return predators;
	}
	
	/**
	 * Gives us the list of preys.
	 * @return the list of preys in the current simulation.
	 * 
	 */
	public ArrayList<Prey> getPreys() {
		return preys;
	}
	
	/**
	 * Gives us the list of predators that died in the last step.
	 * @return the list of predators in the current simulation.
	 * 
	 */
	public ArrayList<Predator> getPredatorsDeathList() {
		return predatorsDeathList;
	}
	
	/**
	 * Gives us the list of preys that died in the last step.
	 * @return the list of preys that died.
	 * 
	 */
	public ArrayList<Prey> getPreysDeathList() {
		return preysDeathList;
	}
	
	/**
	 * Gives us the list of predators that were born in the last step.
	 * @return the list of predators that were born.
	 * 
	 */
	public ArrayList<Predator> getPredatorsBirthList() {
		return predatorsBirthList;
	}
	
	/**
	 * Gives us the list of preys that were born in the last step.
	 * @return the list of preys that were born.
	 * 
	 */
	public ArrayList<Prey> getPreysBirthList() {
		return preysBirthList;
	}
	
	/**
	 * Gives us the list of plants.
	 * @return the list of plants in the current simulation.
	 * 
	 */
	public ArrayList<Plant> getPlants() {
		return plants;
	}
	
	/**
	 * Returns the SimulationID.
	 * @return the simulationID.
	 * 
	 */
	public int getID() {
		return simulationID;
	}
	
	/**
	 * Textual representation of the Simulation Manager.
	 * 
	 */
	@Override
	public String toString() {
		return "Simulation> \n" + "Preys: \n" + preys.toString() + "\n" + "Predators: \n" + predators.toString() + "\n"
		+ "Plants: " + plants.toString() + "\n\n";
	}
	
}
