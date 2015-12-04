package thesis.bot;

import bwapi.Position;

/**
 * Provides some vector math operations on bwapi Positions.
 * 
 * @author Oskari Leppäaho
 * 
 */
public class PosUtils {

	/**
	 * Multiplies a bwapi position by an int.
	 * 
	 * @param pos
	 *            The position to multiply.
	 * @param x
	 *            The multiplier.
	 * @return The product.
	 */
	public static Position multiply(Position pos, int x) {
		return new Position(pos.getX() * x, pos.getY() * x);
	}

	/**
	 * Adds two bwapi positions.
	 * 
	 * @param x
	 *            The position to add.
	 * @param y
	 *            Another position to add.
	 * @return The sum.
	 */
	public static Position add(Position x, Position y) {
		return new Position(x.getX() + y.getX(), x.getY() + y.getY());

	}
}
