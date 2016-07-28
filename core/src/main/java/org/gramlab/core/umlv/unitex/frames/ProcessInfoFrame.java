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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

/**
 * This class describes a frame used to execute shell commands and display
 * stdout and stderr messages produced by these processes.
 * 
 * @author Sébastien Paumier
 */
public class ProcessInfoFrame extends JInternalFrame {
	final ProcessOutputList stdoutList = new ProcessOutputList(
			new ProcessOutputListModel());
	final ProcessOutputList stderrList = new ProcessOutputList(
			new ProcessOutputListModel());
	public final static Color systemColor = new Color(0xF0, 0xCB, 0xAA);
	final static DefaultListCellRenderer myRenderer = new DefaultListCellRenderer() {
		@Override
		public Component getListCellRendererComponent(JList l, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			final Couple c = (Couple) value;
			setBackground(c.isSystemMessage() ? systemColor : Color.WHITE);
			setForeground(l.getForeground());
			setText(c.getString());
			return this;
		}
	};
	final boolean close_on_finish;
	final JButton ok;
	final JButton cancel;
	ExecParameters parameters;
	Executor executor;

	/**
	 * Creates a new <code>ProcessInfoFrame</code>
	 * 
	 * @param c
	 *            array containing the shell commands to run
	 * @param close
	 *            indicates if the frame must be closed after the completion of
	 *            all commands
	 * @param myDo
	 *            object describing actions to do after the completion of all
	 *            commands, unless a command goes wrong
	 * @param stopIfProblem
	 *            indicates if the failure of a command must stop all commands
	 */
	ProcessInfoFrame(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem) {
		super("Working...", true, false, false);
		close_on_finish = close;
		final JPanel top = new JPanel(new BorderLayout());
		parameters = new ExecParameters(stopIfProblem, c, stdoutList,
				stderrList, myDo, true, null);
		stdoutList.setCellRenderer(myRenderer);
		stderrList.setCellRenderer(myRenderer);
		final JScrollPane scroll = new JScrollPane(stdoutList);
		stderrList.setForeground(Color.RED);
		final JScrollPane errorScroll = new JScrollPane(stderrList);
		final JPanel tmp = new JPanel(new BorderLayout());
		tmp.setBorder(BorderFactory.createLoweredBevelBorder());
		tmp.add(scroll, BorderLayout.CENTER);
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.setBorder(BorderFactory.createLoweredBevelBorder());
		tmp2.add(errorScroll, BorderLayout.CENTER);
		final JSplitPane middle = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				tmp, tmp2);
		middle.setDividerLocation(250);
		top.add(middle, BorderLayout.CENTER);
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!executor.hasFinished()) {
					throw new IllegalStateException(
							"The OK button should not be enabled");
				}
				dispose();
				if (parameters.getDO() != null) {
					parameters.getDO().toDo(executor.getSuccess());
				}
			}
		};
		ok = new JButton(okAction);
		ok.setEnabled(false);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				executor.interrupt();
				dispose();
			}
		};
		cancel = new JButton(cancelAction);
		final JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(ok);
		buttons.add(cancel);
		top.add(buttons, BorderLayout.SOUTH);
		final JLabel info = new JLabel(
				"Messages with a colored background are generated by the interface, not by the external programs.");
		info.setBackground(systemColor);
		info.setOpaque(true);
		top.add(info, BorderLayout.NORTH);
		setContentPane(top);
		pack();
		setBounds(100, 100, 600, 400);
	}

	void launchBuilderCommands() {
		final ToDo originalToDo = parameters.getDO();
		final ToDo myDo = new ToDo() {
			@Override
			public void toDo(final boolean success) {
				/*
				 * This is the code to be executed after all commands are
				 * executed
				 */
				final boolean cantCloseBecauseOfErrMessages = (stderrList
						.getModel().size() > 0);
				final boolean PB = !success, CL = close_on_finish;
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							ToDo DO = originalToDo;
							if (PB) {
								setTitle("ERROR");
								DO = null;
							} else {
								setTitle("Done");
							}
							if (!cantCloseBecauseOfErrMessages && !PB && CL) {
								dispose();
								if (DO != null) {
									DO.toDo(success);
								}
							}
						}
					});
				} catch (final InterruptedException e) {
					/* Nothing to do */
				} catch (final InvocationTargetException e) {
					e.printStackTrace();
				}
				ok.setEnabled(true);
				cancel.setEnabled(false);
			}
		};
		/* We create new parameters with our custom todo */
		executor = new Executor(new ExecParameters(
				parameters.isStopOnProblem(), parameters.getCommands(),
				parameters.getStdout(), parameters.getStderr(), myDo,
				parameters.isTraceIntoConsole(), null));
		executor.start();
	}
}
