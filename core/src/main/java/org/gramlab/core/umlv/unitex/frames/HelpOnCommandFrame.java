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
import java.awt.Component;
import java.io.IOException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gramlab.core.umlv.unitex.process.ProcessInfoThread;
import org.gramlab.core.umlv.unitex.process.commands.BuildKrMwuDicCommand;
import org.gramlab.core.umlv.unitex.process.commands.CassysCommand;
import org.gramlab.core.umlv.unitex.process.commands.CheckDicCommand;
import org.gramlab.core.umlv.unitex.process.commands.CommandBuilder;
import org.gramlab.core.umlv.unitex.process.commands.CompressCommand;
import org.gramlab.core.umlv.unitex.process.commands.ConcorDiffCommand;
import org.gramlab.core.umlv.unitex.process.commands.ConcordCommand;
import org.gramlab.core.umlv.unitex.process.commands.ConvertCommand;
import org.gramlab.core.umlv.unitex.process.commands.DicoCommand;
import org.gramlab.core.umlv.unitex.process.commands.ElagCommand;
import org.gramlab.core.umlv.unitex.process.commands.ElagCompCommand;
import org.gramlab.core.umlv.unitex.process.commands.EvambCommand;
import org.gramlab.core.umlv.unitex.process.commands.ExtractCommand;
import org.gramlab.core.umlv.unitex.process.commands.FlattenCommand;
import org.gramlab.core.umlv.unitex.process.commands.Fst2CheckCommand;
import org.gramlab.core.umlv.unitex.process.commands.Fst2ListCommand;
import org.gramlab.core.umlv.unitex.process.commands.Fst2TxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.GrfDiff3Command;
import org.gramlab.core.umlv.unitex.process.commands.GrfDiffCommand;
import org.gramlab.core.umlv.unitex.process.commands.ImplodeTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.LocateCommand;
import org.gramlab.core.umlv.unitex.process.commands.LocateTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiFlexCommand;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.PolyLexCommand;
import org.gramlab.core.umlv.unitex.process.commands.RebuildTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.ReconstrucaoCommand;
import org.gramlab.core.umlv.unitex.process.commands.Reg2GrfCommand;
import org.gramlab.core.umlv.unitex.process.commands.Seq2GrfCommand;
import org.gramlab.core.umlv.unitex.process.commands.SortTxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.StatsCommand;
import org.gramlab.core.umlv.unitex.process.commands.TEI2TxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.Table2GrfCommand;
import org.gramlab.core.umlv.unitex.process.commands.TaggerCommand;
import org.gramlab.core.umlv.unitex.process.commands.TagsetNormTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.Tfst2GrfCommand;
import org.gramlab.core.umlv.unitex.process.commands.Tfst2UnambigCommand;
import org.gramlab.core.umlv.unitex.process.commands.TokenizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.TrainingTaggerCommand;
import org.gramlab.core.umlv.unitex.process.commands.Txt2TfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.UncompressCommand;
import org.gramlab.core.umlv.unitex.process.commands.UntokenizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.UnxmlizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.XAlignCommand;
import org.gramlab.core.umlv.unitex.process.commands.XMLizerCommand;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

public class HelpOnCommandFrame extends JInternalFrame {
	private final Class<?>[] commands = { BuildKrMwuDicCommand.class, CassysCommand.class,
			CheckDicCommand.class, CompressCommand.class, ConcordCommand.class,
			ConcorDiffCommand.class, ConvertCommand.class, DicoCommand.class,
			ElagCommand.class, ElagCompCommand.class, EvambCommand.class,
			ExtractCommand.class, FlattenCommand.class, Fst2CheckCommand.class,
			Fst2ListCommand.class, Fst2TxtCommand.class, Grf2Fst2Command.class,
			GrfDiffCommand.class, GrfDiff3Command.class,
			ImplodeTfstCommand.class, LocateCommand.class,
			LocateTfstCommand.class
			/*
			 * This is normal that MkdirCommand is not in this list, since it's
			 * not a Unitex command
			 */
			, MultiFlexCommand.class, NormalizeCommand.class,
			PolyLexCommand.class, RebuildTfstCommand.class,
			ReconstrucaoCommand.class, Reg2GrfCommand.class,
			Seq2GrfCommand.class, SortTxtCommand.class, StatsCommand.class,
			Table2GrfCommand.class, TaggerCommand.class,
			TagsetNormTfstCommand.class, TEI2TxtCommand.class,
			Tfst2GrfCommand.class, Tfst2UnambigCommand.class,
			TokenizeCommand.class, TrainingTaggerCommand.class,
			Txt2TfstCommand.class, UncompressCommand.class,
			UntokenizeCommand.class, UnxmlizeCommand.class,
			XAlignCommand.class, XMLizerCommand.class };
	boolean refreshLock = false;

	HelpOnCommandFrame() {
		super("Help on commands", true, true, true, true);
		final JPanel top = new JPanel(new BorderLayout());
		final JList list = new JList(commands);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList l,
					Object value, int index, boolean zz, boolean cellHasFocus) {
				final Class<?> c = (Class<?>) value;
				final String name = c.getSimpleName().substring(0,
						c.getSimpleName().lastIndexOf("Command"));
				return super.getListCellRendererComponent(l, name, index, zz,
						cellHasFocus);
			}
		});
		final ProcessOutputList stdoutList = new ProcessOutputList(
				new ProcessOutputListModel());
		stdoutList.setCellRenderer(ProcessInfoFrame.myRenderer);
		top.add(new JScrollPane(stdoutList));
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (refreshLock || e.getValueIsAdjusting()) {
					return;
				}
				refreshLock = true;
				try {
					final Class<?> c = (Class<?>) list.getSelectedValue();
					if (c == null)
						return;
					final CommandBuilder command = (CommandBuilder) c
							.newInstance();
					stdoutList.empty();
					final String[] comm = command.getCommandArguments(false);
					final Process p = Runtime.getRuntime().exec(comm);
					new ProcessInfoThread(stdoutList, p.getInputStream(), null,
							false).start();
					try {
						p.waitFor();
					} catch (final java.lang.InterruptedException e1) {
						e1.printStackTrace();
					}
				} catch (final InstantiationException e1) {
					e1.printStackTrace();
				} catch (final IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (final IOException e1) {
					e1.printStackTrace();
				} finally {
					refreshLock = false;
				}
			}
		});
		top.add(new JScrollPane(list), BorderLayout.WEST);
		setContentPane(top);
		setSize(600, 400);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
