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
public class SortAlphabetPaneFactory extends ConfigurationPaneFactory {
	
	File sortAlphabet=null;
	JPanel choicePanel;
	GramlabProject project;
	
	public SortAlphabetPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		this.sortAlphabet=project.getSortAlphabet();
		setBorder(BorderFactory.createTitledBorder("Configuring sort alphabet"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("Here are all the sort alphabet files found in your project"),gbc);
		add(new JLabel("directory. Please select the one to use within your"),gbc);
		add(new JLabel("project. If you choose to use no sort alphabet file, the"),gbc);
		add(new JLabel("sort will be done according to the Unicode char order."),gbc);
		add(new JLabel(" "),gbc);
		JPanel foo=new JPanel(null);
		foo.setLayout(new BoxLayout(foo,BoxLayout.X_AXIS));
		JButton add=new JButton("Import a sort alphabet file");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(project.getProjectDirectory());
				jfc.setMultiSelectionEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files","txt");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(SortAlphabetPaneFactory.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File src=jfc.getSelectedFile();
				File dst=new File(project.getSrcDirectory(),"Alphabet_sort.txt");
				if (dst.exists()) {
					final String[] options = { "Cancel","Ok" };
					final int n = JOptionPane.showOptionDialog(SortAlphabetPaneFactory.this,
							"Your project already contains a file named src/Alphabet_sort.txt.\n"
							+"Do you want to replace it ?", "", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					if (n == 0) {
						return;
					}
				}
				FileUtil.copyFile(src,dst);
				sortAlphabet=dst;
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
	}

	void updateChoicePanel() {
		choicePanel.removeAll();
		ArrayList<String> files=project.getAllSortAlphabetFiles();
		String currentAlphabet=project.getRelativeFileName(sortAlphabet);
		ButtonGroup bg=new ButtonGroup();
		final JRadioButton noAlphabet=new JRadioButton("Use no sort alphabet file",currentAlphabet==null);
		noAlphabet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (noAlphabet.isSelected()) {
					sortAlphabet=null;
				}
			}
		});
		bg.add(noAlphabet);
		choicePanel.add(noAlphabet);
		for (int i=0;i<files.size();i++) {
			final String s=files.get(i);
			final JRadioButton b=new JRadioButton(s,s.equals(currentAlphabet));
			if (b.isSelected()) {
				sortAlphabet=new File(project.getProjectDirectory(),s);
			}
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						sortAlphabet=new File(project.getProjectDirectory(),s);
					}
				}
			});
			bg.add(b);
			choicePanel.add(b);
		}
		if (bg.getSelection()==null) {
			/* Just in case... */
			noAlphabet.setSelected(true);
			sortAlphabet=null;
		}
		choicePanel.revalidate();
		choicePanel.repaint();
	}
	
	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setSortAlphabet(sortAlphabet);
		return true;
	}

	public static SortAlphabetPaneFactory getPane(GramlabProject project) {
		return new SortAlphabetPaneFactory(project);
	}
	
}
