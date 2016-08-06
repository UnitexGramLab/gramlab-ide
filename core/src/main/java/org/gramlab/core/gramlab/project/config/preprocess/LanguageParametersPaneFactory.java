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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.gramlab.core.gramlab.project.GramlabProject;

@SuppressWarnings("serial")
public class LanguageParametersPaneFactory extends ConfigurationPaneFactory {
	
	File arabicRules=null;
	boolean isKorean=false;
	boolean isMatchWordBoundaries=true;
	boolean isSemitic=false;
	boolean isCharByChar=false;
	boolean isMorphoUseOfSpace=false;
	
	public LanguageParametersPaneFactory(final GramlabProject project) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Language options"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(new JLabel("You can configure here some properties that"),gbc);
		add(new JLabel("should depend on the language of your project."),gbc);
		add(new JLabel("DO NOT MODIFY these values unless you really"),gbc);
		add(new JLabel("know what you are doing."),gbc);
		add(new JLabel(" "),gbc);
		/* init */
		arabicRules=project.getArabicTypoRules();
		isKorean=project.isKorean();
		isMatchWordBoundaries=project.isMatchWordBoundaries();
		isSemitic=project.isSemitic();
		isCharByChar=project.isCharByChar();
		isMorphoUseOfSpace=project.isMorphologicalUseOfSpace();
		
		ArrayList<String> files=project.getAllArabicTypoRulesFiles();
		String arabicTypoRules=project.getRelativeFileName(project.getArabicTypoRules());
		JPanel buttons=new JPanel(null);
		buttons.setBorder(BorderFactory.createTitledBorder("Arabic typographic rules"));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		ButtonGroup bg=new ButtonGroup();
		final JRadioButton noArabicTypoRules=new JRadioButton("Don't use an Arabic rule file",arabicTypoRules==null);
		noArabicTypoRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (noArabicTypoRules.isSelected()) {
					arabicRules=null;
				}
			}
		});
		bg.add(noArabicTypoRules);
		buttons.add(noArabicTypoRules);
		for (int i=0;i<files.size();i++) {
			final String s=files.get(i);
			final JRadioButton b=new JRadioButton(s,s.equals(arabicTypoRules));
			if (b.isSelected()) {
				arabicRules=new File(project.getProjectDirectory(),s);
			}
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						arabicRules=new File(project.getProjectDirectory(),s);
					}
				}
			});
			bg.add(b);
			buttons.add(b);
		}
		if (bg.getSelection()==null) {
			/* Just in case... */
			noArabicTypoRules.setSelected(true);
			arabicRules=null;
		}
		add(new JScrollPane(buttons),gbc);
		final JCheckBox korean=new JCheckBox("Enable Korean features",isKorean);
		korean.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isKorean=korean.isSelected();
			}
		});
		add(korean,gbc);
		final JCheckBox semitic=new JCheckBox("Enable semitic features",isSemitic);
		semitic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isSemitic=semitic.isSelected();
			}
		});
		add(semitic, gbc);
		final JCheckBox matchWordBoundaries=new JCheckBox("Match word boundaries",isMatchWordBoundaries);
		matchWordBoundaries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isMatchWordBoundaries=matchWordBoundaries.isSelected();
			}
		});
		add(matchWordBoundaries,gbc);
		final JCheckBox charByChar=new JCheckBox("Enable char by char analysis",isCharByChar);
		charByChar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isCharByChar=charByChar.isSelected();
			}
		});
		add(charByChar,gbc);
		final JCheckBox morpho=new JCheckBox("Enable morphological use of space",isMorphoUseOfSpace);
		morpho.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isMorphoUseOfSpace=morpho.isSelected();
			}
		});
		add(morpho,gbc);
		gbc.weighty=1;
		add(new JPanel(),gbc);
	}

	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setArabicTypoRules(arabicRules);
		project.setKorean(isKorean);
		project.setMatchWordBoundaries(isMatchWordBoundaries);
		project.setSemitic(isSemitic);
		project.setCharByChar(isCharByChar);
		project.setMorphologicalUseOfSpace(isMorphoUseOfSpace);
		return true;
	}
	
	
	public static LanguageParametersPaneFactory getPane(GramlabProject project) {
		return new LanguageParametersPaneFactory(project);
	}
	
}
