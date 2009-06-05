 /*
  * Unitex
  *
  * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fr.umlv.unitex.UnitexFrame;

/**
 * This class describes a frame that shows all the command lines that have been launched. 
 * @author Sébastien Paumier
 *
 */
public class Console extends JInternalFrame {
   
   static Console console;
   static ConsoleTableModel model;
   static JTable table; 
   static int longestCommandWidth=80;
   
   public static final ImageIcon statusOK=new ImageIcon(Console.class.getResource("OK.png"));
   public static final ImageIcon statusErrorDown=new ImageIcon(Console.class.getResource("error1.png"));
   public static final ImageIcon statusErrorUp=new ImageIcon(Console.class.getResource("error2.png"));
   
   private Console() {
      super("Console", true, true);
      model=new ConsoleTableModel();
      table=new JTable(model);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      table.getColumnModel().getColumn(0).setPreferredWidth(20);
      table.getColumnModel().getColumn(1).setMinWidth(longestCommandWidth);
      table.setDefaultRenderer(Integer.class,new DefaultTableCellRenderer() {
          JLabel errorDown=new JLabel(statusErrorDown,SwingConstants.CENTER);
          JLabel errorUp=new JLabel(statusErrorUp,SwingConstants.CENTER);
          JLabel OK=new JLabel(statusOK,SwingConstants.CENTER);
          JLabel nothing=new JLabel();
          
          @Override
          public Component getTableCellRendererComponent(JTable t, Object value, boolean ss, boolean hasFocus, int row, int column) {
              Integer i=(Integer)value;
              switch(i) {
              case 0: return OK;
              case 1: return errorDown;
              case 2: return errorUp;
              case 3: return nothing;
              default: throw new IllegalArgumentException("Invalid status: "+i);
              }
          }
      });
      table.setDefaultRenderer(ConsoleEntry.class,new DefaultTableCellRenderer() {
          JTextArea command=new JTextArea();
          JTextArea error=new JTextArea(); 
          {
              error.setLineWrap(true);
              error.setWrapStyleWord(true);
              error.setForeground(Color.RED);
          }
          
          @Override
          public Component getTableCellRendererComponent(JTable t, Object value, boolean ss, boolean hasFocus, int row, int column) {
              ConsoleEntry e=(ConsoleEntry)value;
              switch(e.getStatus()) {
              case 0: 
              case 1: 
              case 2: {
                  command.setText(e.getContent());
                  return command;
              }
              case 3: {
                  error.setText(e.getContent());
                  final int h=error.getPreferredSize().height;
                  final JTable t2=t;
                  final int r=row;
                  if (t.getRowHeight(row)<h) {
                      /* If necessary, we resize the row */
                      EventQueue.invokeLater(new Runnable() {
                          public void run() {
                              t2.setRowHeight(r,h);
                          }
                      });
                  }
                  return error;
              }
              default: throw new IllegalArgumentException("Invalid status: "+e.getStatus());
              }
          }
      });
      table.setDefaultEditor(Integer.class,new ConsoleTableCellEditor(model));
      table.setDefaultEditor(ConsoleEntry.class,new DefaultCellEditor(new JTextField()));

      JScrollPane scroll= new JScrollPane(table);
      JPanel middle= new JPanel();
      middle.setOpaque(true);
      middle.setLayout(new BorderLayout());
      middle.setBorder(BorderFactory.createLoweredBevelBorder());
      middle.add(scroll, BorderLayout.CENTER);
      JPanel top= new JPanel();
      top.setOpaque(true);
      top.setLayout(new BorderLayout());
      top.setBorder(new EmptyBorder(2, 2, 2, 2));
      top.add(middle, BorderLayout.CENTER);
      setContentPane(top);
      setBounds(100, 100, 600, 400);
      setVisible(false);
      addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent e) {
          console.setVisible(false);
       }
        });
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
   }

   /**
    * Adds a <code>String</code> to the the command lines 
    * @param command the command line to be added
    */
   public static ConsoleEntry addCommand(String command,boolean isRealCommand,int pos) {
      if (console==null) {
         init();
      }
      ConsoleEntry e=new ConsoleEntry(command,isRealCommand);
      int n=(pos!=-1)?pos:model.getRowCount();
      model.addConsoleEntry(n,e);
      /* Now, we update the width of the last column */
      TableCellRenderer renderer=table.getCellRenderer(n,1);
      Component c=renderer.getTableCellRendererComponent(table,e,false,
              false,n,1);
      if (c.getPreferredSize().width>longestCommandWidth) {
          longestCommandWidth=c.getPreferredSize().width+50;
          table.getColumnModel().getColumn(1).setMinWidth(longestCommandWidth);
      }
      return e;
   }
   
   
   public static ConsoleEntry addCommand(String command) {
       return addCommand(command,true,-1);
   }
           
           
   /**
    * Initializes the console frame 
    *
    */
   private static void init() {
      console= new Console();
      UnitexFrame.addInternalFrame(console,false);
   }
   
   
   private static void showFrame() {
      if (console==null) {
         init();
      }
      console.setVisible(true);
   }


   public static void changeFrameVisibility() {
      if (console==null) {
         showFrame();
      }
      else console.setVisible(!console.isVisible());
   }
   
}
