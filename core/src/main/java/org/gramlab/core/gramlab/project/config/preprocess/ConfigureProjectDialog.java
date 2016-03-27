package fr.gramlab.project.config.preprocess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.config.maven.PomIO;
import fr.gramlab.project.config.preprocess.fst2txt.PreprocessingPaneFactory;
import fr.gramlab.util.KeyUtil;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class ConfigureProjectDialog extends JDialog {
	
	GramlabProject project;
	boolean configurationCompleted=false;
	boolean newProject;
	
	
	private final static Class<?>[] steps={
		MainInfoPaneFactory.class,
		LanguageParametersPaneFactory.class,
		DependenciesPaneFactory.class,
		GetDependenciesPaneFactory.class,
		AlphabetPaneFactory.class,
		SortAlphabetPaneFactory.class,
		NormalizationPaneFactory.class,
		PreprocessingPaneFactory.class,
		MorphoDicsPaneFactory.class,
		DicsPaneFactory.class,
		PolyLexPaneFactory.class
	};
	
	public ConfigureProjectDialog(final GramlabProject p,boolean newProject,boolean fullConfig,Class<?> c) {
		this(p,newProject,fullConfig,getIndex(c));
	}
	
	
	private static int getIndex(Class<?> c) {
		if (c==null) return 1;
		for (int i=0;i<steps.length;i++) {
			if (steps[i].equals(c)) return i;
		}
		throw new IllegalArgumentException("Value not in steps array");
	}


	/**
	 * (step == -1) means that we want the whole configuration process
	 * step>=0 means that we want to configure a single step. 1 is not a valid
	 * value since changing the dependencies imposes to reconfigure everything else
	 */
	private ConfigureProjectDialog(final GramlabProject p,final boolean newProject,final boolean fullConfig,int step) {
		super(Main.getMainFrame(), "Configure project "+p.getName(), true);
		if (step<-1 || step>=steps.length || (step!=-1 && steps[step].equals(GetDependenciesPaneFactory.class))) {
			throw new IllegalArgumentException("Invalid value for step: "+step);
		}
		this.project=p;
		this.newProject=newProject;
		p.backupConfiguration();
		setCurrentPane(newProject,fullConfig,step);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		/* We don't want to keep a configuration backup when the user 
		 * exits the configuration frame */
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (configurationCompleted) {
					if (newProject) GlobalProjectManager.getAs(GramlabProjectManager.class).openProject(p);
					return;
				}
				/* If the user has cancelled the configuration, we 
				 * have some cleanup to do */
				if (newProject) {
					/* If the project was a newly created one, we have 
					 * to delete it if the user cancels */
					GlobalProjectManager.getAs(GramlabProjectManager.class).deleteProject(p,false);
				} else {
					p.restoreConfiguration();
				}
			}
		});
		if (steps[step].equals(PreprocessingPaneFactory.class)) {
			pack();
		} else {
			setSize(400,550);
		}
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}


	private void setCurrentPane(boolean newProject,boolean fullConfig,int step) {
		if (fullConfig) {
			setPaneFullConfig(newProject,step,step);
		} else {
			setSinglePane(step);
		}
	}


	private void setSinglePane(int step) {
		if (steps[step].equals(GetDependenciesPaneFactory.class)) {
			throw new IllegalArgumentException("Cannot have GetDependenciesPaneFactory.class for a single step configuration");
		}
		JPanel p=new JPanel(new BorderLayout());
		ConfigurationPaneFactory pane=getPane(false,step);
		p.add(pane,BorderLayout.CENTER);
		p.add(constructSingleConfigButtonPane(pane),BorderLayout.SOUTH);
		setContentPane(p);
		p.revalidate();
	}


	private void setPaneFullConfig(boolean newProject,int step,int initialStep) {
		JPanel p=new JPanel(new BorderLayout());
		ConfigurationPaneFactory pane=getPane(newProject,step);
		p.add(pane,BorderLayout.CENTER);
		p.add(constructFullConfigButtonPane(pane,newProject,step,initialStep),BorderLayout.SOUTH);
		setContentPane(p);
		p.revalidate();
	}


	private JPanel constructFullConfigButtonPane(final ConfigurationPaneFactory pane,final boolean newProject,
			final int step,final int initialStep) {
		JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configurationCompleted=false;
				dispose();
			}
		});
		p.add(cancel);
		JButton previous=new JButton("< Previous");
		previous.setEnabled(step>initialStep);
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pane.validateConfiguration(project)) {
					return;
				}
				/* Special case: clicking on "Previous" from the alphabet 
				 * configuration pane must come back to the dependencies
				 * configuration, not the pane dealing with retrieving
				 * dependencies
				 */
				setPaneFullConfig(newProject,(steps[step]==AlphabetPaneFactory.class)?(step-2):(step-1),initialStep);
			}
		});
		p.add(previous);
		int nextStep0=step+1;
		if (nextStep0<steps.length) {
			if (steps[nextStep0]==PolyLexPaneFactory.class) {
				/* If the project's language is not supported by PolyLex,
				 * we skip this frame */
				if (null==PolyLexPaneFactory.getPolyLexCompatibleLang(project)) {
					nextStep0++;
				}
			}
		}
		final int nextStep=nextStep0;
		final boolean lastStep=(nextStep>=steps.length-1);
		JButton nextOrFinish=new JButton(lastStep?"Finish":"Next >");
		nextOrFinish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pane.validateConfiguration(project)) {
					return;
				}
				int next=nextStep;
				if (next<steps.length && steps[next]==GetDependenciesPaneFactory.class) {
					/* If we are about to ask maven to get dependencies, first
					 * we check if there are actual dependencies to be retrieved.
					 * If not, we just skip this frame */
					if (project.getPom().getDependencies().size()==0) {
						next++;
						/* If there is no dependency, we may have to remove the existing
						 * dep directory
						 */
						File dep=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
						FileUtil.setRecursivelyWritable(dep);
						FileUtil.rm(dep);
						/* Moreover, if this is a new empty project and
						 * if there is no dependency, there is nothing more
						 * to configure
						 */
						if (newProject) {
							final String[] options = { "Cancel","Ok" };
							final int n = JOptionPane.showOptionDialog(ConfigureProjectDialog.this,
									"If you chose to create a new project with no dependency,\n"
									+"there is nothing more to configure until you populate your\n"
									+"project's directory with some data.", "", JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.WARNING_MESSAGE, null, options, options[1]);
							if (n == 1) {
								saveConfiguration();
								dispose();
							}
							return;
						}
					}
				}
				if (lastStep) {
					if (saveConfiguration()) {
						dispose();
						return;
					}
				} else {
					setPaneFullConfig(newProject,next,initialStep);
				}
			}
		});
		p.add(nextOrFinish);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(previous);
		KeyUtil.addCRListener(nextOrFinish);
		return p;
	}

	private JPanel constructSingleConfigButtonPane(final ConfigurationPaneFactory pane) {
		JPanel p=new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
		}
		});
		p.add(cancel);
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pane.validateConfiguration(project)) {
					return;
				}
				if (saveConfiguration()) {
					dispose();
				}
			}
		});
		p.add(ok);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(ok);
		return p;
	}


	private ConfigurationPaneFactory getPane(boolean newProject,int step) {
		if (step==0) {
			return new MainInfoPaneFactory(project,newProject);
		}
		try {
			Method method=steps[step].getMethod("getPane",GramlabProject.class);
			return (ConfigurationPaneFactory)method.invoke(null,project);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean saveConfiguration() {
		try {
			project.saveConfigurationFiles(true);
			project.deleteBackup();
			configurationCompleted=true;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
                    "Internal error while saving your project configuration:\n\n"+e.getCause(),
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
}
