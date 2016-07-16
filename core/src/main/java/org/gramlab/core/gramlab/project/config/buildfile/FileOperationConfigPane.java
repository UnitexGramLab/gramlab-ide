package org.gramlab.core.gramlab.project.config.buildfile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.ProcessPane;
import org.gramlab.core.gramlab.project.config.concordance.ResultDisplay;
import org.gramlab.core.gramlab.project.config.preprocess.ConfigurationPaneFactory;
import org.gramlab.core.gramlab.util.MyComboCellRenderer;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.config.PreferencesListener;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;

@SuppressWarnings("serial")
public class FileOperationConfigPane extends ConfigurationPaneFactory {
	
	FileOperationType type;
	ResultDisplay display;
	JRadioButton[] radioButtons=new JRadioButton[] {new JRadioButton(),new JRadioButton(),new JRadioButton()};
	DefaultComboBoxModel model;
	JComboBox lastResultFiles;
	
	private final static String[] names = new String[] {
			"Text with outputs",
			"Extract matching sentences",
			"Extract unmatching sentences",
			"Extract only matching sequences" };
	private final static FileOperationType[] values = new FileOperationType[] {
			FileOperationType.MODIFY_TEXT, FileOperationType.EXTRACT_MATCHING_UNITS,
			FileOperationType.EXTRACT_UNMATCHING_UNITS, FileOperationType.EXTRACT_MATCHES };
	final JRadioButton[] buttons = new JRadioButton[names.length];
	private final ButtonGroup bg = new ButtonGroup();
	
	ExtractMatchType extractMatchType;
	private GramlabProject project;
	
