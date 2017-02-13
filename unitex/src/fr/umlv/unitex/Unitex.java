/*
 * Unitex
 *
 * Copyright (C) 2001-2017 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.OceanTheme;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.frames.SplashScreen;
import fr.umlv.unitex.frames.UnitexFrame;

/**
 * This is the main class of the Unitex system.
 * 
 * @author Sébastien Paumier
 */
public class Unitex {
	private static final int PORT = 9999;
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static final String DELIMITER = ";";
	private static boolean SOCKET_OPEN = false;
	
	
	/**
	 * This is used to know whether Unitex code is called from Unitex or from Gramlab 
	 */
	private static boolean running=false;
	
	public static boolean isRunning() {
		return running;
	}
	
	public static void main(final String[] args) {
		running=true;
		try {
			// Issue #27 use ServerSocket to restrict IDE to single instance and to allow failed instance to pass file name to running instance.
			// Run in SwingWorker background thread so doesn't block main thread
			serverSocket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));  
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
			
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					launchUnitex(args);
				}
			});
		} catch (BindException e) {
			System.err.println("IDE instance already running.");
			clientSocketListener(args);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	/**
	 * Starts Unitex. Shows a <code>SplashScreen</code> with the Unitex logo and
	 * then creates a <code>UnitexFrame</code>.
	 */
	static void launchUnitex(final String[] args) {
		Thread.currentThread().setUncaughtExceptionHandler(
				UnitexUncaughtExceptionHandler.getHandler());
		Locale.setDefault(Locale.ENGLISH);
		try {
			javax.swing.plaf.metal.MetalLookAndFeel
					.setCurrentTheme(new OceanTheme());
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (final UnsupportedLookAndFeelException e) {
			System.err
					.println("Ocean Theme not supported on this platform. \nProgram Terminated");
			System.exit(0);
		} catch (final IllegalAccessException e) {
			System.err
					.println("Ocean Theme could not be accessed. \nProgram Terminated");
			System.exit(0);
		} catch (final ClassNotFoundException e) {
			System.err
					.println("Your version of Java does not contain all the classes required by Unitex.\nProgram Terminated");
			System.exit(0);
		} catch (final InstantiationException e) {
			System.err
					.println("Ocean Theme can not be instantiated. \nProgram Terminated");
			System.exit(0);
		} catch (final Exception e) {
			System.err.println("Unexpected error. \nProgram Terminated");
			e.printStackTrace();
			System.exit(0);
		}
		final SplashScreen splash = new SplashScreen(new ImageIcon(
				Unitex.class.getResource("Unitex.jpg")));
		splash.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				final Timer timer = new Timer(1500, null);
				timer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e1) {
						splash.dispose();
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								ConfigManager.setManager(new ConfigManager());
								Config.initConfig(args);
								Preferences preferences = ConfigManager.getManager().getPreferences(null);
								FontInfo menuFontInfo = preferences.getMenuFont();
								setUIFont(new javax.swing.plaf.FontUIResource(menuFontInfo.getFont().toString(), Font.PLAIN, menuFontInfo.getSize()));

								final JFrame frame = new UnitexFrame();
								final Image img16x16 = new ImageIcon(
										Unitex.class.getResource("16x16.png"))
										.getImage();
								final Image img32x32 = new ImageIcon(
										Unitex.class.getResource("32x32.png"))
										.getImage();
								final Image img48x48 = new ImageIcon(
										Unitex.class.getResource("48x48.png"))
										.getImage();
								frame.setIconImages(Arrays.asList(img16x16,
										img32x32, img48x48));
								frame.setVisible(true);
								ConfigManager.getManager().getSvnMonitor(null)
										.start();
								openGraphFiles(args);
							}
						});
						timer.stop();
					}
				});
				timer.start();
			}
		});
		splash.setVisible(true);
	}

	public static void setUIFont (javax.swing.plaf.FontUIResource f){
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	}
	
	/**
     * Extracts the graph files from args passed to application on startup and opens graph frames
     * Issue #27
     */
    public static void openGraphFiles(String[] args) {
    	for (int i = 0; i < args.length; i++) {
    		if (args[i].indexOf("=") == -1) {
    			File f = new File(args[i]);
    			
    			// f.getAbsoluleFile ensures we initialize the full path in cases where file name only is passed
    			GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.newGraphFrame(f.getAbsoluteFile());
    		}		
    	}
    }
	
	// Server socket - listens for connection from failed instance startup
	public static void serverSocketListener() { 
		try {
			socket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String str = in.readLine();
			
			if (str != null && str.length() > 0) {  
				openGraphFiles(stringToArray(str, DELIMITER));
				if (UnitexFrame.mainFrame.isVisible()) {
					UnitexFrame.mainFrame.setVisible(true);
				}
			}       
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// Client socket sends graph file name(s) to running process 
	public static void clientSocketListener(String[] args) { 
		try {
			socket = new Socket(InetAddress.getByAddress( new byte[] {127,0,0,1}), PORT );
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true );
			out.println(arrayToString(args, DELIMITER));
		} catch  (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		} finally {
			try {
				socket.close();
			} catch (IOException ex) {
				System.err.println("Exception closing client socket on port " + PORT);
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
			System.err.println("Exception closing server socket on port " + PORT);
			ex.printStackTrace();
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
