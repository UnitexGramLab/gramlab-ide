/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.frames;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Text;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.UnitexFrame;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.process.*;

/**
 * This class describes a dialog box that allows the user to parameter the
 * preprocessing of a text.
 * 
 * @author Sébastien Paumier
 * 
 */
public class PreprocessDialog extends JDialog {

    JCheckBox sentenceCheck = new JCheckBox("Apply graph in MERGE mode:", true);
    JCheckBox replaceCheck = new JCheckBox("Apply graph in REPLACE mode:", true);
    JTextField sentenceName = new JTextField();
    JTextField replaceName = new JTextField();
    JCheckBox applyDicCheck = new JCheckBox("Apply All default Dictionaries", true);
    JCheckBox analyseUnknownWordsCheck = new JCheckBox("Analyse unknown words as free compound words (this option", true);
    JLabel analyseUnknownWordsLabel = new JLabel("     is available only for Dutch, German, Norwegian & Russian)");
    JCheckBox textFst2Check = new JCheckBox("Construct Text Automaton", false);
    File originalTextFile;
    File sntFile;
    JPanel preprocessingParent;
    JPanel preprocessingCurrent;
    JPanel preprocessingTaggedText;
    JPanel preprocessingUntaggedText;
    boolean taggedText=false;
    
    
    /**
     * Creates and shows a new <code>PreprocessFrame</code>
     * 
     * @param originalTextFile
     *            original text file
     * @param sntFile
     *            snt text file
     * @param taggedText
     *            true if the text is a tagged one
     */
    PreprocessDialog() {
        super(UnitexFrame.mainFrame, "Preprocessing & Lexical parsing", true);
        setContentPane(preprocessingParent=constructPanel());
        refreshOnLanguageChange();
        pack();
        setResizable(false);
        setLocationRelativeTo(UnitexFrame.mainFrame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }


    void refreshOnLanguageChange() {
    	if (Config.getCurrentLanguage().equals("Dutch") 
    			|| Config.getCurrentLanguage().equals("German") 
    			|| Config.getCurrentLanguage().equals("Norwegian") 
    			|| Config.getCurrentLanguage().equals("Russian")) {
            analyseUnknownWordsCheck.setSelected(true);
            analyseUnknownWordsCheck.setEnabled(true);
            analyseUnknownWordsLabel.setEnabled(true);
        } else {
            analyseUnknownWordsCheck.setSelected(false);
            analyseUnknownWordsCheck.setEnabled(false);
            analyseUnknownWordsLabel.setEnabled(false);
        }
        sentenceName.setText(Config.getCurrentSentenceGraph().getAbsolutePath());
        replaceName.setText(Config.getCurrentReplaceGraph().getAbsolutePath());
        applyDicCheck.setSelected(true);
        textFst2Check.setSelected(false);
    }
    
    
    void setFiles(File originalTextFile, File sntFile, boolean taggedText) {
    	this.originalTextFile=originalTextFile;
    	this.sntFile=sntFile;
    	this.taggedText=taggedText;
		preprocessingParent.remove(preprocessingCurrent);
    	if (taggedText) {
    		preprocessingCurrent=preprocessingTaggedText;
    	} else {
    		preprocessingCurrent=preprocessingUntaggedText;
    	}
    	preprocessingParent.add(preprocessingCurrent,BorderLayout.NORTH);
    	preprocessingParent.revalidate();
    	repaint();	
    }

    
    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(constructProcessingPanel(), BorderLayout.NORTH);
        panel.add(constructTokenizingPanel(), BorderLayout.CENTER);
        JPanel down = new JPanel(new BorderLayout());
        down.add(constructLexicalParsingPanel(), BorderLayout.WEST);
        down.add(constructButtonsPanel(), BorderLayout.CENTER);
        panel.add(down, BorderLayout.SOUTH);
        return panel;
    }

