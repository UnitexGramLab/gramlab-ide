package fr.gramlab;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.OceanTheme;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.gramlab.frames.GramlabFrame;
import fr.gramlab.icons.Icons;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.util.GramlabSingleInstanceMonitor;
import fr.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import fr.umlv.unitex.utils.SingleInstanceMonitor;


public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		SingleInstanceMonitor sim = new GramlabSingleInstanceMonitor();
		
		if (!sim.isRunning(args)) {
			Config.setSingleInstanceMonitor(sim);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					launchGramlab(args);
				}
			});
		}
		
	}

	private static GramlabFrame frame;
	
	protected static void launchGramlab(final String[] args) {
		Thread.currentThread().setUncaughtExceptionHandler(
				UnitexUncaughtExceptionHandler.getHandler());
		Locale.setDefault(Locale.ENGLISH);
		try {
			javax.swing.plaf.metal.MetalLookAndFeel
					.setCurrentTheme(new OceanTheme());
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
			System.err
					.println("Ocean Theme not supported on this platform. \nProgram Terminated");
			System.exit(0);
		} catch (IllegalAccessException e) {
			System.err
					.println("Ocean Theme could not be accessed. \nProgram Terminated");
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.err
					.println("Your version of Java does not contain all the classes required by GramLab.\nProgram Terminated");
			System.exit(0);
		} catch (InstantiationException e) {
			System.err
					.println("Ocean Theme can not be instantiated. \nProgram Terminated");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Unexpected error. \nProgram Terminated");
			e.printStackTrace();
			System.exit(0);
		}
		File path=Config.getAppPath(args);

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

	public static GramlabFrame getMainFrame() {
		return frame;
	}
	
}
