package entities;

import entities.Prey;
import sim.Simulation;

/**
 * This class specifies a Transition Prey. It's a Prey with an ID to facilitate 
 * the handling by the server.
 *
 * @author MOREIRA Gabriel
 *
 */
public class TransitionPrey {
	
	private Prey p;
	private int clientID;
	
	/**
	 * Default Constructor
	 * -1 means the TransitionPrey isn't valid.
	 */
	public TransitionPrey() {
		clientID = -1;
		p = null;
	}
	
	/**
	 * 
	 * @param clientID is the simulation ID to which the prey is appended. -1 is the prey was just handled by the server.
	 * @param p is the prey.
	 */
	public TransitionPrey(int clientID, Prey p) {
		this.clientID = clientID;
		this.p = p;
	}
	
	/**
	 * 
	 * @return the Object Prey.
	 * 
	 */
	public Prey getTransitionPrey() {
		return p;
	}
	
	/**
	 * 
	 * @return this prey's ID.
	 */
	public int getClientID() {
		return clientID;
	}

	/**
	 * Sets this Prey's ID. -1 if the prey was handled by the server. Otherwise carries the simulation ID.
	 * @param id the ID to be set.
	 * 
	 */
	public void setClientID(int id) {
		this.clientID = id;
	}
	
	/**
	 * Textual representation of the prey.
	 * 
	 */
	@Override
	public String toString(){
		return p.toString();
	}
	
	/**
	 * Corrects the Prey's position when it changes squares.
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
	
}
