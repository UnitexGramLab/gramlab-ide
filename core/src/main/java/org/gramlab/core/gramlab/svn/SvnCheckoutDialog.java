package fr.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.GramlabConfigManager;
import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.config.maven.MvnCommand;
import fr.gramlab.project.config.maven.Pom;
import fr.gramlab.project.config.maven.PomIO;
import fr.gramlab.project.config.preprocess.CreateProjectDialog;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.FrameUtil;
import fr.umlv.unitex.process.ExecParameters;
import fr.umlv.unitex.process.Executor;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SvnCommand;
import fr.umlv.unitex.process.list.ProcessOutputList;
import fr.umlv.unitex.process.list.ProcessOutputListModel;

public class SvnCheckoutDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	JTextField name=new JTextField("");
	DefaultComboBoxModel model;
	JComboBox url;
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	boolean finished=false;
	
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());

	public SvnCheckoutDialog() {
		super(Main.getMainFrame(), "Checking out a SVN project", true);
		JPanel pane=new JPanel(new BorderLayout());
		mainPanel=createMainPanel(false);
		pane.add(mainPanel,BorderLayout.CENTER);
		JPanel down=new JPanel();
		cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executor!=null) {
					executor.interrupt();
				}
				setVisible(false);
				dispose();
			}
		});
		ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished) {
					setVisible(false);
					dispose();
					return;
				}
				stdout.empty();
				stderr.empty();
				if (url.getSelectedItem()==null || url.getSelectedItem().equals("")) {
					JOptionPane.showMessageDialog(null,
							"You must provide a URL !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String URL=(String) url.getSelectedItem();
				String projectName=name.getText();
				if (projectName.equals("")) {
					projectName=getProjectNameFromURL(URL);
					if (projectName==null) {
						JOptionPane.showMessageDialog(null,
								"Invalid URL !", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (projectName.equals("")) {
						JOptionPane.showMessageDialog(null,
								"Your URL does not contain a project directory!", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				if (!CreateProjectDialog.checkName(projectName)) {
					return;
				}
				stdout.addLine(new Couple("Checking out...",false));
				File projectDir=new File(GramlabConfigManager.getWorkspaceDirectory(),projectName);
				SvnCommand c=new SvnCommand().checkout(URL,projectDir);
				if (authPane!=null) {
					char[] tmp=authPane.getPassword();
					c=c.auth(authPane.getLogin(),tmp);
					/* We clear the password array, as suggested in JPasswordField.getPassword() */
					for (int i=0;i<tmp.length;i++) {
						tmp[i]=0;
					}
				}
				ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterCheckoutDo(projectName), false);
				ArrayList<String> list=GramlabConfigManager.getSvnRepositories();
				if (!list.contains(URL)) {
					list.add(0,URL);
					GramlabConfigManager.setSvnRepositories(list);
				}
				lock();
				executor=new Executor(parameters);
				executor.start();
			}
		});
		down.add(cancel);
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		setContentPane(pane);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}

	protected static String getProjectNameFromURL(String url) {
		try {
			URL u=new URL(url);
			String s=u.getPath();
			if (s.endsWith("/")) {
				s=s.substring(0,s.length()-1);
			}
			int i=s.lastIndexOf('/');
			if (i==-1) return "";
			return s.substring(i+1);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private JPanel createMainPanel(boolean askForAuthentication) {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		stderr.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("You must set the URL of the project repository you want"),gbc);
		p.add(new JLabel("to checkout, including the project name. If you provide"),gbc);
		p.add(new JLabel("a target project name, it will override the project name"),gbc);
		p.add(new JLabel("found on the SVN server. Note that you can only checkout"),gbc);
		p.add(new JLabel("a repository that has been created by sharing a Gramlab"),gbc);
		p.add(new JLabel("project with the \"Share on SVN...\" command that is accessible"),gbc);
		p.add(new JLabel("by a right-click on a project node."),gbc);
		p.add(new JLabel(" "),gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Repository: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		ArrayList<String> list=GramlabConfigManager.getSvnRepositories();
		String[] tab = new String[list.size()];
		int i = 0;
		for (String s:list) {
			tab[i++]=s;
		}
		model=new DefaultComboBoxModel(tab);
		url = new JComboBox(model);
		url.setEditable(true);
		p.add(url,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Target name: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(name,gbc);
		if (askForAuthentication) {
			p.add(new JLabel(" "),gbc);
			authPane=new SvnAuthenticationPane();
			p.add(authPane,gbc);
		}
		p.add(new JLabel(" "),gbc);
		JScrollPane scroll=new JScrollPane(stdout);
		scroll.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll,gbc);
		return p;
	}

	
	class AfterCheckoutDo implements ToDo {
		
		private String name1;
		
		AfterCheckoutDo(String name) {
			this.name1=name;
		}
		
		@Override
		public void toDo(boolean success) {
			SvnCommandResult r;
			if (success) {
				r=new SvnCommandResult(SvnOpResult.OK,"");
			} else {
				r=SvnExecutor.getSvnError(stderr);
			}
			switch (r.getOp()) {
			case OK: {
				final GramlabProject project=new GramlabProject(name1,null,null,null);
				if (!project.getPom().getFile().exists()
						|| !project.getPreferencesFile().exists()
						|| !project.getProjectVersionableConfigFile().exists()) {
					JOptionPane.showMessageDialog(null,
							"Some configuration files are missing. It seems that you did\n"+
							"not checkout a valid Gramlab project."+r.getErr(), "Error",
							JOptionPane.ERROR_MESSAGE);
					GlobalProjectManager.getAs(GramlabProjectManager.class)
						.deleteProject(project,false);
					unlock(true);
					return;
				}
				project.ensureDirectoriesExist();
				getDependencies(project);
				return;
			}
			case AUTHENTICATION_REQUIRED: {
				getContentPane().remove(mainPanel);
				mainPanel=createMainPanel(true);
				getContentPane().add(mainPanel,BorderLayout.CENTER);
				mainPanel.revalidate();
				repaint();
				pack();
				unlock(false);
				return;
			}
			default: {
				JOptionPane.showMessageDialog(null,
						"Something unexpected occurred:\n\n"+r.getErr(), "Error",
						JOptionPane.ERROR_MESSAGE);
				unlock(false);
				return;
			}
		}
	}
	}


	/**
	 * Get the maven dependencies.
	 */
	public void getDependencies(final GramlabProject project) {
		Pom POM=project.getPom();
		POM.loadFromFile();
		if (POM.getDependencies().size()==0) {
			/* There is no need to run maven if there is no dependency */
			GlobalProjectManager.getAs(GramlabProjectManager.class)
				.addProject(project);
			GlobalProjectManager.getAs(GramlabProjectManager.class)
				.openProject(project);
			stdout.addLine(new Couple("Project created successfully.",false));
			unlock(true);
			return;
		}
		final File dep=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		FileUtil.setRecursivelyWritable(dep);
		MultiCommands cmd=POM.getUnpackDependenciesCommand();
		ToDo myDO=new ToDo() {
			@Override
			public void toDo(boolean success) {
				FileUtil.setRecursivelyReadOnly(dep);
				executor=null;
				if (success) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.addProject(project);
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.openProject(project);							
							finished=true;
							stdout.addLine(new Couple("Project created successfully.",false));
							unlock(true);
						}
					});
				} else {
					if (!MvnCommand.mvnInstalled()) {
						stdout.addLine(new Couple("The project you tried to checkout contains maven",false));
						stdout.addLine(new Couple("dependencies that cannot be retrieved because maven",false));
						stdout.addLine(new Couple("is not installed. Please install it and then try to",false));
						stdout.addLine(new Couple("checkout again.",false));
					} else {
						stdout.addLine(new Couple("Some components could not be retrieved.",false));
						stdout.addLine(new Couple("This could be caused by a network problem, by",false));
						stdout.addLine(new Couple("an error in your maven settings, or because",false));
						stdout.addLine(new Couple("one of the components' description is invalid.",false));
					}
					GlobalProjectManager.getAs(GramlabProjectManager.class)
						.deleteProject(project,false);
					unlock(false);
				}
			}

		};
		ExecParameters parameters=new ExecParameters(true,
				cmd, stdout, stderr, myDO, false);
		executor=new Executor(parameters);
		executor.start();
	}
	
	void lock() {
		ok.setEnabled(false);
		name.setEnabled(false);
		url.setEnabled(false);
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			name.setEnabled(true);
			url.setEnabled(true);
			cancel.setEnabled(true);
		}
		finished=success;
	}
	
}
