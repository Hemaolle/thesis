package test;

import thesis.rmi.PolicyFileLocator;

/**
 * class to do some common things for client & server to get RMI working
 * 
 * @author srasul (http://code.nomad-labs.com/)
 * 
 */
public class StandaloneRmiStarter {

	/**
	 * 
	 * @param clazzToAddToServerCodebase
	 *            a class that should be in the java.rmi.server.codebase
	 *            property.
	 */
	public StandaloneRmiStarter(Class clazzToAddToServerCodebase) {

		System.setProperty("java.rmi.server.codebase",
				clazzToAddToServerCodebase.getProtectionDomain()
						.getCodeSource().getLocation().toString());

		System.setProperty("java.security.policy",
				PolicyFileLocator.getLocationOfPolicyFile());

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}


}