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

package fr.umlv.unitex;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * @author Alexander Orlov <alexander.orlov@loxal.net>
 */

/**
 * Implements optional execution capabilities that improve performance on multicore machines.
 */
public class MulticoreCapability implements Runnable {
    private static File originalTextFile;
    private static File sntFile;
    private static String sentenceName;
    private static String replaceName;
    private static boolean applyDicCheck;
    private static boolean analyseUnknownWordsCheck;
    private static boolean textFst2Check;
    private static boolean taggedText;
    private static boolean sentenceCheck;
    private static boolean replaceCheck;

    public File getOriginalTextFile() {
        return originalTextFile;
    }

    public void setOriginalTextFile(File originalTextFile) {
        this.originalTextFile = originalTextFile;
    }

    public File getSntFile() {
        return sntFile;
    }

    public void setSntFile(File sntFile) {
        this.sntFile = sntFile;
    }

    public String getSentenceName() {
        return sentenceName;
    }

    public void setSentenceName(String sentenceName) {
        this.sentenceName = sentenceName;
    }

    public String getReplaceName() {
        return replaceName;
    }

    public void setReplaceName(String replaceName) {
        this.replaceName = replaceName;
    }

    public boolean isApplyDicCheck() {
        return applyDicCheck;
    }

    public void setApplyDicCheck(boolean applyDicCheck) {
        this.applyDicCheck = applyDicCheck;
    }

    public boolean isAnalyseUnknownWordsCheck() {
        return analyseUnknownWordsCheck;
    }

    public void setAnalyseUnknownWordsCheck(boolean analyseUnknownWordsCheck) {
        this.analyseUnknownWordsCheck = analyseUnknownWordsCheck;
    }

    public boolean isTextFst2Check() {
        return textFst2Check;
    }

    public void setTextFst2Check(boolean textFst2Check) {
        this.textFst2Check = textFst2Check;
    }

    public boolean isTaggedText() {
        return taggedText;
    }

    public void setTaggedText(boolean taggedText) {
        this.taggedText = taggedText;
    }

    public final static JCheckBox multicoreMode = new JCheckBox("Multicore Mode");
    final static File filePartitionFolder = new File(Config.getCurrentCorpusDir() + "/filePartitions");

    public static JPanel constructExecutionPanel() {
        final JPanel executionPanel = new JPanel(new BorderLayout());
        executionPanel.setBorder(new TitledBorder("Execution"));
        multicoreMode.setMnemonic(KeyEvent.VK_C);

        if (Runtime.getRuntime().availableProcessors() > 1) { // check if the multicore mode pays off
            multicoreMode.setEnabled(true);
            multicoreMode.setSelected(true);
        } else {
            multicoreMode.setEnabled(false);
        }

        executionPanel.add(multicoreMode);
        return executionPanel;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

