package app;

import cli.CLIParser;
import servent.PongListener;
import servent.SimpleServentListener;

/**
 * Describes the procedure for starting a single Servent
 *
 */
public class ServentMainTest2 {

	/**
	 * Command line arguments are:
	 * 0 - path to servent list file
	 * 1 - this servent's id
	 */
	public static void main(String[] args) {
		try {
			Thread.sleep(22000); //2000 For bootstrap
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		int serventId = 2;
		int portNumber = -1;
		
		String serventListFile = "chord/servent_list_test.properties";
		
		AppConfig.readConfig(serventListFile, serventId);
		
		try {
			portNumber = AppConfig.myServentInfo.getListenerPort();
			
			if (portNumber < 1000 || portNumber > 2000) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

		PongListener pongListener = new PongListener();
		Thread pongListenerThread = new Thread(pongListener);
//		pongListenerThread.start();

		SimpleServentListener simpleListener = new SimpleServentListener(pongListener);
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();
		
		CLIParser cliParser = new CLIParser(simpleListener, pongListener);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();
		
		ServentInitializer serventInitializer = new ServentInitializer();
		Thread initializerThread = new Thread(serventInitializer);
		initializerThread.start();
		
	}
}
