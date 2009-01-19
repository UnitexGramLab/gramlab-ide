/*
 * Unitex
 *
 * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import javax.swing.event.*;

import fr.umlv.unitex.process.*;

/**
 * 
 * @author Olivier Blanc
 *  
 */

class ElagGrfFileFilter extends javax.swing.filechooser.FileFilter {

  ElagGrfFileFilter() {
    super();
  }

  public boolean accept(File f) {

    if (f.isDirectory()) {
      return true;
    }

    String s = f.getPath();

    return (s.endsWith(".grf"));
  }

  public String getDescription() {
    return "Elag Grammar (.grf)";
  }
}

public class ElagCompFrame extends JInternalFrame {

  JFileChooser fc;

  File currdir;
  File elagDir;
  File lstfile;
  File outputfile;

  DefaultListModel rules;
  JList list;

  JLabel lstlabel;
  JLabel outlabel;
  JLabel pathlabel;

  static ElagCompFrame frame;

  public ElagCompFrame() {
    super("Elag Grammar Compilation", true, true, true, true);
    elagDir = new File(Config.getUserCurrentLanguageDir(), "Elag");
    currdir = elagDir;
    if (!currdir.exists()) {
      System.err.println("Creating " + currdir.getAbsolutePath()
          + " directory");
      if (!currdir.mkdirs()) {
        System.err.println("FAILED!");
      }
    }
    lstfile = new File(currdir, "elag.lst");
    rules = new DefaultListModel();
    list = new JList(rules);
    lstlabel = new JLabel();
    outlabel = new JLabel();
    pathlabel = new JLabel();
    JPanel panel = makePanel();
    setLstFile(lstfile);
    setContentPane(panel);
    addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent arg0) {
        setVisible(false);
      }
    });
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    pack();
  }

  JPanel makePanel() {

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
    panel.setOpaque(true);

    Dimension mediumdim = new Dimension(120, 25);
    Dimension longdim = new Dimension(300, 25);
    Dimension listdim = new Dimension(400, 250);

    Dimension btndim = new Dimension(90, 25);
    Dimension btn2dim = new Dimension(90, 60);

    Border labelBorder = new LineBorder(Color.black, 1, true);

    GridBagConstraints c = new GridBagConstraints();

    c.insets = new Insets(4, 4, 4, 4);

    c.anchor = GridBagConstraints.WEST;

    c.weightx = 1.0;
    c.weighty = 1.0;

    JLabel titlelabel = new JLabel();

    titlelabel.setText("Set of Elag Grammars:");
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 1;
    c.gridwidth = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 0.0;
    */
    panel.add(titlelabel, c);
 
 
    lstlabel.setBorder(labelBorder);
    lstlabel.setPreferredSize(mediumdim);
    //lstlabel.setOpaque(true);

    c.gridx = 1;
    c.gridy = 0;
    /*
    c.weightx = 1.0;
    c.weighty = 0.0;
    */
    c.fill = GridBagConstraints.HORIZONTAL;
    panel.add(lstlabel, c);

    JButton button = new JButton("browse");
    button.setPreferredSize(btndim);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        JFileChooser f = new JFileChooser();

        f.setCurrentDirectory(currdir);
        f.setFileFilter(new PersonalFileFilter("lst", "Elag grammar collection ( .lst)"));
        f.setAcceptAllFileFilterUsed(false);

        f.setDialogTitle("Elag List File");
        f.setDialogType(JFileChooser.OPEN_DIALOG);

        if ((f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
          return;
        }

        currdir = f.getCurrentDirectory();
        lstfile = f.getSelectedFile();

        //System.err.println("selec=" + lstfile + "dir=" + currdir);

        String ext;
        if ((ext = Util.getExtensionInLowerCase(lstfile)) == null || !ext.equals("lst")) {
          lstfile = new File(lstfile.getAbsolutePath() + ".lst");
        }

        setLstFile(lstfile);
        fc.setCurrentDirectory(currdir);
      }
    });

    c.gridx = 2;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    /*
    c.weightx = 0.0;
    c.weighty = 0.0;
    */
    c.anchor = GridBagConstraints.EAST;
    panel.add(button, c);

    button = new JButton("save");
    button.setPreferredSize(btndim);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveLstFile();
      }
    });

    c.gridx = 3;
    c.gridy = 0;
    c.anchor = GridBagConstraints.EAST;
    panel.add(button, c);

    pathlabel.setBorder(labelBorder);
    pathlabel.setPreferredSize(longdim);
    pathlabel.setOpaque(true);

    c.gridx = 5;
    c.gridy = 0;
    c.gridwidth = 4;
    c.gridheight = 1;
    c.fill = GridBagConstraints.BOTH;
    /*
    c.weightx = 1.0;
    c.weighty = 0.0;
    */
    panel.add(pathlabel, c);

    fc = new JFileChooser();
    fc.setControlButtonsAreShown(false);
    fc.setPreferredSize(listdim);

    fc.setCurrentDirectory(currdir);
    fc.setFileFilter(new ElagGrfFileFilter());
    fc.setAcceptAllFileFilterUsed(false);

    /*
    fc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.err.println("filechooser new action : " + e);
      }
    });
    */ 

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 4;
    c.gridheight = 4;
    /*
    c.weightx = 1.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.BOTH;
    panel.add(fc, c);

    button = new JButton(">>");
    button.setPreferredSize(btn2dim);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        File f = fc.getSelectedFile();

        if (f != null && f.isFile()) {
          addRule(fc.getSelectedFile().getAbsolutePath());
        }
      }
    });

    c.gridx = 4;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.NONE;
    panel.add(button, c);

    button = new JButton("<<");
    button.setPreferredSize(btn2dim);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        int idx = list.getSelectedIndex();

        if (idx < 0 || idx >= rules.size()) {
          return;
        }

        rules.remove(idx);

        if (idx > 0) {
          idx--;
        }

        list.setSelectedIndex(idx);
      }
    });

    c.gridx = 4;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.NONE;
    panel.add(button, c);

    button = new JButton("view");
    button.setPreferredSize(btn2dim);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        int idx = list.getSelectedIndex();

        if (idx < 0 || idx >= rules.size()) {
          return;
        }

        File grf = new File((String) rules.elementAt(idx));

        if (!grf.getName().endsWith(".grf")) {
          JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
            grf.getName() + " doesn't seem like a GRF file",
            "ERROR", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!grf.isAbsolute()) {
          // System.err.println(grf.getName() + " (" +
          // grf.getAbsolutePath() + ") not absolute\n");
          grf = new File(elagDir, (String) rules.elementAt(idx));
          // System.err.println("-> " + grf.getName() + " (" +
          // grf.getAbsolutePath() + ")\n");
        }

        if (!grf.exists()) {
          JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
              grf.getName() + " doesn't seem to exist", "ERROR",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        UnitexFrame.mainFrame.loadGraph(grf);
      }
    });

    c.gridx = 4;
    c.gridy = 3;
    c.gridwidth = 1;
    c.gridheight = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.NONE;
    panel.add(button, c);

    button = new JButton("locate");
    button.setPreferredSize(btn2dim);

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        int idx = list.getSelectedIndex();

        if (idx < 0 || idx >= rules.size()) {
          return;
        }

        String fname = (String) rules.elementAt(idx);

        if (!fname.endsWith(".grf")) {
          JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
            fname + " doesn't seem like a GRF file", "ERROR",
            JOptionPane.ERROR_MESSAGE);

          return;
        }

        fname = fname.replaceAll(".grf$", "-conc.fst2");
        File conc = new File(fname);

        if (!conc.isAbsolute()) {
          conc = new File(elagDir, fname);
          // System.err.println("Conc = " + conc.getAbsolutePath());
        }

        if (!conc.exists()) {
          JOptionPane
            .showInternalMessageDialog(
                UnitexFrame.desktop,
                "You should Compile your Elag grammar before using the Locate Pattern feature",
                "ERROR", JOptionPane.ERROR_MESSAGE);
          return;
        }
        LocateFrame.showFrame();
        LocateFrame.frame.graphName.setText(conc.getAbsolutePath());
        LocateFrame.frame.graph.setSelected(true);
      }
    });

    c.gridx = 4;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.NONE;
    panel.add(button, c);

    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scroll = new JScrollPane(list);
    scroll.setPreferredSize(listdim);

    c.gridx = 5;
    c.gridy = 1;
    c.gridwidth = 4;
    c.gridheight = 4;
    /*
    c.weightx = 1.0;
    c.weighty = 1.0;
    */
    c.fill = GridBagConstraints.BOTH;
    panel.add(scroll, c);

    titlelabel = new JLabel();
    titlelabel.setText("Compiled Elag Rule:");
    
    outlabel.setBorder(labelBorder);
    outlabel.setPreferredSize(mediumdim);
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    /*
    c.weightx = 0.0;
    c.weighty = 0.0;
    */
    panel.add(titlelabel, c);

    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 2;
    c.gridheight = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    /*
    c.weightx = 1.0;
    c.weighty = 0.0;
    */
    panel.add(outlabel, c);

    button = new JButton("compile");
    button.setPreferredSize(btndim);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        saveLstFile();

        File tagset = new File(elagDir, "tagset.def");

        if (!tagset.exists()) {

          File frenchlang = new File(elagDir, "french.lang");

          if (frenchlang.exists()) {

            String message = "File name 'french.lang' is deprecated, the file should be renamed 'tagset.def.\nDo you want to rename it now ?";

            if (JOptionPane.showInternalConfirmDialog(UnitexFrame.desktop, message, "Warning",
                                                      JOptionPane.YES_NO_OPTION,
                                                      JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {

              frenchlang.renameTo(tagset);
            }
          }

          if (!tagset.exists()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
                                                  "File " + tagset.getAbsolutePath() + " doesn't exist.\n", "ERROR",
                                                  JOptionPane.ERROR_MESSAGE);
            return;
          }
        }

        MultiCommands commands = new MultiCommands();

        for (int i = 0; i < rules.size(); i++) {
          String str = (String) rules.elementAt(i);
          File grf = new File(str);
          if (!grf.isAbsolute()) {
            grf = new File(elagDir, str);
          }
          if (!grf.getName().endsWith(".grf")) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
                                                  grf.getName() + " doesn't spell like a GRF file",
                                                  "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (!grf.exists()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
                                                  "Grammar " + grf.getAbsolutePath() + " doesn't seem to exist",
                                                  "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
          }

          str = grf.getAbsolutePath();
          File fst2 = new File(str.replaceAll(".grf$", ".fst2"));
          File elg = new File(str.replaceAll(".grf$", ".elg"));
          if (! elg.exists()
              || elg.lastModified() < grf.lastModified()
              || elg.lastModified() < tagset.lastModified()) { // we have to compile the grf into elg
            /*
             * command = '"' + Config.getApplicationDir() +
             * "Grf2Fst2" + "\" " + '"' + grf.getAbsolutePath() +
             * '"'; cmds.add(command);
             */
            commands.addCommand(new Grf2Fst2Command().grf(grf).enableLoopAndRecursionDetection(true).tokenizationMode().library());

            /*
             * command = '"' + Config.getApplicationDir() +
             * "Flatten" + '"' + ' ' + '"' + fst2.getAbsolutePath() +
             * '"' + ' ' + "FST"; cmds.add(command);
             */
            commands.addCommand(new FlattenCommand().fst2(fst2).resultType(true));

            ElagCompCommand elagCompCmd = new ElagCompCommand().grammar(fst2).lang(tagset);
            /*
             * command = '"' + Config.getApplicationDir() +
             * "ElagComp" + '"' + " -g " + '"' +
             * fst2.getAbsolutePath() + '"' + " -l " + '"' +
             * tagset.getAbsolutePath() + '"';
             */
            commands.addCommand(elagCompCmd);
              }
        }
        ElagCompCommand elagCompCmd = new ElagCompCommand().lang(tagset).dir(currdir).output(outputfile).ruleList(lstfile);
        /*
         * command = '"' + Config.getApplicationDir() + "ElagComp" + '"' + "
         * -l " + '"' + tagset.getAbsolutePath() + '"' + " -d " + '"' +
         * currdir.getAbsolutePath() + '"' + " -o " + '"' +
         * outputfile.getName() + '"' + " -r " + '"' +
         * lstfile.getAbsolutePath() + '"';
         */
        commands.addCommand(elagCompCmd);
        new ProcessInfoFrame(commands, false);
      }
    });

    c.gridx = 3;
    c.gridy = 5;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.fill = GridBagConstraints.NONE;
    /*
    c.weightx = 0.0;
    c.weighty = 0.0;
    */
    c.anchor = GridBagConstraints.EAST;
    panel.add(button, c);

    button = new JButton("cancel compilation");
    //button.setPreferredSize(btndim);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveLstFile();
        doDefaultCloseAction();
      }
    });

    c.gridx = 5;
    c.gridy = 5;
    c.gridwidth = 4;
    c.gridheight = 1;
    panel.add(button, c);

    return panel;
  }

  void setLstFile(File f) {

    lstfile = f;
    readLstFile();

    lstlabel.setText(lstfile.getName());

    String fname = lstfile.getAbsolutePath();

    if (Util.getExtensionInLowerCase(lstfile).equals("lst")) {

      outputfile = new File(Util.getFileNameWithoutExtension(fname)
          + ".rul");

    } else {

      outputfile = new File(fname + ".rul");
    }

    outlabel.setText(outputfile.getName());
  }

  void saveLstFile() {

    try {

      PrintWriter p = new PrintWriter(new FileWriter(lstfile));

      for (int i = 0; i < rules.size(); i++) {
        p.println((String) rules.elementAt(i));
      }

      p.close();

    } catch (Exception e) {
      System.err.println("I/O error with " + lstfile.getName() + " : "
          + e);
    }
  }

  void readLstFile() {

    rules.clear();

    if (!lstfile.exists()) {
      return;
    }

    try {

      LineNumberReader r = new LineNumberReader(new FileReader(lstfile));

      String s;

      while ((s = r.readLine()) != null) {
        addRule(s);
      }

      r.close();

    } catch (Exception e) {
      System.err.println("I/O error with " + lstfile.getName() + " : "
          + e);
    }
  }

  void addRule(String s) {
    if (rules.indexOf(s) == -1) {
      int idx = rules.size();
      rules.add(idx, s);
      list.ensureIndexIsVisible(idx);
      list.setSelectedIndex(idx);
    }
  }

  /**
   * Initializes the frame
   *  
   */
  private static void init() {
    frame = new ElagCompFrame();
    UnitexFrame.addInternalFrame(frame);
  }

  /**
   * Shows the frame
   *  
   */
  public static void showFrame() {
    if (frame == null) {
      init();
    }
    frame.setVisible(true);
    try {
      frame.setSelected(true);
      frame.setIcon(false);
    } catch (java.beans.PropertyVetoException e2) {
    	e2.printStackTrace();
    }
  }

}
