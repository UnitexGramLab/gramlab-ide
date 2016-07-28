package org.gramlab.core.gramlab.project.config.preprocess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.Language;

@SuppressWarnings("serial")
public class PolyLexPaneFactory extends ConfigurationPaneFactory {
	
	File bin=null;
	
	public PolyLexPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Configuring polylexical units analysis"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("This mecanism can only be used for Dutch, German,"),gbc);
		add(new JLabel("Norwegian and Russian. If you decide to use it,"),gbc);
		add(new JLabel("you have to choose the .bin dictionary to be"),gbc);
		add(new JLabel("used by the PolyLex program."),gbc);
		add(new JLabel(" "),gbc);
		ArrayList<File> files=project.getAllBinFiles();
		String lang=getPolyLexCompatibleLang(project); 
		String currentBin=null;
		if (lang!=null) {
			currentBin=project.getRelativeFileName(project.getPolyLexBin());
		}
		JPanel buttons=new JPanel(null);
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		ButtonGroup bg=new ButtonGroup();
		final JRadioButton noPolyLex=new JRadioButton("Don't apply PolyLex",currentBin==null);
		noPolyLex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (noPolyLex.isSelected()) {
					bin=null;
				}
			}
		});
		bg.add(noPolyLex);
		buttons.add(noPolyLex);
		if (lang!=null) {
			/* We don't want to display the buttons when the language
			 * is not one supported by PolyLex */
			for (int i=0;i<files.size();i++) {
				final File s=files.get(i);
				String name=project.getRelativeFileName(s);
				final JRadioButton b=new JRadioButton(name,name.equals(currentBin));
				if (b.isSelected()) {
					bin=s;
				}
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (b.isSelected()) {
							bin=s;
						}
					}
				});
				bg.add(b);
				buttons.add(b);
			}
		}
		if (bg.getSelection()==null) {
			/* Just in case... */
			noPolyLex.setSelected(true);
			bin=null;
		}
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		add(new JScrollPane(buttons),gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.weighty=0;
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setPolyLexBin(bin);
		return true;
	}
	
	
	public static PolyLexPaneFactory getPane(GramlabProject project) {
		return new PolyLexPaneFactory(project);
	}
	
	
	public static String getPolyLexCompatibleLang(GramlabProject project) {
		String s=project.getLanguage();
		if (s==null) return null;
		Language l=Language.getLanguage(s);
		if (l==null) return null;
		switch(l) {
		case de: return "GERMAN";
		case nl: return "DUTCH";
		case nb:
		case nn: return "NORWEGIAN";
		case ru: return "RUSSIAN";
		default: return null;
		}
	}
}
