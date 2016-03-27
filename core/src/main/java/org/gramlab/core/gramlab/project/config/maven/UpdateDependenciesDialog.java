package fr.gramlab.project.config.maven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.preprocess.GetDependenciesPaneFactory;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.FrameUtil;
import fr.umlv.unitex.process.ExecParameters;
import fr.umlv.unitex.process.Executor;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.list.ProcessOutputList;
import fr.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class UpdateDependenciesDialog extends JDialog {
	
	JPanel mainPanel;
	Executor executor=null;
	JButton ok;
	GramlabProject project;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	JPanel p2=new JPanel(new GridBagLayout());
	
	
	private boolean finished=false;
	

	public UpdateDependenciesDialog(GramlabProject p) {
		super(Main.getMainFrame(), "Updating dependencies of "+p.getName(), true);
		this.project=p;

		setContentPane(createMainPanel());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(400,600);
		FrameUtil.center(getOwner(),this);
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				exportAsMavenComponent(p2);
			}
		});
		setVisible(true);

	}

	
	private void addText(JPanel panel, String... lines) {
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.anchor=GridBagConstraints.WEST;
		for (String s:lines) {
			panel.add(new JLabel(s),gbc);
		}
		panel.revalidate();
		panel.repaint();
	}

	protected void exportAsMavenComponent(final JPanel p) {
		MultiCommands c;
		final File dep=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		FileUtil.setRecursivelyWritable(dep);
		c=project.getPom().getUnpackDependenciesCommand();
		ToDo myDO=new ToDo() {
			@Override
			public void toDo(boolean success) {
				FileUtil.setRecursivelyReadOnly(dep);
				executor=null;
				unlock(success);
				if (success) {
					if (!GetDependenciesPaneFactory.getRepositories(project)) {
						addText(p2," ","Error with repositories.",
							       " ");
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
				c, stdout, stdout, myDO, false);
		executor=new Executor(parameters);
		executor.start();
	}

	
	private JPanel createMainPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		JScrollPane scroll2=new JScrollPane(stdout);
		scroll2.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll2,gbc);
		gbc.weighty=0;
		p.add(p2,gbc);

		JPanel pane=new JPanel(new BorderLayout());
		pane.add(p,BorderLayout.CENTER);
		JPanel down=new JPanel();
		
		ok=new JButton("Hide");
		ok.setEnabled(false);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished==true) {
					setVisible(false);
					dispose();
					return;
				}
				lock();
				p2.removeAll();
				p2.revalidate();
				p2.repaint();
			}
		});
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		return pane;
	}
	
	
	
	public static String inSrcDirectory(GramlabProject p,File f) {
		String foo=FileUtil.isAncestor(p.getSrcDirectory(),f.getParentFile());
		if (foo==null) return null;
		return foo+f.getName();
	}


	void lock() {
		ok.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		}finished=success;
	}
	
}
