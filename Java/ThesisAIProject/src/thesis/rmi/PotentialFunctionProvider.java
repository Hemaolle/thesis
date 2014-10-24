package thesis.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface for a potential function provider. The potential function is a
 * function that guides the AI player's units. The potential field values are
 * calculated in the locations nearest to the unit and the unit then moves in
 * direction of the highest potential next to it.
 * 
 * @author Oskari Leppäaho
 * 
 */
public interface PotentialFunctionProvider extends Remote {
	/**
	 * The name of the remote service.
	 */
	public static final String SERVICE_NAME = "PotentialFunctionService";

	/**
	 * Returns the potential based only on the distance from one enemy unit.
	 * 
	 * TODO: Modify to use multiple enemy units.
	 * 
	 * @param distanceFromEnemy
	 *            Distance between the AI player's unit that the potential is
	 *            being calculated for and the enemy unit.
	 * @return The potential value.
	 * @throws RemoteException
	 *             If something goes wrong with the remote function call.
	 */
	public double getPotential(double distanceFromEnemy,
			double ownMaximumShootDistance, double relativeHP) throws RemoteException;

	/**
	 * Returns the potential based on the distance from one enemy unit and the
	 * map edges.
	 * 
	 * TODO: Modify to use multiple enemy units.
	 * 
	 * @param distancesFromOwnUnits
	 * 
	 * @param distanceFromEnemy
	 *            Distance between the AI player's unit that the potential is
	 *            being calculated for and the enemy unit.
	 * @param distancesFromEdges
	 *            Current unit's distance from the 4 map edges.
	 * @param onCooldown
	 *            Indicates if the unit is on cooldown.
	 * @param relativeHP
	 *            Unit's HP amount relative to other own units.
	 * @return The potential value.
	 * @throws RemoteException
	 *             If something goes wrong with the remote function call.
	 */
	public double getPotential(double distancesFromEnemies[],
			double[] distancesFromOwnUnits, double ownMaximumShootDistance,
			double[] distancesFromEdges, boolean onCooldown, double relativeHP)
			throws RemoteException;
}
