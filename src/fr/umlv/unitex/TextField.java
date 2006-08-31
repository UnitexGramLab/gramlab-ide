 /*
  * Unitex
  *
  * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

/**
 * This class describes the text field used to get the box text in a graph.
 * @author Sébastien Paumier
 *
 */
public class TextField extends JTextField {

   /**
    * Frame that contains this component 
    */
   public GraphFrame parent;

   /**
    * Indicates if the text field content has been modified 
    */
   public boolean modified= false;

   /**
    * Left context for multiple word copy 
    */
   public static String leftContext;

   /**
    * Right context for multiple word copy 
    */
   public static String rightContext;

   /**
    * <code>TextAction</code> that defines what to do for a "paste" operation 
    */
   public SpecialPaste specialPaste;

   /**
    * <code>TextAction</code> that defines what to do for a "copy" operation 
    */
   public SpecialCopy specialCopy;

   /**
    * <code>TextAction</code> that defines what to do for a "select all" operation 
    */
   public SelectAll selectAll;

   /**
    * <code>TextAction</code> that defines what to do for a "cut" operation 
    */
   public Cut cut;

   /**
    * <code>TextAction</code> that shows the graph presentation frame
    */
   public Presentation presentation;

   /**
    * <code>TextAction</code> that shows a dialog box to open a graph
    */
   public Open OPEN;

   /**
    * <code>TextAction</code> that saves the current graph
    */
   public Save SAVE;

   /**
    * Constructs a new <code>TextField</code> 
    * @param n number of columns
    * @param p frame that contains this component
    */
   public TextField(int n, GraphFrame p) {
      super(n);
      setEditable(false);
      modified= false;
      parent= p;
      leftContext= "";
      rightContext= "";
      setDisabledTextColor(Color.white);
      setBackground(Color.white);
      Keymap k= getKeymap();
      k= addKeymap("textfield-keymap", k);
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('l', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('L', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('k', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('K', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('o', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('s', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('p', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('m', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('r', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('x', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('c', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('C', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('v', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('a', Event.CTRL_MASK));
      k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
      specialPaste= new SpecialPaste("special-paste");
      specialCopy= new SpecialCopy("special-copy");
      presentation= new Presentation("presentation");
      selectAll= new SelectAll("select all");
      cut= new Cut("cut");
      OPEN= new Open("open");
      SAVE= new Save("save");

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('v', Event.CTRL_MASK),
         specialPaste);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('V', Event.CTRL_MASK),
         specialPaste);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('c', Event.CTRL_MASK),
         specialCopy);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('C', Event.CTRL_MASK),
         specialCopy);

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('r', Event.CTRL_MASK),
         presentation);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('R', Event.CTRL_MASK),
         presentation);

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('o', Event.CTRL_MASK),
         OPEN);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('O', Event.CTRL_MASK),
         OPEN);

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('s', Event.CTRL_MASK),
         SAVE);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('S', Event.CTRL_MASK),
         SAVE);

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('a', Event.CTRL_MASK),
         selectAll);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('A', Event.CTRL_MASK),
         selectAll);

      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('x', Event.CTRL_MASK),
         cut);
      k.addActionForKeyStroke(
         KeyStroke.getKeyStroke('X', Event.CTRL_MASK),
         cut);

      this.setKeymap(k);

