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
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.RegexFormatter;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.PreferencesListener;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;
import org.gramlab.core.umlv.unitex.text.BigTextList;
import org.gramlab.core.umlv.unitex.text.TextAsListModelImpl;

/**
 * This class describes a frame used to display a dictionary.
 * 
 * @author Sébastien Paumier
 */
public class DelaFrame extends KeyedInternalFrame<File> {
	final JPanel middle;
	final BigTextList text = new BigTextList(true);
	final JScrollBar scrollBar;
	File dela;

	DelaFrame() {
		super("", true, true, true, true);
		final JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		final JScrollPane scrollText = new JScrollPane(text,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		middle.add(scrollText);
		top.add(middle, BorderLayout.CENTER);
		top.add(constructFindPanel(), BorderLayout.NORTH);
		setContentPane(top);
		pack();
		setBounds(100, 100, 500, 500);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//TODO : Change the action to Minimize rather than close on pressing Esc	
		KeyUtil.addCloseFrameListener(top);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				Config.setCurrentDELA(null);
				text.reset();
				setVisible(false);
				/*
				 * We wait to avoid blocking the creation of fooflx.dic by
				 * MultiFlex
				 */
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e2) {
					e2.printStackTrace();
				}
				System.gc();
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				if (dela != null) {
					Config.setCurrentDELA(dela);
				}
			}
		});
		final boolean rightToLeftForText = ConfigManager.getManager()
				.isRightToLeftForText(null);
		text.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		scrollBar = scrollText.getHorizontalScrollBar();
		final Timer t = new Timer(400, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollBar.setValue(0);
			}
		});
		t.setRepeats(false);
		t.start();
		scrollText
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				text.setFont(ConfigManager.getManager().getTextFont(null));
				text.setComponentOrientation(ConfigManager.getManager()
						.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
				scrollText
						.setComponentOrientation(ConfigManager.getManager()
								.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
								: ComponentOrientation.LEFT_TO_RIGHT);
				final Timer t2 = new Timer(400, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						scrollBar.setValue(0);
					}
				});
				t2.setRepeats(false);
				t2.start();
			}
		});
	}

	private JPanel constructFindPanel() {
		final JPanel p = new JPanel(new GridBagLayout());
		final JButton find = new JButton("Find");
		final JButton reload = new JButton("Reload");
		find.setEnabled(false);
		final JFormattedTextField pattern = new JFormattedTextField(
				new RegexFormatter());
		pattern.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				try {
					pattern.commitEdit();
					pattern.setForeground(Color.BLACK);
					find.setEnabled(true);
				} catch (final ParseException e2) {
					pattern.setForeground(Color.RED);
					find.setEnabled(false);
				}
			}
		});
		final JButton previous = new JButton("\u25C0");
		previous.setToolTipText("Previous match");
		final JButton next = new JButton("\u25B6");
		next.setToolTipText("Next match");
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 5, 0, 5);
		p.add(pattern, gbc);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 0;
		p.add(find, gbc);
		p.add(reload, gbc);
		p.add(previous, gbc);
		p.add(next, gbc);
		p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		text.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		find.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveToMatchedElement(pattern.getText(), -1, true);
			}
		});
		reload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { loadDela(dela); }
		});
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveToMatchedElement(pattern.getText(),
						text.getSelectedIndex(), true);
			}
		});
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveToMatchedElement(pattern.getText(),
						text.getSelectedIndex(), false);
			}
		});
		return p;
	}

	void moveToMatchedElement(String regex, int currentPosition, boolean forward) {
		/*
		 * We don't use pattern.getValue(), because in order to match lines, we
		 * have to add .* before and after the actual pattern entered by the
		 * user
		 */
		Pattern p1;
		try {
			p1 = Pattern.compile(".*" + regex + ".*");
		} catch (final PatternSyntaxException e2) {
			return;
		}
		final TextAsListModelImpl model = (TextAsListModelImpl) text.getModel();
		int n;
		if (forward)
			n = model.getNextMatchedElement(currentPosition, p1);
		else
			n = model.getPreviousMatchedElement(currentPosition, p1);
		if (n == -1)
			return;
		text.setSelectedIndex(n);
		/*
		 * Now, we want the selected cell to be in the middle of the visible
		 * cells
		 */
		final int visibleIntervalLength = text.getLastVisibleIndex()
				- text.getFirstVisibleIndex();
		int n2;
		if (n > (text.getFirstVisibleIndex() + visibleIntervalLength / 2)) {
			/* If we have to move forward */
			n2 = n + visibleIntervalLength / 2;
			if (n2 >= model.getSize())
				n2 = model.getSize() - 1;
		} else {
			n2 = n - visibleIntervalLength / 2;
			if (n2 < 0)
				n2 = 0;
		}
		text.ensureIndexIsVisible(n2);
	}

	/**
	 * Loads a dictionary.
	 * 
	 * @param dela1
	 *            the dictionary to be loaded
	 */
	public void loadDela(File dela1) {
		final LoadDelaDo toDo = new LoadDelaDo(dela1);
		final Encoding e = Encoding.getEncoding(dela1);
		if (e == null) {
			UnitexProjectManager.search(dela1)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTranscodeOneFileDialog(dela1, toDo);
		} else {
			toDo.toDo(true);
		}
	}

	void loadUnicodeDela(File dela1) {
		this.dela = dela1;
		text.load(dela1);
		Config.setCurrentDELA(dela1);
		text.setFont(ConfigManager.getManager().getTextFont(null));
		setTitle(dela1.getAbsolutePath());
		setVisible(true);
		final Timer t = new Timer(400, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollBar.setValue(0);
			}
		});
		t.setRepeats(false);
		t.start();
		try {
			setIcon(false);
			setSelected(true);
		} catch (final java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	class LoadDelaDo implements ToDo {
		final File dela_;

		LoadDelaDo(File s) {
			dela_ = s;
		}

		@Override
		public void toDo(boolean success) {
			loadUnicodeDela(dela_);
		}
	}

	@Override
	public File getKey() {
		return dela;
	}

	@Override
	public String getTabName() {
		return dela.getName();
	}
}
