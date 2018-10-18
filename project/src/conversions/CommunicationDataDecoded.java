package conversions;

import java.util.ArrayList;

import entities.Prey;
import entities.TransitionPredator;
import entities.TransitionPrey;
import entities.Predator;

/**
 * This class specifies the data communicated between server and client.
 * @author MOREIRA Gabriel
 *
 */
public class CommunicationDataDecoded {

		public ArrayList<TransitionPrey> transitionPreys;
		public ArrayList<TransitionPredator> transitionPredators;
		public ArrayList<Prey> preys;
		public ArrayList<Predator> predators;
		public int clientID;
		
		/**
		 * Constructor to be used by the Server because it uses IDs and transitionLists.
		 * @param clientID is the ID of all the predators and Preys, i.e. the SimulationID.
		 * @param transitionPredators is the list of TransitionPredators.
		 * @param transitionPreys is the list of TransitionPreys.
		 * 
		 */
		public CommunicationDataDecoded(int clientID, ArrayList<TransitionPredator> transitionPredators, ArrayList<TransitionPrey> transitionPreys) {
			this.transitionPreys = transitionPreys;
			this.transitionPredators = transitionPredators;
			this.clientID = clientID;
		}
		
		/**
		 * Constructor to be used by the Client/Simulation because it deals with lists of predators and preys.
		 * @param predators is the list of predators.
		 * @param preys is the list of preys.
		 * 
		 */
		public CommunicationDataDecoded(ArrayList<Predator> predators, ArrayList<Prey> preys) {
			this.preys = preys;
			this.predators = predators;
		}
		
		/**
		 * 
		 * @return the list of transitionPredators.
		 * 
		 */
		public ArrayList<TransitionPredator> getTransitionPredators() {
			return transitionPredators;
		}
		
		/**
		 * 
		 * @return the list of transitionPreys.
		 * 
		 */
		public ArrayList<TransitionPrey> getTransitionPreys() {
			return transitionPreys;
		}
		
		/**
		 * 
		 * @return the list of predators.
		 * 
		 */
		public ArrayList<Predator> getPredators() {
			ArrayList<Predator> output = new ArrayList<>();
			for (Predator p : predators) {
				output.add(new Predator(p));
			}
			return output;
		}
		
		/**
		 * 
		 * @return the list of preys.
		 * 
		 */
		public ArrayList<Prey> getPreys() {
			ArrayList<Prey> output = new ArrayList<>();
			for (Prey p : preys) {
				output.add(new Prey(p));
			}
			return output;
		}
}
