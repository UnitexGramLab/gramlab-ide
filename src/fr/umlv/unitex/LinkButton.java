package fr.umlv.unitex;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class LinkButton extends JButton {
	
	public LinkButton(String text,boolean underline) {
		setBorderPainted(false);
		setContentAreaFilled(false);
		setForeground(Color.BLUE);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setHorizontalAlignment(SwingConstants.LEFT);
		setMargin(new Insets(0,0,0,0));
		setFocusPainted(false);
		if (underline) {
			HashMap<TextAttribute,Object> attr=new HashMap<TextAttribute,Object>();
			attr.put(TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_ON);
			setFont(getFont().deriveFont(attr));
		}
		setText(text);
	}
	
	public LinkButton() {
		this("",true);
	}
	
	public LinkButton(String text) {
		this(text,true);
	}

}
