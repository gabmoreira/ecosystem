package entities;

import location.*;
import sim.SimulationManager;

/**
 * Class that specifies all Living entities within a simulation.
 * @author MOREIRA Gabriel
 *
 */
public class Living extends Thing {
	
	public Living(SimulationManager associatedSim, Position position, double radius) {
		super(associatedSim, position, radius);
	}
	
}
