package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BootstrapServerTest {

	private volatile boolean working = true;
	private List<Integer> activeServents;

	private class CLIWorker implements Runnable {
		@Override
		public void run() {
			Scanner sc = new Scanner(System.in);

			String line;
			while(true) {
				line = sc.nextLine();

				if (line.equals("stop")) {
					working = false;
					break;
				}
			}

			sc.close();
		}
	}

	public BootstrapServerTest() {
		activeServents = new ArrayList<>();
	}
	
	public void doBootstrap(int bsPort) {
		Thread cliThread = new Thread(new CLIWorker());
		cliThread.start();
		
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(bsPort);
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e1) {
			AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
			System.exit(0);
		}
		
		Random rand = new Random(System.currentTimeMillis());
		
		while (working) {
			try {
				Socket newServentSocket = listenerSocket.accept();
				
				 /* 
				 * Handling these messages is intentionally sequential, to avoid problems with
				 * concurrent initial starts.
				 * 
				 * In practice, we would have an always-active backbone of servents to avoid this problem.
				 */
				
				Scanner socketScanner = new Scanner(newServentSocket.getInputStream());
				String message = socketScanner.nextLine();
				
				/*
				 * New servent has hailed us. He is sending us his own listener port.
				 * He wants to get a listener port from a random active servent,
				 * or -1 if he is the first one.
				 */
				if (message.equals("Hail")) {
					int newServentPort = socketScanner.nextInt();
					
					System.out.println("got " + newServentPort);
					PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
					
					if (activeServents.size() == 0) {
						socketWriter.write(String.valueOf(-1) + "\n");
						activeServents.add(newServentPort); //first one doesn't need to confirm
					} else {
						int randServent = activeServents.get(rand.nextInt(activeServents.size()));
						socketWriter.write(String.valueOf(randServent) + "\n");
					}
					
					socketWriter.flush();
					newServentSocket.close();
				} else if (message.equals("New")) {
					/**
					 * When a servent is confirmed not to be a collider, we add him to the list.
					 */
					int newServentPort = socketScanner.nextInt();
					
					System.out.println("adding " + newServentPort);
					
					activeServents.add(newServentPort);
					newServentSocket.close();
				} else if (message.equals("Kill")) {
					//TODO Remove node
					int nodeToKillPort = socketScanner.nextInt();
					System.out.println("Killing node: " + nodeToKillPort);
				}
				
			} catch (SocketTimeoutException e) {
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expects one command line argument - the port to listen on.
	 */
	public static void main(String[] args) {
		
		int bsPort = 2000;
		
		AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);
		
		BootstrapServerTest bs = new BootstrapServerTest();
		bs.doBootstrap(bsPort);
	}
}
