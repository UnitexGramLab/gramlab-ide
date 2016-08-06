package org.gramlab.core.gramlab.project.config.preprocess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.maven.Artifact;
import org.gramlab.core.gramlab.project.config.maven.PomIO;
import org.gramlab.core.gramlab.project.config.preprocess.fst2txt.PreprocessingPaneFactory;
import org.gramlab.core.gramlab.project.config.preprocess.fst2txt.PreprocessingStep;
import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class ConfigBigPictureDialog extends JDialog {
	
	JPanel container;
	
	public ConfigBigPictureDialog(GramlabProject project) {
		super(Main.getMainFrame(), "Configuring preprocessing of project "+project.getName(), true);
		JPanel p=new JPanel(new BorderLayout());
		container=new JPanel(new BorderLayout());
		p.add(container,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		KeyUtil.addCRListener(ok);
		down.add(ok);
		p.add(down,BorderLayout.SOUTH);
		update(project);
		setContentPane(p);
		setSize(450,550);		
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}

	private Component constructMainPane(GramlabProject project) {
		JPanel main=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(5,5,5,5);
		main.add(createInfoPane(project),gbc);
		main.add(createLanguagePane(project),gbc);
		main.add(createDependenciesPane(project),gbc);
		main.add(createAlphabetPane(project),gbc);
		main.add(createSortAlphabetPane(project),gbc);
		main.add(createNormPane(project),gbc);
		main.add(createPreprocessingPane(project),gbc);
		main.add(createMorphoDicsPane(project),gbc);
		main.add(createDicsPane(project),gbc);
		main.add(createPolyLexPane(project),gbc);
		gbc.weighty=1;
		main.add(new JPanel(),gbc);
		return new JScrollPane(main);
	}
	
	private JPanel createTitledPanel(LayoutManager lm,String title) {
		JPanel p=new JPanel(lm);
		p.setBorder(BorderFactory.createTitledBorder(title));
		return p;
	}

	private void updateInfoPane(GramlabProject p,JLabel name,JLabel language,JLabel encoding,
			JLabel groupId,JLabel artifactId,JLabel version) {
		name.setText(p.getName());
		language.setText(p.getLanguage());
		encoding.setText(""+p.getEncoding());
		Artifact a=p.getPom().getArtifact();
		groupId.setText(a.getGroupId());
		artifactId.setText(a.getArtifactId());
		version.setText(a.getVersion());
	}
	
	private JPanel createInfoPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new GridBagLayout(),"General project information");
		final JLabel name=new JLabel();
		final JLabel language=new JLabel();
		final JLabel encoding=new JLabel();
		final JLabel groupId=new JLabel();
		final JLabel artifactId=new JLabel();
		final JLabel version=new JLabel();
		updateInfoPane(project,name,language,encoding,groupId,artifactId,version);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,MainInfoPaneFactory.class);
				updateInfoPane(project,name,language,encoding,groupId,artifactId,version);
			}
		});
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		p.add(new JLabel("Name: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(name,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Language: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		p.add(language,gbc);
		gbc.gridwidth=1;
		gbc.weightx=0;
		p.add(new JLabel("Encoding: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(encoding,gbc);
		gbc.gridwidth=1;
		gbc.weightx=0;
		p.add(new JLabel("groupId: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(groupId,gbc);
		gbc.gridwidth=1;
		gbc.weightx=0;
		p.add(new JLabel("artifactId: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(artifactId,gbc);
		gbc.gridwidth=1;
		gbc.weightx=0;
		p.add(new JLabel("version: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(version,gbc);
		gbc.weightx=0;
		p.add(set,gbc);
		KeyUtil.addCRListener(set);
		return p;
	}

	private JPanel createLanguagePane(final GramlabProject project) {
		JPanel p=createTitledPanel(new GridBagLayout(),"Language related parameters");
		final JLabel arabic=new JLabel();
		final JLabel korean=new JLabel();
		final JLabel semitic=new JLabel();
		final JLabel charByChar=new JLabel();
		final JLabel morpho=new JLabel();
		updateLanguagePane(project,arabic,korean,semitic,charByChar,morpho);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,LanguageParametersPaneFactory.class);
				updateLanguagePane(project,arabic,korean,semitic,charByChar,morpho);
			}
		});
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(arabic,gbc);
		p.add(korean,gbc);
		p.add(semitic,gbc);
		p.add(charByChar,gbc);
		p.add(morpho,gbc);
		p.add(set,gbc);
		KeyUtil.addCRListener(set);
		return p;
	}

	private static final Color ENABLED=Color.GREEN.darker();
	private static final Color DISABLED=Color.RED;
	
	
	private void updateLanguagePane(GramlabProject project, JLabel arabic,
			JLabel korean, JLabel semitic, JLabel charByChar, JLabel morpho) {
		File f=project.getArabicTypoRules();
		if (f==null) {
			arabic.setText("Use arabic typographical rules: no");
			arabic.setForeground(DISABLED);
		} else {
			arabic.setText("Use arabic typographical rules: "+project.getRelativeFileName(f));
			arabic.setForeground(ENABLED);
		}
		korean.setText("Korean features "+(project.isKorean()?"enabled":"disabled"));
		korean.setForeground(project.isKorean()?ENABLED:DISABLED);
		semitic.setText("Semitic features "+(project.isSemitic()?"enabled":"disabled"));
		semitic.setForeground(project.isSemitic()?ENABLED:DISABLED);
		charByChar.setText("Char by char analysis "+(project.isCharByChar()?"enabled":"disabled"));
		charByChar.setForeground(project.isCharByChar()?ENABLED:DISABLED);
		morpho.setText("Morphological use of space "+(project.isMorphologicalUseOfSpace()?"enabled":"disabled"));
		morpho.setForeground(project.isMorphologicalUseOfSpace()?ENABLED:DISABLED);
	}

	private JPanel createDependenciesPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Dependencies");
		JPanel top=new JPanel(new GridLayout(3,1));
		top.add(new JLabel("Note: if you update the project dependencies,"));
		top.add(new JLabel("you will have to reconfigure the whole project."));
		top.add(new JLabel(" "));
		p.add(top,BorderLayout.NORTH);
		
		final DefaultListModel model=new DefaultListModel();
		for (Artifact a:project.getPom().getDependencies()) {
			model.addElement(a);
		}
		final JList list=new JList(model);
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
		p.add(list,BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,true,DependenciesPaneFactory.class);
				update(project);
			}
		});
		p.add(set,BorderLayout.SOUTH);
		KeyUtil.addCRListener(set);
		return p;
	}

	private void updateAlphabet(JLabel alphabet,GramlabProject project) {
		File f=project.getAlphabet();
		if (f==null) {
			alphabet.setText("Use no alphabet file");
			alphabet.setForeground(DISABLED);
			return;
		}
		alphabet.setText(project.getRelativeFileName(f));
		alphabet.setForeground(ENABLED);
	}

	private JPanel createAlphabetPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Alphabet");
		final JLabel alphabet=new JLabel();
		updateAlphabet(alphabet,project);
		p.add(alphabet,BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,AlphabetPaneFactory.class);
				updateAlphabet(alphabet,project);
			}
		});
		p.add(set,BorderLayout.SOUTH);
		KeyUtil.addCRListener(set);
		return p;
	}

	private void updateSortAlphabet(JLabel alphabet,GramlabProject project) {
		File f=project.getSortAlphabet();
		if (f==null) {
			alphabet.setText("Use no sort alphabet file");
			alphabet.setForeground(DISABLED);
			return;
		}
		alphabet.setText(project.getRelativeFileName(f));
		alphabet.setForeground(ENABLED);
	}

	private JPanel createSortAlphabetPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Sort Alphabet");
		final JLabel alphabet=new JLabel();
		updateSortAlphabet(alphabet,project);
		p.add(alphabet,BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,SortAlphabetPaneFactory.class);
				updateSortAlphabet(alphabet,project);
			}
		});
		p.add(set,BorderLayout.SOUTH);
		KeyUtil.addCRListener(set);
		return p;
	}

	private void updateNorm(JLabel separators,JLabel norm,GramlabProject project) {
		File f=project.getNormTxt();
		if (f==null) {
			norm.setText("Use no normalization file");
			norm.setForeground(DISABLED);
		} else {
			norm.setText(project.getRelativeFileName(f));
			norm.setForeground(ENABLED);
		}
		if (project.separatorNormalization()) {
			separators.setText("Separator normalization: yes");
			separators.setForeground(ENABLED);
		} else {
			separators.setText("Separator normalization: no");
			separators.setForeground(DISABLED);
		}
	}

	private JPanel createNormPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new GridLayout(3,1),"Normalization");
		final JLabel separators=new JLabel();
		final JLabel norm=new JLabel();
		updateNorm(separators,norm,project);
		p.add(separators);
		p.add(norm);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,NormalizationPaneFactory.class);
				updateNorm(separators,norm,project);
			}
		});
		p.add(set);
		KeyUtil.addCRListener(set);
		return p;
	}

	private JPanel createPreprocessingPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Preprocessing");
		DefaultListModel model = new DefaultListModel();
		for (PreprocessingStep s : project.getPreprocessing()
				.getPreprocessingSteps()) {
			if (!s.isSelected()) continue;
			model.addElement(s);
		}
		final JList list = new JList(model);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private final File preprocessDir = new File(project
					.getProjectDirectory(), PomIO.TARGET_PREPROCESS_DIRECTORY);

			private String getSimpleGraphName(File f) {
				return FileUtil.getRelativePath(project.getProjectDirectory(),
						f);
			}

			private String getSimpleDestName(File f) {
				return FileUtil.getRelativePath(preprocessDir, f);
			}

			private JPanel renderer = new JPanel(new GridLayout(3, 1));
			private JLabel l1 = new JLabel();
			private JLabel l2 = new JLabel();
			private JLabel l3 = new JLabel();
			{
				renderer.setBackground(Color.WHITE);
				renderer.add(l1);
				renderer.add(l2);
				renderer.add(l3);
			}

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				PreprocessingStep p = (PreprocessingStep) value;
				l1.setText("Source: " + getSimpleGraphName(p.getGraph()));
				l2.setText("Dest: " + getSimpleDestName(p.getDestFst2()));
				l3.setText("Mode: " + (p.isMerge() ? "MERGE" : "REPLACE"));
				super.getListCellRendererComponent(list, "", index, isSelected,
						cellHasFocus);
				if (!isSelected) {
					renderer.setBackground((index % 2 == 0) ? Color.WHITE
							: Color.LIGHT_GRAY);
				} else {
					renderer.setBackground(getBackground());
				}
				return renderer;
			}
		});
		p.add(list,BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,PreprocessingPaneFactory.class);
				update(project);
			}
		});
		KeyUtil.addCRListener(set);
		p.add(set,BorderLayout.SOUTH);
		return p;
	}

	private JPanel createMorphoDicsPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Morphological-mode dictionaries");
		DefaultListModel model=new DefaultListModel();
		for (File f:project.getMorphoDics()) {
			model.addElement(project.getRelativeFileName(f));
		}
		p.add(new JList(model),BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,MorphoDicsPaneFactory.class);
				update(project);
			}
		});
		p.add(set,BorderLayout.SOUTH);
		KeyUtil.addCRListener(set);
		return p;
	}

	private JPanel createDicsPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new BorderLayout(),"Dictionaries");
		final JCheckBox applyDics=new JCheckBox("Apply dictionaries",project.applyDictionaries());
		applyDics.setForeground(applyDics.isSelected()?ENABLED:DISABLED);
		applyDics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyDics.setForeground(applyDics.isSelected()?ENABLED:DISABLED);
				project.setApplyDictionaries(applyDics.isSelected());
				try {
					project.saveConfigurationFiles(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		p.add(applyDics,BorderLayout.NORTH);
		DefaultListModel model=new DefaultListModel();
		for (File f:project.getDictionaries()) {
			model.addElement(project.getRelativeFileName(f));
		}
		p.add(new JList(model),BorderLayout.CENTER);
		LinkButton set=new LinkButton("Set...");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,DicsPaneFactory.class);
				update(project);
			}
		});
		KeyUtil.addCRListener(set);
		p.add(set,BorderLayout.SOUTH);
		return p;
	}

	private void updatePolylex(JButton set,JLabel polylex,GramlabProject project) {
		if (null==PolyLexPaneFactory.getPolyLexCompatibleLang(project)) {
			polylex.setText("PolyLex does not support language '"+project.getLanguage()+"'");
			polylex.setForeground(DISABLED);
			set.setEnabled(false);
			return;
		}
		set.setEnabled(true);
		File bin=project.getPolyLexBin();
		if (bin==null) {
			polylex.setText("PolyLex is deactivated");
			polylex.setForeground(DISABLED);
		} else {
			polylex.setText("PolyLex will run using "+project.getRelativeFileName(bin));
			polylex.setForeground(ENABLED);
		}
	}

	private JPanel createPolyLexPane(final GramlabProject project) {
		JPanel p=createTitledPanel(new GridLayout(2,1),"Polylexical units analysis");
		final JLabel polylex=new JLabel();
		final LinkButton set=new LinkButton("Set...");
		updatePolylex(set,polylex,project);
		p.add(polylex);
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureProjectDialog(project,false,false,PolyLexPaneFactory.class);
				updatePolylex(set,polylex,project);
			}
		});
		p.add(set);
		KeyUtil.addCRListener(set);
		return p;
	}

	private void update(GramlabProject project) {
		container.removeAll();
		container.add(constructMainPane(project),BorderLayout.CENTER);
		container.revalidate();
		container.repaint();
	}
	
}
