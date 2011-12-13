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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MkdirCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.Seq2GrfCommand;

/**
 * This class describes the "Construct Text FST" frame that offers to the user
 * to build the text automaton.
 * 
 * @author Sébastien Paumier
 */
public class ConstructSeqTfstFrame extends JInternalFrame {
	// private final JTextField SNTfile = new JTextField();
	private final JTextField GRFfile = new JTextField();
	private final JTextField alphFile = new JTextField();
	private int n_op = 0, n_r = 0, n_d = 0, n_a = 0;
	JSpinner spinner_op, spinner_r, spinner_d, spinner_a;
	SpinnerNumberModel sm_op, sm_r, sm_d, sm_a;

	/**
	 * Creates and shows a new <code>ConstructSeqFstFrame</code>.
	 */
	ConstructSeqTfstFrame() {
		super("Construct the Seq FST", false);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructPanel() {
		final JPanel top = new JPanel(new GridLayout(1, 2));
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(textPanel(), BorderLayout.CENTER);
		final JPanel FilePanel = new JPanel(new BorderLayout());
		// midPanel.add(alphabetPanel(),BorderLayout.NORTH);
		FilePanel.add(outputFilePanel(), BorderLayout.SOUTH);
		top.add(FilePanel);
		panel.add(constructButtonsPanel(), BorderLayout.SOUTH);
		new JPanel(new BorderLayout());
		top.add(jokersPanel(), BorderLayout.EAST);
		panel.add(top, BorderLayout.NORTH);
		return panel;
	}

	private JPanel constructButtonsPanel() {
		final JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.setBorder(new EmptyBorder(6, 6, 2, 2));
		final Action okAction = new AbstractAction("Construct FST") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				constructSeqTfst();
			}
		};
		final Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton OK = new JButton(okAction);
		final JButton CANCEL = new JButton(cancelAction);
		buttons.add(CANCEL);
		buttons.add(OK);
		return buttons;
	}

	private JPanel textPanel() {
		final JPanel textP = new JPanel();
		textP.setBorder(new TitledBorder("Name the output GRF File"));
		textP
				.add(new JLabel(
						"The programm will construct the automata that regognizes all theses sequences."));
		return textP;
	}

	private JPanel outputFilePanel() {
		final JPanel fileP = new JPanel(new GridLayout(3, 1));
		final String sntName = Config.getCurrentSnt().getName();
		final String grfFile = Config.getCurrentSntDir().getPath()
				+ File.separatorChar
				+ sntName.substring(0, sntName.length() - 3) + "grf";
		System.out.println("GRF FILE : " + grfFile);
		// ?
		GRFfile.setName(grfFile);
		fileP.setBorder(new EmptyBorder(8, 8, 1, 1));
		fileP.add(new JLabel("choose the name of the resulting grf file"));
		// ?
		GRFfile.setText(grfFile);
		fileP.add(GRFfile, BorderLayout.LINE_START);
		final Action setAction = new AbstractAction("Set ...") {
			public void actionPerformed(ActionEvent arg1) {
				final JFileChooser chooser = Config.getGrfAndFst2DialogBox();
				System.out.println("chooser dir : "
						+ chooser.getCurrentDirectory());
				chooser.setCurrentDirectory(Config.getCurrentSntDir());
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				GRFfile.setText(chooser.getSelectedFile().getAbsolutePath());
				// ?
				GRFfile.setName(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setButton = new JButton(setAction);
		setButton.setSize(4, 1);
		fileP.add(setButton);
		return fileP;
	}

	void constructSeqTfst() {
		final MultiCommands commands = new MultiCommands();
		final File dir = Config.getCurrentSntDir();
		if (!dir.exists()) {
			/* If the directory toto_snt does not exist, we create it */
			commands.addCommand(new MkdirCommand().name(dir));
		}
		/* Cleaning files */
		Config.cleanTfstFiles(true);
		final Seq2GrfCommand seqCmd = new Seq2GrfCommand().alphabet(
				ConfigManager.getManager().getAlphabet(null).getAbsolutePath())
				.output(GRFfile.getText()).jokers(n_op).joker_insert(n_a)
				.joker_replace(n_r).joker_delete(n_d).text(
						Config.getCurrentSnt());
		// File normFile = null;
		// File normGrfFile = null;
		System.out.println("seqCmd =" + seqCmd.getCommandLine());
		commands.addCommand(seqCmd);
		InternalFrameManager.getManager(null).closeTextAutomatonFrame();
		InternalFrameManager.getManager(null).closeTfstTagsFrame();
		/* We also have to rebuild the text automaton */
		Config.cleanTfstFiles(true);
		Launcher.exec(commands, true);
		final File GRF = new File(GRFfile.getName());
		Launcher.exec(commands, true, new ConstructTfstDo(GRF), false);
	}

	class ConstructTfstDo implements ToDo {
		File GrfFileName;

		ConstructTfstDo(File grf) {
			GrfFileName = grf;
		}

		public void toDo() {
			Config.cleanTfstFiles(true);
			InternalFrameManager.getManager(GrfFileName).newGraphFrame(
					GrfFileName);
		}
	}

	private JPanel jokersPanel() {
		// cf TextAutomatonFrame.java line 453
		final JPanel p = new JPanel(new GridLayout(5, 2));
		p.setBorder(BorderFactory.createRaisedBevelBorder());
		final JLabel jokers = new JLabel("Jokers");
		final JLabel insert = new JLabel("insert");
		final JLabel replace = new JLabel("replace");
		final JLabel delete = new JLabel("delete");
		sm_op = new SpinnerNumberModel(0, 0, 10, 1);
		sm_op.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				n_op = sm_op.getNumber().intValue();
				System.out.println("n_op =" + n_op + "\nn_a =" + n_a
						+ "\nn_r =" + n_r + "\nn_d =" + n_d);
			}
		});
		spinner_op = new JSpinner(sm_op);
		sm_a = new SpinnerNumberModel(0, 0, 10, 1);
		sm_a.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				n_a = sm_a.getNumber().intValue();
				System.out.println("n_op =" + n_op + "\nn_a =" + n_a
						+ "\nn_r =" + n_r + "\nn_d =" + n_d);
			}
		});
		spinner_a = new JSpinner(sm_a);
		sm_d = new SpinnerNumberModel(0, 0, 10, 1);
		sm_d.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				n_d = sm_d.getNumber().intValue();
				System.out.println("n_op =" + n_op + "\nn_a =" + n_a
						+ "\nn_r =" + n_r + "\nn_d =" + n_d);
			}
		});
		spinner_d = new JSpinner(sm_d);
		sm_r = new SpinnerNumberModel(0, 0, 10, 1);
		sm_r.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				n_r = sm_r.getNumber().intValue();
				System.out.println("n_op =" + n_op + "\nn_a =" + n_a
						+ "\nn_r =" + n_r + "\nn_d =" + n_d);
			}
		});
		spinner_r = new JSpinner(sm_r);
		p.add(jokers);
		p.add(spinner_op);
		p.add(insert);
		p.add(spinner_a);
		p.add(replace);
		p.add(spinner_r);
		p.add(delete);
		p.add(spinner_d);
		return p;
	}
}
