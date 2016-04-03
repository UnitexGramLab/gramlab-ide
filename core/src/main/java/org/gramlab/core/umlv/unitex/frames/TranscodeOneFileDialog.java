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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.exceptions.InvalidDestinationEncodingException;
import fr.umlv.unitex.exceptions.InvalidSourceEncodingException;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.ConvertCommand;
import fr.umlv.unitex.transcoding.Transcoder;

/**
 * @author Sébastien Paumier
 */
public class TranscodeOneFileDialog extends JDialog {
	final JRadioButton replace = new JRadioButton("Replace", true);
	final JLabel line1 = new JLabel();
	final JLabel line2 = new JLabel();
	File file;
	ToDo toDo;

	TranscodeOneFileDialog() {
		super(UnitexFrame.mainFrame, "Transcoding", true);
		final ButtonGroup bg = new ButtonGroup();
		bg.add(replace);
		final JRadioButton renameSource = new JRadioButton(
				"Rename source with suffix '.old'");
		bg.add(renameSource);
		replace.setSelected(true);
		final JPanel panel = new JPanel(new GridLayout(5, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		panel.add(line1);
		panel.add(new JLabel("is not a Unicode Little-Endian one. Do you want"));
		panel.add(line2);
		panel.add(replace);
		panel.add(renameSource);
		getContentPane().add(panel, BorderLayout.CENTER);
		final JPanel buttons = new JPanel();
		final JButton transcode = new JButton("Transcode");
		transcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				ConvertCommand cmd = new ConvertCommand();
				try {
					cmd = cmd.src(
							Transcoder.getEncodingForLanguage(Config
									.getCurrentLanguage())).dest(
							"LITTLE-ENDIAN");
				} catch (final InvalidDestinationEncodingException e1) {
					e1.printStackTrace();
				} catch (final InvalidSourceEncodingException e1) {
					e1.printStackTrace();
				}
				if (replace.isSelected()) {
					cmd = cmd.replace();
				} else {
					cmd = cmd.renameSourceWithSuffix(".old");
				}
				cmd = cmd.file(file);
				Launcher.exec(cmd, true, toDo);
			}
		});
		final JButton ignore = new JButton("Ignore file");
		ignore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		final JButton more = new JButton("More options...");
		more.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newTranscodingFrame(file,toDo, true);
			}
		});
		buttons.add(transcode);
		buttons.add(ignore);
		buttons.add(more);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		pack();
		setResizable(false);
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	void configure(File f, ToDo toDo1) {
		this.file = f;
		this.toDo = toDo1;
		line1.setText(f.getAbsolutePath());
		line2.setText("to transcode it from "
				+ Transcoder.getEncodingForLanguage(Config.getCurrentLanguage())
				+ " to Unicode Little-Endian ?");
		pack();
	}
}
