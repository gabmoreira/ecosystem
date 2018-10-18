package entities;

import location.Position;
import sim.SimulationManager;

/**
 * Class that specifies ANYTHING in the simulation, living or not. 
 * Has a Position, radius and an appended SimulationManager.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Thing {
	
	Position position;
	double radius;
	SimulationManager associatedSim;
	
	/**
	 * 
	 * @param associatedSim is the Associated SimulationManager.
	 * @param position is this thing's position.
	 * @param radius is thing thing's radius.
	 * 
	 */
	public Thing(SimulationManager associatedSim, Position position, double radius) {
		this.associatedSim = associatedSim;
		this.position = position;
		this.radius = radius;
	}
	
	/**
	 * 
	 * @return this thing's position.
	 * 
	 */
	public Position getPosition() {
		return position;
	}
	
	
	/**
	 * Returns the Radius.
	 * @return this Thing's radius.
	 * 
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Sets the associated Simulation.
	 * @param associatedSim is the simulation to append to this Thing.
	 * 
	 */
	public void setAssociatedSim(SimulationManager associatedSim) {
		this.associatedSim = associatedSim;
	}
	
} 
