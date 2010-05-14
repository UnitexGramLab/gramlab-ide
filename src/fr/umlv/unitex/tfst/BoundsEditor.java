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

package fr.umlv.unitex.tfst;

import java.awt.Color;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import fr.umlv.unitex.TextAutomatonFrame;

public class BoundsEditor extends JFormattedTextField {

    boolean validBounds=false;

    public BoundsEditor() {
        super(new BoundsFormatter());
        addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                try {
                    commitEdit();
                    Bounds b=getValue();
                    if (b==null) {
                        validBounds=false;
                        return;
                    }
                    validBounds=true;
                    setForeground(Color.GREEN);
                } catch (ParseException e1) {
                    validBounds=false;
                    setForeground(Color.RED);
                }
            }
        });
        setEditable(false);
    }
    
        
    
    public void reset() {
        setValue(null);
        validBounds=false;
    }
 
    public boolean boundsAreValid() {
        return validBounds;
    }
    
    public Bounds getValue() {
        return (Bounds)super.getValue();
    }
    
    @Override
    public void setValue(Object value) {
        super.setValue(value);
        TextAutomatonFrame.frame.revalidate();
        TextAutomatonFrame.frame.repaint();
    }
}
