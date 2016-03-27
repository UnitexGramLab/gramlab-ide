package fr.gramlab.project.config.rendering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.preprocess.ConfigurationPaneFactory;
import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.grf.GraphPresentationInfo;

@SuppressWarnings("serial")
public class RenderingConfigPane extends ConfigurationPaneFactory {

	JCheckBox rightToLeftForText;
	GraphPresentationInfo info;
	final JTextField textFont = new JTextField("");
	final JTextField concordanceFont = new JTextField("");
	final JTextField htmlViewer = new JTextField("");
	final JTextField textEditor = new JTextField("");
	JCheckBox useTextEditorForDics;
	FontInfo textFontInfo;
	FontInfo concordanceFontInfo;
	
	public RenderingConfigPane(final GramlabProject project) {
		super(new GridBagLayout());
		info=project.getPreferences().getInfo().clone();
		textFontInfo=project.getPreferences().getTextFont();
		textFont.setText(" " + textFontInfo.getFont().getFontName() + "  " + textFontInfo.getSize());
		textFont.setEnabled(false);
		textFont.setDisabledTextColor(Color.BLACK);
		concordanceFontInfo=project.getPreferences().getConcordanceFont();
		concordanceFont.setText(" " + concordanceFontInfo.getFont().getFontName() + "  " + 
				concordanceFontInfo.getSize());
		concordanceFont.setEnabled(false);
		concordanceFont.setDisabledTextColor(Color.BLACK);
		File f=project.getHtmlViewer();
		htmlViewer.setText((f==null)?"":f.getAbsolutePath());
		f=project.getTextEditor();
		textEditor.setText((f==null)?"":f.getAbsolutePath());
		
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		rightToLeftForText=new JCheckBox("Right to left rendering for text",project.getPreferences().isRightToLeftForText());
		add(rightToLeftForText,gbc);
		
		final JPanel tmp = new JPanel(new GridLayout(2, 1));
		tmp.add(new JLabel("Text Font:"));
		final JPanel tmp1 = new JPanel(new BorderLayout());
		textFont.setPreferredSize(new Dimension(400,textFont.getPreferredSize().height));
		tmp1.add(textFont, BorderLayout.CENTER);
		final Action textFontAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null,false)
					.getFrameManagerAs(InternalFrameManager.class).newFontDialog(textFontInfo);
				if (i != null) {
					textFontInfo = i;
					textFont
							.setText(" " + i.getFont().getFontName() + "  " + i.getSize());
				}
			}
		};
		final JButton setTextFont = new JButton(textFontAction);
		tmp1.add(setTextFont, BorderLayout.EAST);
		tmp.add(tmp1);
		add(tmp,gbc);
	
		final JPanel tmp_ = new JPanel(new GridLayout(2, 1));
		tmp_.add(new JLabel("Concordance Font:"));
		final JPanel tmp2_ = new JPanel(new BorderLayout());
		tmp2_.add(concordanceFont, BorderLayout.CENTER);
		final Action concord = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null,false)
					.getFrameManagerAs(InternalFrameManager.class).newFontDialog(concordanceFontInfo);
				if (i != null) {
					concordanceFontInfo = i;
					concordanceFont.setText(" " + i.getFont().getFontName() + "  "
							+ i.getSize());
				}
			}
		};
		final JButton setConcordanceFont = new JButton(concord);
		tmp2_.add(setConcordanceFont, BorderLayout.EAST);
		tmp_.add(tmp2_);
		add(tmp_,gbc);

		final JPanel htmlViewerPanel = new JPanel(new GridLayout(2, 1));
		htmlViewerPanel.add(new JLabel("Html viewer used to display concordances:"));
		final JPanel tmp3_ = new JPanel(new BorderLayout());
		final Action html = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your html viewer");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				File file=f.getSelectedFile();
				if (!file.canExecute()) {
					JOptionPane.showMessageDialog(null,
		                    "This file is not an executable one",
		                    "Error", JOptionPane.ERROR_MESSAGE);
					return;	
				}
				htmlViewer.setText(file.getAbsolutePath());
			}
		};
		final JButton setHtmlViewer = new JButton(html);
		tmp3_.add(htmlViewer, BorderLayout.CENTER);
		tmp3_.add(setHtmlViewer, BorderLayout.EAST);
		htmlViewerPanel.add(tmp3_);
		add(htmlViewerPanel,gbc);

		final JPanel textEditorPanel = new JPanel(new GridLayout(2, 1));
		textEditorPanel.add(new JLabel("External editor to use to edit text files:"));
		final JPanel tmp4_ = new JPanel(new BorderLayout());
		final Action editor = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your text editor");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				File file=f.getSelectedFile();
				if (!file.canExecute()) {
					JOptionPane.showMessageDialog(null,
		                    "This file is not an executable one",
		                    "Error", JOptionPane.ERROR_MESSAGE);
					return;	
				}
				textEditor.setText(file.getAbsolutePath());
			}
		};
		final JButton setTextEditor = new JButton(editor);
		tmp4_.add(textEditor, BorderLayout.CENTER);
		tmp4_.add(setTextEditor, BorderLayout.EAST);
		textEditorPanel.add(tmp4_);
		add(textEditorPanel,gbc);
		useTextEditorForDics=new JCheckBox("Use external editor to display dictionaries",
				project.useTextEditorForDictionaries());
		add(useTextEditorForDics,gbc);

		final JButton graphConfig = new JButton("Graph configuration");
		graphConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphPresentationInfo i = GlobalProjectManager
						.search(null,false).getFrameManagerAs(InternalFrameManager.class)
						.newGraphPresentationDialog(info, false);
				if (i != null) {
					info = i;
				}
			}
		});
		gbc.fill=GridBagConstraints.NONE;
		add(new JLabel(" "),gbc);
		add(graphConfig,gbc);
		
		gbc.weighty=1;
		add(new JPanel(null),gbc);
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}

	
	
	@Override
	public boolean validateConfiguration(GramlabProject project) {
		File html=null;
		if (!"".equals(htmlViewer.getText())) {
			html=new File(htmlViewer.getText());
			if (!html.exists()) {
				JOptionPane.showMessageDialog(null,
	                    "The specified HTML viewer does not exist",
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		File editor=null;
		if (!"".equals(textEditor.getText())) {
			editor=new File(textEditor.getText());
			if (!editor.exists()) {
				JOptionPane.showMessageDialog(null,
	                    "The specified text editor does not exist",
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		/* Everything is OK, so we can actually modify the project's
		 * configuration */
		Preferences pref=project.getPreferences();
		pref.setRightToLeftForText(rightToLeftForText.isSelected());
		pref.setTextFont(textFontInfo);
		pref.setConcordanceFont(concordanceFontInfo);
		project.setHtmlViewer(html);
		project.setTextEditor(editor);
		project.setUseTextEditorForDictionaries(useTextEditorForDics.isSelected());
		pref.setInfo(info);
		return true;
	}
}
