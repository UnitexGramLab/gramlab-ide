package fr.umlv.unitex.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import fr.umlv.unitex.frames.UnitexFrame;


/**
 * This class ensures only one instance of the IDE can run at one time
 * @author markpower
 *
 */
public class SingleInstanceMonitor {

	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static final String DELIMITER = ";";
	private static boolean SOCKET_OPEN = false;
	private static final String PORT_FILE_NAME = "port_file.txt";
	
	static {
		// Adds a shutdown hook to release resources at JVM shutdown.
		Runtime rt = Runtime.getRuntime();
		rt.addShutdownHook(new Thread(new ShutdownHook()));
	}
	
	public static boolean isRunning(final String[] args) {
    	try {
    		// set the app path so we know where to store the port file
    		Config.setAppPath(args);
    		
    		File f = getPortFile();
			
			if (f.exists()) {
				//System.err.println("IDE instance already running.");
				clientSocketListener(args);
				return true;
			}
			else {
				// Issue #27 use ServerSocket to restrict IDE to single instance and to allow failed instance to pass file name to running instance.
				// Run in SwingWorker background thread so doesn't block main thread
				//serverSocket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));  
				serverSocket = new ServerSocket();
				SocketAddress socketAddress = new InetSocketAddress("localhost", 0);
				serverSocket.bind(socketAddress); 
				SOCKET_OPEN = true;
				
				SwingWorker<String, Void> socketThread = new SwingWorker<String, Void>() { 
					
		            @Override
		            public String doInBackground() { 
		            	while (SOCKET_OPEN) {
		            		serverSocketListener();
		            	}
		                return "";
		            }	
		        };
		        socketThread.execute(); 
		        writePortFile(serverSocket.getLocalPort());
				return false;
			}
		} catch (BindException e) {
			System.err.println("IDE instance already running.");
			e.printStackTrace();
			clientSocketListener(args);
			return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return true;
	    }
    }
    
    private static File getPortFile() {
    	return new File(Config.getApplicationDir(), PORT_FILE_NAME);
    }
    
    private static void writePortFile(int portNumber) {
    	Writer portWriter = null;
		try {
			portWriter = new FileWriter(getPortFile());
			portWriter.write(String.valueOf(portNumber));
			portWriter.flush();
		} catch (Throwable t) {
			System.err.println("Error writing to port file for single instance listener running on port " + portNumber);
		} finally {
			if (portWriter != null) {
				try {
					portWriter.close();
				} catch (Throwable t) {
					;
				}
			}
		}
    }
    
    private static int readPortFile() {
    	int port = -1;
    	BufferedReader reader = null;
    	try {
    		reader = new BufferedReader(new FileReader(getPortFile()));
    		String line = reader.readLine();
    		if (line != null) {
    			port = Integer.parseInt(line);
    		}
    	} catch (Throwable t) {
    		System.err.println("Error reading port file.");
    	} finally {
    		if (reader != null) {
    			try {
    				reader.close();
    			} catch (Throwable t) {
    				;
    			}
    		}
    	}
    	return port;
    }
    
   
    
//    public static boolean isRunning(final String[] args) {
//    	System.out.println("unitexDir = " + getUnitexDir());
//    	String appId = "UnitexGramLab";
//    	boolean alreadyRunning;
//    	
//    	try {
//			JUnique.acquireLock(appId, new MessageHandler() {
//				public String handle(String message) {
//					if (message != null && message.length() > 0) {  
//						openGraphFiles(stringToArray(message, DELIMITER));
//						if (UnitexFrame.mainFrame.isVisible()) {
//							UnitexFrame.mainFrame.setVisible(true);
//						}
//					}       
//					return null;
//				}
//			});
//			alreadyRunning = false;
//		} catch (AlreadyLockedException e) {
//			System.err.println("IDE instance already running.");
//			alreadyRunning = true;
//	    }
//    	
//    	if (alreadyRunning) {
//    		JUnique.sendMessage(appId, arrayToString(args, DELIMITER));
//    	}
//    	return alreadyRunning;
//    }
    
 // Server socket - listens for connection from failed instance startup
 	public static void serverSocketListener() { 
 		try {
 			if (!serverSocket.isClosed()) {
 				socket = serverSocket.accept();
 				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

 				String str = in.readLine();
 				
 				if (str != null && str.length() > 0) {  
 					Config.openGraphFiles(stringToArray(str, DELIMITER));
 					if (UnitexFrame.mainFrame.isVisible()) {
 						UnitexFrame.mainFrame.setVisible(true);
 					}
 				}       
 			}
 		} catch (SocketException ex) {
 			// ignore socket closed exception if we've closed it
 			if (SOCKET_OPEN) {
 				ex.printStackTrace();
 			}
 		} catch (IOException e) {
 			e.printStackTrace();
 			System.exit(-1);
 		}
 	}

 	// Client socket sends graph file name(s) to running process 
 	public static void clientSocketListener(String[] args) { 
 		int port = readPortFile();
 		try {
 			socket = new Socket(InetAddress.getByAddress( new byte[] {127,0,0,1}), port);
 			PrintWriter out = new PrintWriter(socket.getOutputStream(), true );
 			out.println(arrayToString(args, DELIMITER));
 			System.err.println("IDE instance already running.");
 		} catch (IOException e) {
 			System.err.println("Exception opening client socket listener on port " + port + ".\n"
 					+ "The IDE may not have shut down correctly. Please try again.");
 			// the port file is out of date so delete it
 			System.out.println("client socket calling delete port file...");
 			deletePortFile();
 			//e.printStackTrace();
 			System.exit(1);
 		} finally {
 			try {
 				socket.close();
 			} catch (Exception ex) {
 				System.err.println("Exception closing client socket on port " + port);
 				ex.printStackTrace();
 			}
 		}
 	}
 	
 	public static void closeSocket() {
 		SOCKET_OPEN = false;
 		try {
 			if (serverSocket != null) {
 				serverSocket.close();
 			}
 		} catch (IOException ex) {
 			System.err.println("Exception closing server socket on port ");
 			ex.printStackTrace();
 		}
 	}
 	
 	private static void deletePortFile() {
 		System.out.println("Deleting port file...");
 		File f = getPortFile();
 		if (f.exists()) {
 			f.delete();
 		}
 	}
 	
 	// TODO test this already noticed port file is being deleted when second instance JVM shuts down 
 	// which allows the third instance to start up. Suspect there is also socket comm probs on 3rd instance
 	// the reason for this was to ensure the file was deleted when Apple close was used to exit
 	private static class ShutdownHook implements Runnable {

 		public void run() {
 			System.out.println("shutdown hook.....");
 			closeSocket();
 			//deletePortFile();
 		}

 	}
 	
 	// Utility methods to convert String[] to delimited string and back again
 	public static String arrayToString(String[] arr, String delim) {
 		String ret = "";
 		
 		if (arr != null && arr.length > 0) {
 			for (int i = 0; i < arr.length; i++) {
 				ret += arr[i] + delim;
 			}
 			ret = ret.substring(0,ret.lastIndexOf(delim));
 		}
 		return ret;
 	}
 	
 	public static String[] stringToArray(String str, String delim) {
 		ArrayList<String> list = new ArrayList<String>(0);
 		
 		int sidx = 0;
 		int eidx = str.indexOf(delim);
 		
 		while (eidx != -1) {
 			list.add(str.substring(sidx, eidx));
 			sidx = eidx + 1;
 			eidx = str.indexOf(" ", sidx);
 		}
 		list.add(str.substring(sidx));
 		
 		String[] ret = new String[list.size()];
 		list.toArray(ret);
 		
 		return ret;
 	}
}
