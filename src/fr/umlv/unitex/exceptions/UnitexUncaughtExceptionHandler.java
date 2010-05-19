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

package fr.umlv.unitex.exceptions;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Version;

/**
 * This class defines an <code>Exception</code> that is thrown when the user wants to validate
 * a box content with a backslash at the end of the line.
 * @author Sébastien Paumier
 *
 */
public class UnitexUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private static UnitexUncaughtExceptionHandler handler;
	
	private UnitexUncaughtExceptionHandler() {
		/* This should not be called from the outside */
	}
	
    public void uncaughtException(Thread t, Throwable e) {
        Box b = new Box(BoxLayout.Y_AXIS);
        b.add(new JLabel(e.toString()));
        String s=e.toString()+"\n";
        for (StackTraceElement elem : e.getStackTrace()) {
            b.add(new JLabel("at "+elem.toString()));
            s=s+"at "+elem.toString()+"\n";
        }
        if (e.getCause()!=null) {
        	b.add(new JLabel("Caused by: "));
            s=s+"Caused by: "+e.getCause().getStackTrace();
        	for (StackTraceElement elem : e.getCause().getStackTrace()) {
                b.add(new JLabel("at "+elem.toString()));
                s=s+"at "+elem.toString()+"\n";
            }
        }
        JScrollPane scroll=new JScrollPane(b);
        scroll.setPreferredSize(new Dimension(b.getPreferredSize().width+50,400));
        scroll.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
        JPanel p=new JPanel(new BorderLayout());
        p.add(scroll,BorderLayout.CENTER);
        p.add(new JLabel("Do you want to report this problem to Unitex developers ?"),BorderLayout.SOUTH);
        
        if (JOptionPane.showConfirmDialog(null,p, "Java Exception",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==0) {
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
    
    public static UnitexUncaughtExceptionHandler getHandler() {
    	if (handler==null) {
    		handler=new UnitexUncaughtExceptionHandler();
    	}
    	return handler;
    }

}
