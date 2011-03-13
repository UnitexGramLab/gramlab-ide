/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.exceptions.InvalidPolyLexArgumentException;
import fr.umlv.unitex.listeners.LanguageListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.DicoCommand;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Fst2TxtCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MessageCommand;
import fr.umlv.unitex.process.commands.MkdirCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.NormalizeCommand;
import fr.umlv.unitex.process.commands.PolyLexCommand;
import fr.umlv.unitex.process.commands.SortTxtCommand;
import fr.umlv.unitex.process.commands.TokenizeCommand;
import fr.umlv.unitex.process.commands.Txt2TfstCommand;
import fr.umlv.unitex.text.Text;

/**
 * This class describes a dialog box that allows the user to parameter the
 * preprocessing of a text.
 *
 * @author Sébastien Paumier
 */
public class PreprocessDialog extends JDialog {

    private final JCheckBox sentenceCheck = new JCheckBox("Apply graph in MERGE mode:", true);
    private final JCheckBox replaceCheck = new JCheckBox("Apply graph in REPLACE mode:", true);
    private final JTextField sentenceName = new JTextField();
    private final JTextField replaceName = new JTextField();
    private final JCheckBox applyDicCheck = new JCheckBox("Apply All default Dictionaries", true);
    private final JCheckBox analyseUnknownWordsCheck = new JCheckBox("Analyse unknown words as free compound words (this option", true);
    private final JLabel analyseUnknownWordsLabel = new JLabel("     is available only for Dutch, German, Norwegian & Russian)");
    private final JCheckBox textFst2Check = new JCheckBox("Construct Text Automaton");
    private File originalTextFile;
    private File sntFile;
    private final JPanel preprocessingParent;
    private JPanel preprocessingCurrent;
    private JPanel preprocessingTaggedText;
    private JPanel preprocessingUntaggedText;
    private boolean taggedText = false;


    /**
     * Creates and shows a new <code>PreprocessFrame</code>
     */
    PreprocessDialog() {
        super(UnitexFrame.mainFrame, "Preprocessing & Lexical parsing", true);
        setContentPane(preprocessingParent = constructPanel());
        refreshOnLanguageChange();
        Config.addLanguageListener(new LanguageListener() {
			public void languageChanged() {
				refreshOnLanguageChange();
			}
		});
        pack();
        setResizable(false);
        setLocationRelativeTo(UnitexFrame.mainFrame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }


    void refreshOnLanguageChange() {
        if (Config.getCurrentLanguage().equals("Dutch")
                || Config.getCurrentLanguage().equals("German")
                || Config.getCurrentLanguage().equals("Norwegian (Bokmal)")
                || Config.getCurrentLanguage().equals("Norwegian (Nynorsk)")
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
        this.originalTextFile = originalTextFile;
        this.sntFile = sntFile;
        this.taggedText = taggedText;
        preprocessingParent.remove(preprocessingCurrent);
        if (taggedText) {
            preprocessingCurrent = preprocessingTaggedText;
        } else {
            preprocessingCurrent = preprocessingUntaggedText;
        }
        preprocessingParent.add(preprocessingCurrent, BorderLayout.NORTH);
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
        sentenceCheck.setMnemonic(KeyEvent.VK_M);
        replaceCheck.setMnemonic(KeyEvent.VK_R);
        preprocessingUntaggedText.add(constructGenericPanel(sentenceCheck, sentenceName, true));
        preprocessingUntaggedText.add(constructGenericPanel(replaceCheck, replaceName, false));
        return preprocessingCurrent = preprocessingUntaggedText;
    }

    private JPanel constructGenericPanel(final JCheckBox checkBox, final JTextField textField, final boolean mnemonicToken) {
        final JPanel panel = new JPanel(new BorderLayout());
        checkBox.setPreferredSize(new Dimension(190, checkBox.getPreferredSize().height));
        panel.add(checkBox, BorderLayout.WEST);
        textField.setPreferredSize(new Dimension(150, textField.getPreferredSize().height));
        panel.add(textField, BorderLayout.CENTER);
        final Action setAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = Config.getReplaceDialogBox();
                final int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on cancel
                    return;
                }
                textField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        };
        final JButton setReplace = new JButton(setAction);
        if (mnemonicToken) {
            setReplace.setMnemonic(KeyEvent.VK_E);
        } else {
            setReplace.setMnemonic(KeyEvent.VK_S);
        }
        panel.add(setReplace, BorderLayout.EAST);
        return panel;
    }

    private JPanel constructTokenizingPanel() {
        final JPanel tokenizingPanel = new JPanel(new GridLayout(2, 1));
        tokenizingPanel.setBorder(new TitledBorder("Tokenizing"));
        tokenizingPanel.add(new JLabel("The text is automatically tokenized. This operation is language-dependant,"));
        tokenizingPanel.add(new JLabel("so that Unitex can handle languages with special spacing rules."));
        return tokenizingPanel;
    }

