package fr.gramlab.project.config.locate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.umlv.unitex.config.InjectedVariable;
import fr.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class ConfigureVariableInjectionDialog extends JDialog {
	
	GramlabProject project;
	DefaultListModel modelInjectedVariables;

	public ConfigureVariableInjectionDialog(final GramlabProject p) {
		super(Main.getMainFrame(), "Variable injection", true);
		this.project=p;
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(createInjectedVariablesPanel(p),BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveConfiguration()) {
					setVisible(false);
				}
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


	private JPanel createInjectedVariablesPanel(final GramlabProject project) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		/* Subpane */
		JPanel p2 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		p2.add(new JLabel("Name: "), gbc2);
		gbc2.weightx = 1;
		final JTextField name = new JTextField();
		p2.add(name, gbc2);
		gbc2.weightx = 0;
		p2.add(new JLabel(" Value: "), gbc2);
		final JTextField value = new JTextField();
		gbc2.weightx = 1;
		gbc2.gridwidth = GridBagConstraints.REMAINDER;
		p2.add(value, gbc2);
		/* End subpane */
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		p.add(p2, gbc);
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton add = new JButton("Add variable");
		modelInjectedVariables = new DefaultListModel();
		for (InjectedVariable v : project.getInjectedVariables()) {
			modelInjectedVariables.addElement(v);
		}
		final JList list = new JList(modelInjectedVariables);
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				InjectedVariable v = (InjectedVariable) value;
				return super.getListCellRendererComponent(list, v.getName()
						+ "=" + v.getValue(), index, isSelected, cellHasFocus);
			}
		});
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String varName = name.getText();
				if (!InjectedVariable.isValidName(varName)) {
					JOptionPane
							.showMessageDialog(
									null,
									"You must specify a name of the form [a-zA-Z0-9_]+",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String varValue = value.getText();
				if (!InjectedVariable.isValidValue(varValue)) {
					JOptionPane
							.showMessageDialog(
									null,
									"You must specify a valid value made of strict ASCII chars",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (modelContains(varName)) {
					JOptionPane.showMessageDialog(null,
							"A repository with the same name already exists",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				modelInjectedVariables.addElement(new InjectedVariable(varName,
						varValue));
			}

			private boolean modelContains(String s) {
				for (int i = 0; i < modelInjectedVariables.getSize(); i++) {
					InjectedVariable v = (InjectedVariable) modelInjectedVariables
							.get(i);
					if (s.equals(v.getName()))
						return true;
				}
				return false;
			}
		});
		p3.add(add);
		final JButton remove = new JButton("Remove");
		remove.setEnabled(false);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				remove.setEnabled(list.getSelectedIndex() != -1);
			}
		});
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = list.getSelectedIndices();
				for (int i = indices.length - 1; i >= 0; i--) {
					modelInjectedVariables.remove(indices[i]);
				}
			}
		});
		p3.add(remove);
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		p.add(p3, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty=1;
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width,
				150));
		p.add(scroll, gbc);
		return p;
	}

	
	private boolean saveConfiguration() {
		ArrayList<InjectedVariable> injectedVariables = new ArrayList<InjectedVariable>();
		for (int i = 0; i < modelInjectedVariables.getSize(); i++) {
			InjectedVariable v = (InjectedVariable) modelInjectedVariables
					.get(i);
			injectedVariables.add(v);
		}
		project.setInjectedVariables(injectedVariables);
		try {
			project.saveConfigurationFiles(false);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
                    "Error while saving your project configuration:\n\n"+e.getCause(),
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
}
