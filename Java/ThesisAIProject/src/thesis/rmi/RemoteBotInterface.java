package thesis.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface for a remote bot.
 * 
 * @author Oskari Leppäaho
 *
 */
public interface RemoteBotInterface extends Remote {
	public static final String SERVICE_NAME = "BotService";
	/**
	 * Gets the score of a single round of fighting.
	 * 
	 * @return Score for the round.
	 * @throws InterruptedException
	 *             If interrupted.
	 */	
	public int getRoundScore(PotentialFunctionProvider problem) throws RemoteException;	
}
