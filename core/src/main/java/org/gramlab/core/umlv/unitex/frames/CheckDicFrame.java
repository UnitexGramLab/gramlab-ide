/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.CheckDicCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * This class describes the "Check Format" frame, accessible from the "DELA"
 * menu of Unitex. The user can select the kind of dictionary he wants to check.
 * 
 * @author Sébastien Paumier
 */
public class CheckDicFrame extends JInternalFrame {
	private final JRadioButton DELAS = new JRadioButton("DELAS/DELAC");
	private final JRadioButton DELAF = new JRadioButton("DELAF/DELACF", true);

	File dela;

	CheckDicFrame() {
		super("Check Dictionary Format", false, true);
		constructPanel();
		setContentPane(constructPanel());
		setBounds(100, 100, 200, 100);
		pack();
	}

	public void setDela(File dela) {
		this.dela = dela;
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructLeftPanel(), BorderLayout.WEST);
		panel.add(constructRightPanel(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructLeftPanel() {
		final JPanel leftPanel = new JPanel();
		final JPanel tmp = new JPanel(new GridLayout(2, 1));
		tmp.setBorder(new TitledBorder("Dictionary Type:"));
		DELAS.setSelected(false);
		DELAF.setSelected(true);
		final ButtonGroup bg = new ButtonGroup();
		bg.add(DELAS);
		bg.add(DELAF);
		tmp.add(DELAS);
		tmp.add(DELAF);
		leftPanel.add(tmp);
		return leftPanel;
	}

	private JPanel constructRightPanel() {
		final JPanel rightPanel = new JPanel(new GridLayout(2, 1));
		rightPanel.setBorder(new EmptyBorder(12, 5, 5, 7));
		final Action goAction = new AbstractAction("Check Dictionary") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				checkDELA();
			}
		};
		final JButton GO = new JButton(goAction);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		KeyUtil.addEscListener(rightPanel, CANCEL);
		rightPanel.add(GO);
		rightPanel.add(CANCEL);
		return rightPanel;
	}

	/**
	 * Launches the <code>CheckDic</code> verification program, through the
	 * creation of a <code>ProcessInfoFrame</code> object.
	 */
	void checkDELA() {
		if (dela == null) {
			throw new IllegalStateException(
					"dela should have been set before invoking this method");
		}
		CheckDicCommand command = new CheckDicCommand().name(dela)
				.delaType(DELAS.isSelected())
				.alphabet(ConfigManager.getManager().getAlphabet(null));
		final String language = ConfigManager.getManager().getCurrentLanguage();
		if (language.equals("Chinese") || language.equals("Mandarin")) {
			command = command.no_space_warning();
		}
		final File tmp = new File(dela.getParentFile(), "CHECK_DIC.TXT");
		UnitexProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.closeCheckResultFrame();
		Launcher.exec(command.getBuilder(), true, new CheckDicDo(tmp));
	}

	class CheckDicDo implements ToDo {
		final File results;

		public CheckDicDo(File s) {
			results = s;
		}

		@Override
		public void toDo(boolean success) {
			UnitexProjectManager.search(results).getFrameManagerAs(InternalFrameManager.class)
					.newCheckResultFrame(results);
		}
	}
}
