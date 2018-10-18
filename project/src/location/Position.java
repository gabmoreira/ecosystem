package location;

import sim.Simulation;

/**
 * Class that specifies a Position on a 2D plane
 * 
 * @author MOREIRA Gabriel
 *
 */
public class Position {

		private double x;
		private double y;
		
		public Position() {	
		}
		
		/**
		 * 
		 * @param x is the x coordinate.
		 * @param y is the y coordinate.
		 * 
		 */
		public Position(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Makes a deep copy of pos.
		 * @param pos is the Position to be copied.
		 * 
		 */
		public Position(Position pos) {
			this.x = pos.getX();
			this.y = pos.getY();
		}
		
		/**
		 * Gives the X-coordinate
		 * @return The X-coordinate
		 * 
		 */
		public double getX() {
			return x;
		}
		
		/**
		 * Gives the Y-coordinate
		 * @return The Y-coordinate
		 * 
		 */
		public double getY() {
			return y;
		}
		
		/**
		 * Checks whether this position is out of the square SPACE_SIZE*SPACE_SIZE.
		 * @return true if it's out of the square. False otherwise.
		 * 
		 */
		public boolean isOutOfBounds() {
			if (this.x - 6 > Simulation.SPACE_SIZE || this.x + 6 < 0 || this.y - 6> Simulation.SPACE_SIZE || this.y + 6< 0)
				return true;
			else
				return false;
		}
		
		/**
		 * Calculates distance between THIS position and p1.
		 * @param p1 is the Position from which we wish to know the distance.
		 * @return The distance.
		 * 
		 */
		public double distance(Position p1) {
			return Math.sqrt(Math.pow(this.x - p1.getX(), 2) + Math.pow(this.y - p1.getY(), 2));
		}
		
		/**
		 * Changes current position to the one specified.
		 * @param x is the new X-coordinate.
		 * @param y is the new Y-coordinate.
		 * 
		 */
		public void moveTo(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Sets X coordinate.
		 * @param x is the coordinate to set.
		 * 
		 */
		public void setX(double x) {
			this.x = x;
		}
		
		/**
		 * Sets the Y coordinate.
		 * @param y is the coordinate to be set.
		 * 
		 */
		public void setY(double y) {
			this.y = y;
		}
		
		/**
		 * Gives a textual representation of Position.
		 * 
		 */
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
}
