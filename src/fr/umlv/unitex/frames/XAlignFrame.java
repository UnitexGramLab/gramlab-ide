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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.listeners.AlignmentListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.*;
import fr.umlv.unitex.xalign.*;
import fr.umlv.unitex.xalign.DisplayMode;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Properties;

public class XAlignFrame extends JInternalFrame {

    Font sourceFont;
    Font targetFont;
    File alignementFile;
    XMLTextModel text1, text2;
    XAlignModel model;
    ConcordanceModel concordModel1, concordModel2;

    XAlignFrame(final File f1, final File f2, final File align) throws IOException {
        super("XAlign", true, true);
        {
            JTextPane foo = new JTextPane();
            sourceFont = foo.getFont();
            targetFont = foo.getFont();
        }
        alignementFile = align;
        tryToFindFonts(f1, f2);
        setSize(800, 600);
        /* First text */
        MappedByteBuffer buffer1 = XMLTextLoader.buildMappedByteBuffer(f1);
        MappedByteBuffer buffer2 = XMLTextLoader.buildMappedByteBuffer(f2);
        text1 = new XMLTextModelImpl(buffer1);
        text2 = new XMLTextModelImpl(buffer2);
        XMLTextLoader loader1 = new XMLTextLoader(text1, buffer1);
        loader1.load();
        XMLTextLoader loader2 = new XMLTextLoader(text2, buffer2);
        loader2.load();
        model = new XAlignModelImpl(text1, text2);
        concordModel1 = new ConcordanceModelImpl(text1, true, model);
        concordModel2 = new ConcordanceModelImpl(text2, false, model);
        model.load(align);
        model.addAlignmentListener(new AlignmentListener() {
            public void alignmentChanged(AlignmentEvent e) {
                if (AlignmentEvent.MANUAL_EDIT.equals(e)) {
                    setTitle(((alignementFile != null) ? (alignementFile.getAbsolutePath() + " (") : "(alignment ") + "modified)");
                } else if (AlignmentEvent.SAVING.equals(e)) {
                    setTitle((alignementFile != null) ? alignementFile.getAbsolutePath() : "XAlign");
                }
            }
        });

        final JInternalFrame frame = this;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (model.isModified()) {
                    Object[] options_on_exit = {"Save", "Don't save"};
                    Object[] normal_options = {"Save", "Don't save", "Cancel"};
                    int n;
                    if (UnitexFrame.closing) {
                        n = JOptionPane
                                .showOptionDialog(
                                        frame,
                                        "Alignment has been modified. Do you want to save it ?",
                                        "", JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null,
                                        options_on_exit, options_on_exit[0]);
                    } else {
                        n = JOptionPane
                                .showOptionDialog(
                                        frame,
                                        "Alignment has been modified. Do you want to save it ?",
                                        "", JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null,
                                        normal_options, normal_options[0]);
                    }
                    if (n == JOptionPane.CLOSED_OPTION)
                        return;
                    if (n == 0) {
                        saveAlignment(model);
                    }
                    if (n != 2) {
                        text1.reset();
                        text2.reset();
                        model.reset();
                        frame.dispose();
                        return;
                    }
                    return;
                }
                text1.reset();
                text2.reset();
                model.reset();
                frame.dispose();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                UnitexFrame.getFrameManager().closeXAlignLocateFrame();
            }
        });
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new XAlignPane(concordModel1, concordModel2, model, sourceFont, targetFont), BorderLayout.CENTER);
        JPanel radioPanel1 = createRadioPanel(concordModel1, concordModel2, true);
        JPanel radioPanel2 = createRadioPanel(concordModel2, concordModel1, false);
        JButton clearButton = new JButton("Clear alignment");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Are you sure that you want to clear the current alignement ?", "Clear alignment ?", JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
                model.clear();
            }
        });
        JButton alignButton = new JButton("Align");
        alignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAlignment(model);
                if (alignementFile == null || model.isModified()) {
                    /* If the user hasn't saved the alignment */
                    return;
                }
                File alignmentProperties = Config.getAlignmentProperties();
                XAlignCommand cmd = new XAlignCommand();
                cmd = cmd.source(f1).target(f2).properties(alignmentProperties)
                        .alignment(alignementFile);
                Launcher.exec(cmd, true, new XAlignDo(model, alignementFile));
            }
        });
        JButton saveButton = new JButton("Save alignment");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAlignment(model);
            }
        });
        JButton saveAsButton = new JButton("Save alignment as...");
        saveAsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAlignmentAs(model);
            }
        });
        JPanel downPanel = new JPanel(new BorderLayout());
        downPanel.add(radioPanel1, BorderLayout.WEST);
        downPanel.add(radioPanel2, BorderLayout.EAST);
        JButton locate1 = createLocateButton(f1, concordModel1);
        JButton locate2 = createLocateButton(f2, concordModel2);
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonPanel.add(locate1);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(clearButton);
        buttonPanel.add(alignButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(saveAsButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(locate2);
        downPanel.add(buttonPanel, BorderLayout.SOUTH);
        downPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getContentPane().add(downPanel, BorderLayout.SOUTH);
    }

    private JButton createLocateButton(final File file, final ConcordanceModel concordModel) {
        JButton button = new JButton("Locate...");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launchLocate(file, concordModel);
            }
        });
        return button;
    }

    protected void launchLocate(File file, final ConcordanceModel concordModel) {
        String xmlName = file.getAbsolutePath();
        String targetName;
        if (!xmlName.endsWith(".xml")) {
            targetName = xmlName + "_xalign";
        } else {
            targetName = xmlName.substring(0, xmlName.lastIndexOf(".")) + "_xalign";
        }
        String txtName = targetName + ".txt";
        String sntName = targetName + ".snt";
        File txt = new File(txtName);
        final File snt = new File(sntName);
        File sntDir = new File(targetName + "_snt");
        File alphabet = tryToFindAlphabet(file);
        if (alphabet == null) {
            JOptionPane.showMessageDialog(null,
                    "Cannot determine the alphabet file to use\n" +
                            "in order to process your text. You should place\n" +
                            "your file within a language directory (e.g. English).", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String language = alphabet.getParentFile().getName();
        if (!snt.exists()) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Unitex needs a text version of your xml text in order to locate\n" +
                            "expression. Do you agree to build and preprocess\n\n" +
                            txtName + " ?", "", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            MultiCommands commands = new MultiCommands();
            TEI2TxtCommand tei2txt = new TEI2TxtCommand().input(file).output(txt);
            commands.addCommand(tei2txt);
            NormalizeCommand normalize = new NormalizeCommand().text(txt);
            commands.addCommand(normalize);
            MkdirCommand mkdir = new MkdirCommand().name(sntDir);
            commands.addCommand(mkdir);
            TokenizeCommand tokenize = new TokenizeCommand().text(snt)
                    .alphabet(alphabet);
            commands.addCommand(tokenize);
            DicoCommand dico = new DicoCommand()
                    .snt(snt).alphabet(alphabet)
                    .morphologicalDic(Config.morphologicalDic(language));
            if (Config.isArabic()) {
                dico = dico.arabic(new File(Config.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
            }
            if (Config.isSemiticLanguage()) {
            	dico=dico.semitic();
            }
            ArrayList<File> param = Config.getDefaultDicList(language);
            if (param != null && param.size() > 0) {
                dico = dico.dictionaryList(param);
                commands.addCommand(dico);
            } else {
                dico = null;
            }
            ToDo toDo = new ToDo() {
                public void toDo() {
                    UnitexFrame.getFrameManager().newXAlignLocateFrame(language, snt, concordModel);
                }
            };
            Launcher.exec(commands, true, toDo, true);
            return;
        }
        UnitexFrame.getFrameManager().newXAlignLocateFrame(language, snt, concordModel);
    }

    protected void saveAlignment(XAlignModel model1) {
        if (alignementFile != null) {
            saveAlignment(alignementFile, model1);
        } else {
            saveAlignmentAs(model1);
        }
    }

    void saveAlignmentAs(XAlignModel model1) {
        JFileChooser chooser = XAlignConfigFrame.alignmentChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        alignementFile = chooser.getSelectedFile();
        saveAlignment(alignementFile, model1);
    }

    private void saveAlignment(File alignementFile1, XAlignModel model1) {
        try {
            model1.dumpAlignments(alignementFile1);
            alignementFile = alignementFile1;
            setTitle(alignementFile1.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryToFindFonts(File f1, File f2) {
        Font f = tryToFindFont(f1);
        if (f != null) {
            sourceFont = f;
        }
        f = tryToFindFont(f2);
        if (f != null) {
            targetFont = f;
        }
    }

    /**
     * This method tries to determine the language of the given file, on
     * the basis of its path:
     * <p/>
     * ..../Unitex/Thai/...../foo.xml => Thai
     * ..../my unitex/French/...../foo.xml => French
     */
    private Font tryToFindFont(File f) {
        File languageDir = Config.getLanguageDirForFile(f);
        if (languageDir == null) return null;
        /* Now, we will look into the config file which is the preferred font
           * for this language */
        File config = new File(f, "Config");
        if (!config.exists()) {
            return null;
        }
        Properties prop = Preferences.loadProperties(config, null);
        String s = prop.getProperty("TEXT FONT SIZE");
        if (s == null) return null;
        int fontSize = Integer.parseInt(s);
        s = prop.getProperty("TEXT FONT NAME");
        if (s == null) return null;
        Font font = new Font(s, Font.PLAIN, (int) (fontSize / 0.72));
        return font;
    }


    /**
     * This method tries to determine the language of the given file, on
     * the basis of its path:
     * <p/>
     * ..../Unitex/Thai/...../foo.xml => Thai
     * ..../my unitex/French/...../foo.xml => French
     */
    public static File tryToFindAlphabet(File f) {
        File languageDir = Config.getLanguageDirForFile(f);
        if (languageDir == null) {
        	return null;
        }
        File alphabet = new File(languageDir, "Alphabet.txt");
        if (!alphabet.exists()) {
            return null;
        }
        return alphabet;
    }


    private JPanel createRadioPanel(final ConcordanceModel model1, final ConcordanceModel model2, boolean left) {
        JPanel p = new JPanel(new GridLayout(4, 1));
        String[] captions = {"All sentences/Plain text", "Matched sentences", "All sentences/HTML", left ? "Aligned with target concordance" : "Aligned with source concordance"};
        DisplayMode[] modes = {DisplayMode.TEXT, DisplayMode.MATCHES, DisplayMode.BOTH, DisplayMode.ALIGNED};
        final ButtonGroup g = new ButtonGroup();
        for (int i = 0; i < captions.length; i++) {
            final JRadioButton button = new JRadioButton(captions[i], i == 0);
            if (!left) {
                button.setHorizontalTextPosition(SwingConstants.LEFT);
                button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }
            final DisplayMode mode = modes[i];
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (button.isSelected()) {
                        model1.setMode(mode, model2);
                    }
                }
            });
            g.add(button);
            p.add(button);
        }
        if (!left) {
            p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        return p;
    }


    class XAlignDo implements ToDo {

        final XAlignModel model1;
        final File f;

        XAlignDo(XAlignModel model, File f) {
            this.model1 = model;
            this.f = f;
        }

        public void toDo() {
            try {
                model1.load(f);
            } catch (IOException e) {
                UnitexFrame.getFrameManager().closeXAlignFrame();
			}
		}
	}
}
