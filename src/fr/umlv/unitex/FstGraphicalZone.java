 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.event.*;
import java.awt.print.*;

/**
 * This class describes a component on which a sentence graph can be drawn. 
 * @author Sébastien Paumier
 *
 */
public class FstGraphicalZone
   extends GenericGraphicalZone
   implements Printable {

   /**
    * Constructs a new <code>FstGraphicalZone</code>. 
    * @param w width of the drawing area
    * @param h height of the drawing area
    * @param t text field to edit box contents
    * @param p frame that contains the component
    * @param listeners indicates if mouse listeners must be added to the component
    */
   public FstGraphicalZone(
      int w,
      int h,
      FstTextField t,
      TextAutomatonFrame p,
      boolean listeners) {
      super(w, h, t, p);
      if (listeners) {
         addMouseListener(new FstGraphMouseListener());
      }
   }

   protected void init() {
      for (int i= 0; i < graphBoxes.size(); i++) {
         FstGraphBox g= (FstGraphBox)graphBoxes.get(i);
         g.context= (Graphics2D)this.getGraphics();
         g.parentGraphicalZone= this;
         g.update();
      }
   }


   protected GenericGraphBox createBox(int x, int y) {
      FstGraphBox g= new FstGraphBox(x, y, 2,this);
      g.setContent("<E>");
      addBox(g);
      return g;
   }

   protected GenericGraphBox newBox(
      int x,
      int y,
      int type,
      GenericGraphicalZone p) {
      return new FstGraphBox(x, y, type, (FstGraphicalZone)p);
   }
   class FstGraphMouseListener extends MouseAdapter {

      MouseMotionListener motionListener= new FstGraphMouseMotionListener();

      public void mouseClicked(MouseEvent e) {
         int boxSelected;
         FstGraphBox b;

         if (e.isShiftDown()) {
            // Shift+click
            // reverse transitions
            boxSelected=
               getSelectedBox(
                  (int) (e.getX() / scaleFactor),
                  (int) (e.getY() / scaleFactor));
            if (boxSelected != -1) {
               // if we click on a box
               b= (FstGraphBox)graphBoxes.get(boxSelected);
               if (!selectedBoxes.isEmpty()) {
                  // if there are selected boxes, we rely them to the current
                  addReverseTransitionsFromSelectedBoxes(b);
                  unSelectAllBoxes();
                  setModified(true);
               }
            } else {
               // simple click not on a box
               initText("");
               unSelectAllBoxes();
            }
         } else
            if (e.isControlDown()) {
               // Control+click
               // creation of a new box
               b=
                  (FstGraphBox)createBox((int) (e.getX() / scaleFactor),
                     (int) (e.getY() / scaleFactor));
               setModified(true);
               // if some boxes are selected, we rely them to the new one
               if (!selectedBoxes.isEmpty()) {
                  addTransitionsFromSelectedBoxes(b,true);
               }
               // then, the only selected box is the new one
               unSelectAllBoxes();
               b.selected= true;
               selectedBoxes.add(b);
               initText("<E>");
            } else {
               boxSelected=
                  getSelectedBox(
                     (int) (e.getX() / scaleFactor),
                     (int) (e.getY() / scaleFactor));
               if (boxSelected != -1) {
                  // if we click on a box
                  b= (FstGraphBox)graphBoxes.get(boxSelected);
                  if (!selectedBoxes.isEmpty()) {
                     // if there are selected boxes, we rely them to the current
                     addTransitionsFromSelectedBoxes(b,true);
                     unSelectAllBoxes();
                     setModified(true);
                  } else {
                     // if not, we just select this one
                     b.selected= true;
                     selectedBoxes.add(b);
                     initText(b.content);
                  }
               } else {
                  // simple click not on a box
                  unSelectAllBoxes();
                  initText("");
                  texte.setEditable(false);
               }
            }
         repaint();
         e.consume();
         return;
      }

      public void mousePressed(MouseEvent e) {
         int selectedBox;
         addMouseMotionListener(motionListener);
         if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
            return;
         validateTextField();
         X_start_drag= (int) (e.getX() / scaleFactor);
         Y_start_drag= (int) (e.getY() / scaleFactor);
         X_end_drag= X_start_drag;
         Y_end_drag= Y_start_drag;
         X_drag= X_start_drag;
         Y_drag= Y_start_drag;
         dragWidth= 0;
         dragHeight= 0;
         selectedBox= getSelectedBox(X_start_drag, Y_start_drag);
         singleDragging= false;
         dragging= false;
         selecting= false;
         if (selectedBox != -1) {
            // if we start dragging a box
            singleDraggedBox= (FstGraphBox)graphBoxes.get(selectedBox);
            initText(singleDraggedBox.content);
            if (!singleDraggedBox.selected) {
               dragging= true;
               singleDragging= true;
               singleDraggedBox.singleDragging= true;
            }
         }
         if (!selectedBoxes.isEmpty()) {
            dragging= true;
         }
         if ((selectedBox == -1) && selectedBoxes.isEmpty()) {
            // being drawing a selection rectangle
            dragging= false;
            selecting= true;
            initText("");
         }
         repaint();
         e.consume();
      }

      public void mouseReleased(MouseEvent e) {
         removeMouseMotionListener(motionListener);
         if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
            return;
         dragging= false;
         initText("");
         texte.setEditable(false);
         if (singleDragging) {
            singleDragging= false;
            singleDraggedBox.singleDragging= false;
         } else
            if (selecting == true) {
               selectByRectangle(X_drag, Y_drag, dragWidth, dragHeight);
               texte.setEditable(true);
               selecting= false;
            }
         repaint();
         e.consume();
      }

   }

   class FstGraphMouseMotionListener extends MouseMotionAdapter {
      public void mouseDragged(MouseEvent e) {
         int Xtmp= X_end_drag;
         int Ytmp= Y_end_drag;
         X_end_drag= (int) (e.getX() / scaleFactor);
         Y_end_drag= (int) (e.getY() / scaleFactor);
         int dx= X_end_drag - Xtmp;
         int dy= Y_end_drag - Ytmp;

         if (singleDragging) {
            // translates the single dragged box
            singleDraggedBox.translate(dx, dy);
            setModified(true);
         }
         if (dragging) {
            // translates all the selected boxes
            setModified(true);
            translateAllSelectedBoxes(dx, dy);
            // if we were dragging, we have nothing else to do
            repaint();
            e.consume();
            return;
         }

         if (X_start_drag < X_end_drag) {
            X_drag= X_start_drag;
            dragWidth= X_end_drag - X_start_drag;
         } else {
            X_drag= X_end_drag;
            dragWidth= X_start_drag - X_end_drag;
         }
         if (Y_start_drag < Y_end_drag) {
            Y_drag= Y_start_drag;
            dragHeight= Y_end_drag - Y_start_drag;
         } else {
            Y_drag= Y_end_drag;
            dragHeight= Y_start_drag - Y_end_drag;
         }

         repaint();
         e.consume();
      }

   }


   /* end of events handling */

   /*
    * Painting the boxes and transitions
    *
    */
   /*public void addTransitions(FstGraphBox dest) {
      int i, L;
      FstGraphBox g;
      if (selectedBoxes.isEmpty())
         return;
      L= selectedBoxes.size();
      for (i= 0; i < L; i++) {
         g= (FstGraphBox)selectedBoxes.get(i);
         g.addTransitionTo(dest);
      }
   }*/

   /*public void addReverseTransitions(FstGraphBox from) {
      int i, L;
      FstGraphBox g;
      if (selectedBoxes.isEmpty())
         return;
      L= selectedBoxes.size();
      for (i= 0; i < L; i++) {
         g= (FstGraphBox)selectedBoxes.get(i);
         from.addTransitionTo(g);
      }
   }*/

   /*public void drawAllTransitions(Graphics gr) {
      int i, L;
      FstGraphBox g;
      if (graphBoxes.isEmpty())
         return;
      L= graphBoxes.size();
      for (i= 0; i < L; i++) {
         g= (FstGraphBox)graphBoxes.get(i);
         g.drawTransitions(gr);
      }
   }*/

   /*public void drawAllBoxes(Graphics gr) {
      int i, L;
      FstGraphBox g;
      if (graphBoxes.isEmpty())
         return;
      L= graphBoxes.size();
      for (i= 0; i < L; i++) {
         g= (FstGraphBox)graphBoxes.get(i);
         g.draw(gr);
      }
   }
   */

   /**
    * Draws the graph. This method should only be called by the virtual machine.
    * @param f_old the graphical context 
    */
   public void paintComponent(Graphics f_old) {
      Graphics2D f= (Graphics2D)f_old;
      if (pref.antialiasing) {
         f.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      } else {
         f.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
      }
      f.setColor(new Color(205, 205, 205));
      f.fillRect(0, 0, getWidth(), getHeight());
      f.setColor(pref.backgroundColor);
      f.fillRect(0, 0, Width, Height);
      f.setColor(pref.foregroundColor);
      f.drawRect(10, 10, Width - 20, Height - 20);
      f.drawRect(9, 9, Width - 18, Height - 18);
      f.setColor(pref.foregroundColor);
      if (graphBoxes.size() == 0 || graphBoxes.isEmpty()) {
         return;
      }
      if (!is_initialised) {
         this.init();
         is_initialised= true;
      }
      f.setColor(new Color(205, 205, 205));
      f.fillRect(0, 0, getWidth(), getHeight());
      f.setColor(pref.backgroundColor);
      f.fillRect(0, 0, Width, Height);
      f.setColor(pref.foregroundColor);
      f.drawRect(10, 10, Width - 20, Height - 20);
      f.drawRect(9, 9, Width - 18, Height - 18);
      f.setColor(pref.foregroundColor);
      drawAllTransitions(f);
      drawAllBoxes(f);
      if (selecting) {
         // here we draw the selection rectangle
         f.setColor(pref.foregroundColor);
         f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
      }
   }

   /* public void updateAllBoxes() {
       FstGraphBox g;
       int i;
       for (i= 0; i < graphBoxes.size(); i++) {
          g= (FstGraphBox)graphBoxes.get(i);
          g.update();
       }
       repaint();
    }*/

   /**
    * Prints the graph.
    * @param g the graphical context
    * @param p the page format
    * @param pageIndex the page index 
    */
   public int print(Graphics g, PageFormat p, int pageIndex) {
      if (pageIndex != 0)
         return Printable.NO_SUCH_PAGE;
      Graphics2D f= (Graphics2D)g;
      double DPI= 96.0;
      //(double)Toolkit.getDefaultToolkit().getScreenResolution();
      double WidthInInches= p.getImageableWidth() / 72;
      double realWidthInInches= (Width / DPI);
      double HeightInInches= p.getImageableHeight() / 72;
      double realHeightInInches= (Height / DPI);

      double scale_x= WidthInInches / realWidthInInches;
      double scale_y= HeightInInches / realHeightInInches;

      f.translate(p.getImageableX(), p.getImageableY());
      if (scale_x < scale_y)
         f.scale(0.99 * 0.72 * scale_x, 0.99 * 0.72 * scale_x);
      else
         f.scale(0.99 * 0.72 * scale_y, 0.99 * 0.72 * scale_y);
      f.setColor(pref.backgroundColor);
      f.fillRect(0, 0, Width, Height);
      f.setColor(pref.foregroundColor);
      f.drawRect(10, 10, Width - 20, Height - 20);
      f.drawRect(9, 9, Width - 18, Height - 18);
      f.setColor(pref.foregroundColor);
      drawAllTransitions(f);
      drawAllBoxes(f);
      if (selecting) {
         // here we draw the selection rectangle
         f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
      }
      return Printable.PAGE_EXISTS;
   }

} /* end of FstGraphicalZone */
