package fr.gramlab.project.config.maven;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import fr.gramlab.project.GramlabProject;

/**
 * This class provides a renderer that truncates on the left.
 * 
 * @author paumier
 */
@SuppressWarnings("serial")
public class GraphCellListRenderer extends DefaultListCellRenderer {
	
	FontMetrics fm;
	Graphics graphics;
	JComponent component;
	GramlabProject project;
	
	public GraphCellListRenderer(JComponent c,GramlabProject p) {
		this.component=c;
		this.project=p;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (graphics==null) {
			graphics=list.getGraphics();
		}
		String s=adjust((GrfToCompile)value,component.getWidth());
		return super.getListCellRendererComponent(list, s, index, isSelected,
				cellHasFocus);
	}

	private String adjust(GrfToCompile g,int width) {
		String a=MavenDialog.inSrcDirectory(project,g.getGrf())+" => ";
		String b=g.getFst2()+"    ";
		if (fits(a+b,width)) {
			return htmlize(a,b);
		}
		for (int i=1;i<a.length();i++) {
			String tmp="..."+a.substring(i);
			if (fits(tmp+b,width)) {
				return htmlize(tmp,b);
			}
		}
		return htmlize(a,b);
	}

	private String htmlize(String a, String b) {
		return "<html><body>"+a+"<font color=\"red\">"+b+"</font></body></html>";
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
