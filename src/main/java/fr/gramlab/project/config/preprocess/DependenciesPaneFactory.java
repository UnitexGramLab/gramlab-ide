package fr.gramlab.project.config.preprocess;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.maven.Artifact;
import fr.gramlab.project.config.maven.PomIO;

@SuppressWarnings("serial")
public class DependenciesPaneFactory extends ConfigurationPaneFactory {
	
	DefaultListModel model;
	
	public DependenciesPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Set components to use"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		add(new JLabel("You can add or remove maven-packaged"),gbc);
		add(new JLabel("components that your project will use."),gbc);
		add(new JLabel(),gbc);
		final JTextField group=new JTextField();
		final JTextField artifact=new JTextField();
		final JTextField version=new JTextField();
		JPanel tmp=createArtifactPane(group,artifact,version);
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(tmp,gbc);
		JPanel addPane=new JPanel(null);
		addPane.setLayout(new BoxLayout(addPane,BoxLayout.X_AXIS));
		
		model=new DefaultListModel();
		for (Artifact a:project.getPom().getDependencies()) {
			model.addElement(a);
		}
		final JList list=new JList(model);

		JButton addArtifact=new JButton("Add component");
		addArtifact.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),project.getName());
				if (a==null) return;
				if (model.contains(a)) {
					JOptionPane.showMessageDialog(null,
	                        "This component is already is the list", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				model.addElement(a);
				group.setText("");
				artifact.setText("");
				version.setText("");
			}
		});
		addPane.add(addArtifact);
		final JButton removeArtifacts=new JButton("Remove selected component(s)");
		removeArtifacts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices=list.getSelectedIndices();
				for (int i=indices.length-1;i>=0;i--) {
					model.remove(indices[i]);
				}
			}
		});
		removeArtifacts.setEnabled(false);
		addPane.add(removeArtifacts);
		gbc.fill=GridBagConstraints.NONE;
		gbc.weightx=0;
		add(addPane,gbc);
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		//model.addElement(Artifact.checkedArtifactCreation("fr.umlv.unitex","gramlab-base-fr","0.0.1"));
		//model.addElement(Artifact.checkedArtifactCreation("fr.umlv.unitex","gramlab-base-en","0.0.1"));
		add(new JScrollPane(list),gbc);
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Artifact a=(Artifact)value;
				String s="<html><body>groupId: "+a.getGroupId()+
				"<p/>artifactId: "+a.getArtifactId()+
				"<p/>version: "+a.getVersion()+"</body></html>";			
				super.getListCellRendererComponent(list, s, index, isSelected,
						cellHasFocus);
				if (!isSelected) {
					setBackground((index%2==0)?Color.WHITE:Color.LIGHT_GRAY);
				}
				return this;
			}
			
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeArtifacts.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
	}

	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		ArrayList<Artifact> dependencies=new ArrayList<Artifact>();
		for (int i=0;i<model.getSize();i++) {
			dependencies.add((Artifact) model.get(i));
		}
		project.getPom().setDependencies(dependencies);
		PomIO.savePom(project.getPom(),project);
		return true;
	}


	public static DependenciesPaneFactory getPane(GramlabProject project) {
		return new DependenciesPaneFactory(project);
	}
	
}
