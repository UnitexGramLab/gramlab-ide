package fr.gramlab.project.config.concordance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.preprocess.ConfigurationPaneFactory;

@SuppressWarnings("serial")
public class ConcordanceConfigPane extends ConfigurationPaneFactory {

	JComboBox concordanceType;
	JCheckBox noContext;
	JTextField leftContext;
	JTextField rightContext;
	JCheckBox leftS;
	JCheckBox rightS;
	JLabel l1,l2,l3,l4;
	JComboBox sortType;
	GramlabProject project;
	ActionListener validateConfigListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			validateAndSave();
		}
	};
	
	public ConcordanceConfigPane(GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		add(createConcordanceTypePanel(project),gbc);
		add(createContextPanel(project),gbc);
		add(createSortPanel(project),gbc);
		gbc.weighty=1;
		add(new JPanel(null),gbc);
	}

	
	
	private JPanel createConcordanceTypePanel(GramlabProject project) {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Concordance type:"));
		concordanceType=new JComboBox(ConcordanceType.values());
		concordanceType.setSelectedItem(project.getConcordanceType());
		p.add(concordanceType);
		concordanceType.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ConcordanceType type=(ConcordanceType)value;
				return super.getListCellRendererComponent(list, type.getDescription(), index, isSelected,
						cellHasFocus);
			}
		});
		concordanceType.addActionListener(validateConfigListener);
		return p;
	}


	private JPanel createContextPanel(GramlabProject project) {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Context:"));
		noContext=new JCheckBox("No context",project.onlyMatches());
		noContext.addActionListener(validateConfigListener);
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(noContext,gbc);
		gbc.weightx=0;
		gbc.gridwidth=3;
		p.add(new JLabel("Context length:"),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Stop at:"),gbc);

		leftContext=new JTextField(""+project.getLeftContext());
		leftContext.setPreferredSize(new Dimension(40,leftContext.getPreferredSize().height));
		rightContext=new JTextField(""+project.getRightContext());
		rightContext.setPreferredSize(new Dimension(40,rightContext.getPreferredSize().height));
		gbc.gridwidth=1;
		p.add(l1=new JLabel("Left "),gbc);
		p.add(leftContext,gbc);
		p.add(l2=new JLabel(" chars "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		leftS=new JCheckBox(" {S}",project.isLeftStopAtS());
		leftS.addActionListener(validateConfigListener);
		p.add(leftS,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		
		gbc.gridwidth=1;
		p.add(l3=new JLabel("Right "),gbc);
		p.add(rightContext,gbc);
		p.add(l4=new JLabel(" chars "),gbc);
		rightS=new JCheckBox(" {S}",project.isRightStopAtS());
		rightS.addActionListener(validateConfigListener);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(rightS,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		updateContextPanel();
		noContext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateContextPanel();
			}
		});
		concordanceType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateContextPanel();
				validateAndSave();
			}
		});
		return p;
	}


	private void updateContextPanel() {
		boolean contextCheckBoxEnabled=true;
		boolean otherComponentsEnabled=true;
		ConcordanceType t=(ConcordanceType) concordanceType.getSelectedItem();
		if (t!=null && !t.useContext()) {
			contextCheckBoxEnabled=false;
			otherComponentsEnabled=false;
		} else {
			otherComponentsEnabled=!noContext.isSelected();
		}
		noContext.setEnabled(contextCheckBoxEnabled);
		leftContext.setEnabled(otherComponentsEnabled);
		rightContext.setEnabled(otherComponentsEnabled);
		leftS.setEnabled(otherComponentsEnabled);
		rightS.setEnabled(otherComponentsEnabled);
		l1.setEnabled(otherComponentsEnabled);
		l2.setEnabled(otherComponentsEnabled);
		l3.setEnabled(otherComponentsEnabled);
		l4.setEnabled(otherComponentsEnabled);
	}


	private JPanel createSortPanel(GramlabProject project) {
		JPanel p=new JPanel(new GridLayout(1,1));
		p.setBorder(BorderFactory.createTitledBorder("Sort:"));
		sortType=new JComboBox(ConcordanceSortType.values());
		sortType.setPreferredSize(new Dimension(300,sortType.getPreferredSize().height));
		sortType.setSelectedItem(project.getConcordanceSortType());
		p.add(sortType);
		sortType.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ConcordanceSortType type=(ConcordanceSortType)value;
				return super.getListCellRendererComponent(list, type.getDescription(), index, isSelected,
						cellHasFocus);
			}
		});
		sortType.addActionListener(validateConfigListener);
		concordanceType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateSortPanel();
			}
		});
		updateSortPanel();
		return p;
	}

	private void updateSortPanel() {
		boolean sortEnabled=true;
		ConcordanceType t=(ConcordanceType) concordanceType.getSelectedItem();
		if (t!=null && !t.useContext()) {
			sortEnabled=false;
		}
		sortType.setEnabled(sortEnabled);
	}


	@Override
	public boolean validateConfiguration(GramlabProject project) {
		ConcordanceType type=(ConcordanceType) concordanceType.getSelectedItem();
		if (type==null) {
			JOptionPane.showMessageDialog(null,
                    "You must select a concordance type",
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		int left,right;
		/* We don't want to bother the user with invalid left/right context
		 * sizes if contexts are not to be used
		 */
		if (!type.useContext() || noContext.isSelected()) {
			left = -1;
		} else {
			try {
				left = Integer.parseInt(leftContext.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid or empty left context size !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (left<0) {
				JOptionPane.showMessageDialog(null,
						"Invalid left context size !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if (!type.useContext() || noContext.isSelected()) {
			right = -1;
		} else {
			try {
				right = Integer.parseInt(rightContext.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid or empty right context size !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (right<0) {
				JOptionPane.showMessageDialog(null,
						"Invalid right context size !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		ConcordanceSortType sort=(ConcordanceSortType) sortType.getSelectedItem();
		if (sort==null) {
			JOptionPane.showMessageDialog(null,
                    "You must select a sort type",
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		/* Everything is OK, so we can actually modify the project's
		 * configuration */
		project.setConcordanceType(type);
		if (left!=-1) project.setLeftContext(left);
		if (right!=-1) project.setRightContext(right);
		project.setOnlyMatches(noContext.isSelected());
		project.setLeftStopAtS(leftS.isSelected());
		project.setRightStopAtS(rightS.isSelected());
		project.setConcordanceSortType(sort);
		return true;
	}
	
	public void validateAndSave() {
		if (validateConfiguration(project)) {
			try {
				project.saveConfigurationFiles(false);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
	                    "Error while saving your project configuration:\n\n"+e.getCause(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
