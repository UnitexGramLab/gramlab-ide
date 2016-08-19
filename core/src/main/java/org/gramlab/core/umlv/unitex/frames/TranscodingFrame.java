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
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.DropTargetManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.exceptions.InvalidDestinationEncodingException;
import org.gramlab.core.umlv.unitex.exceptions.InvalidSourceEncodingException;
import org.gramlab.core.umlv.unitex.listeners.LanguageListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.ConvertCommand;
import org.gramlab.core.umlv.unitex.transcoding.Transcoder;

/**
 * This class provides a file transcoding internal frame.
 * 
 * @author Sébastien Paumier
 */
public class TranscodingFrame extends JInternalFrame {
	final JList srcEncodingList = new JList(Transcoder.getAvailableEncodings());
	final JList destEncodingList = new JList(Transcoder.getAvailableEncodings());
	final JRadioButton replace = new JRadioButton("Replace");
	final JRadioButton renameSourceWithPrefix = new JRadioButton(
			"Rename source with prefix");
	final JRadioButton renameSourceWithSuffix = new JRadioButton(
			"Rename source with suffix");
	final JRadioButton nameDestWithPrefix = new JRadioButton(
			"Name destination with prefix");
	final JRadioButton nameDestWithSuffix = new JRadioButton(
			"Name destination with suffix");
	final JTextField prefixSuffix = new JTextField("");
	final DefaultListModel listModel = new DefaultListModel();
	final JList fileList = new JList(listModel);
	final JButton addFiles = new JButton("Add Files");
	final JButton removeFiles = new JButton("Remove Files");
	final JButton transcode = new JButton("Transcode");
	final JButton cancel = new JButton("Cancel");
	ToDo toDo;
	boolean closeAfterWork = false;

