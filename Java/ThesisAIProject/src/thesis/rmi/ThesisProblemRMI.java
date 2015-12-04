package thesis.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import test.TestPotentialFunctionProvider;

/**
 * For testing the RMI connection.
 * 
 * @author Oskari Leppäaho
 */

public class ThesisProblemRMI extends RmiStarter {

	Registry registry;
	PotentialFunctionProvider provider;
//	ArrayList<RemoteBotInterface> bots;
	
	public ThesisProblemRMI() {
		super(PotentialFunctionProvider.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			provider = new TestPotentialFunctionProvider();
			PotentialFunctionProvider providerStub = (PotentialFunctionProvider)UnicastRemoteObject.exportObject(provider, 0);
			
			registry = LocateRegistry.getRegistry();
			registry.rebind(PotentialFunctionProvider.SERVICE_NAME, providerStub);
		}
		catch(Exception e ){
			e.printStackTrace();
		}		
	}
	
	public RemoteBotInterface connectClient(String name) {
		try {
			return (RemoteBotInterface)registry.lookup(RemoteBotInterface.SERVICE_NAME + name);
		} catch (Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws RemoteException {
		ThesisProblemRMI starter = new ThesisProblemRMI();
		
		RemoteBotInterface client1;
		//RemoteBotInterface client2;
		
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Press enter when ready to initialize next client >");
		try {
			buffer.readLine();
		} catch (IOException e) {
			System.err.println("Something went wrong when waiting for \n"
					+ "the user to press enter on the console\n"
					+ "(for stepping).");
			e.printStackTrace();
		}
		client1 = starter.connectClient("0");
//		System.out.println("Press enter when ready to initialize next client >");
//		try {
//			buffer.readLine();
//		} catch (IOException e) {
//			System.err.println("Something went wrong when waiting for \n"
//					+ "the user to press enter on the console\n"
//					+ "(for stepping).");
//			e.printStackTrace();
//		}
//		starter.connectClient("2");
		
		System.out.println("Get score from client 1");
		System.out.println(client1.getRoundScore(starter.provider));
	}
}
