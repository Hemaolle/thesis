package thesis.bot;

import java.rmi.RemoteException;

import thesis.bot.PosUtils;
import thesis.rmi.PotentialFunctionProvider;
import bwapi.*;

/**
 * Calculates the potential field that guides the movement of the bot. The
 * default potential field is designed to keep the units at maximum shooting
 * distance from a single enemy unit.
 * 
 * The potential field can also be provided by an external component.
 * 
 * @author Oskari Leppäaho
 * 
 */
public class PotentialCalculator {

	Controller bot;
	PotentialFunctionProvider potentialProvider; // the component that will
													// provide
													// the potential function

	/**
	 * Creates the PotentialCalculator.
	 * 
	 * @param bot
	 *            A reference to the bot controller.
	 */
	public PotentialCalculator(Controller bot) {
		this.bot = bot;
	}

	/**
	 * Sets the potential provider.
	 * 
	 * @param potentialProvider
	 *            The new potential provider.
	 */
	public void setPotentialProvider(PotentialFunctionProvider potentialProvider) {
		this.potentialProvider = potentialProvider;
	}

	/**
	 * Get the potential for a position. Uses
	 * {@link #getPotential(double, double)} to calculate the potential.
	 * 
	 * @param pos
	 *            The position
	 * @return
	 */
	double getPotential(Position pos) {
		return getPotential(pos.getX(), pos.getY());
	}

	/**
	 * Get the potential for coordinates. <strike>The potential is calculated by
	 * adding -(0.05 * enemyDistance - 5)^2 for each enemy unit. This potential
	 * tries to keep a single unit at it's maximum attacking range from a single
	 * enemy.</strike>
	 * 
	 * Uses the potentialProvider if it was set. Otherwise uses the potential
	 * function defined here (changes frequently during development).
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return Total potential for the location.
	 */
	private double getPotential(double x, double y) {
		double potential = 0;

		// Multiply map width and height by tile size to get values in pixels.
		double distMapBottom = bot.game.mapHeight() * 32 - y;
		double distMapTop = y;
		double distMapLeft = x;
		double distMapRight = bot.game.mapWidth() * 32 - x;
		
		//System.out.println("distMapBottom " + distMapBottom + " distMapTop " + distMapTop + " distMapLeft " + distMapLeft + " distMapRight " + distMapRight);

		double[] distancesFromEdges = { distMapBottom, distMapTop, distMapLeft,
				distMapRight };

		for (Unit u : bot.getEnemyUnitsNoRevealers()) {
			Position enemyPos = u.getPosition();
			double xlen = enemyPos.getX() - x;
			double ylen = enemyPos.getY() - y;
			double enemyDistance = Math.sqrt(xlen * xlen + ylen * ylen);
			if (potentialProvider == null) {

				// fast game
				//potential = 0;

				// original
				// potential += -(0.05 * enemyDistance - 5)
				// * (0.05 * enemyDistance - 5);

				// potential += 3 * (enemyDistance * enemyDistance); first
				// evolution run
				// potential += -(enemyDistance - 120) * (enemyDistance - 136);
				// potential -= 1.0 / (0.0001 * distMapBottom);
				// potential -= 1.0 / (0.0001 * distMapTop);
				// potential -= 1.0 / (0.0001 * distMapLeft);
				// potential -= 1.0 / (0.0001 * distMapRight);
				// potential += ((enemyDistance - 96.4901227341802) /
				// (enemyDistance * 191.22811092392067));
				potential += -235.4179177051288;
				potential += mapEdgePotential(distMapBottom);
				potential += mapEdgePotential(distMapTop);
				potential += mapEdgePotential(distMapLeft);
				potential += mapEdgePotential(distMapRight);
			} else {
				try {
					potential += potentialProvider.getPotential(enemyDistance,
							distancesFromEdges);
				} catch (RemoteException e) {
					System.err.println("Remote potential evaluation failed: ");
					e.printStackTrace();
				}
			}
		}
		return potential;
	}

	public double mapEdgePotential(double x) {
		return (((-371.5028023902601 - -45.14319119960675) + x) / x) * 110.42600238815817;
	}

