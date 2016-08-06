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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.files.FileUtil;

@SuppressWarnings("serial")
public class NormalizationPaneFactory extends ConfigurationPaneFactory {

	File normTxt=null;
	JPanel choicePanel;
	GramlabProject project;

	JCheckBox noSeparatorNormalization=new JCheckBox("No separator normalization",false);
	
	public NormalizationPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		this.normTxt=project.getNormTxt();
		setBorder(BorderFactory.createTitledBorder("Configuring normalization"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("Here are all the Norm.txt files found in your project"),gbc);
		add(new JLabel("directory. Please select the one to use within your"),gbc);
		add(new JLabel("project."),gbc);
		add(new JLabel(" "),gbc);
		JPanel foo=new JPanel(null);
		foo.setLayout(new BoxLayout(foo,BoxLayout.X_AXIS));
		JButton add=new JButton("Import a normalization file");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(project.getProjectDirectory());
				jfc.setMultiSelectionEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files","txt");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(NormalizationPaneFactory.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File src=jfc.getSelectedFile();
				File dst=new File(project.getSrcDirectory(),"Norm.txt");
				if (dst.exists()) {
					final String[] options = { "Cancel","Ok" };
					final int n = JOptionPane.showOptionDialog(NormalizationPaneFactory.this,
							"Your project already contains a file named src/Norm.txt.\n"
							+"Do you want to replace it ?", "", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					if (n == 0) {
						return;
					}
				}
				FileUtil.copyFile(src,dst);
				normTxt=dst;
				updateChoicePanel();
			}
		});
		foo.add(add);
		foo.add(Box.createHorizontalGlue());
		add(foo,gbc);
		choicePanel=new JPanel(null);
		choicePanel.setLayout(new BoxLayout(choicePanel,BoxLayout.Y_AXIS));
		updateChoicePanel();
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		add(new JScrollPane(choicePanel),gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.weighty=0;
		noSeparatorNormalization.setSelected(!project.separatorNormalization());
		add(noSeparatorNormalization,gbc);
	}

	private void updateChoicePanel() {
		choicePanel.removeAll();
		ArrayList<String> files=project.getAllNormalizationFiles();
		String currentNormTxt=project.getRelativeFileName(normTxt);
		ButtonGroup bg=new ButtonGroup();
		final JRadioButton noNormTxt=new JRadioButton("Use no Norm.txt file",currentNormTxt==null);
		noNormTxt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (noNormTxt.isSelected()) {
					normTxt=null;
				}
			}
		});
		bg.add(noNormTxt);
		choicePanel.add(noNormTxt);
		for (int i=0;i<files.size();i++) {
			final String s=files.get(i);
			final JRadioButton b=new JRadioButton(s,s.equals(currentNormTxt));
			if (b.isSelected()) {
				normTxt=new File(project.getProjectDirectory(),s);
			}
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						normTxt=new File(project.getProjectDirectory(),s);
					}
				}
			});
			bg.add(b);
			choicePanel.add(b);
		}
		if (bg.getSelection()==null) {
			/* Just in case... */
			noNormTxt.setSelected(true);
			normTxt=null;
		}
		choicePanel.revalidate();
		choicePanel.repaint();
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setNormTxt(normTxt);
		project.setSeparatorNormalization(!noSeparatorNormalization.isSelected());
		return true;
	}

	public static NormalizationPaneFactory getPane(GramlabProject project) {
		return new NormalizationPaneFactory(project);
	}
	
}
