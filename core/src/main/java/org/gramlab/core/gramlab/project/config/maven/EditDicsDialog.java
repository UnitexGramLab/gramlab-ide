package org.gramlab.core.gramlab.project.config.maven;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class EditDicsDialog extends JDialog {
	
	GramlabProject project;
	DefaultListModel model;
	JTextField dstBin=new JTextField("");
	DefaultListModel dicModel;

	public EditDicsDialog(final GramlabProject p,final DefaultListModel model,final int index) {
		super(Main.getMainFrame(), "Dictionary configuration", true);
		this.project=p;
		this.model=model;
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(createPanel(index),BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		final JButton ok=new JButton("Ok");
		dicModel.addListDataListener(new ListDataListener() {
			
			private void updateButton() {
				ok.setText(dicModel.size()==0?"Delete item":"Ok");
			}
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateButton();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				updateButton();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				updateButton();
			}
		});
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dicModel.size()==0) {
					model.remove(index);
					setVisible(false);
					return;
				}
				String bin=dstBin.getText();
				if (bin.equals("")) {
					JOptionPane.showMessageDialog(null,
							"You must specify a target name for your dictionary group",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (bin.contains("/") || bin.contains("\\")) {
					JOptionPane.showMessageDialog(null,
							"Your target name should not contain any file separator / or \\\n"+
					        "since it will automatically be located in the target Dela directory.",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!bin.endsWith(".bin")) {
					bin = bin + ".bin";
				}
				/* Now, we have to check if there is already a target with that name */
				if (existsTarget(index,bin)) {
					JOptionPane.showMessageDialog(null,
							"There is already a target .bin with this name.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				ArrayList<File> dicList=new ArrayList<File>();
				for (int i=0;i<dicModel.size();i++) {
					dicList.add((File) dicModel.get(i));
				}
				model.remove(index);
				model.add(index,new BinToBuild(bin,dicList));
				setVisible(false);
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


	private JPanel createPanel(final int index) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		BinToBuild b=(BinToBuild)model.get(index);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Target name: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(dstBin,gbc);
		gbc.weightx=0;
		gbc.fill=GridBagConstraints.NONE;
		dstBin.setText(b.getBin());
		dicModel=new DefaultListModel();
		for (File dic:b.getDics()) {
			dicModel.addElement(dic);
		}
		final JList list=new JList(dicModel);
		
		p.add(new JLabel(" "),gbc);
		p.add(new JLabel("Set the list of dictionaries you want to compress"),gbc);
		p.add(new JLabel("into the target .bin file. An item with an empty list"),gbc);
		p.add(new JLabel("will be deleted."),gbc);
		JPanel box=new JPanel(null);
		box.setLayout(new BoxLayout(box,BoxLayout.X_AXIS));
		JButton addDic=new JButton("Add dictonaries");
		addDic.addActionListener(new ActionListener() {
			
			private File getProbableDirectory() {
				if (dicModel.size()!=0) {
					File f=(File) dicModel.get(0);
					return f.getParentFile();
				}
				return project.getDelaDirectory();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(getProbableDirectory());
				jfc.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						".dic","dic");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(EditDicsDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File[] files=jfc.getSelectedFiles();
				if (files==null) {
					return;
				}
				for (File f:files) {
					if (null==MavenDialog.inSrcDirectory(project,f)) {
						JOptionPane.showMessageDialog(null,
								"Error with dictionary "+f.getAbsolutePath()+":\n\n"+
								"You cannot select a dictionary outside the src directory of your project!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						if (!dicModel.contains(f)) {
							dicModel.addElement(f);
						}
					}
				}
			}
		});
		final JButton remove=new JButton("Remove selected items");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices=list.getSelectedIndices();
				if (indices==null) return;
				for (int i=indices.length-1;i>=0;i--) {
					dicModel.remove(indices[i]);
				}
			}
		});
		remove.setEnabled(false);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				remove.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
		box.add(addDic);
		box.add(remove);
		box.add(Box.createHorizontalGlue());
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(box,gbc);
		gbc.weightx=1;
		gbc.weighty=1;
		p.add(new JScrollPane(list),gbc);
		return p;
	}


	private boolean existsTarget(int index,String targetBin) {
		int n = model.size();
		for (int i = 0; i < n; i++) {
			if (i==index) continue;
			String bin = ((BinToBuild) model.get(i)).getBin();
			if (targetBin.equals(bin))
				return true;
		}
		return false;
	}

}
