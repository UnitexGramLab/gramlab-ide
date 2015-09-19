package fr.gramlab.project.config.preprocess.fst2txt;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.maven.PomIO;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class PreprocessingStepDialog extends JDialog {
	
	GramlabProject project;
	DefaultListModel modelInjectedVariables;
	PreprocessingTableModel model;

	JRadioButton merge=new JRadioButton("Merge");
	JRadioButton replace=new JRadioButton("Replace");
	JTextField dstFst2=new JTextField("");

	public PreprocessingStepDialog(final GramlabProject p,final PreprocessingTableModel model,
			final File f,final PreprocessingStep editedStep) {
		super(Main.getMainFrame(), "Setting a preprocessing graph", true);
		this.project=p;
		this.model=model;
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(createPanel(f,editedStep),BorderLayout.CENTER);
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
				if (editedStep==null) {
					PreprocessingStep s=checkedCreation(f,merge.isSelected(),dstFst2.getText());
					if (s!=null) {
						model.addElement(s);
						setVisible(false);
					}
				} else {
					editedStep.setMerge(merge.isSelected());
					String fst2=dstFst2.getText();
					if (fst2.equals("")) {
						JOptionPane.showMessageDialog(null,
								"You must specify a target name for your graph",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!fst2.endsWith(".fst2")) {
						fst2 = fst2 + ".fst2";
					}
					File dir = new File(project.getProjectDirectory(),
							PomIO.TARGET_PREPROCESS_DIRECTORY);
					if (merge.isSelected())
						dir = new File(dir, "Sentence");
					else
						dir = new File(dir, "Replace");
					File target = new File(dir, fst2);
					if (existsTarget(target)) {
						JOptionPane.showMessageDialog(null,
								"There is already a target graph with this name.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					editedStep.setDestFst2(target);
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


	private JPanel createPanel(File f,PreprocessingStep editedStep) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel("Graph:"),gbc);
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		JTextField t=new JTextField(project.getRelativeFileName(f));
		t.setEditable(false);
		p.add(t,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Target name:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(dstFst2,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Mode:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		ButtonGroup bg=new ButtonGroup();
		bg.add(merge);
		bg.add(replace);
		if (shouldSelectMerge(f,editedStep)) {
			merge.doClick();
		} else {
			replace.doClick();
		}
		JPanel foo=new JPanel(null);
		foo.setLayout(new BoxLayout(foo,BoxLayout.X_AXIS));
		foo.add(merge);
		foo.add(replace);
		foo.add(Box.createHorizontalGlue());
		p.add(foo,gbc);
		if (editedStep!=null) {
			dstFst2.setText(editedStep.getDestFst2().getName());
		} else {
			dstFst2.setText(getTargetNameProposal(f));
		}
		return p;
	}

	
	private String getTargetNameProposal(File f) {
		String tmp=FileUtil.getFileNameWithoutFilePath(f);
		tmp=FileUtil.getFileNameWithoutExtension(tmp);
		if (!exists2(tmp)) return tmp;
		int i=2;
		while (true) {
			String res=tmp+i;
			if (!exists2(res)) return res;
			i++;
		}
	}


	private boolean shouldSelectMerge(File f,PreprocessingStep editedStep) {
		if (editedStep!=null) return editedStep.isMerge();
		File replaceDir=project.getReplaceDirectory();
		if (null!=FileUtil.isAncestor(replaceDir,f)) return false;
		return true;
	}


	private boolean exists2(String dstFst2) {
		if (!dstFst2.endsWith(".fst2")) {
			dstFst2 = dstFst2 + ".fst2";
		}
		File dir = new File(project.getProjectDirectory(),
				PomIO.TARGET_PREPROCESS_DIRECTORY);
		if (merge.isSelected())
			dir = new File(dir, "Sentence");
		else
			dir = new File(dir, "Replace");
		File target = new File(dir, dstFst2);
		return existsTarget(target);
	}

	private boolean existsSource(File f) {
		int n = model.getRowCount();
		for (int i = 0; i < n; i++) {
			File src = (File) model.getValueAt(i,1);
			if (f.equals(src))
				return true;
		}
		return false;
	}

	private boolean existsTarget(File targetFst2) {
		int n = model.getRowCount();
		for (int i = 0; i < n; i++) {
			File fst2 = (File) model.getValueAt(i,2);
			if (targetFst2.equals(fst2))
				return true;
		}
		return false;
	}

	private PreprocessingStep checkedCreation(File graph,boolean merge, String dstFst2) {
		if (!graph.exists()) {
			JOptionPane.showMessageDialog(null,
					"This graph does not exist", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (existsSource(graph)) {
			int ret=JOptionPane.showConfirmDialog(null,"The graph is already in your preprocessing graph\n"+
												"list. Are you sure you want to add it again ?" , "", JOptionPane.YES_NO_OPTION);
			if (ret==JOptionPane.NO_OPTION) return null;
		}
		
		if (null == FileUtil.isAncestor(project.getProjectDirectory(),
				graph)) {
			JOptionPane.showMessageDialog(null,
					"The graph must be in your project directory",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dstFst2.equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify a target name for your graph",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (!dstFst2.endsWith(".fst2")) {
			dstFst2 = dstFst2 + ".fst2";
		}
		File dir = new File(project.getProjectDirectory(),
				PomIO.TARGET_PREPROCESS_DIRECTORY);
		if (merge)
			dir = new File(dir, "Sentence");
		else
			dir = new File(dir, "Replace");
		File target = new File(dir, dstFst2);
		if (existsTarget(target)) {
			JOptionPane.showMessageDialog(null,
					"There is already a target graph with this name.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return new PreprocessingStep(graph, target, merge, true);
	}


}
