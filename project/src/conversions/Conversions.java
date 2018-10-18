package conversions;

import java.util.ArrayList;

import entities.*;
import location.Position;
import net.NetUser;
import entities.TransitionPredator;
import entities.TransitionPrey;
import sim.SimulationManager;

/**
 * 
 * This class contains static methods to transform doubles to bytes, bytes to doubles, and to encode and decode the server-client communication data.
 * @author MOREIRA Gabriel
 *
 */
public class Conversions {

	/**
	 * Converts double array to byte array.
	 * @param dataDouble is the array containing the doubles to be converted.
	 * @return The byte array corresponding to the double array.
	 * 
	 */
	public static byte[] doubleToByte(double[] dataDouble) {
		byte[] dataBytes = new byte[dataDouble.length*8];
		byte[] aux = new byte[8];
		for (int i = 0; i < dataDouble.length; i++) {
				java.nio.ByteBuffer.wrap(aux).putDouble(dataDouble[i]);
				for (int j = i * 8; j < (i+1)*8; j++) {
					dataBytes[j] = aux[j - i*8];
				}
		}
		return dataBytes;
	}
	
	/**
	 * Converts byte array to double array.
	 * @param dataBytes is the array containing the bytes to be converted.
	 * @return The double array corresponding to the byte array.
	 * 
	 */
	public static double[] byteToDouble(byte[] dataBytes) {
		double[] dataDouble = new double[dataBytes.length / 8];
		byte[] aux = new byte[8];
		for (int i = 0; i < dataDouble.length; i++) {
			for (int j = 0; j < 8; j++) {
				aux[j] = dataBytes[j+i*8];
			}
			dataDouble[i] = java.nio.ByteBuffer.wrap(aux).getDouble();
		}
		return dataDouble;
	}
	
	
	/**
	 * Takes the information provided as the argument and returns the byte array with that data.
	 * Used to communicate exiting and entering Animals in the Simulations.
	 * @param id is the SimulationID.
	 * @param predators is the array of predators.
	 * @param preys is the array of preys.
	 * @return the byte array containing the information (id, preys and predators).
	 * 
	 */
	public static byte[] encodeCommunicationData(int id, ArrayList<Predator> predators, ArrayList<Prey> preys) {
		double[] dataToServer = new double[3 + 5*predators.size() + 5*preys.size()];
		dataToServer[0] = (double) id;
		dataToServer[1] = predators.size();
		dataToServer[2] = preys.size();
		int j = 3;
		int k = j;
		for (int i = 0; i < predators.size() * 2; i+=2) {
			dataToServer[k + i] = predators.get(i/2).getPosition().getX();
			dataToServer[k + i + 1] = predators.get(i/2).getPosition().getY();
			j+=2;
		}
		k = j;
		for (int i = 0; i < preys.size() * 2; i+=2) {
			dataToServer[k + i] = preys.get(i/2).getPosition().getX();
			dataToServer[k + i + 1] = preys.get(i/2).getPosition().getY();
			j+=2;
		}
		k = j;
		for (int i = 0; i < predators.size() * 3; i+=3) {
			dataToServer[k + i] = predators.get(i/3).getAge();
			dataToServer[k + i + 1] = predators.get(i/3).getHunger();
			dataToServer[k + i + 2] = predators.get(i/3).getFatigue();
			j+=3;
		}
		k = j;
		for (int i = 0; i < preys.size() * 3; i+=3) {
			dataToServer[k + i] = preys.get(i/3).getAge();
			dataToServer[k + i + 1] = preys.get(i/3).getHunger();
			dataToServer[k + i + 2] = preys.get(i/3).getFatigue();
			j+=3;
		}	
		return doubleToByte(dataToServer);
	}
	
	/**
	 * Takes an array of doubles and retrieves the information relative to the array of predators, preys and the simulationID where that data came from.
	 * @param val is the array containing the information to be decoded.
	 * @param user specifies whether this method is used by CLIENT or SERVER.
	 * @param associatedSim specifies the associated simulation to which we want to append the data.
	 * @return a CommunicationDataDecoded Object containing the data.
	 * 
	 */
	public static CommunicationDataDecoded decodeCommunicationData(double[] val, NetUser user, SimulationManager associatedSim) {
		int clientID = (int) val[0];
		int numTransitionPredators = (int) val[1];
		int numTransitionPreys = (int) val[2];
		
		ArrayList<Position> positions = new ArrayList<>();
		ArrayList<Integer> ages = new ArrayList<>();
		ArrayList<Double> fatigues = new ArrayList<>();
		ArrayList<Double> hungers = new ArrayList<>();
		ArrayList<TransitionPredator> transitionPredators = new ArrayList<>();
		ArrayList<TransitionPrey> transitionPreys = new ArrayList<>();
		ArrayList<Predator> predators = new ArrayList<>();
		ArrayList<Prey> preys = new ArrayList<>();

		for(int i = 0; i < numTransitionPredators*2; i+=2) {
			int x = (int) val[i + 3];
			int y = (int) val[i + 4];
			positions.add(new Position(x, y));
		}		
		for(int i = 0; i < numTransitionPredators*3; i+=3) {
			int age = (int) val[3 + 2*numTransitionPredators + 2*numTransitionPreys + i];
			double fatigue = val[4 + 2*numTransitionPredators + 2*numTransitionPreys + i];
			double hunger = val[5 + 2*numTransitionPredators + 2*numTransitionPreys + i];
			ages.add(age);
			hungers.add(hunger);
			fatigues.add(fatigue);
		}		
		for (int i = 0; i < numTransitionPredators; i++) {
			Predator p = new Predator(associatedSim, positions.get(i), fatigues.get(i), ages.get(i), hungers.get(i), Predator.AVERAGE_ADULT_RADIUS);
			if (user == NetUser.SERVER)
				transitionPredators.add(new TransitionPredator(clientID, p));
			else
				predators.add(p);
		}
		
		positions.clear();
		hungers.clear();
		ages.clear();
		fatigues.clear();	
		
		for(int i = 0; i < numTransitionPreys*2; i+=2) {
			int x = (int) val[i + 3 + 2*numTransitionPredators];
			int y = (int) val[i + 4 + 2*numTransitionPredators];
			positions.add(new Position(x, y));
		}	
		for(int i = 0; i < numTransitionPreys*3; i+=3) {
			int age = (int) val[3 + 5*numTransitionPredators + 2*numTransitionPreys + i];
			double fatigue = val[4 + 5*numTransitionPredators + 2*numTransitionPreys + i];
			double hunger = val[5 + 5*numTransitionPredators + 2*numTransitionPreys + i];
			ages.add(age);
			hungers.add(hunger);
			fatigues.add(fatigue);
		}	
		for (int i = 0; i < numTransitionPreys; i++) {
			Prey p = new Prey(associatedSim, positions.get(i), fatigues.get(i), ages.get(i), hungers.get(i), Prey.AVERAGE_PREY_RADIUS);
			if (user == NetUser.SERVER)
				transitionPreys.add(new TransitionPrey(clientID, p));
			else
				preys.add(p);
		}	
		return (user == NetUser.SERVER) ? new CommunicationDataDecoded(clientID, transitionPredators, transitionPreys) : new CommunicationDataDecoded(predators, preys);
	}
}
