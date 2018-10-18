package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import conversions.CommunicationDataDecoded;
import conversions.Conversions;
import entities.Predator;
import entities.Prey;
import entities.TransitionPredator;
import entities.TransitionPrey;
import net.NetUser;
import sim.Simulation;

/**
 * This class specifies the Server to receive and communicate data from the simulation clients.
 * This Server can handle 1, 2, 3 or 4 clients at once.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class ServerMain {

	private static ArrayList<TransitionPredator> transitionPredators;	
	private static ArrayList<TransitionPrey> transitionPreys;
	private static CommunicationDataDecoded comDataReceived;
	private static ArrayList<Predator> predatorsToCurrentClient;
	private static ArrayList<Prey> preysToCurrentClient;
	private static ArrayList<TransitionPredator> transitionPredatorRemoval;
	private static ArrayList<TransitionPrey> transitionPreyRemoval;
	private static ArrayList<Integer> clients;
	private static int numClients;
	private static int clientID;
	private static boolean newClient;
	
	/**
	 * Starts the ArrayList containing the future clients IDs.
	 * Real clients have positive IDs. Hence the -1 means that the client isn't valid.
	 * 
	 */
	public static void startClientList() {
		clients = new ArrayList<Integer>();		
		clients.add(-1);
		clients.add(-1);
		clients.add(-1);
		clients.add(-1);
		numClients = 0;
	}
	
	/**
	 * Determines if current client is a new client.
	 * 
	 */
	public static void checkIfNewClient() {
		newClient = true;
		for(int c : clients) {
			if (clientID == c) {
				newClient = false;
			}
		}	
	}
	
	/**
	 * Procedure to register a new client.
	 * 
	 */
	public static void signUpNewClient() {
		clients.set(numClients, clientID);
		System.out.println("Server> New client connected! " + "Clients: " + (numClients + 1));
		numClients++;
	}
	
	/**
	 * Clears the arrays containing the predators and preys to send to the client.
	 * 
	 */
	private static void clearOutputBuffer() {
		preysToCurrentClient.clear();
		predatorsToCurrentClient.clear();
	}
	
	/**
	 * Removes from the transition arrays the TransitionPrey and TransitionPredator that were already handled.
	 * 
	 */
	private static void removeHandledTransitions() {
		transitionPredatorRemoval = new ArrayList<>();
		transitionPreyRemoval = new ArrayList<>();
		
		for(TransitionPredator p : transitionPredators) {
			if(p.getClientID() == -1) {
				transitionPredatorRemoval.add(p);
			}
		}
		transitionPredators.removeAll(transitionPredatorRemoval);
		
		for(TransitionPrey p : transitionPreys) {
			if(p.getClientID() == -1) {
				transitionPreyRemoval.add(p);				
			}
		}
		transitionPreys.removeAll(transitionPreyRemoval);
	}
	
	/**
	 * Method to handle one client.
	 * 
	 */
	private static void handleOneClient() {
		for(TransitionPredator p : transitionPredators) {
			p.positionCorrection();
			predatorsToCurrentClient.add(p.getTransitionPredator());
		}
		for(TransitionPrey p : transitionPreys) {
			p.positionCorrection();
			preysToCurrentClient.add(p.getTransitionPrey());
		}
		transitionPredators.clear();
		transitionPreys.clear();
	}
	
	/**
	 * Method to handle two clients.
	 * 
	 */
	private static void handleTwoClients() {
		if(clientID == clients.get(0)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getY() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getX() <= 0 || p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getY() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getX() <= 0 || p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}
		}
		else if(clientID == clients.get(1)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getY() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getX() <= 0 || p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getY() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getX() <= 0 || p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}
		}
	}

	/**  
	 * Method to handle three clients
	 * 
	 */
	private static void handleThreeClients() {
		if(clientID == clients.get(0)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)) {
					p.positionCorrection();
					predatorsToCurrentClient.add(p.getTransitionPredator());
					p.setClientID(-1);
				}
				else if(p.getClientID() == clients.get(2) && (p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getY() <= 0)) {
					p.positionCorrection();
					predatorsToCurrentClient.add(p.getTransitionPredator());
					p.setClientID(-1);
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
					p.setClientID(-1);
				}
				else if(p.getClientID() == clients.get(2) && (p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getY() <= 0)) {
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
					p.setClientID(-1);
				}
			}
		}
		else if(clientID == clients.get(1)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getY() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getX() <= 0 || p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getY() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getX() <= 0 || p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}	
		}
		else if(clientID == clients.get(2)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(2) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getY() <= 0 || p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(2) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getY() <= 0 || p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}	
		}
	}

	/**
	 * Method to handle four clients.
	 * 
	 */
	private static void handleFourClients() {
		if(clientID == clients.get(0)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)) {
					p.positionCorrection();
					predatorsToCurrentClient.add(p.getTransitionPredator());
					p.setClientID(-1);
				}
				else if(p.getClientID() == clients.get(2) && (p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getY() <= 0)) {
					p.positionCorrection();
					predatorsToCurrentClient.add(p.getTransitionPredator());
					p.setClientID(-1);
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
					p.setClientID(-1);
				}
				else if(p.getClientID() == clients.get(2) && (p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getY() <= 0)) {
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
					p.setClientID(-1);
				}
			}
		}
		else if(clientID == clients.get(1)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(3) && (p.getTransitionPredator().getPosition().getY() <= 0 || p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(3) && (p.getTransitionPrey().getPosition().getY() <= 0 || p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}	
		}
		else if(clientID == clients.get(3)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(2) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(1) && (p.getTransitionPredator().getPosition().getY() <= 0 || p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(2) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(1) && (p.getTransitionPrey().getPosition().getY() <= 0 || p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}	
		}
		else if(clientID == clients.get(2)) {
			for(TransitionPredator p : transitionPredators) {
				if(p.getClientID() == clients.get(3) && (p.getTransitionPredator().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPredator().getPosition().getX() <= 0)){
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPredator().getPosition().getY() <= 0 || p.getTransitionPredator().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.positionCorrection();
					p.setClientID(-1);
					predatorsToCurrentClient.add(p.getTransitionPredator());
				}
			}
			for(TransitionPrey p : transitionPreys) {
				if(p.getClientID() == clients.get(3) && (p.getTransitionPrey().getPosition().getX() >= Simulation.SPACE_SIZE || p.getTransitionPrey().getPosition().getX() <= 0)) {
					p.positionCorrection();
					p.setClientID(-1);
					preysToCurrentClient.add(p.getTransitionPrey());
				}
				else if(p.getClientID() == clients.get(0) && (p.getTransitionPrey().getPosition().getY() <= 0 || p.getTransitionPrey().getPosition().getY() >= Simulation.SPACE_SIZE)) {
					p.setClientID(-1);
					p.positionCorrection();
					preysToCurrentClient.add(p.getTransitionPrey());
				}
			}	
		}
	}
	
	/**
	 * Main method to run the Server.
	 * @param args are not implemented.
	 * 
	 */
	public static void main(String[] args) {
		int port = 6789;	
		transitionPredators = new ArrayList<>();	
		transitionPreys = new ArrayList<>();
		predatorsToCurrentClient = new ArrayList<>();
		preysToCurrentClient = new ArrayList<>();
		predatorsToCurrentClient = new ArrayList<>();
		preysToCurrentClient = new ArrayList<>();
		
		startClientList();
	
		System.out.println("Server> Running...");
		while (true) {	
			ServerSocket listenSocket;
			try {
				listenSocket = new ServerSocket(port);
				Socket connectedSocket = listenSocket.accept();
				InputStream inputStream = connectedSocket.getInputStream();
				OutputStream outputStream = connectedSocket.getOutputStream();
		
				byte[] dataReceived = new byte[4096*8]; 
				inputStream.read(dataReceived);

				/** converts the received data (in bytes) to a double array */
				double[] val = Conversions.byteToDouble(dataReceived);
				
				/** converts the double array containing the data to format CommunicationDataDecoded */		
				comDataReceived = Conversions.decodeCommunicationData(val, NetUser.SERVER, null);
				clientID = comDataReceived.clientID;
				
				checkIfNewClient();
				
				/** adds the new animals that are transiting between squares if the client is one of the max 4 handled simulations */
				if(!newClient || (newClient && numClients < 4)) {
					transitionPredators.addAll(comDataReceived.getTransitionPredators());
					transitionPreys.addAll(comDataReceived.getTransitionPreys());
				}
													
				/** If new client - sign him up. Otherwise handle the number of clients we already have */
				if(newClient && numClients < 4) {
					signUpNewClient();
				}
				else if(newClient && numClients >= 4) {
					System.out.println("Server> Cannot handle more than 4 simulations at once! Please close the 5th simulation.");
					
					/** sends meaningless data to the client */
					double[] emptyData = {-1, 0, 0};
					outputStream.write(Conversions.doubleToByte(emptyData));
				}
				else {
					switch(numClients) {
					case 1:
						handleOneClient();
						break;
					case 2:
						handleTwoClients();
						break;
					case 3:
						handleThreeClients();
						break;
					case 4:
						handleFourClients();
						break;
					}			
				}
				outputStream.write(Conversions.encodeCommunicationData(-1, predatorsToCurrentClient, preysToCurrentClient));
				removeHandledTransitions();
				clearOutputBuffer();
				listenSocket.close();
			} catch (IOException e) {
				System.out.println("Server> Server encountered a problem. Shutting down.");
				System.exit(1);
			}			
		}
	}
}

