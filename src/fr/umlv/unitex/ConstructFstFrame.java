/*
 * Unitex
 *
 * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import fr.umlv.unitex.process.*;

/**
 * This class describes the "Construct Text FST" frame that offers to the user
 * to build the text automaton.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConstructFstFrame extends JDialog {

  JCheckBox reconstrucao = new JCheckBox("Build clitic normalization grammar");

  private JLabel reconstrucaoLabel = new JLabel(
      "        (available only for Portuguese (Portugal))");

  JCheckBox normFst = new JCheckBox(
      "Apply the Normalization grammar (Norm.fst2)");

  JCheckBox cleanFst = new JCheckBox("Clean Text FST");
  JCheckBox morphFst = new JCheckBox(
      "Use morpheme structures: available for Korean");

  JCheckBox elagFst = new JCheckBox("Normalize according to Elag tagset.def");

  /**
   * Creates and shows a new <code>ConstructFstFrame</code>.
   *  
   */
  public ConstructFstFrame() {
    super(UnitexFrame.mainFrame, "Construct the Text FST", true);
    setContentPane(constructPanel());
    pack();
    setResizable(false);
    setLocationRelativeTo(UnitexFrame.mainFrame);
    this.setVisible(true);
  }

  private JPanel constructPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(constructNormalizationPanel(), BorderLayout.NORTH);
    panel.add(constructDicPanel(), BorderLayout.CENTER);
    panel.add(constructButtonsPanel(), BorderLayout.SOUTH);
    return panel;
  }

  private JPanel constructNormalizationPanel() {
    JPanel normalizationPanel = new JPanel(new GridLayout(5, 1));
    normalizationPanel.setBorder(new TitledBorder("Normalization"));
    boolean portuguese = Config.getCurrentLanguage().equals("Portuguese (Portugal)");
    reconstrucao.setEnabled(portuguese);
    reconstrucao.setSelected(portuguese);
    reconstrucaoLabel.setEnabled(portuguese);
    //		normFst.setSelected(true);
    cleanFst.setSelected(true);
    boolean morphemeCase = Config.isKorean();
    morphFst.setEnabled(morphemeCase);
    morphFst.setSelected(morphemeCase);
    elagFst.setSelected(false);
    if(!morphemeCase){
      normFst.setSelected(true);
    } else {
    	normFst.setSelected(false);
    }

    normalizationPanel.add(reconstrucao);
    normalizationPanel.add(reconstrucaoLabel);
    normalizationPanel.add(normFst);
    normalizationPanel.add(cleanFst);
    normalizationPanel.add(morphFst);
    normalizationPanel.add(elagFst);

    return normalizationPanel;
  }

  private JPanel constructDicPanel() {
    JPanel dicPanel = new JPanel(new GridLayout(3, 1));
    dicPanel.setBorder(new TitledBorder(
          "Use Following Dictionaries previously constructed:"));
    dicPanel.add(new JLabel("The program will construct the text FST"));
    dicPanel
      .add(new JLabel("according to the DLF and DLC files previously"));
    dicPanel.add(new JLabel("constructed for the current text."));
    return dicPanel;
  }

  private JPanel constructButtonsPanel() {
    JPanel buttons = new JPanel(new GridLayout(1, 2));
    buttons.setBorder(new EmptyBorder(8, 8, 2, 2));
    buttons.setLayout(new GridLayout(1, 2));

    Action okAction = new AbstractAction("Construct FST") {

      public void actionPerformed(ActionEvent arg0) {
        setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            File dir = Config.getCurrentSntDir();
            if (!dir.exists()) {
              // if the directory toto_snt does not exist, we
              // create
              // it
              dir.mkdir();
            }
            //File graph = new File(Config
            //		.getUserCurrentLanguageDir(), "Graphs");
            //graph = new File(graph, "Normalization");
            // we clean the text automaton files
            Config.deleteFileByName(new File(Config
                .getCurrentSntDir(), "sentence*.grf"));
            Config.deleteFileByName(new File(Config
                .getCurrentSntDir(), "cursentence.grf"));
            Config.deleteFileByName(new File(Config
                .getCurrentSntDir(), "cursentence.txt"));
            // we clean the ELAG files
            Config
          .deleteFileByName(new File(Config
              .getCurrentSntDir(),
              "currentelagsentence.grf"));
            Config
              .deleteFileByName(new File(Config
                    .getCurrentSntDir(),
                    "currentelagsentence.txt"));
            Config.deleteFileByName(new File(Config
                  .getCurrentSntDir(), "text-elag.fst2"));
            Config.deleteFileByName(new File(Config
                  .getCurrentSntDir(), "text-elag.fst2.bak"));

            File graphDir = new File(Config
                .getUserCurrentLanguageDir(), "Graphs");
            File normalizationDir = new File(graphDir,
                "Normalization");
            File delaDir = new File(Config
                .getUnitexCurrentLanguageDir(), "Dela");
            File vProSuf = new File(normalizationDir,
                "V-Pro-Suf.fst2");
            File normalizePronouns = new File(normalizationDir,
                "NormalizePronouns.fst2");
            File raizBin = new File(delaDir, "Raiz.bin");
            File raizInf = new File(delaDir, "Raiz.inf");
            File futuroCondicionalBin = new File(delaDir,
                "FuturoCondicional.bin");
            File futuroCondicionalInf = new File(delaDir,
                "FuturoCondicional.inf");

            MultiCommands commands = new MultiCommands();
            if (normFst.isSelected() && reconstrucao.isSelected()
                && vProSuf.exists()
                && normalizePronouns.exists()
                && raizBin.exists()
                && futuroCondicionalBin.exists()
                && raizInf.exists()
                && futuroCondicionalInf.exists()) {
              // if the user has choosen both to build the clitic
              // normalization grammar
              // and to apply this grammar, and if the necessary
              // files for the
              // Reconstrucao program exist, we launch the
              // construction of this grammar
              LocateCommand locateCmd = new LocateCommand().snt(
                  Config.getCurrentSnt()).fst2(vProSuf)
                .alphabet().longestMatches().mergeOutputs()
                .noLimit();
              commands.addCommand(locateCmd);
              ReconstrucaoCommand reconstrucaoCmd = new ReconstrucaoCommand()
                .alphabet()
                .ind(
                    new File(Config.getCurrentSntDir(),
                      "concord.ind"))
                .rootDic(raizBin)
                .dic(futuroCondicionalBin)
                .fst2(normalizePronouns)
                .nasalFst2(
                    new File(graphDir,
                      "NasalSuffixPronouns.fst2"))
                .output(
                    new File(normalizationDir,
                      "Norm.grf"));
              commands.addCommand(reconstrucaoCmd);
              Grf2Fst2Command grfCommand = new Grf2Fst2Command()
                .grf(new File(normalizationDir, "Norm.grf"))
                .tokenizationMode().library();
              commands.addCommand(grfCommand);
                }

            Txt2Fst2Command txtCmd = new Txt2Fst2Command().text(
                Config.getCurrentSnt()).alphabet().clean(
                cleanFst.isSelected());
            if (normFst.isSelected()) {
              txtCmd=txtCmd.fst2(
                  new File(normalizationDir, "Norm.fst2"));
            }
            commands.addCommand(txtCmd);


            /*
               if (elagFst.isSelected()) {

               TagsetNormFst2Command tagsetcmd = new TagsetNormFst2Command()
               .tagset(new File(Config.getCurrentElagDir(), "tagset.def"))
               .automaton(new File(Config.getCurrentSntDir(), "text.fst2"));

               commands.addCommand(tagsetcmd);
               }
               */

            TextAutomatonFrame.hideFrame();
            new ProcessInfoFrame(commands, false,
                new ConstructFstDo(elagFst.isSelected()), false);
          }
        });
        dispose();
      }
    };

    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed(ActionEvent arg0) {
        setVisible(false);
        dispose();
      }
    };

    JButton OK = new JButton(okAction);
    JButton CANCEL = new JButton(cancelAction);
    buttons.add(CANCEL);
    buttons.add(OK);
    return buttons;
  }

  class ConstructFstDo extends ToDoAbstract {

    boolean normalize;

    public ConstructFstDo(boolean norm) { normalize = norm; }

    public void toDo() {
      TextAutomatonFrame.showFrame();
      try {

        TextAutomatonFrame.getFrame().setIcon(false);
        TextAutomatonFrame.getFrame().setSelected(true);

        if (normalize) { TextAutomatonFrame.normalizeFst(true); }

      } catch (java.beans.PropertyVetoException e) {
    	  e.printStackTrace();
      }
    }
  }

}
