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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.exceptions.NotAUnicodeLittleEndianFileException;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.Tfst2GrfCommand;
import fr.umlv.unitex.tfst.TagFilter;
import fr.umlv.unitex.tfst.TfstTableModel;
import fr.umlv.unitex.tfst.TokenTags;
import fr.umlv.unitex.tfst.TokensInfo;

public class ExportTextAsPOSListDialog extends JDialog {
    private File output;
    boolean canceled = false;
    private final JProgressBar progress = new JProgressBar();
    private TagFilter filter;

    /**
     * Creates a new font dialog box.
     *
     * @param in indicates if we select an input or an output font for graphs.
     */
    public ExportTextAsPOSListDialog(File output, TagFilter filter) {
        super(UnitexFrame.mainFrame, "Export text as POS list", true);
        configure(output, filter);
        setContentPane(constructPanel());
        progress.setPreferredSize(new Dimension(300, 30));
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                canceled = true;
            }
        });
        setLocationRelativeTo(UnitexFrame.mainFrame);
    }

    private JPanel constructPanel() {
        JPanel p = new JPanel();
        p.add(progress);
        return p;
    }

    void configure(File o, TagFilter f) {
        if (o == null) {
            throw new IllegalArgumentException(
                    "Cannot configure a null output file");
        }
        this.output = o;
        this.filter = f;
    }

    public void launch() {
        canceled = false;
        progress.setMinimum(0);
        final int sentenceCount = UnitexFrame.getFrameManager()
                .getTextAutomatonFrame().getSentenceCount();
        progress.setMaximum(sentenceCount);
        progress.setValue(0);
        progress.setStringPainted(true);
        final ExportTextAsPOSListDialog dialog = this;
        new Thread(new Runnable() {
            public void run() {
                TokensInfo.save();
                File tfst = new File(Config.getCurrentSntDir(), "text.tfst");
                File tmpGrf = new File(Config.getCurrentSntDir(), "foo.grf");
                File sentenceText = new File(Config.getCurrentSntDir(),
                        "foo.txt");
                File sentenceTok = new File(Config.getCurrentSntDir(),
                        "foo.tok");
                TfstTableModel model = new TfstTableModel(filter, false);
                try {
                    OutputStreamWriter writer=Config.getEncoding().getOutputStreamWriter(output);
                    for (int i = 1; i <= sentenceCount; i++) {
                        if (canceled) {
                            break;
                        }
                        Tfst2GrfCommand cmd = new Tfst2GrfCommand().automaton(
                                tfst).sentence(i).output("foo");
                        Launcher.execWithoutTracing(cmd);
                        String text = readSentenceText(sentenceText);
                        try {
                            TokensInfo.loadTokensInfo(sentenceTok, text);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        GraphIO g = GraphIO.loadGraph(tmpGrf, true, true);
                        model.init(g.boxes);
                        if (model.getRowCount() == 0) {
                            /* If the sentence automaton has been emptied, we
                             * generate the token list, excluding spaces */
                            for (int j = 0; j < TokensInfo.getTokenCount(); j++) {
                                String s = TokensInfo.getTokenAsString(j);
                                UnicodeIO.writeString(writer, s);
                            }
                        } else
                            for (int j = 0; j < model.getRowCount(); j++) {
                                TokenTags t = model.getTokenTags(j);
                                UnicodeIO.writeString(writer, t.toString() + " ");
                            }
                        /* And we add a sentence delimiter */
                        UnicodeIO.writeString(writer, "{S}\n");
                        final int z = i;
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                progress.setValue(z);
                                progress.setString(z + "/" + sentenceCount);
                            }
                        });
                    }
                    writer.close();
                } catch (IOException e1) {
                    /* nop */
                } finally {
                    TokensInfo.restore();
                    tmpGrf.delete();
                    sentenceText.delete();
                    sentenceTok.delete();
                    try {
                        /* We have to wait a little bit because if the text is too
                         * small, we may have finished before setVisible(true) is
                         * invoked. In that case, the JDialog made visible could never
                         * become invisible again, blocking the user */
                        Thread.sleep(100);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        EventQueue.invokeAndWait(new Runnable() {
                            public void run() {
                                dialog.setVisible(false);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    String readSentenceText(File f) {
        String s = "";
        try {
            InputStreamReader reader=Encoding.getInputStreamReader(f); 
            if (reader==null) return null;
            s = UnicodeIO.readLine(reader);
            if (s == null || s.equals("")) {
                return "";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
