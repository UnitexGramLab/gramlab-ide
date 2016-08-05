package fr.gramlab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.config.ProjectPreferences;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;

import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

public class GramlabConfigManager {
	
	private static GramlabProject currentProject;
	private static int system = -1;
	private static String user;
	private static File workspace=null;
	private static File configFile=null;
	
	private static ArrayList<String> previousOpenProjects=new ArrayList<String>();
	private static String previousCurrentProject;
	private static ArrayList<String> svnRepositories=new ArrayList<String>();

  /**
   * PF4J plugin manager
   */
  private static DefaultPluginManager pluginManager;  

	public static void initConfig(File path) {
		system = Config.getSystem();
		if (path==null) {
			path = new File(System.getProperty("user.dir"));
		}

		ConfigManager.setManager(new ProjectPreferences(path));
		setCurrentProject(null);
		user = System.getProperty("user.name");

    // start the plugin manager
    startPluginManager(new File(path, Config.DEFAULT_PLUGINS_DIRECTORY));
        
		findWorkspace();
	}

	private static void findWorkspace() {
		File directory = new File(
				ConfigManager.getManager().getMainDirectory(), "Users");
		if (!directory.exists()) {
			directory.mkdir();
		}
		if (system == Config.WINDOWS_SYSTEM) {
			/* Configuration procedure under Windows */
			configFile = new File(new File(ConfigManager.getManager()
					.getMainDirectory(), "Users"), user + ".gramlab");
			if (!configFile.exists() || !configFile.getParentFile().canWrite()) {
				/* Windows 7 forbids writing in Users if Unitex is in 'Program Files',
				 * so we try to look for a home dir
				 */
				if (System.getProperty("user.home")!=null) {
					configFile = new File(System.getProperty("user.home"), ".gramlab");
				} else {
					JOptionPane.showMessageDialog(null, "Unable to find a consistent writable location\nto store your configuration file", "Welcome",
							JOptionPane.PLAIN_MESSAGE);
					System.exit(1);
				}
			}
		} else {
			/* For other systems, we look for .gramlab in the home directory */
			configFile = new File(System.getProperty("user.home"), ".gramlab");
		}
		if (!configFile.exists()) {
			setWorkspaceDirectory(chooseNewUserWorkspace(),true);
			/* If the workspace was created, we are done */
		} else {
			readConfigFile(configFile);
		}
	}

  /**
   * Starts the Plugin Manager
   * @author martinec
   */
  private static void startPluginManager(File path) {
    File pluginsDirectory = path;

    // create the plugins directory if doesn't exist
    pluginsDirectory.mkdirs();

    // create the plugin manager
    pluginManager = new DefaultPluginManager(pluginsDirectory);

    // load the plugins
    pluginManager.loadPlugins();

    // start (active/resolved) the plugins
    pluginManager.startPlugins();
  }
    
	private static void readConfigFile(File f) {
		try {
			svnRepositories.clear();
			Scanner scanner=new Scanner(f,"UTF8");
			if (!scanner.hasNextLine()) {
				System.out.println("Error: " + configFile + " is empty!");
				System.exit(1);
			}
			String line=scanner.nextLine();
			if (line.startsWith("svn_repositories: ")) {
				readSvnRepositories(line,scanner);
				line=scanner.nextLine();
			}
			workspace=new File(line);
			if (!workspace.exists()) {
				JOptionPane.showMessageDialog(null, "Unable to find your workspace directory:\n\n"
						+workspace.getAbsolutePath()+
						"\n\nPlease select a new one.", "Welcome",
						JOptionPane.ERROR_MESSAGE);
				setWorkspaceDirectory(chooseNewUserWorkspace(),true);
				return;
			}
			previousCurrentProject=null;
			previousOpenProjects.clear();
			if (scanner.hasNextLine()) {
				/* We read the current project */
				previousCurrentProject=scanner.nextLine();
			}
			/* And the other opened projects */
			while (scanner.hasNextLine()) {
				previousOpenProjects.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: invalid configuration file " + configFile);
			System.exit(1);
		}
	}

	private static void readSvnRepositories(String line, Scanner scanner) {
		Scanner lineScanner=new Scanner(line);
		lineScanner.next();
		if (!lineScanner.hasNextInt()) {
			System.out.println("Error: invalid line in gramlab configuration file:\n"+line);
			System.exit(1);
		}
		int n=lineScanner.nextInt();
		for (int i=0;i<n;i++) {
			svnRepositories.add(scanner.nextLine());
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

	public static void setCurrentProject(GramlabProject p) {
		currentProject=p;
	}
	
	public static File getWorkspaceDirectory() {
		return workspace;
	}  
	
	public static void saveConfigFile() {
		setWorkspaceDirectory(workspace,true);
	}
	
	public static void setWorkspaceDirectory(File dir,boolean saveConfigFile) {
		workspace=dir;
		if (!saveConfigFile) return;
		try {
			configFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					configFile));
			String s;
			s="svn_repositories: "+svnRepositories.size()+"\n";
			bw.write(s, 0, s.length());
			for (String repo:svnRepositories) {
				s=repo+"\n";
				bw.write(s, 0, s.length());
			}
			s = workspace.getAbsolutePath()+"\n";
			bw.write(s, 0, s.length());
			GramlabProject current=GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject();
			if (current!=null) {
				/* We save the current project */
				s=current.getName()+"\n";
				bw.write(s, 0, s.length());
			}
			for (GramlabProject p:GlobalProjectManager.getAs(GramlabProjectManager.class).getProjects()) {
				if (!p.equals(current) && p.isOpen()) {
					s=p.getName()+"\n";
					bw.write(s, 0, s.length());
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static GramlabProject getCurrentProject() {
		return currentProject;
	}

  public static DefaultPluginManager getPluginManager() {
		return pluginManager;
	}

	public static ArrayList<String> getPreviousOpenProjects() {
		return previousOpenProjects;
	}

	public static String getPreviousCurrentProject() {
		return previousCurrentProject;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getSvnRepositories() {
		return (ArrayList<String>) svnRepositories.clone();
	}
	
	@SuppressWarnings("unchecked")
	public static void setSvnRepositories(ArrayList<String> repositories) {
		svnRepositories=(ArrayList<String>) repositories.clone();
	}
}
