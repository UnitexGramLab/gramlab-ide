package fr.gramlab.project.config.preprocess;

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

import fr.gramlab.project.GramlabProject;
import fr.gramlab.util.filelist.SelectableFileList;
import fr.gramlab.util.filelist.SelectableFileListModel;

@SuppressWarnings("serial")
public class MorphoDicsPaneFactory extends ConfigurationPaneFactory {
	
	SelectableFileList list;
	
	public MorphoDicsPaneFactory(GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Morphological-mode dictionaries"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("Here are all the .bin dictionaries files found in your"),gbc);
		add(new JLabel("Dela directories. Please select the ones to be used as"),gbc);
		add(new JLabel("morphological-mode by Dico and Locate."),gbc);
		add(new JLabel(" "),gbc);
		ArrayList<File> files=project.getAllBinFiles();
		ArrayList<File> selectedMorphoDics=project.getMorphoDics();
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		final SelectableFileListModel model=new SelectableFileListModel(files,selectedMorphoDics);
		list=new SelectableFileList(model,project);
		add(new JScrollPane(list),gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.weighty=0;
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
		final JCheckBox filter=new JCheckBox("Show selected dictionaries only");
		filter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.setFilter(filter.isSelected());
			}
		});
		add(filter,gbc);
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setMorphoDics(list.getSelectedFiles());
		return true;
	}

	public static MorphoDicsPaneFactory getPane(GramlabProject project) {
		return new MorphoDicsPaneFactory(project);
	}
	
}