	TranscodingFrame() {
		super("Transcode Files", true, true);
		setContentPane(constructPanel());
		setBounds(100, 100, 500, 500);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		final String language;
		if((GlobalProjectManager.getGlobalProjectManager()) instanceof GramlabProjectManager){
			language=GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject().getLanguage();
		}
		else{
			language = Config.getCurrentLanguage();
		}	
		srcEncodingList.setSelectedValue(
				Transcoder.getEncodingForLanguage(language),
				true);
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				srcEncodingList.setSelectedValue(Transcoder
						.getEncodingForLanguage(language),
						true);
			}
		});
		destEncodingList.setSelectedValue("LITTLE-ENDIAN", true);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				listModel.removeAllElements();
				toDo = null;
			}
		});
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		final JPanel tmp = new JPanel(new GridLayout(1, 2));
		tmp.add(constructSrcEncodingPanel());
		tmp.add(constructDestEncodingPanel());
		final JPanel up = new JPanel(new BorderLayout());
		up.add(tmp, BorderLayout.CENTER);
		up.add(constructFileNamePanel(), BorderLayout.EAST);
		panel.add(up, BorderLayout.NORTH);
		final JPanel down = new JPanel(new BorderLayout());
		down.add(constructFileListPanel(), BorderLayout.CENTER);
		down.add(constructButtonPanel(), BorderLayout.EAST);
		panel.add(down, BorderLayout.CENTER);
		KeyUtil.addEscListener(panel, cancel);
		return panel;
	}

	private JPanel constructSrcEncodingPanel() {
		final JPanel srcEncodingPanel = new JPanel(new BorderLayout());
		srcEncodingPanel
				.add(new JLabel("Source encoding:"), BorderLayout.NORTH);
		srcEncodingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		srcEncodingPanel.add(new JScrollPane(srcEncodingList),
				BorderLayout.CENTER);
		return srcEncodingPanel;
	}

	private JPanel constructDestEncodingPanel() {
		final JPanel destEncodingPanel = new JPanel(new BorderLayout());
		destEncodingPanel.add(new JLabel("Destination encoding:"),
				BorderLayout.NORTH);
		destEncodingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		destEncodingPanel.add(new JScrollPane(destEncodingList),
				BorderLayout.CENTER);
		return destEncodingPanel;
	}

	private JPanel constructFileNamePanel() {
		final JPanel fileNamePanel = new JPanel(new GridLayout(7, 1));
		fileNamePanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		fileNamePanel.add(replace);
		fileNamePanel.add(renameSourceWithPrefix);
		fileNamePanel.add(renameSourceWithSuffix);
		fileNamePanel.add(nameDestWithPrefix);
		fileNamePanel.add(nameDestWithSuffix);
		fileNamePanel.add(new JLabel("Prefix/suffix:"));
		fileNamePanel.add(prefixSuffix);
		final ButtonGroup bg = new ButtonGroup();
		bg.add(replace);
		bg.add(renameSourceWithPrefix);
		bg.add(renameSourceWithSuffix);
		bg.add(nameDestWithPrefix);
		bg.add(nameDestWithSuffix);
		renameSourceWithSuffix.setSelected(true);
		prefixSuffix.setText("-old");
		return fileNamePanel;
	}

	private JPanel constructFileListPanel() {
		final JPanel fileListPanel = new JPanel(new BorderLayout());
		fileListPanel.setBorder(new TitledBorder("Selected files:"));
		fileListPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
		DropTargetManager.getDropTarget().newTranscodeDropTarget(fileList);
		return fileListPanel;
	}

	private JPanel constructButtonPanel() {
		final TranscodingFrame zis = this;
		addFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int returnVal = Config.getTranscodeDialogBox()
						.showOpenDialog(zis);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				final File[] graphs = Config.getTranscodeDialogBox()
						.getSelectedFiles();
				for (final File graph : graphs) {
					if (!listModel.contains(graph)) {
						listModel.addElement(graph);
					}
				}
			}
		});
		removeFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Object[] graphs = fileList.getSelectedValues();
				for (final Object graph : graphs) {
					listModel.removeElement(graph);
				}
			}
		});
		transcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String src = (String) srcEncodingList.getSelectedValue();
				if (src == null) {
					JOptionPane.showMessageDialog(null,
							"You must select an input encoding", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String dest = (String) destEncodingList
						.getSelectedValue();
				if (dest == null) {
					JOptionPane.showMessageDialog(null,
							"You must select a destination encoding", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String preSuf = prefixSuffix.getText();
				if (!replace.isSelected() && preSuf.equals("")) {
					JOptionPane.showMessageDialog(null,
							"You must specify a prefix/suffix", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				ConvertCommand command;
				try {
					command = new ConvertCommand().src(src).dest(dest);
				} catch (final InvalidDestinationEncodingException e) {
					e.printStackTrace();
					return;
				} catch (final InvalidSourceEncodingException e) {
					e.printStackTrace();
					return;
				}
				if (replace.isSelected())
					command = command.replace();
				else if (renameSourceWithPrefix.isSelected())
					command = command.renameSourceWithPrefix(preSuf);
				else if (renameSourceWithSuffix.isSelected()) {
					command = command.renameSourceWithSuffix(preSuf);
				} else if (nameDestWithPrefix.isSelected()) {
					command = command.renameDestWithPrefix(preSuf);
				} else {
					command = command.renameDestWithSuffix(preSuf);
				}
				final ConvertCommand cmd = command;
				final ToDo d = toDo;
				toDo = null;
				setVisible(false);
				final int l = listModel.getSize();
				for (int i = 0; i < l; i++) {
					cmd.file((File) listModel.getElementAt(i));
				}
				Launcher.exec(cmd, closeAfterWork, d);
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doDefaultCloseAction();
			}
		});
		final JPanel buttonPanel = new JPanel(new BorderLayout());
		final JPanel tmp = new JPanel(new GridLayout(4, 1));
		tmp.add(addFiles);
		tmp.add(removeFiles);
		tmp.add(transcode);
		tmp.add(cancel);
		buttonPanel.add(tmp, BorderLayout.NORTH);
		buttonPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
		return buttonPanel;
	}

	/**
	 * @return the list model of the conversion frame
	 */
	public DefaultListModel getListModel() {
		return listModel;
	}

	void configure(File file, ToDo toDo1, boolean closeAfterWork2) {
		listModel.removeAllElements();
		this.closeAfterWork = closeAfterWork2;
		this.toDo = toDo1;
		if (toDo1 != null) {
			listModel.addElement(file);
			addFiles.setEnabled(false);
			removeFiles.setEnabled(false);
		} else {
			addFiles.setEnabled(true);
			removeFiles.setEnabled(true);
		}
	}
}