    private JPanel constructLexicalParsingPanel() {
        final JPanel lexicalParsing = new JPanel(new GridLayout(4, 1));
        lexicalParsing.setBorder(new TitledBorder("Lexical Parsing"));
        applyDicCheck.setMnemonic(KeyEvent.VK_D);
        lexicalParsing.add(applyDicCheck);
        analyseUnknownWordsCheck.setMnemonic(KeyEvent.VK_W);
        lexicalParsing.add(analyseUnknownWordsCheck);
        lexicalParsing.add(analyseUnknownWordsLabel);
        textFst2Check.setMnemonic(KeyEvent.VK_A);
        lexicalParsing.add(textFst2Check);
        return lexicalParsing;
    }

    private JPanel constructButtonsPanel() {
        final JPanel buttons = new JPanel(new GridLayout(3, 1));
        buttons.setBorder(new EmptyBorder(8, 8, 2, 2));
        final Action goAction = new AbstractAction("GO!") {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                preprocess();
            }
        };
        final JButton goButton = new JButton(goAction);
        goButton.setMnemonic(KeyEvent.VK_G);

        final Action cancelButIndexAction = new AbstractAction("Cancel but tokenize text") {
            public void actionPerformed(ActionEvent e) {
                // if the user has clicked on cancel but tokenize, we must
                // tokenize anyway
                setVisible(false);
                tokenize();
            }
        };
        final JButton cancelButIndex = new JButton(cancelButIndexAction);
        cancelButIndex.setMnemonic(KeyEvent.VK_T);

        final Action cancelAction = new AbstractAction("Cancel and close text") {
            public void actionPerformed(ActionEvent e) {
                // if the user has clicked on cancel, we do nothing
                setVisible(false);
                UnitexFrame.getFrameManager().closeTextFrame();
            }
        };
        final JButton cancel = new JButton(cancelAction);
        cancel.setMnemonic(KeyEvent.VK_L);