	/**
	 * Gets the potentials around an unit in 8 directions and the unit's current
	 * position. The potentials are measured in 1 pixel's distance from the
	 * unit's current position (diagonals included).
	 * 
	 * @param u
	 *            The unit around which to measure the potentials.
	 * @return An array with the potentials around the unit. The order is from
	 *         top left to bottom right, row by row.
	 */
	double[] getPotentialsAround(Unit u) {
		double[] potentials = new double[9];
		int k = 0;
		for (int n = -1; n <= 1; n++) {
			for (int m = -1; m <= 1; m++) {
				double i = m;
				double j = n;

				/*
				 * It's OK that i and j will be < 1, because getPotential can
				 * handle doubles. It's important that the diagonal potentials
				 * are checked in positions whose distance from the unit is the
				 * same as with the other directions. Otherwise the diagonal
				 * directions might get favored more than other directions.
				 * 
				 * But remember not to divide by zero ;)
				 */
				if (i != 0)
					i /= Math.sqrt(i * i + j * j);
				if (j != 0)
					j /= Math.sqrt(i * i + j * j);

				double posX = u.getPosition().getX() + i;
				double posY = u.getPosition().getY() + j;
				potentials[k] = getPotential(posX, posY);
				k++;
			}
		}
		return potentials;
	}

	/**
	 * Gets the move direction based on an array of potentials. The move
	 * direction will be the direction with the highest potential. Default value
	 * is (0,0: no movement).
	 * 
	 * @param potentials
	 *            An array of potentials around an unit. The order is from top
	 *            left to bottom right, row by row.
	 * @return The direction corresponding to the highest potential.
	 */
	Position getMoveDirection(double[] potentials) {
		int highestIndex = Controller.findHighestDefaultTo4(potentials);
		// Indices:
		// 0 1 2 -1,-1 0,-1 1,-1
		// 3 4 5 -1, 0 0, 0 1 ,0
		// 6 7 8 -1, 1 0, 1 1, 1
		int x = highestIndex % 3 - 1;
		int y = ((highestIndex) / 3 - 1);
		return new Position(x, y);
	}

	/**
	 * Checks for the highest potential in each pixel coordinate from a position
	 * in a direction for a distance. If the potential is the same in all
	 * distances (or highest potentials are at the from position and some
	 * direction) returns the from position.
	 * 
	 * @param from
	 *            The position from which to start checking.
	 * @param dir
	 *            The direction in which to check. Should be a position with
	 *            coordinates between -1 and 1.
	 * @param distance
	 *            Distance for which to check.
	 * @return The position with the highest potential. Default is the current
	 *         position.
	 */
	Position getHighestPotentialPosition(Position from, Position dir,
			int distance) throws IllegalArgumentException {
		if (dir.getX() < -1 || 1 < dir.getX() || dir.getY() < -1
				|| 1 < dir.getY())
			throw new IllegalArgumentException("Direction should "
					+ "have coordinates between -1 and 1. Was " + dir.getX()
					+ " and " + dir.getY());

		double highestPotential = getPotential(from);
		Position highestPosition = from;
		if ((dir.getX() * dir.getX() + dir.getY() * dir.getY()) == 2) {
			distance = (int) (distance / Math.sqrt(2.0));
		}

		// -------------debug
		// Position enemyPos = new Position(0,0);
		// for (Unit u : bot.getEnemyUnitsNoRevealers()) {
		// enemyPos = u.getPosition();
		// }
		//
		// -------------enddebug

		for (int i = 0; i <= distance; i++) {

			Position offset = PosUtils.multiply(dir, i);

			double currentPotential = getPotential(PosUtils.add(from, offset));

			// -------------debug
			//
			// System.out.println("Distance: " +
			// enemyPos.getPDistance(from.plus(offset)) + " potential: " +
			// currentPotential);
			// -------------enddebug

			if (currentPotential > highestPotential) {
				highestPosition = PosUtils.add(from, offset);
				highestPotential = currentPotential;
			}
		}
		return highestPosition;
	}
}
