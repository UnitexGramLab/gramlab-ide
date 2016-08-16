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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.exceptions.InvalidDestinationEncodingException;
import org.gramlab.core.umlv.unitex.exceptions.InvalidSourceEncodingException;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.ConvertCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * @author Sébastien Paumier
 */
public class TransliterationFrame extends JInternalFrame {
	private final JRadioButton DELAS = new JRadioButton("DELAS/DELAC");
	private final JRadioButton DELAF = new JRadioButton("DELAF/DELACF", true);
	private final JRadioButton srcArabic = new JRadioButton("Arabic", true);
	private final JRadioButton srcBuckwalter = new JRadioButton("Buckwalter");
	private final JRadioButton srcBuckwalterPlusPlus = new JRadioButton(
			"Buckwalter++");
	private final JRadioButton destArabic = new JRadioButton("Arabic");
	private final JRadioButton destBuckwalter = new JRadioButton("Buckwalter");
	private final JRadioButton destBuckwalterPlusPlus = new JRadioButton(
			"Buckwalter++", true);

	TransliterationFrame() {
		super("Transliteration", false, true);
		setContentPane(constructPanel());
		pack();
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructLeftPanel(), BorderLayout.WEST);
		panel.add(constructRightPanel(), BorderLayout.CENTER);
		final JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(constructEncodingPanel(), BorderLayout.CENTER);
		panel2.add(panel, BorderLayout.SOUTH);
		return panel2;
	}

	private JPanel constructEncodingPanel() {
		final JPanel p = new JPanel(new GridLayout(1, 2));
		final JPanel src = new JPanel(new GridLayout(3, 1));
		src.setBorder(BorderFactory.createTitledBorder("Input"));
		final ButtonGroup bgSrc = new ButtonGroup();
		src.add(srcArabic);
		src.add(srcBuckwalter);
		src.add(srcBuckwalterPlusPlus);
		bgSrc.add(srcArabic);
		bgSrc.add(srcBuckwalter);
		bgSrc.add(srcBuckwalterPlusPlus);
		p.add(src);
		final JPanel dest = new JPanel(new GridLayout(3, 1));
		dest.setBorder(BorderFactory.createTitledBorder("Output"));
		final ButtonGroup bgDest = new ButtonGroup();
		dest.add(destArabic);
		dest.add(destBuckwalter);
		dest.add(destBuckwalterPlusPlus);
		bgDest.add(destArabic);
		bgDest.add(destBuckwalter);
		bgDest.add(destBuckwalterPlusPlus);
		p.add(dest);
		return p;
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
		final Action goAction = new AbstractAction("Transliterate") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				transliterate();
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
		rightPanel.add(GO);
		rightPanel.add(CANCEL);
		KeyUtil.addEscListener(rightPanel, CANCEL);
		return rightPanel;
	}

	/**
	 * Launches the <code>CheckDic</code> verification program, through the
	 * creation of a <code>ProcessInfoFrame</code> object.
	 */
	void transliterate() {
		ConvertCommand command = new ConvertCommand().file(
				Config.getCurrentDELA()).replace();
		if (DELAF.isSelected())
			command = command.delaf();
		else
			command = command.delas();
		try {
			if (srcArabic.isSelected())
				command = command.src("UTF16LE");
			else if (srcBuckwalter.isSelected())
				command = command.src("buckwalter");
			else
				command = command.src("buckwalter++");
		} catch (final InvalidSourceEncodingException e) {
			e.printStackTrace();
		}
		try {
			if (destArabic.isSelected())
				command = command.dest("UTF16LE");
			else if (destBuckwalter.isSelected())
				command = command.dest("buckwalter");
			else
				command = command.dest("buckwalter++");
		} catch (final InvalidDestinationEncodingException e) {
			e.printStackTrace();
		}
		setVisible(false);
		Launcher.exec(command.getBuilder(), true,
				new ReloadDicDo(Config.getCurrentDELA()));
	}

	class ReloadDicDo implements ToDo {
		File dela;

		public ReloadDicDo(File dela) {
			this.dela = dela;
		}

		@Override
		public void toDo(boolean success) {
			UnitexProjectManager.search(dela)
					.getFrameManagerAs(InternalFrameManager.class).newDelaFrame(dela);
		}
	}
}
