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
	public double getPotential(double distance) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPotential(double distanceFromEnemy,
			double[] distancesFromEdges) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
