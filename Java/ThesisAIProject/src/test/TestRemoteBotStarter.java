package test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import thesis.rmi.PotentialFunctionProvider;
import thesis.rmi.RemoteBotInterface;
import thesis.rmi.RmiStarter;

/**
 * Mockup for testing the starting of a remote bot.
 * 
 * @author Oskari Leppäaho
 *
 */

public class TestRemoteBotStarter extends RmiStarter {

	static String currentName;
	
	public TestRemoteBotStarter() {
		super(TestRemoteBot.class);
	}

	public static void main(String[] args) {
		currentName = args[0];
		new TestRemoteBotStarter();
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			Registry registry = LocateRegistry.getRegistry();
			RemoteBotInterface remoteBot = new TestRemoteBot();
			RemoteBotInterface remoteBotStub = (RemoteBotInterface)UnicastRemoteObject.exportObject(remoteBot, 0);
			
			registry.rebind(RemoteBotInterface.SERVICE_NAME + currentName, remoteBotStub);
			System.out.println("Bind " + RemoteBotInterface.SERVICE_NAME + currentName);
			
            
//            PotentialFunctionProvider provider = (PotentialFunctionProvider)registry.lookup(PotentialFunctionProvider.SERVICE_NAME);
//            System.out.println("Remote bot evaluating using remote potential: " + provider.evaluateFunction(5));            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
		
	}

}
