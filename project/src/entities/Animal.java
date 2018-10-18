package entities;

import java.security.SecureRandom;
import java.util.ArrayList;
import location.Position;
import location.Velocity;
import sim.Simulation;
import sim.SimulationManager;

/**
 *  Specifies an Animal.
 *  
 * @author MOREIRA Gabriel
 *
 */
public class Animal extends Living {

	boolean awake;
	boolean updatedAwareness;
	int dyingAge;	
	ArrayList<Predator> predatorsAround;
	ArrayList<Prey> preysAround;
	ArrayList<Plant> plantsAround;	
	Velocity velocity;
	Gaze eyesight;
	private SecureRandom rnd;
	private int age;
	private double fatigue;
	private double hunger;
	public static final double SPEED = 1.0;
	public static final double 	CHASE_SPEED = SPEED*2;
	public static final double EYESIGHT_RANGE_PERCENTAGE = 0.25;
	public static final int REPRODUCTION_AGE = 55;
	public static final int AVERAGE_DYING_AGE = 180;
	public static final double REPRODUCTION_PROBABILITY = 1.0;
	Drive priority;	
	
	/**
	 * Creates a new Animal associated to the simulationManager associatedSim.
	 * 
	 * @param associatedSim is the associated simulation manager.
	 * @param position specifies the Animal's position.
	 * @param fatigue between 0.0 and 1.0.
	 * @param age is the Animal's age
	 * @param hunger between 0.0 and 1.0.
	 * @param radius of the circle that represents this Animal.
	 * 
	 */
	public Animal(SimulationManager associatedSim, Position position, double fatigue, int age, double hunger, double radius) {
		super(associatedSim, position, radius);
		this.fatigue = fatigue;
		this.age = age;
		this.hunger = hunger;
		this.eyesight = new Gaze(Animal.EYESIGHT_RANGE_PERCENTAGE*Simulation.SPACE_SIZE);
		this.radius = radius;
		this.awake = true;
		this.velocity = new Velocity(0,0);
	    rnd = new SecureRandom();
		dyingAge = ((int) rnd.nextGaussian()*20) + Animal.AVERAGE_DYING_AGE;
	}
	
	/**
	 * Puts the animal in a sleep state - motionless, unaware.
	 * Fatigue diminishes and hunger increases.
	 * 
	 */
	public void sleep() {
		velocity = new Velocity(0,0);
		awake = false;
		if (fatigue - 0.05 >= 0)
			fatigue -= 0.05;
		else
			fatigue = 0;
		hunger += 0.01;
		updatedAwareness = false;
	}
	
	/**
	 * We use the SimulationManager to get all the Things around this animal.
	 * This animal can only see entities that are within his eyesight.
	 * All the retrieved instances are stored in (...)Around ArrayLists.
	 * 
	 */
	public void lookAround() {
		predatorsAround = associatedSim.getPredatorsAroundThis(this.position, eyesight.getRange());
		preysAround = associatedSim.getPreysAroundThis(this.position, eyesight.getRange());
		plantsAround = associatedSim.getPlantsAroundThis(this.position, eyesight.getRange());
		updatedAwareness = true;
	}
	
	/**
	 * Animal moves aimlessly in a brownian-motion.
	 * Hunger and fatigue increase.
	 * 
	 */
	public void randomRoam() {
		SecureRandom rnd = new SecureRandom();
		double radius = SPEED;
		double angle = rnd.nextDouble() * 2 * Math.PI;
		velocity = new Velocity(radius*Math.cos(angle), radius*Math.sin(angle));
		velocity.normalize(SPEED);
		if (hunger + 0.01 < 1.0) 
			hunger += 0.01;
		else
			hunger = 1.0;
		if (fatigue + 0.01 < 1.0)
			fatigue += 0.01;
		else
			fatigue = 1.0;
		updatedAwareness = false;
		setNewPosition();
	}
	
	/**
	 * Animal sets his velocity vector to point at the desired position, which might not be achieved instantly.
	 * A new position is set for the animal, using the calculated velocity vector. This means the animal has 
	 * started moving towards position p.
	 * @param p is the position we want to get to.
	 * 
	 */
	public void goTo(Position p, Moving movement) {
		velocity = new Velocity(p.getX() - this.getPosition().getX(), p.getY() - this.getPosition().getY());
		switch(movement) {
			case WALKING:
				velocity.normalize(SPEED);
				break;
			case RUNNING:
				velocity.normalize(CHASE_SPEED);
				hunger += 0.01;
				fatigue += 0.01;
		}
		if (hunger + 0.01 < 1.0) 
			hunger += 0.01;
		else
			hunger = 1.0;
		if (fatigue + 0.01 < 1.0)
			fatigue += 0.01;
		else
			fatigue = 1.0;
		updatedAwareness = false;
		setNewPosition();
	}
	
	/**
	 * Same as goTo, but using CHASE_SPEED.
	 * @param p is the position we are chasing.
	 * 
	 */
	public void chase(Position p) {
		velocity = new Velocity(p.getX() - this.getPosition().getX(), p.getY() - this.getPosition().getY());
		velocity.normalize(CHASE_SPEED);
		if (hunger + 0.02 < 1.0) 
			hunger += 0.012;
		else
			hunger = 1.0;
		if (fatigue + 0.02 < 1.0)
			fatigue += 0.011;
		else
			fatigue = 1.0;
		updatedAwareness = false;
		setNewPosition();
	}
	
