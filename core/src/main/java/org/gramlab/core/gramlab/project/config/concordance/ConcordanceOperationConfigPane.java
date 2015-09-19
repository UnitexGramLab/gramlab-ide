package fr.gramlab.project.config.concordance;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.gramlab.project.ProcessPane;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.preprocess.ConfigurationPaneFactory;
import fr.umlv.unitex.LinkButton;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;

@SuppressWarnings("serial")
public class ConcordanceOperationConfigPane extends ConfigurationPaneFactory {
	
	ConcordanceOperationType type;
	ResultDisplay display;
	JRadioButton[] radioButtons=new JRadioButton[3];
	
	
	private final static String[] names = new String[] {
			"Diff with previous concordance",
			"Show only ambiguous outputs", 
			"Build concordance" };
	private final static ConcordanceOperationType[] values = new ConcordanceOperationType[] {
		ConcordanceOperationType.SHOW_DIFFERENCES_WITH_PREVIOUS_CONCORDANCE,
		ConcordanceOperationType.SHOW_AMBIGUOUS_OUTPUTS, 
		ConcordanceOperationType.BUILD_CONCORDANCE };
	final JRadioButton[] buttons = new JRadioButton[names.length];
	private final ButtonGroup bg = new ButtonGroup();
	
	ConcordanceConfigPane concordanceConfigPane;

	private GramlabProject project;
	
	public ConcordanceOperationConfigPane(GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		type = project.getBuildConcordanceType();
		for (int i = 0; i < names.length; i++) {
			buttons[i] = new JRadioButton(names[i], values[i] == type);
			final JRadioButton b = buttons[i];
			final ConcordanceOperationType t = values[i];
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
		add(createOtherPanel(), gbc);
		gbc.weighty = 1;
		add(new JPanel(null), gbc);
	}

	private JPanel createOtherPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Concordance"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.insets=new Insets(0,0,0,5);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(buttons[0],gbc);
		p.add(buttons[1],gbc);
		p.add(buttons[2],gbc);
		concordanceConfigPane=new ConcordanceConfigPane(project);
		LinkButton param=new LinkButton();
		Runnable onHide=new Runnable() {
			@Override
			public void run() {
				concordanceConfigPane.validateAndSave();
			}
		};
		JPanel foo=ProcessPane.createHidablePane(param,"Concordance parameters \u25BC", 
				"Concordance parameters \u25B2", concordanceConfigPane,onHide);
		foo.add(param,BorderLayout.NORTH);
		p.add(foo,gbc);
		
		LinkButton showResults=new LinkButton();
		final JPanel tmp=new JPanel(new BorderLayout());
		tmp.add(createDisplayPanel(project),BorderLayout.CENTER);
		JPanel foo2=ProcessPane.createHidablePane(showResults,"Display \u25BC", 
				"Display \u25B2",tmp,null);
		foo2.add(showResults,BorderLayout.NORTH);
		p.add(foo2,gbc);
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
		display=project.getConcordanceDisplay();
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
			radioButtons[i]=new JRadioButton();
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
						validateAndSave();
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

	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		if (!concordanceConfigPane.validateConfiguration(project)) {
			return false;
		}
		/*
		 * Everything is OK, so we can actually modify the project's
		 * configuration
		 */
		project.setConcordanceDisplay(display);
		project.setBuildConcordanceType(type);
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
