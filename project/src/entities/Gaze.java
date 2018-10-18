package entities;

/**
 * Sets the gaze/eyesight of an animal
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Gaze {

	private double range;
	
	/**
	 * Creates a Gaze with a given range.
	 * @param range is the reach of the Gaze.
	 * 
	 */
	public Gaze(double range) {
		this.range = range;
	}
	
	/**
	 * 
	 * @return the Gaze's range.
	 */
	public double getRange() {
		return range;
	}
}
