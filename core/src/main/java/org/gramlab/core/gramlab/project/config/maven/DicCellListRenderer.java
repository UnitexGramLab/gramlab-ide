package org.gramlab.core.gramlab.project.config.maven;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import org.gramlab.core.gramlab.project.GramlabProject;

/**
 * This class provides a renderer that truncates on the left.
 * 
 * @author paumier
 */
@SuppressWarnings("serial")
public class DicCellListRenderer extends DefaultListCellRenderer {
	
	FontMetrics fm;
	Graphics graphics;
	JComponent component;
	GramlabProject project;
	
	public DicCellListRenderer(JComponent c,GramlabProject p) {
		this.component=c;
		this.project=p;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (graphics==null) {
			graphics=list.getGraphics();
		}
		String s=adjust((BinToBuild)value,component.getWidth());
		return super.getListCellRendererComponent(list, s, index, isSelected,
				cellHasFocus);
	}

	private String adjust(BinToBuild bin,int width) {
		String a=getDicList(bin.getDics())+" => ";
		String b=bin.getBin()+"    ";
		if (fits(a+b,width)) {
			return htmlize(a,b,bin.getDics().size()>1);
		}
		for (int i=1;i<a.length();i++) {
			String tmp="..."+a.substring(i);
			if (fits(tmp+b,width)) {
				return htmlize(tmp,b,bin.getDics().size()>1);
			}
		}
		return htmlize(a,b,bin.getDics().size()>1);
	}

	private String getDicList(ArrayList<File> dics) {
		StringBuilder builder=new StringBuilder();
		String srcRelativeName=MavenDialog.inSrcDirectory(project,dics.get(0));
		builder.append(srcRelativeName);
		for (int i=1;i<dics.size();i++) {
			srcRelativeName=MavenDialog.inSrcDirectory(project,dics.get(i));
			builder.append(", ");
			builder.append(srcRelativeName);
		}
		return builder.toString();
	}


	private String htmlize(String a, String b, boolean multiDics) {
		return "<html><body>"+a+"<font color=\""+(multiDics?"green":"blue")+"\">"+b+"</font></body></html>";
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

}