	private JPanel constructProcessingPanel() {
		/* Building the panels for both tagged and untagged texts */
		preprocessingTaggedText = new JPanel(new BorderLayout());
		preprocessingTaggedText.setBorder(new TitledBorder("Preprocessing"));
		preprocessingTaggedText
				.add(new JLabel(
						"Sentence and Replace graphs should not be applied on tagged texts."));
		preprocessingUntaggedText = new JPanel(new GridLayout(2, 1));
		preprocessingUntaggedText.setBorder(new TitledBorder("Preprocessing"));
		preprocessingUntaggedText.add(constructSentencePanel());
		preprocessingUntaggedText.add(constructReplacePanel());
		return preprocessingCurrent=preprocessingUntaggedText;
	}

    private JPanel constructSentencePanel() {
        JPanel sentence = new JPanel(new BorderLayout());
        Dimension d = sentenceCheck.getPreferredSize();
        sentenceCheck.setPreferredSize(new Dimension(190, d.height));
        sentence.add(sentenceCheck, BorderLayout.WEST);
        d = sentenceName.getPreferredSize();
        sentenceName.setPreferredSize(new Dimension(150, d.height));
        sentence.add(sentenceName, BorderLayout.CENTER);
        Action setAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = Config.getSentenceDialogBox();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on CANCEL
                    return;
                }
                sentenceName.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setSentence = new JButton(setAction);
        sentence.add(setSentence, BorderLayout.EAST);
        return sentence;
    }

    private JPanel constructReplacePanel() {
        JPanel replace = new JPanel(new BorderLayout());
        Dimension d = replaceCheck.getPreferredSize();
        replaceCheck.setPreferredSize(new Dimension(190, d.height));
        replace.add(replaceCheck, BorderLayout.WEST);
        d = replaceName.getPreferredSize();
        replaceName.setPreferredSize(new Dimension(150, d.height));
        replace.add(replaceName, BorderLayout.CENTER);
        Action setAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = Config.getReplaceDialogBox();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on CANCEL
                    return;
                }
                replaceName.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setReplace = new JButton(setAction);
        replace.add(setReplace, BorderLayout.EAST);
        return replace;
    }

    private JPanel constructTokenizingPanel() {
        JPanel tokenizingPanel = new JPanel(new GridLayout(2, 1));
        tokenizingPanel.setBorder(new TitledBorder("Tokenizing"));
        tokenizingPanel.add(new JLabel("The text is automatically tokenized. This operation is language-dependant,"));
        tokenizingPanel.add(new JLabel("so that Unitex can handle languages with special spacing rules."));
        return tokenizingPanel;
    }

    private JPanel constructLexicalParsingPanel() {
        JPanel lexicalParsing = new JPanel(new GridLayout(4, 1));
        lexicalParsing.setBorder(new TitledBorder("Lexical Parsing"));
        lexicalParsing.add(applyDicCheck);
        lexicalParsing.add(analyseUnknownWordsCheck);
        lexicalParsing.add(analyseUnknownWordsLabel);
        lexicalParsing.add(textFst2Check);
        return lexicalParsing;
    }

    private JPanel constructButtonsPanel() {
        JPanel buttons = new JPanel(new GridLayout(3, 1));
        buttons.setBorder(new EmptyBorder(8, 8, 2, 2));
        Action goAction = new AbstractAction("GO!") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                // post pone code
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        File dir = Config.getCurrentSntDir();
                        MultiCommands commands = new MultiCommands();
                        // NORMALIZING TEXT...
                        NormalizeCommand normalizeCmd = new NormalizeCommand().textWithDefaultNormalization(originalTextFile);
                        commands.addCommand(normalizeCmd);
                        // CREATING SNT DIR
                        MkdirCommand mkdir = new MkdirCommand().name(dir);
                        commands.addCommand(mkdir);
                        // SENTENCE GRAPH...
                        if (sentenceCheck.isSelected()) {
                            File sentence = new File(sentenceName.getText());
                            if (!sentence.exists()) {
                                commands.addCommand(new MessageCommand("*** WARNING: sentence delimitation skipped because the graph was not found ***\n", true));
                            } else {
                                String grfName = sentenceName.getText();
                                File fst2;
                                if (grfName.substring(grfName.length() - 3, grfName.length()).equalsIgnoreCase("grf")) {
                                    // we must compile the grf
                                    Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(new File(grfName)).enableLoopAndRecursionDetection(true).tokenizationMode().library();
                                    commands.addCommand(grfCmd);
                                    String fst2Name = grfName.substring(0, grfName.length() - 3);
                                    fst2Name = fst2Name + "fst2";
                                    fst2 = new File(fst2Name);
                                    // and flatten it for better performance
                                    // (Fst2Txt is slow with complex graphs)
                                    FlattenCommand flattenCmd = new FlattenCommand().fst2(fst2).resultType(false).depth(5);
                                    commands.addCommand(flattenCmd);
                                } else {
                                    if (!(grfName.substring(grfName.length() - 4, grfName.length()).equalsIgnoreCase("fst2"))) {
                                        // if the extension is nor GRF neither
                                        // FST2
                                        JOptionPane.showMessageDialog(null, "Invalid graph name extension !", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    fst2 = new File(grfName);
                                }
                                Fst2TxtCommand cmd = new Fst2TxtCommand().text(Config.getCurrentSnt()).fst2(fst2).alphabet().mode(true);
                                if (Config.isCharByCharLanguage())
                                    cmd = cmd.charByChar(Config.morphologicalUseOfSpaceAllowed());
                                commands.addCommand(cmd);
                            }
                        }
                        // REPLACE GRAPH...
                        if (replaceCheck.isSelected()) {
                            File f = new File(replaceName.getText());
                            if (!f.exists()) {
                                commands.addCommand(new MessageCommand("*** WARNING: Replace step skipped because the graph was not found ***\n", true));
                            } else {
                                String grfName = replaceName.getText();
                                File fst2;
                                if (grfName.substring(grfName.length() - 3, grfName.length()).equalsIgnoreCase("grf")) {
                                    // we must compile the grf
                                    Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(new File(grfName)).enableLoopAndRecursionDetection(true).tokenizationMode();
                                    commands.addCommand(grfCmd);
                                    String fst2Name = grfName.substring(0, grfName.length() - 3);
                                    fst2Name = fst2Name + "fst2";
                                    fst2 = new File(fst2Name);
                                } else {
                                    if (!(grfName.substring(grfName.length() - 4, grfName.length()).equalsIgnoreCase("fst2"))) {
                                        // if the extension is nor GRF neither
                                        // FST2
                                        JOptionPane.showMessageDialog(null, "Invalid graph name extension !", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    fst2 = new File(grfName);
                                }
                                Fst2TxtCommand cmd = new Fst2TxtCommand().text(Config.getCurrentSnt()).fst2(fst2).alphabet().mode(false);
                                if (Config.isCharByCharLanguage())
                                    cmd = cmd.charByChar(Config.morphologicalUseOfSpaceAllowed());
                                commands.addCommand(cmd);
                            }
                        }
                        // TOKENIZING...
                        TokenizeCommand tokenizeCmd = new TokenizeCommand().text(Config.getCurrentSnt()).alphabet();
                        if (Config.isCharByCharLanguage()) {
                            tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
                        }
                        commands.addCommand(tokenizeCmd);
                        // APPLYING DEFAULT DICTIONARIES...
                        DicoCommand dicoCmd;
                        if (applyDicCheck.isSelected()) {
                                dicoCmd = new DicoCommand().snt(Config.getCurrentSnt()).alphabet(Config.getAlphabet()).morphologicalDic(Preferences.pref.morphologicalDic);
                                if (Config.isKorean()) {
                                    dicoCmd=dicoCmd.korean();
                                }
                                ArrayList<File> param = getDefaultDicList();
                                if (param != null && param.size() > 0) {
                                    dicoCmd = dicoCmd.dictionaryList(param);
                                    commands.addCommand(dicoCmd);
                                } else {
                                    dicoCmd = null;
                                }
                                // ANALYSING UNKNOWN WORDS
                                String lang = Config.getCurrentLanguage();
                                File dic = new File(Config.getUnitexCurrentLanguageDir(), "Dela");
                                if (lang.equals("German"))
                                    dic = new File(dic, "dela.bin");
                                else if (lang.equals("Norwegian"))
                                    dic = new File(dic, "Dela-sample.bin");
                                else if (lang.equals("Russian"))
                                    dic = new File(dic, "CISLEXru_igrok.bin");
                                if (analyseUnknownWordsCheck.isSelected()) {
                                    PolyLexCommand polyLexCmd;
                                    try {
                                        polyLexCmd = new PolyLexCommand().language(lang).alphabet().bin(dic).wordList(new File(Config.getCurrentSntDir(), "err")).output(new File(Config.getCurrentSntDir(), "dlf")).info(new File(Config.getCurrentSntDir(), "decomp.txt"));
                                        commands.addCommand(polyLexCmd);
                                    } catch (InvalidPolyLexArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // SORTING TEXT DICTIONARIES
                                if (dicoCmd != null) {
                                    // sorting DLF
                                    SortTxtCommand sortCmd = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "dlf")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "dlf.n"));
                                    if (Config.getCurrentLanguage().equals("Thai")) {
                                        sortCmd = sortCmd.thai();
                                    } else {
                                        sortCmd = sortCmd.sortAlphabet();
                                    }
                                    commands.addCommand(sortCmd);
                                    // sorting DLC
                                    SortTxtCommand sortCmd2 = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "dlc")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "dlc.n"));
                                    if (Config.getCurrentLanguage().equals("Thai")) {
                                        sortCmd2 = sortCmd2.thai();
                                    } else {
                                        sortCmd2 = sortCmd2.sortAlphabet();
                                    }
                                    commands.addCommand(sortCmd2);
                                    // sorting ERR
                                    SortTxtCommand sortCmd3 = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "err")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "err.n"));
                                    if (Config.getCurrentLanguage().equals("Thai")) {
                                        sortCmd3 = sortCmd3.thai();
                                    } else {
                                        sortCmd3 = sortCmd3.sortAlphabet();
                                    }
                                    commands.addCommand(sortCmd3);
                                }
                            }
                        // CONSTRUCTING TEXT AUTOMATON
                        if (textFst2Check.isSelected()) {
                            File norm = Config.getCurrentNormGraph();
                            if (!norm.exists()) {
                                commands.addCommand(new MessageCommand("*** WARNING: normalization graph was not found ***\n", true));
                                norm = null;
                            } else {
                                String grfName = norm.getAbsolutePath();
                                if (grfName.substring(grfName.length() - 3, grfName.length()).equalsIgnoreCase("grf")) {
                                    // we must compile the grf
                                    Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(new File(grfName)).enableLoopAndRecursionDetection(true).tokenizationMode();
                                    commands.addCommand(grfCmd);
                                    String fst2Name = grfName.substring(0, grfName.length() - 3);
                                    fst2Name = fst2Name + "fst2";
                                    norm = new File(fst2Name);
                                } else {
                                    if (!(grfName.substring(grfName.length() - 4, grfName.length()).equalsIgnoreCase("fst2"))) {
                                        // if the extension is nor GRF neither
                                        // FST2
                                        JOptionPane.showMessageDialog(null, "Invalid graph name extension !", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                }
                            }
                            Txt2TfstCommand txtCmd = new Txt2TfstCommand().text(Config.getCurrentSnt()).alphabet().clean(true);
                            if (Config.isKorean()) {
                                txtCmd=txtCmd.korean();
                            }
                            if (norm != null) {
                                txtCmd = txtCmd.fst2(norm);
                            }
                            commands.addCommand(txtCmd);
                        }
                        UnitexFrame.getFrameManager().closeTextFrame();
                        Text.removeSntFiles();
                        new ProcessInfoFrame(commands, true, new PreprocessDo(sntFile, taggedText));
                    }
                });
            }
        };
        JButton OK = new JButton(goAction);
        Action cancelButIndexAction = new AbstractAction("Cancel but tokenize text") {
            public void actionPerformed(ActionEvent arg0) {
                // if the user has clicked on CANCEL but tokenize, we must
                // tokenize anyway
                setVisible(false);
                // post pone code
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        File dir = Config.getCurrentSntDir();
                        if (!dir.exists()) {
                            // if the directory toto_snt does not exist, we
                            // create it
                            dir.mkdir();
                        }
                        MultiCommands commands = new MultiCommands();
                        // NORMALIZING TEXT...
                        NormalizeCommand normalizeCmd = new NormalizeCommand().textWithDefaultNormalization(originalTextFile);
                        commands.addCommand(normalizeCmd);
                        // TOKENIZING...
                        TokenizeCommand tokenizeCmd = new TokenizeCommand().text(Config.getCurrentSnt()).alphabet();
                        if (Config.getCurrentLanguage().equals("Thai") || Config.getCurrentLanguage().equals("Chinese")) {
                            tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
                        }
                        commands.addCommand(tokenizeCmd);
                        UnitexFrame.getFrameManager().closeTextFrame();
                        Text.removeSntFiles();
                        new ProcessInfoFrame(commands, true, new PreprocessDo(sntFile, taggedText));
                    }
                });
            }
        };
        JButton CANCELbutIndex = new JButton(cancelButIndexAction);
        Action cancelAction = new AbstractAction("Cancel and close text") {
            public void actionPerformed(ActionEvent arg0) {
                // if the user has clicked on CANCEL, we do nothing
                setVisible(false);
                UnitexFrame.getFrameManager().closeTextFrame();
            }
        };
        JButton CANCEL = new JButton(cancelAction);
        buttons.add(OK);
        buttons.add(CANCELbutIndex);
        buttons.add(CANCEL);
        return buttons;
    }

    static ArrayList<File> getDefaultDicList() {
        return getDefaultDicList(Config.getCurrentLanguage());
    }

    public static ArrayList<File> getDefaultDicList(String language) {

        ArrayList<File> res = new ArrayList<File>();
        File userLanguageDir = new File(Config.getUserDir(), language);
        File name2 = new File(userLanguageDir, "user_dic.def");
        try {
            BufferedReader br = new BufferedReader(new FileReader(name2));
            String s;
            while ((s = br.readLine()) != null) {
                res.add(new File(new File(userLanguageDir, "Dela"), s));
            }
            br.close();
        } catch (FileNotFoundException ee) {
            // nothing to do
        } catch (IOException e) {
            e.printStackTrace();
        }
        name2 = new File(userLanguageDir, "system_dic.def");
        try {
            BufferedReader br = new BufferedReader(new FileReader(name2));
            String s;
            File systemDelaDir = new File(new File(Config.getUnitexDir(), language), "Dela");
            while ((s = br.readLine()) != null) {
                res.add(new File(systemDelaDir, s));
            }
            br.close();
        } catch (FileNotFoundException ee) {
            // nothing to do
        }

        catch (IOException e) {
            // e.printStackTrace();
        }
        return res;
    }

    class PreprocessDo implements ToDo {
        File SNT;
        boolean b;

        public PreprocessDo(File s, boolean taggedText) {
            SNT = s;
            b = taggedText;
        }

        public void toDo() {
            Text.loadCorpus(SNT, b);
        }
    }

}