	/**
	 * Calculates and sets the animal's new position, given the velocity vector.
	 * 
	 */
	public void setNewPosition() {
		position.moveTo(position.getX() + velocity.getX(), position.getY() + velocity.getY());
	}
	
	/**
	 * Determines whether or not the animal sees the entity specified.
	 * @param e is the entity we are determining to be within our sight or not.
	 * @return is true or false whether this animal sees the specified entity e.
	 * 
	 */
	public boolean doISee(Entity e) {
		if (updatedAwareness) {
			switch(e) {
				case PREY:
					if (preysAround.isEmpty())
						return false;
					else
						return true;
				case PREDATOR:	
					if (predatorsAround.isEmpty())
						return false;
					else
						return true;
				case PLANT:
					if (plantsAround.isEmpty())
						return false;
					else
						return true;
				default:
					return false;			
			}
		}
		else
			return false;	
	}
	
	/**
	 * Determines the animal's most important priority (the animal's Drive).
	 * 
	 */
	public void sortPriorities() {
		if (hunger < 0.7 && fatigue < 0.7 && age >= Animal.REPRODUCTION_AGE)
			priority = Drive.REPRODUCTION;
		else if (hunger < 0.1 && fatigue < 0.1)
			priority = Drive.ROAM;
		else if (fatigue > 0.7 && fatigue >= hunger)
			priority = Drive.FATIGUE;
		else if (hunger > 0.7 && hunger > fatigue)
			priority = Drive.HUNGER;
		else
			priority = Drive.ROAM;
	}
	
	/**
	 * Determines if plant is within our reach to be eaten.
	 * Plants can be eaten if animal position touches the radius of the plant.
	 * @param p is the plant we want to eat.
	 * @return True if plant can be eaten. False otherwise.
	 * 
	 */
	public boolean ICanEatThisPlant(Plant p) {
		if (this.getPosition().distance(p.getPosition()) < p.getRadius())
			return true;
		else 
			return false;
	}
	
	/**
	 * Determines if prey is within our reach to be eaten.
	 * Preys can be eaten if Predator touches the Prey (or is any closer than that)
	 * @param p is the prey we want to eat.
	 * @return True if prey can be eaten. False otherwise.
	 * 
	 */
	public boolean ICanEatThisPrey(Prey p) {
		if (this.getPosition().distance(p.getPosition()) < p.getRadius() + this.getRadius())
			return true;
		else 
			return false;
	}
	
	/**
	 * Eats plant.
	 * @param p is the plant to be eaten.
	 * Plant is then deleted from the simulation.
	 * 
	 */
	public void eatPlant(Plant p) {
		this.radius += 0.5;
		associatedSim.getPlants().remove(p);
		hunger -= 0.75;
		if (hunger < 0.0)
			hunger = 0.0;
	}
	
	/**
	 * Increases the animal's age.
	 * 
	 */
	public void growUp() {
		age += 1;
		if(fatigue + 0.01 < 1.0)
			fatigue += 0.01;
		else
			fatigue = 1.0;
		if(hunger + 0.01 <1.0)
			hunger += 0.01;
		else
			hunger = 1.0;
	}
	
	/**
	 * Determines whether the animal has reached its dying age. Predetermined randomly from the moment of birth.
	 * @return True if animal has reached dying age. False otherwise
	 * 
	 */
	public boolean itsTimeToDie() {
		if (this.age >= this.dyingAge)
			return true;
		else
			return false;
	}
	
	/**
	 * Kills this animal by writing it onto the DeathList.
	 * 
	 */
	public void die() {
		if (associatedSim.getPredators().contains(this))
			associatedSim.getPredatorsDeathList().add((Predator) this);
		else
			associatedSim.getPreysDeathList().add((Prey) this);	
	}
	
	/**
	 * 
	 * @return the animal's age
	 * 
	 */
	public int getAge() {
		return age;
	}
	
	/**
	 * 
	 * @return the animal's hunger
	 * 
	 */
	public double getHunger() {
		return hunger;
	}
	
	/**
	 * Sets the Animal's fatigue.
	 * @param fatigue is the value to be set.
	 * 
	 */
	public void setFatigue(double fatigue) {
		this.fatigue = fatigue;
	}
	
	/**
	 * Sets the Animal's Hunger
	 * @param hunger is the value to be set;
	 * 
	 */
	public void setHunger(double hunger) {
		this.hunger = hunger;
	}
	
	/**
	 * Returns the fatigue of the Animal.
	 * @return the animal's fatigue
	 * 
	 */
	public double getFatigue() {
		return fatigue;
	}
	
	/**
	 * String representation of an Animal.
	 * @return this Animal in a string format.
	 * 
	 */
	@Override
	public String toString() {
		return "Animal id:" + ". Age: " + this.age + ". Position: " + this.position.toString() + ".\n" 
				+ "Fatigue: " + fatigue + ". Hunger: " + hunger + ".\n";
	}
	
}
