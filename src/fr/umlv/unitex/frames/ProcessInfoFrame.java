/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.console.Console;
import fr.umlv.unitex.console.ConsoleEntry;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import fr.umlv.unitex.process.Log;
import fr.umlv.unitex.process.ProcessInfoThread;
import fr.umlv.unitex.process.ProcessOutputListModel;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.AbstractMethodCommand;
import fr.umlv.unitex.process.commands.CommandBuilder;
import fr.umlv.unitex.process.commands.GrfDiffCommand;
import fr.umlv.unitex.process.commands.MessageCommand;
import fr.umlv.unitex.process.commands.MultiCommands;

/**
 * This class describes a frame used to execute shell commands and display
 * stdout and stderr messages produced by these processes.
 * 
 * @author Sébastien Paumier
 */
public class ProcessInfoFrame extends JInternalFrame {
	private Process p;
	private final ProcessOutputListModel stdoutModel = new ProcessOutputListModel();
	private final ProcessOutputListModel stderrModel = new ProcessOutputListModel();
	private final JList stdoutList = new JList(stdoutModel);
	private final JList stderrList = new JList(stderrModel);
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
	private final boolean close_on_finish;
	private final boolean stop_if_problem;
	private MultiCommands commands = null;
	ToDo DO;
	private final JButton ok;
	private final JButton cancel;

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
	 *            commands
	 * @param stopIfProblem
	 *            indicates if the failure of a command must stop all commands
	 */
	ProcessInfoFrame(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem) {
		super("Working...", true, false, false);
		commands = c;
		close_on_finish = close;
		stop_if_problem = stopIfProblem;
		DO = myDo;
		final JPanel top = new JPanel(new BorderLayout());
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
			public void actionPerformed(ActionEvent arg0) {
				if (p != null) {
					try {
						p.exitValue();
					} catch (final IllegalThreadStateException ex) {
						return;
					}
				}
				dispose();
				if (DO != null) {
					DO.toDo();
				}
			}
		};
		ok = new JButton(okAction);
		ok.setEnabled(false);
		final Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				if (p != null) {
					p.destroy();
				}
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
		if (commands == null)
			return;
		final Thread thread = new Thread() {
			@Override
			public void run() {
				boolean problem = false;
				CommandBuilder command;
				for (int i = 0; (!problem) && i < commands.numberOfCommands(); i++)
					if ((command = commands.getCommand(i)) != null) {
						switch (command.getType()) {
						case CommandBuilder.MESSAGE: {
							try {
								final CommandBuilder c = command;
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										stdoutModel.addElement(new Couple(
												((MessageCommand) c)
														.getMessage(), true));
									}
								});
							} catch (final InterruptedException e1) {
								e1.printStackTrace();
							} catch (final InvocationTargetException e1) {
								e1.printStackTrace();
							}
							break;
						}
						case CommandBuilder.ERROR_MESSAGE: {
							final ConsoleEntry entry = Console
									.addCommand(
											"Error message emitted by the graphical interface",
											true, null);
							try {
								final CommandBuilder c = command;
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										final String message = ((MessageCommand) c)
												.getMessage();
										stderrModel.addElement(new Couple(
												message, true));
										entry.addErrorMessage(message);
									}
								});
							} catch (final InterruptedException e1) {
								e1.printStackTrace();
							} catch (final InvocationTargetException e1) {
								e1.printStackTrace();
							}
							break;
						}
						case CommandBuilder.PROGRAM: {
							final ConsoleEntry entry = Console.addCommand(
									command.getCommandLine(), false, Log
											.getCurrentLogID());
							final String[] comm = command.getCommandArguments();
							try {
								p = Runtime.getRuntime().exec(comm);
								new ProcessInfoThread(stdoutList, p
										.getInputStream(), false,
										ProcessInfoFrame.this, true, null)
										.start();
								new ProcessInfoThread(stderrList, p
										.getErrorStream(), false,
										ProcessInfoFrame.this, true, entry)
										.start();
								try {
									p.waitFor();
								} catch (final java.lang.InterruptedException e) {
									if (stop_if_problem) {
										try {
											SwingUtilities
													.invokeAndWait(new Runnable() {
														public void run() {
															stderrModel
																	.addElement(new Couple(
																			"The program "
																					+ comm[0]
																					+ " has been interrupted\n",
																			true));
															stderrList
																	.ensureIndexIsVisible(stderrModel
																			.getSize() - 1);
														}
													});
										} catch (final InterruptedException e1) {
											e1.printStackTrace();
										} catch (final InvocationTargetException e1) {
											e1.printStackTrace();
										}
										problem = true;
									}
								}
							} catch (final java.io.IOException e) {
								final String programName = comm[0];
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										stderrModel.addElement(new Couple(
												"Cannot launch the program "
														+ programName + "\n",
												true));
										stderrList
												.ensureIndexIsVisible(stderrModel
														.getSize() - 1);
									}
								});
								if (stop_if_problem) {
									problem = true;
								}
							}
							try {
								if (p == null) {
									if (stop_if_problem) {
										problem = true;
									}
								} else {
									if (command instanceof GrfDiffCommand) {
										/*
										 * GrfDiff has a special return value
										 * meaning
										 */
										if (stop_if_problem
												&& p.exitValue() == 2) {
											problem = true;
										}
									} else if (p.exitValue() != 0) {
										if (stop_if_problem) {
											problem = true;
										}
									}
								}
							} catch (final IllegalThreadStateException e) {
								p.destroy();
							}
							break;
						}// end of program command
						case CommandBuilder.METHOD: {
							Console.addCommand(command.getCommandLine(), false,
									null);
							final AbstractMethodCommand cmd = (AbstractMethodCommand) command;
							if (!cmd.execute()) {
								if (stop_if_problem) {
									try {
										final CommandBuilder c = command;
										SwingUtilities
												.invokeAndWait(new Runnable() {
													public void run() {
														stderrModel
																.addElement(new Couple(
																		"Command failed: "
																				+ c
																						.getCommandLine(),
																		true));
													}
												});
									} catch (final InterruptedException e1) {
										e1.printStackTrace();
									} catch (final InvocationTargetException e1) {
										e1.printStackTrace();
									}
									problem = true;
								}
							}
							break;
						}// end of method command
						} // end of switch
					} // end of if ((command = commands.getCommand(i)) != null)
				final boolean cantCloseBecauseOfErrMessages = (stderrModel
						.size() > 0);
				final boolean PB = problem, CL = close_on_finish;
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							if (PB) {
								setTitle("ERROR");
								DO = null;
							} else {
								setTitle("Done");
							}
							if (!cantCloseBecauseOfErrMessages && !PB && CL) {
								dispose();
								if (DO != null) {
									DO.toDo();
								}
							}
						}
					});
				} catch (final InterruptedException e) {
					e.printStackTrace();
				} catch (final InvocationTargetException e) {
					e.printStackTrace();
				}
				ok.setEnabled(true);
				cancel.setEnabled(false);
			}
		};
		thread.setUncaughtExceptionHandler(UnitexUncaughtExceptionHandler
				.getHandler());
		thread.start();
	}
}
