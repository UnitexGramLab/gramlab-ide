package org.gramlab.core.gramlab.project.config.preprocess;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.ProjectVersionableConfig;
import org.gramlab.core.gramlab.project.config.maven.Artifact;
import org.gramlab.core.gramlab.project.config.maven.MvnCommand;
import org.gramlab.core.gramlab.project.config.maven.PomIO;
import org.gramlab.core.umlv.unitex.config.NamedRepository;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class GetDependenciesPaneFactory extends ConfigurationPaneFactory {

	Executor executor=null;
	private boolean dependenciesSuccessfullyRetrieved=false;
	
	public GetDependenciesPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Retrieving components"));
		dependenciesSuccessfullyRetrieved=false;
		final GridBagConstraints gbc=new GridBagConstraints();
		final ProcessOutputList list=new ProcessOutputList(new ProcessOutputListModel());
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Couple c=(Couple)value;
				return super.getListCellRendererComponent(list, c.getString(), index, isSelected,
						cellHasFocus);
			}
		});
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=1;
		gbc.weighty=1;
		add(new JScrollPane(list),gbc);
		/* a panel to insert text after completion of the maven command */
		final JPanel p2=new JPanel(new GridBagLayout());
		addText(p2," ","Please wait while maven retrieves the components",
	       "your project uses..."," ");
		gbc.weightx=0;
		gbc.weighty=0;
		add(p2,gbc);
		// down button panel
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dependenciesSuccessfullyRetrieved=false;
				if (executor!=null) {
					executor.interrupt();
				}
				setVisible(false);
			}
		});
		final JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		ok.setEnabled(false);
		
		gbc.fill=GridBagConstraints.NONE;
		Timer t=new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final File dep=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
				FileUtil.setRecursivelyWritable(dep);
				MultiCommands cmd=project.getPom().getUnpackDependenciesCommand();
				ToDo myDO=new ToDo() {
					@Override
					public void toDo(boolean success) {
						FileUtil.setRecursivelyReadOnly(dep);
						dependenciesSuccessfullyRetrieved=success;
						executor=null;
						p2.removeAll();
						if (success) {
							if (!getRepositories(project)) {
								addText(p2," ","Error with repositories.",
									       " ");
								dependenciesSuccessfullyRetrieved=false;
								return;
							}
							ok.setEnabled(true);
							addText(p2," ","All the components were successfully retrieved",
									       " ");
						} else if (!MvnCommand.mvnInstalled()) {
							addText(p2," ","Maven is not installed. You must install it before",
									       "using dependencies in your projects.",
									       " ");
						} else {
							addText(p2," ","Some components could not be retrieved.",
									       "This could be caused by a network problem, by",
									       "an error in your maven settings, or because",
									       "one of your components' description is invalid.",
									       " ");
						}
					}



				};
				ExecParameters parameters=new ExecParameters(true,
						cmd, list, list, myDO, false);
				executor=new Executor(parameters);
				executor.start();
			}
		});
		t.setRepeats(false);
		t.start();
	}

	public static boolean getRepositories(GramlabProject project) {
		ProjectVersionableConfig cfg=new ProjectVersionableConfig(project);
		cfg.setDefaultGraphRepository(project.getDefaultGraphRepository());
		cfg.setNamedRepositories(project.getNamedRepositories());
		for (Artifact a:project.getPom().getDependencies()) {
			if (!getRepositories(project,a,cfg)) {
				return false;
			}
		}
		project.setDefaultGraphRepository(cfg.getDefaultGraphRepository());
		project.setNamedRepositories(cfg.getNamedRepositories());
		return true;
	}

	/**
	 * We read the 'repositories' file in the given dependency and try
	 * to merge its repositories with the current project's ones.
	 */
	private static boolean getRepositories(GramlabProject project,Artifact a,ProjectVersionableConfig cfg) {
		File depDir=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		File foo=new File(new File(depDir,project.getName()),"repositories");
		ProjectVersionableConfig cfg2=ProjectVersionableConfig.load(project,foo);
		if (cfg2.getDefaultGraphRepository()!=null) {
			if (cfg.getDefaultGraphRepository()!=null && !cfg2.equals(cfg.getDefaultGraphRepository())) {
				JOptionPane.showMessageDialog(null,
						"The default graph repository "+project.getRelativeFileName(cfg2.getDefaultGraphRepository())+"\n"
						+"defined in the dependency "+project.getName()+"\n"
						+"is conflicting with previous definition: "+project.getRelativeFileName(cfg.getDefaultGraphRepository())+".\n", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			cfg.setDefaultGraphRepository(cfg2.getDefaultGraphRepository());
		}
		for (NamedRepository nr:cfg2.getNamedRepositories()) {
			if (namedRepositoryConflict(project.getName(),nr,cfg.getNamedRepositories(),project)) {
				return false;
			}
		}
		return true;
	}

	private static boolean namedRepositoryConflict(
			String AV,NamedRepository nr,
			ArrayList<NamedRepository> namedRepositories,GramlabProject project) {
		for (NamedRepository foo:namedRepositories) {
			if (foo.getName().equals(nr.getName())) {
				/* Same name => conflict ? */
				if (foo.getFile().equals(nr.getFile())) {
					return false;
				}
				JOptionPane.showMessageDialog(null,
						"The named repository "+nr.getName()+"="+project.getRelativeFileName(nr.getFile())+"\n"
						+"defined in the dependency "+AV+"\n"
						+"is conflicting with previous definition: "+foo.getName()+"="+project.getRelativeFileName(foo.getFile())+".\n", "Error",
						JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
		namedRepositories.add(nr);
		return false;
	}
	
	private void addText(JPanel panel, String... lines) {
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.anchor=GridBagConstraints.WEST;
		for (String s:lines) {
			panel.add(new JLabel(s),gbc);
		}
		revalidate();
		repaint();
	}
	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		return dependenciesSuccessfullyRetrieved;
	}
	
	public static GetDependenciesPaneFactory getPane(GramlabProject project) {
		return new GetDependenciesPaneFactory(project);
	}
	

}