      addKeyListener(new MyKeyListener());
   }

   GraphFrame parent() {
      return UnitexFrame.getCurrentFocusedGraphFrame();
   }

   class SpecialCopy extends TextAction implements ClipboardOwner {
      public SpecialCopy(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         if (parent().graphicalZone.selectedBoxes.isEmpty()
            || parent().graphicalZone.selectedBoxes.size() < 2) {
            // is there is no or one box selected, we copy normally
            copy();
            UnitexFrame.clip.setContents(null, this);
            return;
         }
         UnitexFrame.clip.setContents(
            new MultipleBoxesSelection(
               new MultipleSelection(parent().graphicalZone.selectedBoxes,true)),
            this);
      }

      public void lostOwnership(Clipboard c, Transferable d) {
    	  // nothing to do
      }
   }

   class SelectAll extends TextAction implements ClipboardOwner {
      public SelectAll(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         if (!parent().graphicalZone.selectedBoxes.isEmpty()
            && parent().graphicalZone.selectedBoxes.size() == 1)
            selectAll();
         else
            parent().graphicalZone.selectAllBoxes();
      }

      public void lostOwnership(Clipboard c, Transferable d) {
    	  // nothing
      }
   }

   class Cut extends TextAction implements ClipboardOwner {
      public Cut(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         if (!parent().graphicalZone.selectedBoxes.isEmpty()
            && parent().graphicalZone.selectedBoxes.size() == 1) {
            cut();
         } else {
            UnitexFrame.clip.setContents(
               new MultipleBoxesSelection(
                  new MultipleSelection(parent().graphicalZone.selectedBoxes,true)),
               this);
            parent().graphicalZone.removeSelected();
            setText("");
         }
      }

      public void lostOwnership(Clipboard c, Transferable d) {
    	  // nothing to do
      }
   }

   class SpecialPaste extends TextAction {
      public SpecialPaste(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         Transferable data;
         MultipleSelection res= null;
         data= UnitexFrame.clip.getContents(this);
         try {
            if (data != null)
               res=
                  (MultipleSelection)data.getTransferData(
                     new DataFlavor("unitex/boxes", "Unitex dataflavor"));
         } catch (UnsupportedFlavorException e2) {
        	 e2.printStackTrace();
         } catch (IOException e2) {
        	 e2.printStackTrace();
         }
         if (res == null || TextField.this.modified == true) {
            // if there is no boxes to copy we do a simple paste
            paste();
            return;
         }
         res.n++;
         parent().graphicalZone.pasteSelection(res);
      }
   }

   class Presentation extends TextAction {
      public Presentation(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         new GraphPresentationMenu();
      }
   }

   class Open extends TextAction {
      public Open(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         UnitexFrame.mainFrame.openGraph();
      }
   }

   class Save extends TextAction {
      public Save(String s) {
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         GraphFrame f=UnitexFrame.getCurrentFocusedGraphFrame();
         if (f!=null) {
            UnitexFrame.mainFrame.saveGraph(f);
         }
      }
   }

   /**
    * Initializes the text field with a string 
    * @param s the new text content
    */
   public void initText(String s) {
      modified= false;
      setEditable(true);
      setText(s);
      requestFocus();
      getCaret().setVisible(true);
      selectAll();
   }

   /**
    * Returns a new <code>ListDocument</code> object.
    * @return the <code>ListDocument</code>
    */
   public Document createDefaultModel() {
      return new ListDocument();
   }

   /**
    * Validates the content of the text field as the content of selected boxes. 
    * @return <code>true</code> if boxes have actually been modified, <code>false</code> otherwise 
    */
   public boolean validateTextField() {
      if (!hasChangedTextField())
         return false;
      if (isValidGraphBoxContent(getText())) {
         parent().setModified(true);
         parent().graphicalZone.setTextForSelected(getText());
         parent().graphicalZone.unSelectAllBoxes();
         setText("");
         parent().repaint();
         setEditable(false);
         return true;
      }
      return false;
   }

   class MyKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
         if (e.isControlDown() || e.isAltDown()) {
            // if the control key or alt key is pressed, we do nothing: the event we be caught by the ActionListeners
            return;
         }
         if (e.getKeyCode() == 10)         
            validateTextField();
         
         modified= true;
      }
   }

   /**
    * Tests if the content of the text field has changed. 
    * @return <code>true</code> if the content has changed, <code>false</code> otherwise 
    */
   public boolean hasChangedTextField() {
      return modified;
   }

   private int test_transduction(char s[], int i) {
      int compteur;
      if (s[i] != '/')
         return 0;
      i--;
      compteur= 0;
      while (i >= 0 && s[i] == '\\') {
         compteur++;
         i--;
      }
      if ((compteur % 2) != 0)
         return 0;
      return 1;
   }

   private boolean tokenize(String s) {
      int L= s.length(), i= 0;
      String tmp;
      char ligne[]= new char[10000];
      ligne= s.toCharArray();
      if (ligne[0] == '+') {
         JOptionPane.showMessageDialog(
            null,
            "Unexpected \"+\" as first character of the line",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (L>=2 && ligne[L-1] == '+' && ligne[L-2]!='\\') {
         JOptionPane.showMessageDialog(
            null,
            "Unexpected \"+\" as last character of the line",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return false;
      }
      while (i < L) {
         tmp= "";
         if (ligne[i] == ':') {
            // case of a sub graph call
            i++;
            while ((i < L) && (ligne[i] != '+')) {
               if (ligne[i] == '\\') {
                  tmp= tmp.concat(String.valueOf(ligne[i++]));
                  if (i >= L) {
                     JOptionPane.showMessageDialog(
                        null,
                        "Unexpected \"\\\" at end of line",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                     return false;
                  }
               }
               tmp= tmp.concat(String.valueOf(ligne[i++]));
            }
            if (tmp.length() == 0) {
               JOptionPane.showMessageDialog(
                  null,
                  "Missing graph name after \":\"",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
               return false;
            }
            i++;
         } else {
            // all other cases
            if (ligne[i] == '+') {
               // if we find a + just after a + it is an error
               JOptionPane.showMessageDialog(
                  null,
                  "Empty line error: \"++\"",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
               return false;
            }
            while ((i < L) && (ligne[i] != '+')) {
               if (ligne[i] == '"') {
                  // case of a quote expression
                  tmp= tmp.concat(String.valueOf(ligne[i++]));
                  while ((i < L) && ligne[i] != '"') {
                     if (ligne[i] == '\\') {
                        tmp= tmp.concat(String.valueOf(ligne[i++]));
                        if (i >= L) {
                           JOptionPane.showMessageDialog(
                              null,
                              "Unexpected \"\\\" at end of line",
                              "Error",
                              JOptionPane.ERROR_MESSAGE);
                           return false;
                        }
                     }
                     tmp= tmp.concat(String.valueOf(ligne[i++]));
                  }
                  if (i >= L) {
                     JOptionPane.showMessageDialog(
                        null,
                        "No closing \"",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                     return false;
                  }
                  tmp= tmp.concat(String.valueOf(ligne[i++]));
               } else
                  if (ligne[i] == '<') {
                     // case of a <...> expression
                     tmp= tmp.concat(String.valueOf(ligne[i++]));
                     while ((i < L) && ligne[i] != '>') {
                        if (ligne[i] == '\\') {
                           tmp= tmp.concat(String.valueOf(ligne[i++]));
                           if (i >= L) {
                              JOptionPane.showMessageDialog(
                                 null,
                                 "Unexpected \"\\\" at end of line",
                                 "Error",
                                 JOptionPane.ERROR_MESSAGE);
                              return false;
                           }
                        }
                        tmp= tmp.concat(String.valueOf(ligne[i++]));
                     }
                     if (i >= L) {
                        JOptionPane.showMessageDialog(
                           null,
                           "No closing \">\"",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                        return false;
                     }
                     tmp= tmp.concat(String.valueOf(ligne[i++]));
                  } else
                     if (ligne[i] == '{') {
                        // case of a {...} expression
                        tmp= tmp.concat(String.valueOf(ligne[i++]));
                        while ((i < L) && ligne[i] != '}') {
                           if (ligne[i] == '\\') {
                              tmp= tmp.concat(String.valueOf(ligne[i++]));
                              if (i >= L) {
                                 JOptionPane.showMessageDialog(
                                    null,
                                    "Unexpected \"\\\" at end of line",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                                 return false;
                              }
                           }
                           tmp= tmp.concat(String.valueOf(ligne[i++]));
                        }
                        if (i >= L) {
                           JOptionPane.showMessageDialog(
                              null,
                              "No closing \"}\"",
                              "Error",
                              JOptionPane.ERROR_MESSAGE);
                           return false;
                        }
                        tmp= tmp.concat(String.valueOf(ligne[i++]));
                     } else {
                        if (ligne[i] == '\\') {
                           tmp= tmp.concat(String.valueOf(ligne[i++]));
                           if (i >= L) {
                              JOptionPane.showMessageDialog(
                                 null,
                                 "Unexpected \"\\\" at end of line",
                                 "Error",
                                 JOptionPane.ERROR_MESSAGE);
                              return false;
                           }
                        }
                        tmp= tmp.concat(String.valueOf(ligne[i++]));
                     }
            }
            i++;
         }
      }
      return true;
   }

   /**
    * Tests if a content is a valid content for a graph box.
    * @param s the content to test 
    * @return <code>true</code> if the content is valid, <code>false</code> otherwise 
    */
   public boolean isValidGraphBoxContent(String s) {
      if (s.equals(""))
         return true;
      char ligne[]= new char[10000];
      String tmp= "";
      int i, L;

      ligne= s.toCharArray();
      i= 0;
      L= s.length();
      if (L == 2 && ligne[0] == '$' && (ligne[1] == '(' || ligne[1] == ')')) {
         JOptionPane.showMessageDialog(
            null,
            "You must indicate a variable name between $ and ( or )",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return false;
      }
      while ((i != L) && (test_transduction(ligne, i) == 0))
         tmp= tmp.concat(String.valueOf(ligne[i++]));
      if ((i != L) && (i == 0)) {
         JOptionPane.showMessageDialog(
            null,
            "Empty text before \"/\"",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (L > 2
         && ligne[0] == '$'
         && (ligne[L - 1] == '(' || ligne[L - 1] == ')')
         && s.lastIndexOf('+') == -1) {
         // case of a variable start $a( or end $a)
         for (int k= 1; k < L - 1; k++)
            if (ligne[k] != '_'
               && !(ligne[k] >= '0' && ligne[k] <= '9')
               && !(ligne[k] >= 'a' && ligne[k] <= 'z')
               && !(ligne[k] >= 'A' && ligne[k] <= 'Z')) {
               JOptionPane.showMessageDialog(
                  null,
                  "A variable name can only contain the following characters:\nA..Z,a..z,0..9 and the underscore '_'",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
               return false;
            }
         return true;
      }
      if (tokenize(tmp))
         return true;
      return false;
   }

}
