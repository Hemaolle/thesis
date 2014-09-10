package thesis.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import thesis.bot.Controller;

/**
 * Starter for running a bot that can be accessed remotely. Takes one command
 * line argument that will be the unique part of the name that can be used to
 * access the started bot.
 * 
 * @author Oskari Leppäaho
 * 
 */
public class RemoteBotRMI extends RmiStarter {

	static String currentName;

	public RemoteBotRMI() {
		super(RemoteBotInterface.class);
	}

	public static void main(String[] args) {
		currentName = args[0];
		new RemoteBotRMI();
	}

	/**
	 * Adds the bot to the RMI registry.
	 */
	@Override
	public void doCustomRmiHandling() {
		try {
			Registry registry = LocateRegistry.getRegistry();
			RemoteBotInterface remoteBot = new Controller();
			RemoteBotInterface remoteBotStub = (RemoteBotInterface) UnicastRemoteObject
					.exportObject(remoteBot, 0);

			registry.rebind(RemoteBotInterface.SERVICE_NAME + currentName,
					remoteBotStub);
			System.out.println("Bind " + RemoteBotInterface.SERVICE_NAME
					+ currentName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
