package org.gramlab.core.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.util.filelist.SelectableFileList;
import org.gramlab.core.gramlab.util.filelist.SelectableFileListModel;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class SvnAddDialog extends JDialog {
	
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	GramlabProject project;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());
	SvnStatusInfo svnStatusInfo;
	SelectableFileListModel model;
	private boolean finished=false;

	
	public SvnAddDialog(ArrayList<File> files,GramlabProject p) {
		super(Main.getMainFrame(), "SVN Add for project "+p.getName(), true);
		model=new SelectableFileListModel(files,files);
		this.project=p;
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
				if (model.getRowCount()==0) {
					/* There is no possible modification at all */
					setVisible(false);
					dispose();
					return;
				}
				if (model.getSelectedFiles().size()==0) {
					/* There is at least one possible modification, but none
					 * is selected in the table */
					JOptionPane.showMessageDialog(null,
							"There is no selected file to add.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				svnAddFiles();
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

	
	protected void svnAddFiles() {
		lock();
		ArrayList<String> list=new ArrayList<String>();
		for (File f:model.getSelectedFiles()) {
			list.add(project.getRelativeFileName(f));
		}
		if (list.isEmpty()) {
			new AfterAddDo(null,null).toDo(true);
			return;
		}
		File targetList=new File(project.getProjectDirectory(),"..target-list");
		SvnUtil.createTargetListFile(targetList,list);
		stdout.addLine(new Couple("Adding unversioned files...",false));
		SvnCommand c=new SvnCommand().add(targetList);
		if (authPane!=null) {
			char[] tmp=authPane.getPassword();
			c=c.auth(authPane.getLogin(),tmp);
			/* We clear the password array, as suggested in JPasswordField.getPassword() */
			for (int i=0;i<tmp.length;i++) {
				tmp[i]=0;
			}
		}
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterAddDo(model.getSelectedFiles(),targetList), false,project.getProjectDirectory());
		executor=new Executor(parameters);
		executor.start();
	}

	private JPanel createMainPanel(boolean askForAuthentication) {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		stderr.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		SelectableFileList table=new SelectableFileList(model,project);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll=new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(400,100));
		p.add(scroll,gbc);		
		if (askForAuthentication) {
			p.add(new JLabel(" "),gbc);
			authPane=new SvnAuthenticationPane();
			p.add(authPane,gbc);
		}
		gbc.weighty=0;
		p.add(new JLabel(" "),gbc);
		JScrollPane scroll2=new JScrollPane(stdout);
		scroll2.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll2,gbc);
		return p;
	}

	
	class AfterAddDo implements ToDo {
		
		File targetList;
		ArrayList<File> files;
		
		public AfterAddDo(ArrayList<File> files,File targetList) {
			this.targetList=targetList;
			this.files=files;
		}

		@Override
		public void toDo(boolean success) {
			if (targetList!=null) {
				targetList.delete();
			}
			SvnCommandResult r;
			if (success) {
				r=new SvnCommandResult(SvnOpResult.OK,"");
			} else {
				r=SvnExecutor.getSvnError(stderr);
			}
			switch (r.getOp()) {
			case OK: {
				project.asyncUpdateSvnInfo(files,false);
				unlock(true);
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
	
	void lock() {
		ok.setEnabled(false);
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		finished=success;
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			cancel.setEnabled(true);
		}
	}

}
