package fr.umlv.unitex.utils;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.umlv.unitex.config.Config;

/**
 *
 * @author mdamis
 *
 * @source http://stackoverflow.com/a/18509384 for opening a link in the default
 *         browser.
 */
public class HelpMenuBuilder {

  /**
   * Opens an URI using desktop.browse() or xdg-open as fallback
   * @author martinec
   */
  static void openUri(String uri) {
    if (Desktop.isDesktopSupported() &&
        Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      Desktop desktop = Desktop.getDesktop();
      try {
        desktop.browse(new URI(uri));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (Config.getSystem() != Config.WINDOWS_SYSTEM) {
        try {
          String openCommand = Config.getSystem() == Config.MAC_OS_X_SYSTEM ?
                               "open" : "xdg-open";
          final Process p = Runtime.getRuntime().exec(new String[] { openCommand, uri });
          try {
            p.waitFor();
          } catch (final java.lang.InterruptedException e) {
            e.printStackTrace();
          }
        } catch (final java.io.IOException e) {
        }
    }
  }

  public static JMenu build(File appDir) {
    JMenu helpMenu = new JMenu("Help");

    JMenu manuals = buildManualsMenu(appDir);
    if (manuals.getItemCount() > 0) {
      helpMenu.add(manuals);
      helpMenu.addSeparator();
    }

    helpMenu.add(buildWebsiteMenuItem());
    helpMenu.add(buildForumMenuItem());

    return helpMenu;
  }

  static JMenu buildManualsMenu(File appDir) {
    JMenu manuals = new JMenu("Manuals");

    File manualsDir = new File(appDir.getPath() + File.separatorChar
        + "manual");

    if (manualsDir.exists() && manualsDir.isDirectory()) {
      for (File manualDirContent : manualsDir.listFiles()) {
        if (manualDirContent.isDirectory()) {
          for (final File manual : manualDirContent.listFiles()) {
            if (manual.getName().contains(".pdf")) {
              JMenuItem manualAction = new JMenuItem(
                  manualDirContent.getName());
                  manualAction
                  .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      openUri(manual.toURI().toString());
                    }
                  });
              manuals.add(manualAction);
            }
          }
        }
      }
    }
    return manuals;
  }

  static JMenuItem buildWebsiteMenuItem() {
    JMenuItem website = new JMenuItem("Website");
    website.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openUri("https://unitexgramlab.org");
      }
    });
    return website;
  }

  static JMenuItem buildForumMenuItem() {
    JMenuItem forum = new JMenuItem("Forum");
    forum.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openUri("http://forum.unitexgramlab.org");
      }
    });
    return forum;
  }
}
