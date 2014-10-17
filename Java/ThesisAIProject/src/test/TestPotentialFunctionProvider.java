package test;

import java.rmi.RemoteException;

/**
 * Mockup of a PotentialFunctionProvider.
 * 
 * @author Oskari Leppäaho
 */

import thesis.rmi.PotentialFunctionProvider;

public class TestPotentialFunctionProvider implements PotentialFunctionProvider {	

	@Override
	public double getPotential(double distanceFromEnemy,
			double ownMaximumShootDistance) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPotential(double[] distancesFromEnemies,
			double[] distancesFromOwnUnits, double ownMaximumShootDistance,
			double[] distancesFromEdges, boolean onCooldown)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
