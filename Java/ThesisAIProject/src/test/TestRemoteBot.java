package test;

import java.rmi.RemoteException;

import thesis.rmi.PotentialFunctionProvider;
import thesis.rmi.RemoteBotInterface;

/**
 * 
 * Mockup for testing the RemoteBotInterface.
 * 
 * @author Oskari Leppäaho
 *
 */

public class TestRemoteBot implements RemoteBotInterface {

	@Override
	public int getRoundScore(PotentialFunctionProvider problem)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
