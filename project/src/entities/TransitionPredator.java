package entities;

import entities.Predator;
import sim.Simulation;

/**
 * This class specifies an Predator with an ID to facilitate the handling by the server.
 * @author MOREIRA Gabriel
 *
 */
public class TransitionPredator {
	
	private Predator p;
	private int clientID;
	
	/**
	 * Default Constructor. 
	 * -1 means the TransitionPredator isn't valid.
	 * 
	 */
	public TransitionPredator() {
		clientID = -1;
		p = null;
	}
	
	/**
	 * 
	 * @param clientID is the simulation ID to which the predator is appended. -1 is the predator was just handled by the server.
	 * @param p is the Predator.
	 * 
	 */
	public TransitionPredator(int clientID, Predator p) {
		this.clientID = clientID;
		this.p = p;
	}
	
	/**
	 * 
	 * @return the object Predator.
	 * 
	 */
	public Predator getTransitionPredator() {
		return p;
	}
	
	/**
	 * 
	 * @return this Predator's ID.
	 * 
	 */
	public int getClientID() {
		return clientID;
	}
	
	/**
	 * Sets this predators ID. -1 if the predator was handled by the server. Otherwise carries the simulation ID.
	 * @param id is the ID to be set.
	 * 
	 */
	public void setClientID(int id) {
		this.clientID = id;
	}

	/**
	 * Corrects the Predator's position when it changes squares.
	 * 
	 */
	public void positionCorrection() {
		if (p.getPosition().getX() >= Simulation.SPACE_SIZE)
			p.getPosition().setX(p.getPosition().getX() - Simulation.SPACE_SIZE);
		else if (p.getPosition().getY() >= Simulation.SPACE_SIZE)
			p.getPosition().setY(p.getPosition().getY() - Simulation.SPACE_SIZE);
		else if (p.getPosition().getX() <= 0) 
			p.getPosition().setX(p.getPosition().getX() + Simulation.SPACE_SIZE);
		else if (p.getPosition().getY() <= 0)
			p.getPosition().setY(p.getPosition().getY() + Simulation.SPACE_SIZE);
	}
	
	/**
	 * Returns the Predator as a string.
	 * 
	 */
	@Override
	public String toString() {
		return p.toString();
	}
}

