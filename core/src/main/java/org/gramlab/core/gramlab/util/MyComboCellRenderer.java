package org.gramlab.core.gramlab.util;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * The default combo box renderer always truncates large text with ... on the right,
 * which is not user friendly when the list contains long file names.
 * This class provides a renderer that truncates on the left, so that the
 * file name is always shown.
 * 
 * @author paumier
 */
@SuppressWarnings("serial")
public class MyComboCellRenderer extends DefaultListCellRenderer {
	
	FontMetrics fm;
	Graphics graphics;
	JComponent component;
	
	public MyComboCellRenderer(JComponent c) {
		this.component=c;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String s=adjust((String)value,component.getWidth());
		return super.getListCellRendererComponent(list, s, index, isSelected,
				cellHasFocus);
	}

	private String adjust(String s,int width) {
		if (fits(s,width)) {
			return s;
		}
		for (int i=1;i<s.length();i++) {
			String tmp="..."+s.substring(i);
			if (fits(tmp,width)) {
				return tmp;
			}
		}
		return s;
	}

	private boolean fits(String s,int width) {
		if (fm==null) {
			if (graphics==null) {
				return false;
			}
			fm=graphics.getFontMetrics();
		}
		int w=(int)fm.getStringBounds(s,graphics).getWidth();
		return w<=width;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		this.graphics=g;
		super.paintComponent(g);
	}
	
}
