package org.gramlab.core.gramlab.svn;

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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class SvnCommitDialog extends JDialog {
	
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	GramlabProject project;
	JCheckBox hideUnversionedItems;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());
	SvnStatusInfo svnStatusInfo;
	CommitTableModel tableModel;
	CommitTreeModel treeModel;
	JTextArea message=new JTextArea();
	ArrayList<File> clickedFiles;
	JTree tree;
	
	private boolean finished=false;
	

	public SvnCommitDialog(GramlabProject p,ArrayList<File> clickedFiles) {
		this(null,p,clickedFiles);
	}
	
	@SuppressWarnings("unchecked")
	public SvnCommitDialog(SvnStatusInfo info,final GramlabProject p,ArrayList<File> clickedFiles) {
		super(Main.getMainFrame(), "SVN Commit for project "+p.getName(), true);
		if (clickedFiles==null) {
			this.clickedFiles=new ArrayList<File>();
		} else {
			this.clickedFiles=(ArrayList<File>) clickedFiles.clone();
		}
		if (info==null) {
			svnStatusInfo=SvnExecutor.getSvnStatusInfo(p);
		} else {
			svnStatusInfo=info;
		}
		tableModel=new CommitTableModel(p,svnStatusInfo,this.clickedFiles);
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
				if (finished==true) {
					setVisible(false);
					dispose();
					return;
				}
				if (tableModel.getRowCount()==0) {
					/* There is no possible modification at all */
					setVisible(false);
					dispose();
					return;
				}
				if (!tableModel.mustCommit()) {
					/* There is at least one possible modification, but none
					 * is selected in the table */
					JOptionPane.showMessageDialog(null,
							"There is no selected file to commit.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				lock();
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
		ArrayList<String> list=tableModel.getUnversionedFiles();
		if (list.isEmpty()) {
			new AfterAddDo(null).toDo(true);
			return;
		}
		stdout.empty();
		stderr.empty();
		File targetList=createTargetListFile(list);
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
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterAddDo(targetList), false,project.getProjectDirectory());
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

	private File createLogFile(String content) {
		File f=new File(project.getProjectDirectory(),"..log-message");
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			/* We use the default encoding, because this file will be read by the svn
			 * client, and I (SP) did not find any precision on the file format
			 * expected by the -F option, so I assume that the system encoding 
			 * will be used.
			 */
			OutputStreamWriter writer=new OutputStreamWriter(stream);
			writer.write(content);
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
		if (tableModel.getRowCount()==0) {
			JPanel p=new JPanel();
			p.add(new JLabel("There is no modification to commit."));
			p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			return p;
		}
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		stderr.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Commit message:"),gbc);
		JScrollPane scroll3=new JScrollPane(message);
		scroll3.setPreferredSize(new Dimension(100,60));
		p.add(scroll3,gbc);
		p.add(new JLabel(" "),gbc);
		gbc.weighty=2;
		
		treeModel=new CommitTreeModel(tableModel);
		tree=new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		tree.setCellRenderer(new SelectionTreeCellEditor(tableModel));
		tree.setEditable(true);
		tree.setCellEditor(new SelectionTreeCellEditor(tableModel));
		expandSelectedNodes(tree);
		JScrollPane scroll=new JScrollPane(tree);
		scroll.setPreferredSize(new Dimension(500,300));
		p.add(scroll,gbc);		
		hideUnversionedItems=new JCheckBox("Hide and ignore unversioned items",false);
		hideUnversionedItems.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeModel.setHideUnversionedItems(hideUnversionedItems.isSelected());
			}
		});
		gbc.weighty=0;
		p.add(hideUnversionedItems,gbc);
		p.add(new JLabel(" "),gbc);
		if (askForAuthentication) {
			p.add(new JLabel(" "),gbc);
			authPane=new SvnAuthenticationPane();
			p.add(authPane,gbc);
		}
		JScrollPane scroll2=new JScrollPane(stdout);
		scroll2.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll2,gbc);
		return p;
	}

	
	private void expandSelectedNodes(JTree tree) {
		if (clickedFiles==null) return;
		for (File f:clickedFiles) {
			CommitTreeNode node=treeModel.getNode(project.getRelativeFileName(f));
			if (node==null) {
				continue;
			}
			if (node.isLeaf()) {
				/* Because of a clever? Swing optimization,
				 * expandPath will do nothing if the path leads
				 * to a leaf
				 */
				node=(CommitTreeNode) node.getParent();
			}
			TreePath path=new TreePath(treeModel.getPathToRoot(node));
			tree.expandPath(path);
		}
	}


	class AfterAddDo implements ToDo {
		
		File targetList;
		
		public AfterAddDo(File targetList) {
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
				svnDeleteFiles();
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

	
	protected void svnDeleteFiles() {
		ArrayList<String> list=tableModel.getRemovedFiles();
		if (list.isEmpty()) {
			new AfterDeleteDo(null).toDo(true);
			return;
		}
		File targetList=createTargetListFile(list);
		stdout.addLine(new Couple("Deleting files...",false));
		SvnCommand c=new SvnCommand().delete(targetList);
		if (authPane!=null) {
			char[] tmp=authPane.getPassword();
			c=c.auth(authPane.getLogin(),tmp);
			/* We clear the password array, as suggested in JPasswordField.getPassword() */
			for (int i=0;i<tmp.length;i++) {
				tmp[i]=0;
			}
		}
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterDeleteDo(targetList), false,project.getProjectDirectory());
		executor=new Executor(parameters);
		executor.start();
	}

	
	
	class AfterDeleteDo implements ToDo {
		
		File targetList;
		
		public AfterDeleteDo(File targetList) {
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
				svnCommitFiles();
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

	
	
	protected void svnCommitFiles() {
		ArrayList<String> list=tableModel.getFilesToCommit();
		File targetList=createTargetListFile(list);
		File log=createLogFile(message.getText());
		stdout.addLine(new Couple("Committing...",false));
		SvnCommand c=new SvnCommand().commit(targetList,log);
		if (authPane!=null) {
			char[] tmp=authPane.getPassword();
			c=c.auth(authPane.getLogin(),tmp);
			/* We clear the password array, as suggested in JPasswordField.getPassword() */
			for (int i=0;i<tmp.length;i++) { 
				tmp[i]=0;
			}
		}
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, 
				new AfterCommitDo(targetList,log), false,project.getProjectDirectory());
		executor=new Executor(parameters);
		executor.start();
	}


	class AfterCommitDo implements ToDo {
		
		File targetList,log;
		
		public AfterCommitDo(File targetList,File log) {
			this.targetList=targetList;
			this.log=log;
		}

		@Override
		public void toDo(boolean success) {
			if (targetList!=null) {
				targetList.delete();
			}
			if (log!=null) {
				log.delete();
			}
			SvnCommandResult r;
			if (success) {
				r=new SvnCommandResult(SvnOpResult.OK,"");
			} else {
				r=SvnExecutor.getSvnError(stderr);
			}
			switch (r.getOp()) {
			case OK: {
				/* If we have deleted items, we have to force a refresh on 
				 * their parent directories
				 */
				ArrayList<File> removed=new ArrayList<File>();
				for (String s:tableModel.getRemovedFiles()) {
					removed.add(project.getFileFromNormalizedName(s).getParentFile());
				}
				project.asyncUpdateSvnInfo(removed,false);
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
			case OUT_OF_DATE: {
				JOptionPane.showMessageDialog(null,
						"Some resources are out of date. Try updating first.", "Error",
						JOptionPane.ERROR_MESSAGE);
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
		message.setEnabled(false);
		tree.setEnabled(false);
		hideUnversionedItems.setEnabled(false);
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			message.setEnabled(true);
			tree.setEnabled(true);
			hideUnversionedItems.setEnabled(true);
			cancel.setEnabled(true);
		}
		finished=success;
	}
	
}
