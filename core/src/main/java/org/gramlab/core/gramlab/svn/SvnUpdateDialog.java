package org.gramlab.core.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.maven.PomIO;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class SvnUpdateDialog extends JDialog {
	
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	GramlabProject project;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());
	JRadioButton updateToHead;
	JRadioButton updateToRevision;
	JTextField revision;
	int head;
	boolean finished=false;
	boolean toHead;

	public SvnUpdateDialog(GramlabProject p,final ArrayList<File> files,boolean updateToHead) {
		super(Main.getMainFrame(), "SVN Updating project "+p.getName(), true);
		if (files!=null && files.size()==0) {
			throw new IllegalArgumentException();
		}
		this.project=p;
		this.toHead=updateToHead;
		if (!toHead) {
			head=getHeadRevisionNumber(p);
		}
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
				int revisionNumber=-1;
				if (!toHead && updateToRevision.isSelected()) {
					String s=revision.getText();
					if (s.equals("")) {
						JOptionPane.showMessageDialog(null, "You must provide a revision number!",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						revisionNumber=Integer.parseInt(s);
						if (revisionNumber<0 || revisionNumber>head) {
							throw new NumberFormatException();
						}
					} catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(null, "Invalid revision number!",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				lock();
				stdout.empty();
				stderr.empty();
				stdout.addLine(new Couple("Updating...",false));
				ArrayList<File> list;
				if (files!=null) {
					list=files;
				} else {
					list=new ArrayList<File>();
					list.add(project.getProjectDirectory());
				}
				MultiCommands commands=new MultiCommands();
				for (File f:list) {
					SvnCommand c=new SvnCommand().update(revisionNumber,f,false);
					if (authPane!=null) {
						char[] tmp=authPane.getPassword();
						c=c.auth(authPane.getLogin(),tmp);
						/* We clear the password array, as suggested in JPasswordField.getPassword() */
						for (int i=0;i<tmp.length;i++) {
							tmp[i]=0;
						}
					}
					commands.addCommand(c);
				}
				ExecParameters parameters=new ExecParameters(true, commands, stdout, stderr, new AfterUpdateDo(), false);
				executor=new Executor(parameters);
				executor.start();
			}
		});
		down.add(cancel);
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		setContentPane(pane);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(300,200);
		FrameUtil.center(null,this);
		setVisible(true);
	}

	private int getHeadRevisionNumber(GramlabProject p) {
		String info=SvnExecutor.getCommandOutput(new SvnCommand().info(p.getProjectDirectory(),false,true));
		Scanner s=new Scanner(info);
		while (s.hasNextLine()) {
			String tmp=s.nextLine();
			if (!tmp.startsWith("Revision: ")) continue;
			tmp=tmp.substring(tmp.indexOf(' ')).trim();
			return Integer.parseInt(tmp);
		}
		throw new IllegalStateException("Cannot get head revision number");
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
		ButtonGroup bg=new ButtonGroup();
		String caption=toHead?"Update to head":("Update to head (r"+head+")");
		updateToHead=new JRadioButton(caption,toHead);
		p.add(updateToHead,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		updateToRevision=new JRadioButton("Update to revision: ",!toHead);
		bg.add(updateToHead);
		bg.add(updateToRevision);
		p.add(updateToRevision,gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		revision=new JTextField("");
		revision.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				updateToRevision.doClick();
			}
		});
		p.add(revision,gbc);	
		if (toHead) {
			updateToRevision.setEnabled(false);
			revision.setEnabled(false);
		}
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

	
	class AfterUpdateDo implements ToDo {
		
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
				project.refreshConfigFiles();
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
		if (!project.pomXmlWasUpdated()) {
			/* Nothing to do if pom.xml did not change, except looking for grf conflicts */
			project.getSvnMonitor().monitor(true);
			unlock(true);
			return;
		}
		final File dep=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		FileUtil.setRecursivelyWritable(dep);	
		MultiCommands cmd=project.getPom().getUnpackDependenciesCommand();
		ToDo myDO=new ToDo() {
			@Override
			public void toDo(boolean success) {
				FileUtil.setRecursivelyReadOnly(dep);
				executor=null;
				if (success) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							project.getSvnMonitor().monitor(true);
							unlock(true);
						}
					});
				} else {
					stdout.addLine(new Couple("Some components could not be retrieved.",false));
					stdout.addLine(new Couple("This could be caused by a network problem, by",false));
					stdout.addLine(new Couple("an error in your maven settings, or because",false));
					stdout.addLine(new Couple("one of the components' description is invalid.",false));
					unlock(true);
				}
			}

		};
		ExecParameters parameters=new ExecParameters(true,
				cmd, stdout, stderr, myDO, false);
		executor=new Executor(parameters);
		executor.start();
	}
	
	boolean previous;
	
	void lock() {
		ok.setEnabled(false);
		updateToHead.setEnabled(false);
		previous=updateToRevision.isEnabled();
		updateToRevision.setEnabled(false);
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			updateToHead.setEnabled(true);
			updateToRevision.setEnabled(previous);
			cancel.setEnabled(true);
		}
		finished=success;
	}

}
