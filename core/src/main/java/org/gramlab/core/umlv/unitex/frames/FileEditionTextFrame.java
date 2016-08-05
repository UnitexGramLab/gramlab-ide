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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.MyCursors;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.editor.EditionTextArea;
import org.gramlab.core.umlv.unitex.editor.FileEditionMenu;
import org.gramlab.core.umlv.unitex.editor.FileManager;

/*
 * This class is used to display the text
 *  
 */
public class FileEditionTextFrame extends TabbableInternalFrame {
	/**
	 * Area where the text is stored and can be edited
	 */
	final EditionTextArea text;
	FileManager fileManager;
	File file;
	long lastModification;

	final Timer autoRefresh = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (file == null) {
				setTitle(text.isModified() ? "(unsaved)" : "");
				return;
			}
			String title1 = "";
			if (text.isModified()) {
				title1 = "(modified) ";
			}
			setTitle(title1 + file.getAbsolutePath());
			if (!file.exists()) {
				/* Case of a file that has been removed */
				final Timer t = (Timer) e.getSource();
				t.stop();
				final String[] options = { "Yes", "No" };
				final int n = JOptionPane.showOptionDialog(
						FileEditionTextFrame.this,
						"The file "
								+ file.getAbsolutePath()
								+ " does\n"
								+ "not exist anymore on disk. Do you want to close the frame?",
						"", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 1) {
					return;
				}
				doDefaultCloseAction();
				return;
			}
			if (file.lastModified() > lastModification) {
				final int ret = JOptionPane.showConfirmDialog(
						FileEditionTextFrame.this,
						"File has changed on disk. Do you want to reload it ?",
						"", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					lastModification = file.lastModified();
					FileManager.load(file, text);
				} else {
					/*
					 * We don't want to be asked again until another
					 * modification
					 */
					lastModification = file.lastModified();
				}
			}
		}
	});

	final Action saveAction = new AbstractAction("Save", MyCursors.saveIcon) {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveFile(file);
		}
	};
	Action saveAsAction = new AbstractAction("Save as...") {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveFile(null);
		}
	};
	private final Action cutAction = new AbstractAction("Cut",
			MyCursors.cutIcon) {
		@Override
		public void actionPerformed(ActionEvent e) {
			text.cut();
		}
	};
	private final Action copyAction = new AbstractAction("Copy",
			MyCursors.copyIcon) {
		@Override
		public void actionPerformed(ActionEvent e) {
			text.copy();
		}
	};
	private final Action pasteAction = new AbstractAction("Paste",
			MyCursors.pasteIcon) {
		@Override
		public void actionPerformed(ActionEvent e) {
			text.paste();
		}
	};
	private final Action findAction = new AbstractAction("Find",
			MyCursors.findIcon) {
		@Override
		public void actionPerformed(ActionEvent e) {
			GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class)
					.newFindDialog(FileEditionTextFrame.this);
		}
	};

	FileEditionTextFrame(File file) {
		super("", true, true, true, true);
		text = new EditionTextArea();
		this.file = file;
		if (file == null) {
			this.setTitle("New File");
		} else {
			this.setTitle(file.getAbsolutePath());
			this.lastModification = file.lastModified();
			FileManager.load(file, text);
		}
		init();
		autoRefresh.setInitialDelay(5);
		autoRefresh.start();
	}

	private void init() {
		fileManager = new FileManager();
		final JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		final JScrollPane scroll = new JScrollPane(text);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		final JPanel middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll);
		setJMenuBar(initMenuBar());
		top.add(middle, BorderLayout.CENTER);
		final JToolBar toolBar = initToolBar();
		top.add(toolBar, BorderLayout.NORTH);
		setContentPane(top);
		pack();
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				if (text.isModified()) {
					final Object[] options_on_exit = { "Save", "Don't save" };
					final Object[] normal_options = { "Save", "Don't save",
							"Cancel" };
					int n;
					String message;
					if (file == null) {
						message = "This file has never been saved. Do you want to save it ?";
					} else {
						message = "The file " + file.getAbsolutePath() + "\n"
								+ "has been modified. Do you want to save it ?";
					}
					try {
						FileEditionTextFrame.this.setSelected(true);
					} catch (final PropertyVetoException e1) {
						/* */
					}
					if (UnitexFrame.closing) {
						n = JOptionPane.showOptionDialog(
								FileEditionTextFrame.this, message, "",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null,
								options_on_exit, options_on_exit[0]);
					} else {
						n = JOptionPane
								.showOptionDialog(
										FileEditionTextFrame.this,
										"Text has been modified. Do you want to save it ?",
										"", JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										normal_options, normal_options[0]);
					}
					if (n == JOptionPane.CLOSED_OPTION || n == 2) {
						ConfigManager.getManager().userRefusedClosingFrame();
						return;
					}
					if (n == 0) {
						saveFile(file);
						dispose();
						autoRefresh.stop();
						return;
					}
					if (n != 2) {
						dispose();
						autoRefresh.stop();
						return;
					}
					return;
				}
				dispose();
				autoRefresh.stop();
			}
		});
	}

	private JMenuBar initMenuBar() {
		final JMenuBar jb = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem newFrame = new JMenuItem("New");
		newFrame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileManager.newFile();
			}
		});
		final JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileEditionMenu.openFile();
			}
		});
		final JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		final JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(file);
			}
		});
		final JMenuItem saveAs = new JMenuItem("Save As...");
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(null);
			}
		});
		fileMenu.add(newFrame);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(close);
		final JMenu edit = new JMenu("Edit");
		final JMenuItem cut = new JMenuItem(cutAction);
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		final JMenuItem copy = new JMenuItem(copyAction);
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		edit.addSeparator();
		final JMenuItem paste = new JMenuItem(pasteAction);
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_MASK));
		final JMenuItem find = new JMenuItem(findAction);
		find.setMnemonic('f');
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_MASK));
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(find);
		jb.add(fileMenu);
		jb.add(edit);
		return jb;
	}

	/**
	 * Initialization of the tool bar
	 */
	private JToolBar initToolBar() {
		final JToolBar myToolBar = new JToolBar("file Edition tool bar");
		myToolBar.setMargin(new Insets(0, 0, 0, 0));
		final JButton save = new JButton(saveAction);
		save.setHideActionText(true);
		initToolBarIcone(save);
		final JButton copy = new JButton(copyAction);
		copy.setHideActionText(true);
		initToolBarIcone(copy);
		final JButton cut = new JButton(cutAction);
		cut.setHideActionText(true);
		initToolBarIcone(cut);
		final JButton paste = new JButton(pasteAction);
		paste.setHideActionText(true);
		initToolBarIcone(paste);
		final JButton find = new JButton(findAction);
		find.setHideActionText(true);
		initToolBarIcone(find);
		save.setToolTipText("Save text");
		copy.setToolTipText("Copy");
		cut.setToolTipText("Cut");
		paste.setToolTipText("Paste");
		find.setToolTipText("Find");
		myToolBar.add(save);
		myToolBar.add(copy);
		myToolBar.add(cut);
		myToolBar.add(paste);
		myToolBar.add(find);
		text.setFont(ConfigManager.getManager().getTextFont(null));
		text.setLineWrap(true);
		return myToolBar;
	}

	private void initToolBarIcone(JButton button) {
		button.setMaximumSize(new Dimension(36, 36));
		button.setMinimumSize(new Dimension(36, 36));
		button.setPreferredSize(new Dimension(36, 36));
	}

	/**
	 * Save a file
	 */
	void saveFile(File f) {
		if (f == null) {
			final JFileChooser chooser = new JFileChooser();
			if (file!=null) {
				chooser.setCurrentDirectory(file.getParentFile());
			} else {
				chooser.setCurrentDirectory(ConfigManager.getManager()
						.getCurrentLanguageDir());
			}
			chooser.setMultiSelectionEnabled(false);
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			final int returnVal = chooser.showSaveDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			f = chooser.getSelectedFile();
		}
		this.file = f;
		setTitle(f.getAbsolutePath());
		fileManager.save(f.getAbsolutePath());
		lastModification = file.lastModified();
	}

	public void saveFile() {
		saveFile(file);
	}

	/**
	 * Returns the text.
	 * 
	 * @return MyTextArea
	 */
	public EditionTextArea getText() {
		return text;
	}

	File getFile() {
		return file;
	}

	@Override
	public String getTabName() {
		if (file == null)
			return "(unsaved)";
		return file.getName();
	}
}
