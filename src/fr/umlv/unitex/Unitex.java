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

package fr.umlv.unitex;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.metal.*;

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
     * 
     */
    public static void launchUnitex(final String[] args) {
        Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable e) {
                Box b = new Box(BoxLayout.Y_AXIS);
                b.add(new JLabel(e.toString()));
                String s=e.toString()+"\n";
                for (StackTraceElement elem : e.getStackTrace()) {
                    b.add(new JLabel("at "+elem.toString()));
                    s=s+"at "+elem.toString()+"\n";
                }
                b.add(new JLabel(" "));
                b.add(new JLabel("Do you want to report this problem to Unitex developers ?"));
                if (JOptionPane.showConfirmDialog(null, b, "Java Exception",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==0) {
                    try {
                        Desktop.getDesktop().mail(new URI("mailto","unitex@univ-mlv.fr?subject=Java Exception"
                                +"&body="
                                +"Revision date: "+Version.getRevisionDate()+"\n"
                                +"Java revision Number: "+Version.getRevisionNumberForJava()+"\n"
                                +"C/C++ revision Number: "+Version.getRevisionNumberForC()+"\n"
                                +"System: "+Config.getCurrentSystemName()+"\n"
                                +"\n"
                                +s,null));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
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
        new SplashScreen(new ImageIcon(Unitex.class.getResource("Unitex.jpg")), 1500);
        Config.initConfig(args.length == 1 ? args[0] : null);
        JFrame frame = new UnitexFrame();
        Image img16x16 = new ImageIcon(Unitex.class.getResource("16x16.png")).getImage();
        Image img32x32 = new ImageIcon(Unitex.class.getResource("32x32.png")).getImage();
        Image img48x48 = new ImageIcon(Unitex.class.getResource("48x48.png")).getImage();
        frame.setIconImages(Arrays.asList(img16x16, img32x32, img48x48));
        frame.setVisible(true);
    }

}
