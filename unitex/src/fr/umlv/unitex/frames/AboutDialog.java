package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.Version;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.text.BigTextArea;

/**
 * 
 * @author mdamis
 * @author martinec
 *
 */
public class AboutDialog extends JDialog {
  public AboutDialog(JFrame owner, String title, ImageIcon logo, String fileName, File disclaimersDir, File licensesDir) {
    super(owner, "About " + "Unitex/GramLab");

    final JPanel top = new JPanel(new BorderLayout());
    final JPanel up = new JPanel(new BorderLayout());
    final JPanel image = new JPanel(new BorderLayout());
    image.setBorder(new EmptyBorder(4, 3, 1, 1));
    image.add(new JLabel(logo));
    up.add(image, BorderLayout.WEST);
    final JPanel info = new JPanel(new BorderLayout());
    info.setBorder(new TitledBorder("Disclaimer"));
    
    JTextField versionInfo = new JTextField();

    String unitexToolLoggerSemVer = "?";

    // try to get the result of run 'UnitexToolLogger VersionInfo -s'
    try {
      unitexToolLoggerSemVer = Config.getUnitexToolLoggerSemVer(); 
    } catch (final Exception e) {
      // do nothing  
    }
    
    versionInfo = new JTextField(Version.VERSION_TITLE   + ": "       +
                                 Version.VERSION_SEMVER  + " | "      +
                                 "UnitexToolLogger"      + ": "       +
                                 unitexToolLoggerSemVer);

    versionInfo.setEditable(false);
    up.add(versionInfo, BorderLayout.NORTH);
        
    BigTextArea disclaimerContent = new BigTextArea();
    if(disclaimersDir.exists() && disclaimersDir.isDirectory()) {
      File disclaimer = new File(disclaimersDir, fileName);
      if(disclaimer.exists()) {
        disclaimerContent = new BigTextArea(disclaimer);
        int disclaimerHeight = image.getHeight() > 150 ? image.getHeight() : 150;
        disclaimerContent.setPreferredSize(new Dimension(400, disclaimerHeight));
      }
    }
    info.add(disclaimerContent, BorderLayout.CENTER);
    
    up.add(info, BorderLayout.CENTER);
    top.add(up, BorderLayout.NORTH);
    
    final JTabbedPane licensesPanel = new JTabbedPane();
    
    if(licensesDir.exists() && licensesDir.isDirectory()) {
      File[] licenses = licensesDir.listFiles();
      for(File license : licenses) {
        if(license.getName().contains(".txt")) {
          String[] licenseName = license.getName().split(".txt");
          BigTextArea licenseContent = new BigTextArea(new File(licensesDir, license.getName()));
          licensesPanel.add(licenseContent, licenseName[0]);
        }
      }
    }
    
    top.add(licensesPanel, BorderLayout.CENTER);
    setContentPane(top);
    licensesPanel.setPreferredSize(new Dimension(500, 300));
    pack();
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    setLocationRelativeTo(UnitexFrame.mainFrame);
    setVisible(true);
  }
}
