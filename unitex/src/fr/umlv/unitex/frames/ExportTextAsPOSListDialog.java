/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.Tfst2GrfCommand;
import fr.umlv.unitex.tfst.TagFilter;
import fr.umlv.unitex.tfst.TfstTableModel;
import fr.umlv.unitex.tfst.TokenTags;
import fr.umlv.unitex.tfst.TokensInfo;
import fr.umlv.unitex.utils.KeyUtil;

public class ExportTextAsPOSListDialog extends JDialog {
	File output;
	boolean canceled = false;
	final JProgressBar progress = new JProgressBar();
	TagFilter filter;
	boolean delafStyle;

	/**
	 * Creates a new font dialog box.
	 * 
	 * @param in
	 *            indicates if we select an input or an output font for graphs.
	 */
	public ExportTextAsPOSListDialog(File output, TagFilter filter,
			boolean delafStyle) {
		super(UnitexFrame.mainFrame, "Export text as POS list", true);
		configure(output, filter, delafStyle);
		setContentPane(constructPanel());
		progress.setPreferredSize(new Dimension(300, 30));
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
			}
		});
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}

	private JPanel constructPanel() {
		final JPanel p = new JPanel();
		p.add(progress);
		KeyUtil.addCloseDialogListener(p);
		return p;
	}

	void configure(File o, TagFilter f, boolean delafStyle1) {
		if (o == null) {
			throw new IllegalArgumentException(
					"Cannot configure a null output file");
		}
		this.output = o;
		this.filter = f;
		this.delafStyle = delafStyle1;
	}

	public void launch() {
		canceled = false;
		progress.setMinimum(0);
		final int sentenceCount = GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.getTextAutomatonFrame().getSentenceCount();
		progress.setMaximum(sentenceCount);
		progress.setValue(0);
		progress.setStringPainted(true);
		final ExportTextAsPOSListDialog dialog = this;
		final File sntDir = Config.getCurrentSntDir();
		final Encoding encoding = ConfigManager.getManager().getEncoding(null);
		new Thread(new Runnable() {
			@Override
			public void run() {
				TokensInfo.save();
				final File tfst = new File(sntDir, "text.tfst");
				final File tmpGrf = new File(sntDir, "foo.grf");
				final File sentenceText = new File(sntDir, "foo.txt");
				final File sentenceTok = new File(sntDir, "foo.tok");
				final TfstTableModel model = new TfstTableModel(filter,
						delafStyle);
				final ArrayList<Integer> errorList = new ArrayList<>();
				try {
					final OutputStreamWriter writer = encoding
							.getOutputStreamWriter(output);
					for (int i = 1; i <= sentenceCount; i++) {
						if (canceled) {
							break;
						}
						final Tfst2GrfCommand cmd = new Tfst2GrfCommand()
								.automaton(tfst).sentence(i).output("foo");
						Launcher.execWithoutTracing(cmd);
						final String text = readSentenceText(sentenceText);
						TokensInfo.loadTokensInfo(sentenceTok, text);
						final GraphIO g = GraphIO.loadGraph(tmpGrf, true, true);
						if (g == null) {
							errorList.add(i);
							continue;
						}
						model.init(g.getBoxes());
						if (model.getRowCount() == 0) {
							/*
							 * If the sentence automaton has been emptied, we
							 * generate the token list, excluding spaces
							 */
							for (int j = 0; j < TokensInfo.getTokenCount(); j++) {
								final String s = TokensInfo.getTokenAsString(j);
								UnicodeIO.writeString(writer, s);
							}
						} else
							for (int j = 0; j < model.getRowCount(); j++) {
								final TokenTags t = model.getTokenTags(j);
								UnicodeIO.writeString(writer, t.toString()
									+ " ");
							}
						/* And we add a sentence delimiter */
						UnicodeIO.writeString(writer, "{S}\n");
						final int z = i;
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								progress.setValue(z);
								progress.setString(z + "/" + sentenceCount);
							}
						});
					}
					writer.close();
				} catch (final IOException e1) {
					/* nop */
				} finally {
					TokensInfo.restore();
					tmpGrf.delete();
					sentenceText.delete();
					sentenceTok.delete();
					try {
						/*
						 * We have to wait a little bit because if the text is
						 * too small, we may have finished before
						 * setVisible(true) is invoked. In that case, the
						 * JDialog made visible could never become invisible
						 * again, blocking the user
						 */
						Thread.sleep(100);
					} catch (final InterruptedException e2) {
						e2.printStackTrace();
					}
					try {
						EventQueue.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
							}
						});
					} catch (final InterruptedException e) {
						e.printStackTrace();
					} catch (final InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				
				if (!errorList.isEmpty()) {
					StringBuilder sb = new StringBuilder("The following sentence(s) couldn't "
							+ "be exported: \n");
					for(int j = 0; j < errorList.size(); j++) {
						sb.append("Sentence #").append(errorList.get(j)).append("\n");
					}
					JOptionPane.showMessageDialog(null,
							sb.toString(), "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}).start();
	}

	String readSentenceText(File f) {
		String s = "";
		try {
			final InputStreamReader reader = Encoding.getInputStreamReader(f);
			if (reader == null)
				return null;
			s = UnicodeIO.readLine(reader);
			if (s == null || s.equals("")) {
				return "";
			}
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return s;
	}
}
