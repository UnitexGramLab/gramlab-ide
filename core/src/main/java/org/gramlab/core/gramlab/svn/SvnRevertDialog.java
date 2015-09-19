package fr.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.util.filelist.SelectableFileList;
import fr.gramlab.util.filelist.SelectableFileListModel;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.frames.FrameUtil;
import fr.umlv.unitex.process.ExecParameters;
import fr.umlv.unitex.process.Executor;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.SvnCommand;
import fr.umlv.unitex.process.list.ProcessOutputList;
import fr.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class SvnRevertDialog extends JDialog {
	
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

	
	public SvnRevertDialog(ArrayList<File> files,GramlabProject p) {
		super(Main.getMainFrame(), "SVN revert for project "+p.getName(), true);
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
							"There is no selected file to apply revert on.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				lock();
				revertFiles();
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

	
	protected void revertFiles() {
		ArrayList<String> list=new ArrayList<String>();
		for (File f:model.getSelectedFiles()) {
			list.add(project.getRelativeFileName(f));
		}
		if (list.isEmpty()) {
			new AfterRevertDo(null).toDo(true);
			return;
		}
		stdout.empty();
		stderr.empty();
		File targetList=createTargetListFile(list);
		stdout.addLine(new Couple("Reverting local changes...",false));
		SvnCommand c=new SvnCommand().revert(targetList);
		if (authPane!=null) {
			char[] tmp=authPane.getPassword();
			c=c.auth(authPane.getLogin(),tmp);
			/* We clear the password array, as suggested in JPasswordField.getPassword() */
			for (int i=0;i<tmp.length;i++) {
				tmp[i]=0;
			}
		}
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterRevertDo(targetList), false,project.getProjectDirectory());
		executor=new Executor(parameters);
		executor.start();
	}

	private File createTargetListFile(ArrayList<String> list) {
		File f=new File(project.getProjectDirectory(),"..target-list");
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			/* We use the default encoding, because this file will be read by the svn
			 * client, and I (SP) did not find any precision on the file format
			 * expected by the --targets option, so I assume that the system encoding 
			 * will be used.
			 */
			OutputStreamWriter writer=new OutputStreamWriter(stream);
			for (String s:list) {
				writer.write(s+"\n");
			}
			writer.close();
			stream.close();
			return f;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
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

	
	class AfterRevertDo implements ToDo {
		
		File targetList;
		
		public AfterRevertDo(File targetList) {
			this.targetList=targetList;
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
				project.asyncUpdateSvnInfo(null,false);
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
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			cancel.setEnabled(true);
		}
		finished=success;
	}

}
