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
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.CheckDicCommand;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class describes the "Check Format" frame, accessible from the "DELA"
 * menu of Unitex. The user can select the kind of dictionary he wants to check.
 *
 * @author Sébastien Paumier
 */
public class CheckDicFrame extends JInternalFrame {

    private final JRadioButton DELAS = new JRadioButton("DELAS/DELAC");
    private final JRadioButton DELAF = new JRadioButton("DELAF/DELACF", true);

    CheckDicFrame() {
        super("Check Dictionary Format", false, true);
        constructPanel();
        setContentPane(constructPanel());
        setBounds(100, 100, 200, 100);
        pack();
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(constructLeftPanel(), BorderLayout.WEST);
        panel.add(constructRightPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel constructLeftPanel() {
        JPanel leftPanel = new JPanel();
        JPanel tmp = new JPanel(new GridLayout(2, 1));
        tmp.setBorder(new TitledBorder("Dictionary Type:"));
        DELAS.setSelected(false);
        DELAF.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(DELAS);
        bg.add(DELAF);
        tmp.add(DELAS);
        tmp.add(DELAF);
        leftPanel.add(tmp);
        return leftPanel;
    }

    private JPanel constructRightPanel() {
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBorder(new EmptyBorder(12, 5, 5, 7));
        Action goAction = new AbstractAction("Check Dictionary") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                checkDELA();
            }
        };
        JButton GO = new JButton(goAction);
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        };
        JButton CANCEL = new JButton(cancelAction);
        rightPanel.add(GO);
        rightPanel.add(CANCEL);
        return rightPanel;
    }

    /**
     * Launches the <code>CheckDic</code> verification program, through the
     * creation of a <code>ProcessInfoFrame</code> object.
     */
    void checkDELA() {
        CheckDicCommand command = new CheckDicCommand().name(
                Config.getCurrentDELA()).delaType(DELAS.isSelected()).alphabet(
                Config.getAlphabet());
    	if (Config.getCurrentLanguage().equals("Chinese") ||
    	        Config.getCurrentLanguage().equals("Mandarin")) {
    	    command=command.no_space_warning();
    	}
        File tmp = new File(Config.getCurrentDELA().getParentFile(),
                "CHECK_DIC.TXT");
        UnitexFrame.getFrameManager().closeCheckResultFrame();
        Launcher.exec(command.getBuilder(), true, new CheckDicDo(tmp));
    }

    class CheckDicDo implements ToDo {
        final File results;

        public CheckDicDo(File s) {
            results = s;
        }

        public void toDo() {
            UnitexFrame.getFrameManager().newCheckResultFrame(results);
        }
	}

}