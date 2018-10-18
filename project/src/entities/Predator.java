package entities;

import java.security.SecureRandom;

import location.*;
import sim.Simulation;
import sim.SimulationManager;

/**
 * Class that specifies a Predator. Predator eats Plants and Preys. Gets hungry and fatigated. Reproduces. 
 * Represented in red/salmon in the graphic simulation.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Predator extends Animal {
	
	public static final int AVERAGE_ADULT_RADIUS = 7;
	public static final int AVERAGE_BABY_RADIUS = 5;
	
	/**
	 * Creates a new Predator.
	 * @param associatedSim is the associated SimulationManager.
	 * @param position is this Predator's Position.
	 * @param fatigue is this Predator's fatigue.
	 * @param age is this Predator's Age.
	 * @param hunger is this Predator's hunger.
	 * @param radius is this Predator's Radius.
	 * 
	 */
	public Predator(SimulationManager associatedSim, Position position, double fatigue, int age, double hunger, double radius) {
		super(associatedSim, position, fatigue, age, hunger, radius);
	}
	
	/**
	 * Copies the predator p.
	 * @param p is the predator to copy.
	 * 
	 */
	public Predator(Predator p) {
		super(p.associatedSim, p.position, p.getFatigue(), p.getAge(), p.getHunger(), p.getRadius());
	}
	
	/**
	 * Creates a baby Predator.
	 * 
	 */
	public void reproduce() {
			Position babyPos;
			babyPos = new Position(this.position);
			Predator babyPredator = new Predator(associatedSim, babyPos, 0, 0, 0, AVERAGE_BABY_RADIUS);
			associatedSim.getPredatorsBirthList().add(babyPredator);
			this.setFatigue(this.getFatigue() + 0.025);
			this.setHunger(this.getHunger() + 0.15);
	}
	
	/**
	 * Generates random predator.
	 * @param associatedSim is the Simulation Manager to which we wish to add a random predator.
	 * @return the new random predator.
	 * 
	 */
	public static Predator randomPredator(SimulationManager associatedSim) {
		SecureRandom rnd = new SecureRandom();
		Position rndPos = new Position(rnd.nextDouble()*Simulation.SPACE_SIZE, rnd.nextDouble()*Simulation.SPACE_SIZE);
		double fatigue = rnd.nextDouble();
		double hunger = rnd.nextDouble();
		int age = (int) (rnd.nextDouble() * 10);
		double radius = rnd.nextGaussian() + AVERAGE_ADULT_RADIUS;
		return new Predator(associatedSim, rndPos, fatigue, age, hunger, radius);
	}
	
	/**
	 * Kills and eats prey.
	 * @param p is the prey to be killed/eaten.
	 * 
	 */
	public void kill(Prey p) {
		this.radius += 2.2;
		associatedSim.getPreys().remove(p);
		this.setHunger(this.getHunger() - 0.9);
		if (this.getHunger() <= 0.0)
			this.setHunger(0.0);
	}
	
	/**
	 * After the animal is aware of the surroundings within his sight, he determines #1 priority and acts accordingly.
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
				if (doISee(Entity.PREDATOR) && predatorsAround.size() > 1) {
					if (predatorsAround.get(1).getAge() >= Animal.REPRODUCTION_AGE) {
						if(this.getPosition().distance(predatorsAround.get(1).getPosition()) < this.getRadius()*2)
							this.reproduce();
						else
							goTo(predatorsAround.get(1).getPosition(), Moving.WALKING);
					}
				}
				else
					randomRoam();
				break;
			case FATIGUE:
				sleep();
				break;
			case ROAM:
				randomRoam();
				break;
			case HUNGER:
				if(doISee(Entity.PREY)) {
					if (ICanEatThisPrey(preysAround.get(0))) {
						kill(preysAround.get(0));
					}
					else if(this.getFatigue() < 0.96)
						goTo(preysAround.get(0).getPosition(), Moving.RUNNING);
					else if(doISee(Entity.PLANT) && this.getHunger() > 0.98) {
						if (ICanEatThisPlant(plantsAround.get(0)))
							eatPlant(plantsAround.get(0));
						else
							goTo(plantsAround.get(0).getPosition(), Moving.WALKING);
					}
				}							
				else if(doISee(Entity.PLANT) && this.getHunger() > 0.9) {
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
