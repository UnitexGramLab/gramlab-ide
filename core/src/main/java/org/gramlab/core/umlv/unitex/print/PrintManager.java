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
package fr.umlv.unitex.print;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import fr.umlv.unitex.frames.GraphFrame;
import fr.umlv.unitex.frames.TextAutomatonFrame;

public class PrintManager {
	private static PrinterJob printerJob;
	private static PageFormat pageFormat;

	private static PrinterJob getPrinterJob() {
		if (printerJob == null) {
			printerJob = PrinterJob.getPrinterJob();
		}
		return printerJob;
	}

	private static PageFormat getPageFormat() {
		if (pageFormat == null) {
			pageFormat = getPrinterJob().defaultPage();
		}
		return pageFormat;
	}

	private static void printOneGraph(GraphFrame g) {
		if (g == null) {
			throw new IllegalArgumentException("Cannot print a null graph");
		}
		final PrinterJob job = getPrinterJob();
		if (!job.printDialog())
			return;
		job.setPrintable(g.getGraphicalZone(), getPageFormat());
		try {
			job.print();
		} catch (final PrinterException e) {
			JOptionPane.showMessageDialog(null, "Error while printing graph",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void printAllGraphs(ArrayList<GraphFrame> frames) {
		if (frames.size() == 0)
			return;
		final PrinterJob job = getPrinterJob();
		if (!job.printDialog())
			return;
		for (final GraphFrame g : frames) {
			job.setPrintable(g.getGraphicalZone(), getPageFormat());
			try {
				job.print();
			} catch (final PrinterException e) {
				JOptionPane.showMessageDialog(null,
						"Error while printing graph", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Launch the page setup for printing.
	 */
	public static void pageSetup() {
		final PrinterJob job = getPrinterJob();
		PageFormat format = getPageFormat();
		format = job.pageDialog(format);
	}

	/**
	 * Prints a <code>TextAutomatonFrame</code>.
	 * 
	 * @param g
	 *            the <code>TextAutomatonFrame</code> to be printed.
	 */
	private static void printTextAutomatonFrame(TextAutomatonFrame g) {
		final PrinterJob job = getPrinterJob();
		final PageFormat format = getPageFormat();
		if (!job.printDialog())
			return;
		try {
			job.setPrintable(g.getGraphicalZone(), format);
			job.print();
		} catch (final PrinterException e) {
			JOptionPane.showMessageDialog(null,
					"Error while printing sentence graph", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void print(JInternalFrame f) {
		if (f == null)
			return;
		if (f instanceof GraphFrame) {
			printOneGraph((GraphFrame) f);
			return;
		}
		if (f instanceof TextAutomatonFrame) {
			printTextAutomatonFrame((TextAutomatonFrame) f);
		}
	}
}
