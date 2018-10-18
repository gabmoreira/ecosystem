package sim;

import java.io.IOException;
import java.util.ArrayList;

import entities.Plant;
import entities.Predator;
import entities.Prey;
import entities.Thing;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controls the behavior of the elements displayed by the graphical display.
 * 
 * For simplicity, the simulation elements simply consist in Circle instances, that
 * can be directly displayed by a graphical display.
 * 
 * Here the dynamic behavior is simplistic: every time update() is called, a 
 * completely random position is computed for each simulation element.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Simulation {

	/** The elements are positioned in a SPACE_SIZE x SPACE_SIZE 2D space */
	public static final int SPACE_SIZE = 400;

	/** Updates per second (Hz) */
	public static final int UPDATE_RATE_HZ = 21;

	/** Complete simulation duration (s) */
	public static final int DURATION_S = 240;

	/** The color of the background */
	public static final Color BACKGROUND = Color.rgb(1, 11, 61);
	
	/** The color of a predator */
	public static final Color PREDATOR_COLOR = Color.SALMON;
	
	/** The color of a prey */
	public static final Color PREY_COLOR = Color.MOCCASIN;
	
	/** The color of a plant */
	public static final Color PLANT_COLOR = Color.rgb(14, 163, 41);
	
	/** The color of a water */
	public static final Color WATER_COLOR = Color.AQUAMARINE;

	/** Keeps track of the number of updates since model creation */ 
	private int nbUpdates;
	SimulationManager sim;
	
	/** Elements, i.e. circles under control */
	private ArrayList<Circle> elements;
	
	/** Simulation entities under control */
	private ArrayList<Thing> entities;
	
	/** Returns the list of elements of the model */
	public ArrayList<Circle> getElements() {
		return elements;
	}

	/**
	 * Creates a model with the specified amount of elements.
	 * @throws IOException 
	 */
	public Simulation() throws IOException {
		
		sim = new SimulationManager();
		sim.randomStart();
		
		System.out.println("Simulation> Initializing simulation...");
		
		nbUpdates = 0;
		int elementsSize = sim.getPredators().size() + sim.getPreys().size() + sim.getPlants().size();
		elements = new ArrayList<>(elementsSize);
		entities = new ArrayList<>(elementsSize);
		getElements(elements, entities);
		
	}
	
	/**
	 * Gets all the entities from the Simulation Manager.
	 * @param elements is the list of elements, i.e. circles to represent graphically.
	 * @param entities is the list of the entities in the current simulation.
	 * 
	 */
	private void getElements(ArrayList<Circle> elements, ArrayList<Thing> entities)
	{
		for (Predator p : sim.getPredators()) {
			Circle c = new Circle(p.getRadius(), PREDATOR_COLOR);
			c.setOpacity(0.72);
			elements.add(c);
			entities.add(p);
		}
		for (Prey p : sim.getPreys()) {
			Circle c = new Circle(p.getRadius(), PREY_COLOR);
			c.setOpacity(0.62);
			elements.add(c);
			entities.add(p);
		}
		for (Plant p : sim.getPlants()) {
			Circle c = new Circle(p.getRadius(), PLANT_COLOR);
			c.setOpacity(0.18);
			elements.add(c);
			entities.add(p);
		}
	}

	/**
	 * Update the model elements, by computing a new random position for
	 * each element. This method is periodically called by the graphical
	 * display.
	 * @throws IOException 
	 * 
	 */
	public void update() throws IOException {
		nbUpdates++;
		
		elements.clear();
		entities.clear();
		
		getElements(elements, entities);

		for (int i = 0; i < elements.size(); i++) {	
			elements.get(i).setCenterX(entities.get(i).getPosition().getX());
			elements.get(i).setCenterY(entities.get(i).getPosition().getY());
		}
		sim.updateEntities();
	}

	/** 
	 * Indicates whether model updates are terminated.
	 * 
	 * Here termination is reached after N seconds.
	 */
	public boolean isTerminated() {
		return nbUpdates >= DURATION_S * UPDATE_RATE_HZ;
	}

	/**
	 * Cleanly terminates the simulation.
	 * 
	 * Here termination consists in stopping the program with System.exit().
	 */
	public void exit() {
		System.out.println("Simulation> Simulation terminated.");		
		System.exit(0);
	}

}
