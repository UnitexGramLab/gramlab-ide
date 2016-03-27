package fr.gramlab.project.config.preprocess;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fr.gramlab.project.Language;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.maven.Artifact;
import fr.gramlab.project.config.maven.PomIO;
import fr.umlv.unitex.io.Encoding;

@SuppressWarnings("serial")
public class MainInfoPaneFactory extends ConfigurationPaneFactory {
	
	JTextField name;
	JComboBox encoding;
	JTextField group;
	JTextField artifact;
	JTextField version;
	JTextField language;
	
	public MainInfoPaneFactory(GramlabProject project,boolean editable) {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		name=new JTextField(project.getName());
		encoding=new JComboBox(Encoding.values());
		Artifact a=project.getPom().getArtifact();
		group=new JTextField(a.getGroupId());
		artifact=new JTextField(a.getArtifactId());
		version=new JTextField(a.getVersion());
		final JRadioButton common=new JRadioButton("Common or ...");
		final JRadioButton selectLanguage=new JRadioButton("Select a language: ");
		JComboBox languageComboBox=new JComboBox(Language.getSortedValues());
		encoding.setSelectedItem(project.getEncoding());
		JPanel main=CreateProjectDialog.createProjectSettings(name,common,selectLanguage,languageComboBox,
				encoding,group,artifact,version,editable);
		add(main,BorderLayout.NORTH);
	}
	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),project.getName());
		if (a==null) {
			return false;
		}
		project.setEncoding((Encoding) encoding.getSelectedItem());
		project.getPom().setArtifact(a);
		PomIO.savePom(project.getPom(),project);
		return true;
	}
}
