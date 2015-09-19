package fr.gramlab.project.config.graph_compilation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import fr.gramlab.project.config.maven.PomIO;
import fr.umlv.unitex.LinkButton;
import fr.umlv.unitex.config.NamedRepository;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class ConfigureRepositoriesDialog extends JDialog {
	
	GramlabProject project;
	DefaultListModel modelRepositories;
	private JTextField defaultGraphRepository=new JTextField();

	
	public ConfigureRepositoriesDialog(final GramlabProject p) {
		super(Main.getMainFrame(), "Graph repositories for project "+p.getName(), true);
		this.project=p;
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(createRepositoriesPanel(p),BorderLayout.CENTER);
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

	
	/**
	 * Returns true if the given directory is either in the src or in the dep directories
	 * of the given project.
	 */
	public static boolean isValidRepository(GramlabProject p,File dir) {
		File srcDir=p.getSrcDirectory();
		if (null!=FileUtil.isAncestor(srcDir,dir)) return true;
		File depDir=new File(p.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		return (null!=FileUtil.isAncestor(depDir,dir));
	}
	
	
	private JPanel createRepositoriesPanel(final GramlabProject project) {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Warning: you should not use the default repository since"),gbc);
		p.add(new JLabel("it may cause trouble if you want to export your project"),gbc);
		p.add(new JLabel("as a maven package. You should always use named repositories"),gbc);
		p.add(new JLabel("instead."),gbc);
		p.add(new JLabel(" "),gbc);
		LinkButton set=new LinkButton("Default one:");
		gbc.gridwidth=1;
		p.add(set,gbc);
		gbc.gridwidth=GridBagConstraints.RELATIVE;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		File f=project.getDefaultGraphRepository();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(defaultGraphRepository,gbc);
		if (f!=null) {
			defaultGraphRepository.setText(project.getNormalizedFileName(f));
		}
		gbc.weightx=0;
		addRepositoryActionListener(set,defaultGraphRepository,project);
		
		/* Subpane */
		JPanel p2=new JPanel(new GridBagLayout());
		GridBagConstraints gbc2=new GridBagConstraints();
		gbc2.anchor=GridBagConstraints.WEST;
		gbc2.fill=GridBagConstraints.HORIZONTAL;
		p2.add(new JLabel("Name: "),gbc2);
		final JTextField name=new JTextField();
		name.setPreferredSize(new Dimension(40,name.getPreferredSize().height));
		p2.add(name,gbc2);
		LinkButton set2=new LinkButton("Set:");
		p2.add(set2,gbc2);
		final JTextField dir=new JTextField();
		gbc2.weightx=1;
		p2.add(dir,gbc2);
		addRepositoryActionListener(set2,dir,project);
		gbc2.gridwidth=GridBagConstraints.REMAINDER;
		gbc2.weightx=0;
		/* End subpane */
		p.add(p2,gbc);
		
		JPanel p3=new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton add=new JButton("Add repository");
		modelRepositories=new DefaultListModel();
		for (NamedRepository r:project.getNamedRepositories()) {
			modelRepositories.addElement(r);
		}
		final JList list=new JList(modelRepositories);
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				NamedRepository r=(NamedRepository)value;
				return super.getListCellRendererComponent(list,
						r.getName()+"="+project.getRelativeFileName(r.getFile()), index, isSelected,
						cellHasFocus);
			}
		});
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s=name.getText();
				if (!NamedRepository.isValidName(s)) {
					JOptionPane.showMessageDialog(null,
							"You must specify a name of the form [a-zA-Z0-9_]+", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if ("".equals(dir.getText())) {
					JOptionPane.showMessageDialog(null,
							"You must specify a directory", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				File f=project.getProjectFileFromNormalizedName(dir.getText());
				if (!isValidRepository(project,f)) {
					JOptionPane.showMessageDialog(null,
							"A repository must be either in 'src' or in 'dep'.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!f.exists()) {
					JOptionPane.showMessageDialog(null,
							"The given directory does not exist", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (modelContains(s)) {
					JOptionPane.showMessageDialog(null,
							"A repository with the same name already exists", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				modelRepositories.addElement(new NamedRepository(s,f));
			}

			private boolean modelContains(String s) {
				for (int i=0;i<modelRepositories.getSize();i++) {
					NamedRepository r=(NamedRepository) modelRepositories.get(i);
					if (s.equals(r.getName())) return true;
				}
				return false;
			}

		});
		p3.add(add);
		final JButton remove=new JButton("Remove");
		remove.setEnabled(false);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				remove.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices=list.getSelectedIndices();
				for (int i=indices.length-1;i>=0;i--) {
					modelRepositories.remove(indices[i]);
				}
			}
		});
		p3.add(remove);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.NONE;
		p.add(p3,gbc);
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weighty=1;
		JScrollPane scroll=new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width+30,150));
		p.add(scroll,gbc);
		return p;
	}
	
	
	private void addRepositoryActionListener(JButton b,final JTextField text,final GramlabProject project) {
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(project.getProjectDirectory());
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				final int returnVal = jfc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File f=jfc.getSelectedFile();
				String s=project.getRelativeFileName(f);
				if (!isValidRepository(project,f)) {
					JOptionPane.showMessageDialog(null,
							"A repository must be either in 'src' or in 'dep'.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				text.setText(s);
			}
		});
	}




	private boolean saveConfiguration() {
		File f=null;
		String s=defaultGraphRepository.getText();
		if (!s.equals("")) {
			f=project.getProjectFileFromNormalizedName(s);
			if (!isValidRepository(project,f)) {
				JOptionPane.showMessageDialog(null,
						"A repository must be either in 'src' or in 'dep'.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		ArrayList<NamedRepository> repositories=new ArrayList<NamedRepository>();
		for (int i=0;i<modelRepositories.getSize();i++) {
			NamedRepository r=(NamedRepository) modelRepositories.get(i);
			repositories.add(r);
		}
		/* Everything is OK, so we can actually modify the project's
		 * configuration */
		project.setDefaultGraphRepository(f);
		project.setNamedRepositories(repositories);
		try {
			project.saveConfigurationFiles(false);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
                    "Error while saving your project configuration:\n\n"+e.getCause(),
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
}