        buttons.add(goButton);
        buttons.add(cancelButIndex);
        buttons.add(cancel);
        return buttons;
    }

    private MultiCommands normalizingText(final MultiCommands commands) {
        NormalizeCommand normalizeCmd = new NormalizeCommand().textWithDefaultNormalization(originalTextFile);
        commands.addCommand(normalizeCmd);
        return commands;
    }

    void tokenize() {
        MultiCommands commands = new MultiCommands();
        File dir = Config.getCurrentSntDir();
        if (!dir.exists()) {
            // if the directory toto_snt does not exist, we
            // create it
            MkdirCommand mkdir = new MkdirCommand().name(dir);
            commands.addCommand(mkdir);
        }
        commands = normalizingText(commands);
        // TOKENIZING...
        TokenizeCommand tokenizeCmd = new TokenizeCommand().text(Config.getCurrentSnt())
                .alphabet(Config.getAlphabet());
        if (Config.getCurrentLanguage().equals("Thai") || Config.getCurrentLanguage().equals("Chinese")) {
            tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
        }
        commands.addCommand(tokenizeCmd);
        UnitexFrame.getFrameManager().closeTextFrame();
        Text.removeSntFiles();
        Launcher.exec(commands, true, new AfterPreprocessDo(sntFile, taggedText));
    }

    private MultiCommands applyDefaultDictionaries(final MultiCommands commands) {
        DicoCommand dicoCmd;
        dicoCmd = new DicoCommand().snt(Config.getCurrentSnt())
                .alphabet(Config.getAlphabet())
                .morphologicalDic(Preferences.morphologicalDic());
        if (Config.isKorean()) {
            dicoCmd = dicoCmd.korean();
        }
        if (Config.isArabic()) {
            dicoCmd = dicoCmd.arabic(new File(Config.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
        }
        if (Config.isSemiticLanguage()) {
        	dicoCmd=dicoCmd.semitic();
        }
        ArrayList<File> param = Config.getDefaultDicList();
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
        else if (lang.equals("Norwegian (Bokmal)"))
            dic = new File(dic, "Dela-sample.bin");
        else if (lang.equals("Norwegian (Nynorsk)"))
            dic = new File(dic, "Dela-sample.bin");
        else if (lang.equals("Russian"))
            dic = new File(dic, "CISLEXru_igrok.bin");
        if (analyseUnknownWordsCheck.isSelected()) {
            PolyLexCommand polyLexCmd;
            try {
                polyLexCmd = new PolyLexCommand().language(lang)
                        .alphabet(Config.getAlphabet()).bin(dic)
                        .wordList(new File(Config.getCurrentSntDir(), "err"))
                        .output(new File(Config.getCurrentSntDir(), "dlf"))
                        .info(new File(Config.getCurrentSntDir(), "decomp.txt"));
                commands.addCommand(polyLexCmd);
            } catch (InvalidPolyLexArgumentException e) {
                e.printStackTrace();
            }
        }
        // SORTING TEXT DICTIONARIES
        File alph = new File(Config
                .getUserCurrentLanguageDir(), "Alphabet_sort.txt");
        if (dicoCmd != null) {
            // sorting DLF
            SortTxtCommand sortCmd = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "dlf")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "dlf.n"));
            if (Config.getCurrentLanguage().equals("Thai")) {
                sortCmd = sortCmd.thai();
            } else {
                sortCmd = sortCmd.sortAlphabet(alph);
            }
            commands.addCommand(sortCmd);
            // sorting DLC
            SortTxtCommand sortCmd2 = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "dlc")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "dlc.n"));
            if (Config.getCurrentLanguage().equals("Thai")) {
                sortCmd2 = sortCmd2.thai();
            } else {
                sortCmd2 = sortCmd2.sortAlphabet(alph);
            }
            commands.addCommand(sortCmd2);
            // sorting ERR
            SortTxtCommand sortCmd3 = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "err")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "err.n"));
            if (Config.getCurrentLanguage().equals("Thai")) {
                sortCmd3 = sortCmd3.thai();
            } else {
                sortCmd3 = sortCmd3.sortAlphabet(alph);
            }
            commands.addCommand(sortCmd3);
            // sorting TAGS_ERR
            SortTxtCommand sortCmd4 = new SortTxtCommand().file(new File(Config.getCurrentSntDir(), "tags_err")).saveNumberOfLines(new File(Config.getCurrentSntDir(), "tags_err.n"));
            if (Config.getCurrentLanguage().equals("Thai")) {
                sortCmd4 = sortCmd4.thai();
            } else {
                sortCmd4 = sortCmd4.sortAlphabet(alph);
            }
            commands.addCommand(sortCmd4);
        }

        return commands;
    }

    private void showMsgDialog() {
        // if the extension is nor GRF neither
        // FST2
        JOptionPane.showMessageDialog(null, "Invalid graph name extension !", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private MultiCommands constructTextAutomaton(final MultiCommands commands) {
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
                    showMsgDialog();
                }
            }
        }
        Txt2TfstCommand txtCmd = new Txt2TfstCommand().text(Config.getCurrentSnt())
                .alphabet(Config.getAlphabet()).clean(true);
        if (Config.isKorean()) {
            txtCmd = txtCmd.korean();
        }
        if (norm != null) {
            txtCmd = txtCmd.fst2(norm);
        }
        commands.addCommand(txtCmd);

        return commands;
    }

    private MultiCommands replaceGraph(final MultiCommands commands) {
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
                    showMsgDialog();
                }
                fst2 = new File(grfName);
            }
            Fst2TxtCommand cmd = new Fst2TxtCommand().text(Config.getCurrentSnt()).fst2(fst2)
                    .alphabet(Config.getAlphabet()).mode(false);
            if (Config.isCharByCharLanguage())
                cmd = cmd.charByChar(Config.morphologicalUseOfSpaceAllowed());
            commands.addCommand(cmd);
        }
        return commands;
    }

    private MultiCommands sentenceGraph(final MultiCommands commands) {
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
                    showMsgDialog();
                }
                fst2 = new File(grfName);
            }
            Fst2TxtCommand cmd = new Fst2TxtCommand().text(Config.getCurrentSnt()).fst2(fst2)
                    .alphabet(Config.getAlphabet()).mode(true);
            if (Config.isCharByCharLanguage())
                cmd = cmd.charByChar(Config.morphologicalUseOfSpaceAllowed());
            commands.addCommand(cmd);
        }
        return commands;
    }

    private MultiCommands createSndDir(final MultiCommands commands) {
        final File dir = Config.getCurrentSntDir();
        if (!dir.exists()) {
            MkdirCommand mkdir = new MkdirCommand().name(dir);
            commands.addCommand(mkdir);
        }
        return commands;
    }

    private MultiCommands tokenization(final MultiCommands commands) {
        TokenizeCommand tokenizeCmd = new TokenizeCommand().text(Config.getCurrentSnt())
                .alphabet(Config.getAlphabet());
        if (Config.isCharByCharLanguage()) {
            tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
        }
        commands.addCommand(tokenizeCmd);
        return commands;
    }

    void preprocess() {
        // build & execute a command chain
        MultiCommands commands = new MultiCommands();
        commands = normalizingText(commands);
        commands = createSndDir(commands);
        if (sentenceCheck.isSelected()) {
            commands = sentenceGraph(commands);
        }
        if (replaceCheck.isSelected()) {
            commands = replaceGraph(commands);
        }
        commands = tokenization(commands);
        if (applyDicCheck.isSelected()) {
            commands = applyDefaultDictionaries(commands);
        }

        if (textFst2Check.isSelected()) {
            commands = constructTextAutomaton(commands);
        }

        UnitexFrame.getFrameManager().closeTextFrame();
        Text.removeSntFiles();
        Launcher.exec(commands, true, new AfterPreprocessDo(sntFile, taggedText));
    }

}