package org.gramlab.core.gramlab.project.config.preprocess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.util.filelist.SelectableFileList;
import org.gramlab.core.gramlab.util.filelist.SelectableFileListModel;

@SuppressWarnings("serial")
public class DicsPaneFactory extends ConfigurationPaneFactory {
	
	SelectableFileList list;
	
	public DicsPaneFactory(GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Dictionaries"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("Here are all the .bin and .fst2 dictionaries files found"),gbc);
		add(new JLabel("in your Dela directories. Please select the ones to be"),gbc);
		add(new JLabel("used by the Dico program. You can set their application"),gbc);
		add(new JLabel("order by selecting a dictionary and using the buttons to"),gbc);
		add(new JLabel("move it up or down the list (the upper in the list, the"),gbc);
		add(new JLabel("higher priority). "),gbc);
		add(new JLabel(" "),gbc);
		ArrayList<File> files=project.getAllDictionaryFiles();
		ArrayList<File> selectedDics=project.getDictionaries();
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		final SelectableFileListModel model=new SelectableFileListModel(files,selectedDics);
		list=new SelectableFileList(model,project);
		add(new JScrollPane(list),gbc);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		gbc.weighty=0;
		
		final JButton up=new JButton("Up");
		final JButton down=new JButton("Down");
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.shiftSelectedRows(false);
			}
		});
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.shiftSelectedRows(true);
			}
		});
		up.setEnabled(false);
		down.setEnabled(false);
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] indices=list.getSelectedRows();
				if (indices==null || indices.length==0) {
					up.setEnabled(false);
					down.setEnabled(false);
					return;
				}
				up.setEnabled(indices[0]!=0);
				down.setEnabled(indices[indices.length-1]!=list.getRowCount()-1);
			}
		});
		
		final JCheckBox filter=new JCheckBox("Show selected dictionaries only");
		filter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.setFilter(filter.isSelected());
			}
		});
		JPanel foo1=new JPanel(null);
		JButton selectAll=new JButton("Select all");
		selectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.selectAll();
			}
		});
		JButton unselectAll=new JButton("Unselect all");
		unselectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.unselectAll();
			}
		});
		foo1.setLayout(new BoxLayout(foo1,BoxLayout.X_AXIS));
		foo1.add(selectAll);
		foo1.add(unselectAll);
		foo1.add(Box.createHorizontalGlue());
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(foo1,gbc);
		JPanel foo2=new JPanel(null);
		foo2.setLayout(new BoxLayout(foo2,BoxLayout.X_AXIS));
		foo2.add(filter);
		foo2.add(Box.createHorizontalGlue());
		foo2.add(up);
		foo2.add(down);
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(foo2,gbc);
		gbc.fill=GridBagConstraints.NONE;
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setDictionaries(list.getSelectedFiles());
		return true;
	}

	public static DicsPaneFactory getPane(GramlabProject project) {
		return new DicsPaneFactory(project);
	}
	
}
