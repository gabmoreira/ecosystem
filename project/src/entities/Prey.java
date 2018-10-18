package entities;

import java.security.SecureRandom;
import location.Position;
import location.Velocity;
import sim.Simulation;
import sim.SimulationManager;

/**
 * Class that specifies a Prey.
 * Represented in white in the Graphic Simulation.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Prey extends Animal {

	SecureRandom rnd;
	
	public static final int AVERAGE_PREY_RADIUS = 5;
	public static final int BABY_PREY_RADIUS = 4;
	
	/**
	 * Creates a new Prey.
	 * @param associatedSim is the associated SimulationManager
	 * @param position is this Prey's Position.
	 * @param fatigue is this Prey's fatigue.
	 * @param age is this Prey's age.
	 * @param hunger is this Prey's hunger.
	 * @param radius is this Prey's radius.
	 * 
	 */
	public Prey(SimulationManager associatedSim, Position position, double fatigue, int age, double hunger, double radius) {
		super(associatedSim, position, fatigue, age, hunger, radius);
	}
	
	public Prey(Prey p) {
		super(p.associatedSim, p.position, p.getFatigue(), p.getAge(), p.getHunger(), p.radius);
	}
	
	/**
	 * Creates a baby Prey by adding it to the associatedSimulation.
	 * 
	 */
	public void reproduce() {
		Position babyPos;
		babyPos = new Position(this.position);
		Prey babyPrey = new Prey(associatedSim, babyPos, 0, 0, 0, BABY_PREY_RADIUS);
		associatedSim.getPreysBirthList().add(babyPrey);
		if (this.getFatigue() + 0.1 < 1.0)
			this.setFatigue(this.getFatigue() + 0.1);
		else
			this.setFatigue(1.0);
		if (this.getHunger() + 0.35 < 1.0)
			super.setHunger(this.getHunger() + 0.35);
		else
			this.setHunger(1.0);		
	}
	
	/**
	 * Generates random prey.
	 * @param associatedSim is the Simulation Manager to which we wish to add a random prey.
	 * @return the new random prey.
	 * 
	 */
	static public Prey randomPrey(SimulationManager associatedSim) {
		SecureRandom rnd = new SecureRandom();
		Position rndPos = new Position(rnd.nextDouble()*Simulation.SPACE_SIZE, rnd.nextDouble()*Simulation.SPACE_SIZE);
		double fatigue = 0;
		double hunger = 0;
		int age = (int) (rnd.nextDouble() * 10 + 40);
		double radius = rnd.nextGaussian()*0.9 + AVERAGE_PREY_RADIUS;
		return new Prey(associatedSim, rndPos, fatigue, age, hunger, radius);
	}
	
	/**
	 * Sorts Prey's priorities in a different fashion from other Animals, hence the Override.
	 * 
	 */
	@Override
	public void sortPriorities() {
		lookAround();
		if (doISee(Entity.PREDATOR) && predatorsAround.get(0).getPosition().distance(this.position) < 6*Animal.CHASE_SPEED)
			priority = Drive.ESCAPE;
		else if (this.getHunger() < 0.7 && super.getAge() >= Animal.REPRODUCTION_AGE)
			priority = Drive.REPRODUCTION;
		else if (this.getHunger() < 0.1) 
			priority = Drive.ROAM;
		else if (this.getHunger() > 0.7)
			priority = Drive.HUNGER;
		else
			priority = Drive.ROAM;
	}
	
	/**
	 * Runs away from Predator.
	 * @param p is the Predator we're running from.
	 * 
	 */
	public void runAway(Predator p) {
		velocity = new Velocity(p.getPosition().getX() - this.getPosition().getX(), p.getPosition().getY() - this.getPosition().getY());
		velocity.symmetric();
		velocity.normalize(CHASE_SPEED);
		if (this.getHunger() + 0.02 < 1.0) 
			this.setHunger(this.getHunger() + 0.012);
		else
			this.setHunger(1.0);
		if (this.getFatigue() + 0.05 < 1.0)
			this.setFatigue(this.getFatigue() + 0.05);
		else
			this.setFatigue(1.0);
		setNewPosition();
		updatedAwareness = false;
	}

	
	/**
	 * After the animal is aware of the surroundings within his sight, he determines #1 priority and acts accordingly.
	 * Different from the method in Predator because preys also have ESCAPE has a purpose, and aren't driven by their FATIGUE.
	 * 
	 */
	public void satisfyNeeds() {
		if (this.itsTimeToDie()) {
			this.die();
			return;
		}
		lookAround();
		sortPriorities();	
		switch(priority) {
			case REPRODUCTION:
				if (doISee(Entity.PREY) && preysAround.size() > 1) {
					if (preysAround.get(1).getAge() >= Animal.REPRODUCTION_AGE) {
						if(this.getPosition().distance(preysAround.get(1).getPosition()) < this.getRadius()*2)
							this.reproduce();
						else if (this.getHunger() < 0.8)
							goTo(preysAround.get(1).getPosition(), Moving.WALKING);
						else
							priority = Drive.HUNGER;
					}
				}
				else
					randomRoam();
				break;
			case ESCAPE:
				if(doISee(Entity.PREDATOR))
					runAway(predatorsAround.get(0));
				break;
			case ROAM:
				randomRoam();
				break;
			case HUNGER:
				if(doISee(Entity.PLANT)) {
					if (ICanEatThisPlant(plantsAround.get(0)))
						eatPlant(plantsAround.get(0));
					else
						goTo(plantsAround.get(0).getPosition(), Moving.WALKING);
				}
				else
					randomRoam();
				break;				
			default:
				randomRoam();
				break;
		}
	}
}
