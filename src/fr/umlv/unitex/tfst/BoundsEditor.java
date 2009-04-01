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
