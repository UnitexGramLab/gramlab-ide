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

package fr.umlv.unitex.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Util;
import fr.umlv.unitex.frames.FileEditionTextFrame;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.io.Encoding;

/**
 * This class provides methods for loading corpora
 */
public class FileManager {

    private Timer currentTimer = null;
    private FileEditionTextFrame fileEditionTextFrame;
    private boolean FILE_TOO_LARGE = false;

    /**
     * loads file and checks encoding type
     *
     * @param file the text file
     */
    public void loadFile(File file) {
        if (file == null) {
            System.err.println("Internal error in FileManager.loadFile");
            return;
        }
        if (!file.exists()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot find " + file.getAbsolutePath(),
                    "Error",
                    ImageObserver.ERROR);
            return;
        }
        if (!file.canRead()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot read " + file.getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (file.length() <= 2) {
            JOptionPane.showMessageDialog(
                    null,
                    file.getAbsolutePath() + " is empty",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "This is not necessarily the text being processed by Unitex", "Warning", JOptionPane.WARNING_MESSAGE);
        load(file);
    }


    /**
     * loads a file from
     *
     * @param file file path
     */
    private void load(File file) {
        fileEditionTextFrame = UnitexFrame.getFrameManager().newFileEditionTextFrame(file);
        EditionTextArea text = fileEditionTextFrame.getText();
        if (file.length() <= 2) {
            FILE_TOO_LARGE = true;
            text.setDocument(new PlainDocument());
            text.setText(Config.EMPTY_FILE_MESSAGE);
        } else if (file.length() < Preferences.MAX_TEXT_FILE_SIZE) {
            load(file, text);
            text.setCaretPosition(0);
            FILE_TOO_LARGE = false;
        } else {
            FILE_TOO_LARGE = true;
            text.setDocument(new PlainDocument());
            text.setText(Config.FILE_TOO_LARGE_MESSAGE);
        }
    }

    public void killTimer() {
        if (currentTimer != null) {
            currentTimer.stop();
            currentTimer = null;
        }
    }

    /**
     * saves a file
     *
     * @param absolutePath the absolute path of the file
     */
    public void save(String absolutePath) {

        try {
            FileEditionTextFrame fetf = UnitexFrame.getFrameManager().getSelectedFileEditionTextFrame();
            if (fetf == null) return;
            EditionTextArea t = fetf.getText();
            OutputStreamWriter osr=Config.getEncoding().getOutputStreamWriter(new File(absolutePath));
            String content = t.getText();
            int l = content.length();
            for (int i = 0; i < l; i++) {
                char c = content.charAt(i);
                osr.write(c);
            }
            osr.flush();
            osr.close();
            t.setUnmodified();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unable to save " + absolutePath,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "please, select a text edition frame",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "please, select a text edition frame",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }


    /**
     * Load an little endian Unicode texte file put it in a document
     *
     * @param file     the text to load
     * @param textArea aera where put the text
     */
    Document load(File file, final EditionTextArea textArea)
            throws  IllegalArgumentException {

        final StringContent content = new StringContent();
        final PlainDocument document = new PlainDocument(content);
        String fileContent=Encoding.getContent(file);
        textArea.setDocument(document);
        try {
			document.insertString(0,fileContent, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        return document;
    }

    /**
     * load an empty text
     */
    public void newFile() {
        UnitexFrame.getFrameManager().newFileEditionTextFrame(null);
    }


    /**
     * convert a file to an HTML file
     *
     * @param fileDestinationpath the file destination path
     */
    public void convertToHtml(String fileDestinationpath) {
        String path = Util.getFilePathWithoutFileName(fileDestinationpath) + "\\";
        String fileName = Util.getFileNameWithoutFilePath(fileDestinationpath);

        try {

            String charset = "UTF-8";
            // Lecture du fichier
            File file = new File(fileDestinationpath);
            File HtmlFile = new File(path + fileName + ".html");

            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            char[] tab = new char[(int) file.length()];
            isr.read(tab);
            isr.close();
            OutputStreamWriter osr =
                    new OutputStreamWriter(new FileOutputStream(HtmlFile), charset);

            osr.write("<html><body>");

            // offset de 2 pour enlever l'ent�te unicode
            osr.write(tab, 2, tab.length - 2);

            osr.write("</html></body>");

            osr.flush();
            osr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to save !!");
        }
    }

}
