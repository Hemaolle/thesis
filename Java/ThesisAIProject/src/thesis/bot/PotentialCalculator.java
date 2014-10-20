package thesis.bot;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import thesis.bot.PosUtils;
import thesis.rmi.PotentialFunctionProvider;
import bwapi.*;
import bwta.BWTA;

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
	 * @param u
	 *            The unit for which to calculate the potential.
	 * @return
	 */
	double getPotential(Position pos, Unit u) {
		return getPotential(pos.getX(), pos.getY(), u);
	}

	/**
	 * Gets the potential for coordinates. Uses the potentialProvider if it was
	 * set. Otherwise uses the potential function defined here (changes
	 * frequently during development).
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return Total potential for the location.
	 */
	private double getPotential(double x, double y, Unit u) {
		double ownMaximumShootDistance = u.getType().groundWeapon().maxRange();
		double potential = 0;

		Position nearestPoint = BWTA.getRegion(u.getPosition()).getPolygon()
				.getNearestPoint(u.getPosition());
		nearestPoint.getDistance(u.getPosition());
		double distMapEdge = nearestPoint.getDistance(u.getPosition());

		boolean onCooldown = !(u.getGroundWeaponCooldown() == 0);
		double[] distancesFromEdges = { distMapEdge };
		double[] distancesFromEnemies = getEnemyDistances(x, y);
		double[] distancesFromOwnUnits = getOwnUnitDistances(x, y, u);

		if (potentialProvider == null) {

			// fast game
			// potential = 0;

			// original
			// potential += -(0.05 * enemyDistance - 5)
			// * (0.05 * enemyDistance - 5);			
			double maxPotential = -Double.MAX_VALUE;
			double currentPotential;		
			if (!onCooldown)
				for (int j = 0; j < distancesFromEnemies.length; j++) {
					currentPotential = enemyPotential(distancesFromEnemies[j],
							ownMaximumShootDistance);
					if (maxPotential < currentPotential)
						maxPotential = currentPotential;
				}
			else
				for (int j = 0; j < distancesFromEnemies.length; j++) {
					currentPotential = enemyPotentialWhenOnCooldown(
							distancesFromEnemies[j], ownMaximumShootDistance);
					if (maxPotential < currentPotential)
						maxPotential = currentPotential;
				}
			potential += maxPotential;
			for (int j = 0; j < distancesFromOwnUnits.length; j++) {
				potential += ownPotential(distancesFromOwnUnits[j]);
			}
			potential += mapEdgePotential(distMapEdge);
		} else {
			try {
				potential += potentialProvider.getPotential(
						distancesFromEnemies, distancesFromOwnUnits,
						ownMaximumShootDistance, distancesFromEdges, onCooldown);
			} catch (RemoteException e) {
				System.err.println("Remote potential evaluation failed: ");
				e.printStackTrace();
			}
		}

		return potential;
	}

	/**
	 * Gets the distances of the own units from given position. Ignores unit u.
	 * 
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param u
	 *            This unit will be ignored
	 * @return Array of own unit distances from given location (without unit u).
	 */
	private double[] getOwnUnitDistances(double x, double y, Unit ignoreUnit) {
		List<Unit> myUnits = new ArrayList<Unit>(bot.getMyUnitsNoRevealers());
		myUnits.remove(ignoreUnit);
		return getUnitDistances(x, y, myUnits);
	}

	/**
	 * Gets the distances of the enemy units from given position.
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return Array of enemy unit distances from that location.
	 */
	private double[] getEnemyDistances(double x, double y) {
		return getUnitDistances(x, y, bot.getEnemyUnitsNoRevealers());
	}

	/**
	 * Gets the distances of the units from given position.
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param units
	 *            List of units whose distances to get.
	 * @return Array of enemy unit distances from that location.
	 */
	private double[] getUnitDistances(double x, double y, List<Unit> units) {
		double[] distances = new double[units.size()];
		int i = 0;
		for (Unit u : units) {
			Position pos = u.getPosition();
			double xlen = pos.getX() - x;
			double ylen = pos.getY() - y;
			double distance = Math.sqrt(xlen * xlen + ylen * ylen);
			distances[i++] = distance;
		}
		return distances;
	}

	/**
	 * Calculates the potential depending on proximity to an enemy unit.
	 * 
	 * @param x
	 *            Unit's distance from an enemy unit.
	 * @return The potential caused by the enemy unit.
	 */
	public double enemyPotential(double x, double ownMSD) {
		return 309.17765473823886 * 365.38753697382253;
	}

	/**
	 * Calculates the potential depending on proximity to an enemy unit when the
	 * own unit is on cooldown.
	 * 
	 * @param x
	 *            Unit's distance from an enemy unit.
	 * @return The potential caused by the enemy unit.
	 */
	public double enemyPotentialWhenOnCooldown(double x, double ownMSD) {
		return 309.17765473823886 * 365.38753697382253;
	}

	/**
	 * Calculates the potential depending on proximity to an own unit.
	 * 
	 * @param x
	 *            Unit's distance from an own unit.
	 * @return The potential caused by the own unit.
	 */
	public double ownPotential(double x) {
		return ((249.7439773635689 / -117.80470067152851) - 74.99324876403239) - 74.99324876403239;
	}

	/**
	 * Calculates the potential depending on proximity to a map edge.
	 * 
	 * @param x
	 *            Unit's distance from map edge
	 * @return The potential caused by the map edge.
	 */
	public double mapEdgePotential(double x) {
		return 272.3407434864084;
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
				potentials[k] = getPotential(posX, posY, u);
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
	 * @param u
	 *            The unit for which to check.
	 * @return The position with the highest potential. Default is the current
	 *         position.
	 */
	Position getHighestPotentialPosition(Position from, Position dir,
			int distance, Unit u) throws IllegalArgumentException {
		if (dir.getX() < -1 || 1 < dir.getX() || dir.getY() < -1
				|| 1 < dir.getY())
			throw new IllegalArgumentException("Direction should "
					+ "have coordinates between -1 and 1. Was " + dir.getX()
					+ " and " + dir.getY());

		double highestPotential = getPotential(from, u);
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

			double currentPotential = getPotential(PosUtils.add(from, offset),
					u);

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