	public FileOperationConfigPane(GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		type = project.getResultType();
		for (int i = 0; i < names.length; i++) {
			buttons[i] = new JRadioButton(names[i], values[i] == type);
			final JRadioButton b = buttons[i];
			final FileOperationType t = values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						type = t;
					}
				}
			});
			bg.add(b);
		}
		add(createOutputPanel(project), gbc);
		gbc.weighty = 1;
		add(new JPanel(null), gbc);
	}

	private JPanel createOutputPanel(final GramlabProject project) {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Create an output text file"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.insets=new Insets(0,0,0,5);
		gbc.weightx=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		final LinkButton set=new LinkButton("Set file:");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String path;
				if (lastResultFiles.getSelectedIndex()!=-1) {
					path=project.getFileFromNormalizedName((String) lastResultFiles.getSelectedItem())
						.getAbsolutePath();
				} else {
					path=project.getProjectDirectory().getAbsolutePath();
				}
				JFileChooser f=new JFileChooser(path);
				f.setDialogTitle("Choose an output file");
				f.setDialogType(JFileChooser.SAVE_DIALOG);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				File file=f.getSelectedFile();
				lastResultFiles.setSelectedItem(project.getRelativeFileName(file));
				validateAndSave(false);
			}
		});
		gbc.gridwidth=1;
		p.add(set,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		
		ArrayList<File> list = project.getLastResultFiles();
		String[] tab = new String[list.size()];
		int i = 0;
		for (File f : list) {
			tab[i++] = project.getNormalizedFileName(f);
		}
		model=new DefaultComboBoxModel(tab);
		lastResultFiles = new JComboBox(model);
		lastResultFiles.setPreferredSize(new Dimension(0,0));
		lastResultFiles.setEditable(true);
		lastResultFiles.setRenderer(new MyComboCellRenderer(lastResultFiles));
		p.add(lastResultFiles,gbc);
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(buttons[0],gbc);
		p.add(buttons[1],gbc);
		p.add(buttons[2],gbc);
		p.add(buttons[3],gbc);
		JPanel foo=new JPanel(new GridLayout(3,1));
		final String[] names=new String[] {"Text order","Sorted (all)","Sorted (no duplicates)"};
		final ExtractMatchType[] values=new ExtractMatchType[] {
				ExtractMatchType.TEXT_ORDER,
				ExtractMatchType.SORTED_ALL,
				ExtractMatchType.SORTED_NO_DUPLICATES
		};
		extractMatchType=project.getExtractMatchType();
		ButtonGroup group=new ButtonGroup();
		for (i=0;i<names.length;i++) {
			final ExtractMatchType v=values[i];
			final JRadioButton b=new JRadioButton(names[i],v==extractMatchType);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						extractMatchType=v;
						validateAndSave(false);

					}
					buttons[3].doClick();
				}
			});
			group.add(b);
			foo.add(b);
		}
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		foo.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel foo2=new JPanel(new BorderLayout());
		foo2.add(foo,BorderLayout.CENTER);
		foo2.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
		p.add(foo2,gbc);

		LinkButton setDisplayTool=new LinkButton();
		final JPanel tmp=new JPanel(new BorderLayout());
		tmp.add(createDisplayPanel(project),BorderLayout.CENTER);
		JPanel foo3=ProcessPane.createHidablePane(setDisplayTool,"Display \u25BC", 
				"Display \u25B2",tmp,null);
		foo3.add(setDisplayTool,BorderLayout.NORTH);
		p.add(foo3,gbc);
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				if (project.getName().equals(language)) {
					/* If the preferences of the current project have changed... */
					tmp.removeAll();
					tmp.add(createDisplayPanel(project),BorderLayout.CENTER);
					tmp.revalidate();
					tmp.repaint();
				}
			}
		});
		return p;
	}

	private JPanel createDisplayPanel(final GramlabProject project) {
		JPanel p=new JPanel(new GridLayout(3,1));
		p.setBorder(BorderFactory.createTitledBorder("Display results with:"));
		display=project.getBuildResultDisplay();
		File html=project.getHtmlViewer();
		File editor=project.getTextEditor();
		final ButtonGroup bg=new ButtonGroup();
		ResultDisplay[] values=new ResultDisplay[] {
				ResultDisplay.INTERNAL_FRAME,
				ResultDisplay.HTML_VIEWER,
				ResultDisplay.TEXT_EDITOR
		};
		String[] names=new String[] {"Internal frame","Html viewer","External text editor"};
		boolean[] enabled=new boolean[] {true,true,true};
		final JButton[] buttons=new JButton[] {null,null,null};
		if (html==null) {
			enabled[1]=false;
			if (display==ResultDisplay.HTML_VIEWER) {
				display=ResultDisplay.INTERNAL_FRAME;
			}
			final JButton b=new JButton("Set");
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File f=getExecutableFile();
					if (f==null) return;
					project.setHtmlViewer(f);
					try {
						project.saveConfigurationFiles(false);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,
			                    "Error while saving your configuration files",
			                    "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					b.setVisible(false);
					radioButtons[1].setEnabled(true);
					/* setSelected(true) does not raise an action event */
					radioButtons[1].doClick();
				}
			});
			buttons[1]=b;
		}
		if (editor==null) {
			enabled[2]=false;
			if (display==ResultDisplay.TEXT_EDITOR) {
				display=ResultDisplay.INTERNAL_FRAME;
			}
			final JButton b=new JButton("Set");
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File f=getExecutableFile();
					if (f==null) return;
					project.setTextEditor(f);
					try {
						project.saveConfigurationFiles(false);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,
			                    "Error while saving your configuration files",
			                    "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					b.setVisible(false);
					radioButtons[2].setEnabled(true);
					/* setSelected(true) does not raise an action event */
					radioButtons[2].doClick();
				}
			});
			buttons[2]=b;
		}
		for (int i=0;i<values.length;i++) {
			final JRadioButton b=radioButtons[i];
			b.setText(names[i]);
			b.setSelected(display==values[i]);
			b.setEnabled(enabled[i]);
			final ResultDisplay value=values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						/* See comment in validateConfiguration */
						display=value;
					}
				}
			});
			bg.add(b);
			if (buttons[i]==null) {
				p.add(b);
			} else {
				JPanel foo=new JPanel(new BorderLayout());
				foo.add(b,BorderLayout.CENTER);
				foo.add(buttons[i],BorderLayout.EAST);
				p.add(foo);
			}
		}
		return p;
	}

	protected File getExecutableFile() {
		final JFileChooser f = new JFileChooser();
		f.setDialogTitle("Choose an executable file");
		f.setDialogType(JFileChooser.OPEN_DIALOG);
		if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		File file=f.getSelectedFile();
		if (!file.canExecute()) {
			JOptionPane.showMessageDialog(null,
                    "This file is not an executable one",
                    "Error", JOptionPane.ERROR_MESSAGE);
			return null;	
		}
		return file;
	}

	
	public boolean validateConfiguration(GramlabProject project,boolean complainIfNoFile) {
		String s=(String) lastResultFiles.getSelectedItem();
		File result=null;
		if (s==null || (result=project.getFileFromNormalizedName(s))==null) {
			if (complainIfNoFile) {
				JOptionPane.showMessageDialog(null,
	                   "The result type you have selected requires an output file name",
	                   "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		/* We update the project's last result files */
		if (result!=null) {
			ArrayList<File> list = project.getLastResultFiles();
			list.remove(result);
			list.add(0,result);
			model.removeAllElements();
			for (File f : list) {
				model.addElement(project.getNormalizedFileName(f));
			}
			project.setLastResultFiles(list);
		}
		/*
		 * Everything is OK, so we can actually modify the project's
		 * configuration
		 */
		project.setBuildResultDisplay(display);
		project.setResultType(type);
		project.setResultOutputFile(result);
		project.setExtractMatchType(extractMatchType);
		return true;
	}

	public void validateAndSave(boolean complainIfNoFile) {
		if (validateConfiguration(project,complainIfNoFile)) {
			try {
				project.saveConfigurationFiles(false);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
	                    "Error while saving your project configuration:\n\n"+e.getCause(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		return validateConfiguration(project,false);
	}

}
