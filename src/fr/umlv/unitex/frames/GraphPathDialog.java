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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Util;
import fr.umlv.unitex.listeners.FontListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.Fst2ListCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.text.BigTextList;

/**
 * This class defines a frame that allows the user to show paths of a graph.
 *
 * @author Sébastien Paumier 11.11.2005 modified HyunGue HUH
 */
public class GraphPathDialog extends JDialog {
    private final BigTextList textArea = new BigTextList();
    final JTextField graphName = new JTextField();
    private JCheckBox limit;
    private JTextField limitSize;
    private JRadioButton ignoreOutputs;
    private JRadioButton separateOutputs;
    private JRadioButton mergeOutputs;
    private JRadioButton exploreRecursively;
    private JRadioButton onlyPaths;

    GraphPathDialog() {
        super(UnitexFrame.mainFrame, "Explore graph paths", true);
        setContentPane(constructPanel());
        setBounds(100, 100, 420, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        textArea.setFont(Config.getCurrentTextFont());
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                textArea.setFont(font);
            }
        });
        setLocationRelativeTo(UnitexFrame.mainFrame);
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(constructTopPanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel constructTopPanel() {
        JPanel top = new JPanel(new GridLayout(6, 1));
        top.add(constructGraphNamePanel());
        ButtonGroup bg = new ButtonGroup();
        ignoreOutputs = new JRadioButton("Ignore outputs", true);
        separateOutputs = new JRadioButton("Separate inputs and outputs");
        mergeOutputs = new JRadioButton("Merge inputs and outputs");
        bg.add(ignoreOutputs);
        bg.add(separateOutputs);
        bg.add(mergeOutputs);
        top.add(ignoreOutputs);
        top.add(separateOutputs);
        top.add(mergeOutputs);
        top.add(constructDownPanel());
        JPanel top1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup pathWithSubGraph = new ButtonGroup();
        onlyPaths = new JRadioButton("Only paths", true);
        exploreRecursively = new JRadioButton("Do not explore subgraphs recursively");
        pathWithSubGraph.add(onlyPaths);
        pathWithSubGraph.add(exploreRecursively);
        top1.add(onlyPaths);
        top1.add(exploreRecursively);
        top.add(top1);
        return top;
    }

    private JPanel constructDownPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(constructLimitPanel(), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new GridLayout(1, 2));
        Action goAction = new AbstractAction("GO") {
            public void actionPerformed(ActionEvent arg0) {
                Fst2ListCommand cmd = new Fst2ListCommand();
                Grf2Fst2Command grfCmd = new Grf2Fst2Command();
                File fst2;
                File list; /* output file name */
                int n;
                if (limit.isSelected()) {
                    try {
                        n = Integer.parseInt(limitSize.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,
                                "You must specify a valid limit", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    cmd = cmd.limit(n);
                } else {
                    cmd = cmd.noLimit();
                }
                if (ignoreOutputs.isSelected()) {
                    cmd = cmd.ignoreOutputs();
                } else {
                    cmd = cmd.separateOutputs(separateOutputs.isSelected());
                }
                grfCmd.grf(new File(graphName.getText()))
                        .enableLoopAndRecursionDetection(true).repository();
                fst2 = new File(Util.getFileNameWithoutExtension(graphName
                        .getText())
                        + ".fst2");
                if (onlyPaths.isSelected()) {
                    list = new File(Config.getUserCurrentLanguageDir(),
                            "list.txt");
                    cmd = cmd.listOfPaths(fst2, list);
                } else {
                    list = new File(Util.getFileNameWithoutExtension(graphName
                            .getText())
                            + "autolst.txt");
                    cmd = cmd.listsOfSubgraph(fst2);
                }
                MultiCommands commands = new MultiCommands();
                commands.addCommand(grfCmd);
                commands.addCommand(cmd);
                textArea.reset();
                Launcher.exec(commands, true, new ShowPathsDo(list));
            }
        };
        JButton GO = new JButton(goAction);
        buttons.add(GO);
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        };
        JButton CANCEL = new JButton(cancelAction);
        buttons.add(CANCEL);
        panel.add(buttons, BorderLayout.EAST);
        return panel;
    }

    void close() {
        setVisible(false);
        textArea.reset();
        textArea.clearSelection();
    }

    private JPanel constructLimitPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        limit = new JCheckBox("Maximum number of sequences: ", true);
        limitSize = new JTextField("100");
        limitSize.setPreferredSize(new Dimension(50, 20));
        panel.add(limit, BorderLayout.WEST);
        panel.add(limitSize, BorderLayout.CENTER);
        panel.add(new JLabel("   "), BorderLayout.EAST);
        return panel;
    }

    private JPanel constructGraphNamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(" Graph: "), BorderLayout.WEST);
        panel.add(graphName, BorderLayout.CENTER);
        return panel;
    }

    class ShowPathsDo implements ToDo {
        private final File name;

        ShowPathsDo(File name) {
            this.name = name;
        }

        public void toDo() {
            textArea.load(name);
        }
    }
}
