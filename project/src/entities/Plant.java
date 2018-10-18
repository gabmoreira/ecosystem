package entities;

import java.security.SecureRandom;

import location.*;
import sim.Simulation;
import sim.SimulationManager;

/**
 * Class that specifies a plant that has a position and radius and can be eaten by animals
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Plant extends Living{
	
	/**
	 * Creates a new Plant.
	 * @param associatedSim is the associated SimulationManager.
	 * @param position is the Plant's Position.
	 * @param radius is the Plant's radius.
	 * 
	 */
	public Plant(SimulationManager associatedSim, Position position, double radius) {
		super(associatedSim, position, radius);
	}
	
	/**
	 * Creates a random Plant.
	 * @param associatedSim is the associated SimulationManager
	 * @return The new Plant.
	 * 
	 */
	public static Plant randomPlant(SimulationManager associatedSim) {
		SecureRandom rnd = new SecureRandom();
		Position rndPos = new Position(rnd.nextDouble()*(Simulation.SPACE_SIZE + 30), rnd.nextDouble()*(Simulation.SPACE_SIZE + 30));
		double radius = 7;
		return new Plant(associatedSim, rndPos, radius);
	}
	
	/**
	 * Returns this Plant's radius.
	 * @return this Plant's radius.
	 * 
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Returns this Plant in a string format.
	 * @return this Plant in a string.
	 * 
	 */
	@Override
	public String toString() {
		return "Plant. Position: " + this.getPosition() + ". Radius: " + this.getRadius();
	}
}
