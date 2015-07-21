package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import fr.gramlab.icons.Icons;
import fr.umlv.unitex.Version;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.text.BigTextArea;

/**
 * This class defines a dialog box that contains the Gramlab logo, the body of the
 * license and some informations about Gramlab.
 * 
 * @author Sébastien Paumier
 * 
 */
@SuppressWarnings("serial")
public class AboutGramlabDialog extends JDialog {
	
	public AboutGramlabDialog(JFrame parent) {
		super(parent,"About Gramlab",false);
		final JPanel top = new JPanel(new BorderLayout());
		File appDir=ConfigManager.getManager().getApplicationDirectory();
		final BigTextArea licenseLGPL = new BigTextArea(new File(appDir, "LGPL.txt"));
		final BigTextArea licenseLGPLLR = new BigTextArea(new File(appDir, "LGPLLR.txt"));
		final BigTextArea apache = new BigTextArea(new File(appDir, "Apache-1.1.txt"));
		final BigTextArea bsd = new BigTextArea(new File(appDir, "BSD_tre.txt"));
		final BigTextArea disclaimerUnitex = new BigTextArea(new File(appDir, "Disclaimer.txt"));
		final BigTextArea disclaimerGramlab = new BigTextArea(new File(appDir, "Disclaimer-Gramlab.txt"));
		final JPanel up = new JPanel(new BorderLayout());
		final JPanel image = new JPanel(new BorderLayout());
		image.setBorder(new EmptyBorder(4, 3, 1, 1));
		image.add(new JLabel(Icons.logo));
		up.add(image, BorderLayout.WEST);
		final JTextField revision = new JTextField("  Revision: "
				+ Version.getRevisionNumberForGramlab() + " (Gramlab) "
				+ Version.getRevisionNumberForJava() + " (Unitex Java), "
				+ Version.getRevisionNumberForC() + " (Unitex C/C++) "
				+ ", revision date: " + Version.getRevisionDate());
		revision.setEditable(false);
		up.add(revision, BorderLayout.NORTH);
		top.add(up, BorderLayout.NORTH);
		final JTabbedPane licenses = new JTabbedPane();
		licenses.add(disclaimerGramlab, "Gramlab");
		licenses.add(disclaimerUnitex, "Unitex");
		licenses.add(licenseLGPL, "LGPL");
		licenses.add(licenseLGPLLR, "LGPLLR");
		licenses.add(apache, "Apache");
		licenses.add(bsd, "TRE's BSD");
		top.add(licenses, BorderLayout.CENTER);
		setContentPane(top);
		licenses.setPreferredSize(new Dimension(500, 300));
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
