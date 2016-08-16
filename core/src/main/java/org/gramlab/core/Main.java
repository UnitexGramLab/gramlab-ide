package org.gramlab.core;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.OceanTheme;

import org.gramlab.core.gramlab.frames.ChangePerspective;
import org.gramlab.core.gramlab.frames.GramlabFrame;
import org.gramlab.core.gramlab.icons.Icons;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.FontInfo;
import org.gramlab.core.umlv.unitex.Unitex;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.Preferences;
import org.gramlab.core.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import org.gramlab.core.umlv.unitex.frames.SplashScreen;
import org.gramlab.core.umlv.unitex.frames.UnitexFrame;


public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				choosePerspective(args);				
			}
		});
	}
	
	static void choosePerspective(final String[] args) {
        
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
		final SplashScreen splash = new SplashScreen(new ImageIcon(Icons.class.getResource("logo.png")));
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
								new ChangePerspective("none",args );
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
	
	private static JFrame frame = null;
	
	public static JFrame getProjectorientedMainFrame() {
		return frame;
	}
	
	public static void launchGramlab(String[] args) {
		
		File path=null;
		if (args!=null && args.length==1) {
			path=new File(args[0]);
		}
        final File path1=path;
        EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GlobalProjectManager(new GramlabProjectManager());
				GramlabConfigManager.initConfig(path1);
				frame = new GramlabFrame();
				final Component glass=frame.getGlassPane();
				final JPanel p=new JPanel(new GridBagLayout());
				frame.setGlassPane(p);
				frame.getGlassPane().setVisible(true);
		        frame.setVisible(true);
				frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		        frame.repaint();
		        EventQueue.invokeLater(new Runnable() {
		        	@Override
		        	public void run() {
		        		p.add(new JLabel(Icons.logo),null);
		        		GlobalProjectManager.getAs(GramlabProjectManager.class).loadProjects();
						Timer t=new Timer(2000,new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								frame.setGlassPane(glass);
							}
						});
						t.setRepeats(false);
						t.start();
		        	}
		        });
			}
		});        
 	}
	
	private static UnitexFrame classicFrame=null;
	
	public static UnitexFrame getClassicMainFrame() {
		return classicFrame;
	}
	
	/**
	 * Starts Unitex. Shows a <code>SplashScreen</code> with the Unitex logo and
	 * then creates a <code>UnitexFrame</code>.
	 */
	public static void launchUnitex(final String[] args) {
		
		String path=null;
		if (args!=null && args.length==1) {
			path=args[0];
		}
		final String path1 = path;
		final SplashScreen splash = new SplashScreen(new ImageIcon(Icons.class.getResource("logo.png")));
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
								Config.initConfig(path1);
								Preferences preferences = ConfigManager.getManager().getPreferences(null);
								FontInfo menuFontInfo = preferences.getMenuFont();
								setUIFont(new javax.swing.plaf.FontUIResource(menuFontInfo.getFont().toString(), Font.PLAIN, menuFontInfo.getSize()));

								classicFrame = new UnitexFrame();
								final Image img16x16 = new ImageIcon(
										Unitex.class.getResource("16x16.png"))
										.getImage();
								final Image img32x32 = new ImageIcon(
										Unitex.class.getResource("32x32.png"))
										.getImage();
								final Image img48x48 = new ImageIcon(
										Unitex.class.getResource("48x48.png"))
										.getImage();
								classicFrame.setIconImages(Arrays.asList(img16x16,
										img32x32, img48x48));
								classicFrame.setVisible(true);
								ConfigManager.getManager().getSvnMonitor(null)
										.start();
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
	
	public static JFrame getMainFrame() {
		if(getProjectorientedMainFrame()!=null){
			return frame;
		}
		else{
			return classicFrame;
		}
	}
	
}
