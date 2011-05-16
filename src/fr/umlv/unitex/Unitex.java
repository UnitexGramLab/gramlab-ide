/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.OceanTheme;

import fr.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import fr.umlv.unitex.frames.SplashScreen;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.svn.SvnMonitor;

/**
 * This is the main class of the Unitex system.
 *
 * @author Sébastien Paumier
 */

public class Unitex {

    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                launchUnitex(args);
            }
        });
    }

    /**
     * Starts Unitex. Shows a <code>SplashScreen</code> with the Unitex logo and
     * then creates a <code>UnitexFrame</code>.
     */
    private static void launchUnitex(final String[] args) {
        Thread.currentThread().setUncaughtExceptionHandler(UnitexUncaughtExceptionHandler.getHandler());
        Locale.setDefault(Locale.ENGLISH);
        try {
            javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new OceanTheme());
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Ocean Theme not supported on this platform. \nProgram Terminated");
            System.exit(0);
        } catch (IllegalAccessException e) {
            System.err.println("Ocean Theme could not be accessed. \nProgram Terminated");
            System.exit(0);
        } catch (ClassNotFoundException e) {
            System.err.println("Your version of Java does not contain all the classes required by Unitex.\nProgram Terminated");
            System.exit(0);
        } catch (InstantiationException e) {
            System.err.println("Ocean Theme can not be instantiated. \nProgram Terminated");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Unexpected error. \nProgram Terminated");
            e.printStackTrace();
            System.exit(0);
        }

        final SplashScreen splash = new SplashScreen(new ImageIcon(Unitex.class.getResource("Unitex.jpg")));
        splash.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                final Timer timer = new Timer(1500, null);
                timer.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e1) {
                        splash.dispose();
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                Config.initConfig(args.length == 1 ? args[0] : null);
                                JFrame frame = new UnitexFrame();
                                Image img16x16 = new ImageIcon(Unitex.class.getResource("16x16.png")).getImage();
                                Image img32x32 = new ImageIcon(Unitex.class.getResource("32x32.png")).getImage();
                                Image img48x48 = new ImageIcon(Unitex.class.getResource("48x48.png")).getImage();
                                frame.setIconImages(Arrays.asList(img16x16, img32x32, img48x48));
                                frame.setVisible(true);
                                SvnMonitor.start();
                            }
                        });
                        timer.stop();
                    }
                });
                timer.start();
            }
        });
        splash.setVisible(true);
    }

}
