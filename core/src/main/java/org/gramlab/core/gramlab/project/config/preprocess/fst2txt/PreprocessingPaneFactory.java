package org.gramlab.core.gramlab.project.config.preprocess.fst2txt;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.maven.PomIO;
import org.gramlab.core.gramlab.project.config.preprocess.ConfigurationPaneFactory;

@SuppressWarnings("serial")
public class PreprocessingPaneFactory extends ConfigurationPaneFactory {
	
	PreprocessingTableModel model;
	
	public PreprocessingPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Configuring preprocessing"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Select all the preprocessing graphs (.grf or .fst2) you want to apply. A .grf graph will be compiled"), gbc);
		add(new JLabel("and a .fst2 will be copied as is. The resulting .fst2 will be named 'Target name'.fst2 and will be stored into"),gbc);
		add(new JLabel("the " + PomIO.TARGET_PREPROCESS_DIRECTORY	+ " directory of your project."), gbc);
		add(new JLabel("Double-click on a line to edit it."),gbc);
		add(new JLabel(" "), gbc);
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		model = new PreprocessingTableModel(project.getPreprocessing()
				.getPreprocessingSteps());
		final JButton up=new JButton("Up");
		final JButton down=new JButton("Down");
		final PreprocessingTable table = new PreprocessingTable(model,project);
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.shiftSelectedRows(false);
			}
		});
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.shiftSelectedRows(true);
			}
		});
		up.setEnabled(false);
		down.setEnabled(false);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] indices=table.getSelectedRows();
				if (indices==null || indices.length==0) {
					up.setEnabled(false);
					down.setEnabled(false);
					return;
				}
				up.setEnabled(indices[0]!=0);
				down.setEnabled(indices[indices.length-1]!=model.getRowCount()-1);
			}

		});
		JPanel addPane = new JPanel(null);
		addPane.setLayout(new BoxLayout(addPane, BoxLayout.X_AXIS));
		JButton addStep = new JButton("Add graph");
		addStep.addActionListener(new ActionListener() {
			private File getProbableDirectory() {
				if (model.getRowCount()!=0) {
					File f=(File) model.getValueAt(0,1);
					return f.getParentFile();
				}
				return project.getPreprocessingDirectory();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(getProbableDirectory());
				jfc.setMultiSelectionEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						".grf & .fst2", "grf", "fst2");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(PreprocessingPaneFactory.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					new PreprocessingStepDialog(project,model,jfc.getSelectedFile(),null);
				}
			}
		});
		addPane.add(addStep);
		final JButton removeSteps = new JButton("Remove selected graph(s)");
		removeSteps.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for (int i = indices.length - 1; i >= 0; i--) {
					model.remove(indices[i]);
				}
			}
		});
		removeSteps.setEnabled(false);
		addPane.add(removeSteps);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeSteps.setEnabled(table.getSelectedRow() != -1);
			}
		});
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		add(addPane, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		JScrollPane scroll=new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(2*table.getPreferredSize().width,300));
		add(scroll,gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		gbc.anchor=GridBagConstraints.CENTER;
		JPanel p=new JPanel();
		p.add(up);
		p.add(down);
		add(p,gbc);
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		ArrayList<PreprocessingStep> steps = model.getElements();
		Preprocessing p = project.getPreprocessing();
		p.setPreprocessingSteps(steps);
		return true;
	}

	public static PreprocessingPaneFactory getPane(GramlabProject project) {
		return new PreprocessingPaneFactory(project);
	}
	
}
