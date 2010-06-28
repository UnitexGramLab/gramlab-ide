/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.process.ProcessInfoThread;
import fr.umlv.unitex.process.ProcessOutputListModel;
import fr.umlv.unitex.process.commands.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;

public class HelpOnCommandFrame extends JInternalFrame {

    @SuppressWarnings("unchecked")
    final
    Class[] commands = {
    		BuildKrMwuDicCommand.class
        	, CheckDicCommand.class
            , CompressCommand.class
            , ConcordCommand.class
            , ConcorDiffCommand.class
            , ConvertCommand.class
            , DicoCommand.class
            , ElagCommand.class
            , ElagCompCommand.class
            , EvambCommand.class
            , ExtractCommand.class
            , FlattenCommand.class
            , Fst2CheckCommand.class
            , Fst2ListCommand.class
            , Fst2TxtCommand.class
            , Grf2Fst2Command.class
            , ImplodeTfstCommand.class
            , LocateCommand.class
            , LocateTfstCommand.class
            /* This is normal that MkdirCommand is not in this list,
             * since it's not a Unitex command */
            , MultiFlexCommand.class
            , NormalizeCommand.class
            , PolyLexCommand.class
            , RebuildTfstCommand.class
            , ReconstrucaoCommand.class
            , Reg2GrfCommand.class
            , SortTxtCommand.class
            , StatsCommand.class
            , Table2GrfCommand.class
            , TaggerCommand.class
            , TagsetNormTfstCommand.class
            , TEI2TxtCommand.class
            , Tfst2GrfCommand.class
            , Tfst2UnambigCommand.class
            , TokenizeCommand.class
            , Txt2TfstCommand.class
            , UncompressCommand.class
            , XAlignCommand.class
            , XMLizerCommand.class
    };

    boolean refreshLock = false;

    HelpOnCommandFrame() {
        super("Help on commands", true, true, true, true);
        JPanel top = new JPanel(new BorderLayout());
        final JList list = new JList(commands);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @SuppressWarnings("unchecked")
            @Override
            public Component getListCellRendererComponent(JList l, Object value, int index, boolean zz, boolean cellHasFocus) {
                Class c = (Class) value;
                String name = c.getSimpleName().substring(0, c.getSimpleName().lastIndexOf("Command"));
                return super.getListCellRendererComponent(l, name, index, zz, cellHasFocus);
            }
        });
        final ProcessOutputListModel stdout = new ProcessOutputListModel();
        final JList stdoutList = new JList(stdout);
        stdoutList.setCellRenderer(ProcessInfoFrame.myRenderer);
        top.add(new JScrollPane(stdoutList));

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (refreshLock || e.getValueIsAdjusting()) {
                    return;
                }
                refreshLock = true;
                try {
                    Class<?> c = (Class<?>) list.getSelectedValue();
                    if (c == null) return;
                    CommandBuilder command = (CommandBuilder) c.newInstance();
                    stdout.removeAllElements();
                    final String[] comm = command.getCommandArguments();
                    Process p = Runtime.getRuntime().exec(comm);
                    new ProcessInfoThread(stdoutList, p
                            .getInputStream(), false, null, false, null).start();
                    try {
                        p.waitFor();
                    } catch (java.lang.InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                finally {
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