package location;

/**
 * Class that specifies a velocity vector on a 2D cartesian plane.
 * Normalize allows us to give the vector the adequate modulus, provided the initial displacement vector
 * 
 * @author MOREIRA Gabriel
 *
 */

public class Velocity {
	
	private double x;
	private double y;
	
	/**
	 * Default Constructor.
	 * 
	 */
	public Velocity() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Method calculates a vector from the initial position to the desired end position
	 * 
	 * @param x coordinate x of the displacement vector
	 * @param y coordinate y of the displacement vector
	 * 
	 */
	public Velocity(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the X component of the vector.
	 * 
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return the Y component of the vector.
	 * 
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Sums 2 velocity vectors.
	 * @param v2 Vector to sum to this vector.
	 * 
	 */
	public void vectorSum(Velocity v2) {
		this.x += v2.getX();
		this.y += v2.getY();
	}
	
	/**
	 * Gives the symmetric of the velocity vector.
	 * 
	 */
	public void symmetric() {
		this.x = -this.x;
		this.y = -this.y;
	}
	
	/**
	 * Calculates the modulus of a velocity vector.
	 * @param velocity is the velocity vector whose modulus we wish to calculate.
	 * @return the modulus.
	 * 
	 */
	public double modulus(Velocity velocity) {
		return Math.sqrt(Math.pow(velocity.getX(), 2) + Math.pow(velocity.getY(), 2));
	}

	/**
	 * MUST be called after the constructor to correct the modulus of the velocity vector.
	 * @param speed is desired speed (norm).
	 * 
	 */
	public void normalize(double speed) {
		double ajusted_speed = speed;
		if (modulus(this) < speed) {
			ajusted_speed = modulus(this);
		}
		if (modulus(this) != 0) {
			this.x = ajusted_speed * (x / modulus(this));
			this.y = ajusted_speed * (y / modulus(this));
		}

	}
	
	/**
	 * Gives a textual representation of a velocity vector.
	 * 
	 */
	@Override 
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
