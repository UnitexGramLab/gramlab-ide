package fr.gramlab.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fr.gramlab.workspace.Project;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.ConfigModel;

public class GramlabConfigManager {
	
	private static ConfigModel defaultConfig;
	private static Project currentProject;
	private static int system = -1;
	private static String user;
	private static File workspace;

	public static void initConfig() {
		system = Config.getSystem();
		File jarPath = new File(System.getProperty("user.dir"));
		defaultConfig = new DefaultConfig(jarPath);
		setCurrentProject(null);
		user = System.getProperty("user.name");
		findWorkspace();
	}

	private static void findWorkspace() {
		File directory = new File(
				ConfigManager.getManager().getMainDirectory(), "Users");
		if (!directory.exists()) {
			directory.mkdir();
		}
		File configFile;
		if (system == Config.WINDOWS_SYSTEM) {
			/* Configuration procedure under Windows */
			configFile = new File(new File(ConfigManager.getManager()
					.getMainDirectory(), "Users"), user + ".gramlab");
		} else {
			/* For other systems, we look for .gramlab in the home directory */
			configFile = new File(System.getProperty("user.home"), ".gramlab");
		}
		if (!configFile.exists()) {
			try {
				workspace = chooseNewUserWorkspace();
				configFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						configFile));
				String s = workspace.getAbsolutePath();
				bw.write(s, 0, s.length());
				bw.close();
				/* If the workspace was created, we welcome the user */
				String message = "Welcome " + user + "!\n\n";
				message = message
						+ "Your Gramlab workspace is:\n\n";
				message = message + workspace.getAbsolutePath() + "\n";
				JOptionPane.showMessageDialog(null, message, "Welcome",
						JOptionPane.PLAIN_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						configFile));
				String s = br.readLine();
				if (s == null || s.equals("")) {
					System.out.println("Error: " + configFile + " is empty!");
					System.exit(1);
				}
				workspace = new File(s);
				br.close();
			} catch (IOException e) {
				System.out.println("Error: " + configFile + " is empty!");
				System.exit(1);
			}
		}
	}

	private static File chooseNewUserWorkspace() {
		String message = "Welcome " + user + "!\n\n";
		message = message
				+ "To use Gramlab, you must choose a workspace\ndirectory to store your data ";
		message = message + "(that you\ncan change later if you want).";
		message = message + "\n\nClick on OK to choose your directory.";
		JOptionPane.showMessageDialog(null, message, "Welcome",
				JOptionPane.PLAIN_MESSAGE);
		JFileChooser f = new JFileChooser();
		f.setDialogTitle("Choose your workspace directory");
		f.setDialogType(JFileChooser.OPEN_DIALOG);
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION || 
				f.getSelectedFile().equals(ConfigManager.getManager().getMainDirectory())) {
			if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				message = "You must choose a workspace directory.\n\n";
				message = message + "Click on OK to select one or on\n";
				message = message + "Cancel to exit.";
			} else {
				message = "You cannot choose the Gramlab directory as your workspace one";
			}
			final String[] options = { "OK", "Cancel" };
			int n = JOptionPane.showOptionDialog(null, message, "Error",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
					options, options[0]);
			if (n == 1)
				System.exit(0);
		}
		return f.getSelectedFile();
	}

	public static void setCurrentProject(Project p) {
		ConfigModel currentProjectConfig;
		if (p == null) {
			currentProjectConfig = defaultConfig;
		} else {
			currentProjectConfig = p.getConfigModel();
		}
		currentProject=p;
		ConfigManager.setManager(currentProjectConfig);
	}
	
	
	public static File getWorkspaceDirectory() {
		return workspace;
	}
	
	public static Project getCurrentProject() {
		return currentProject;
	}
	
}
