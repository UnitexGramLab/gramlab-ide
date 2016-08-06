package org.gramlab.core.gramlab.project.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class ConsolePanel extends JPanel {
	
	private GridBagConstraints gbc=new GridBagConstraints();
	private JPanel panel=new JPanel(new GridBagLayout());
	private JScrollPane parentScroll=null;
	
	public ConsolePanel() {
		super(new BorderLayout());
		super.add(panel,BorderLayout.NORTH);
		super.add(new JPanel(),BorderLayout.CENTER);
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
	}
	
	
	@Override
	public Component add(Component comp) {
		panel.add(comp,gbc);
		panel.revalidate();
		panel.repaint();
		scroll();
		return comp;
	}
	
	private void scroll() {
		JScrollPane p=getParentScrollPane();
		if (p==null) return;
		p.getViewport().scrollRectToVisible(new Rectangle(0,p.getViewport().getView().getHeight()-1,1,1));
	}


	@Override
	public void removeAll() {
		panel.removeAll();
		panel.revalidate();
		repaint();
	}

	private boolean parentScrollNotSet=true;
	
	public JScrollPane getParentScrollPane() {
		if (parentScroll==null && parentScrollNotSet) {
			parentScrollNotSet=false;
			parentScroll=lookForParentScroll();
		}
		return parentScroll;
	}


	private JScrollPane lookForParentScroll() {
		Container c=this;
		while (c!=null && (!(c instanceof JFrame))) {
			if (c instanceof JScrollPane) return (JScrollPane)c;
			c=c.getParent();
		}
		return null;
	}


	public void copy() {
		StringBuilder b=new StringBuilder();
		for (int i=0;i<panel.getComponentCount();i++) {
			Component comp=panel.getComponent(i);
			if (comp instanceof JTextComponent) {
				JTextComponent c=(JTextComponent) comp;
				String s=unhtmlize(c.getText());
				b.append(s+"\n");
			} else if (comp instanceof JLabel) {
				JLabel l=(JLabel) comp;
				b.append(l.getText()+"\n");
			} else if (comp instanceof JPanel) {
				JPanel p=(JPanel)comp;
				for (int j=0;j<p.getComponentCount();j++) {
					Component comp2=p.getComponent(j);
					if (comp2 instanceof JTextComponent) {
						JTextComponent c=(JTextComponent) comp2;
						String s=unhtmlize(c.getText());
						b.append(s+"\n");
					} else if (comp2 instanceof JLabel) {
						JLabel l=(JLabel) comp2;
						b.append(l.getText()+"\n");
					}
				}
			}
		}
		Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(b.toString()),null);
	}


	private String unhtmlize(String text) {
		if (text==null || !text.startsWith("<html>")) return text;
		return text.replaceAll("<[^>]+>","");
	}
	
	private double preprocessingTotalTime=-1;
	private double locateTime=-1;

	public double getPreprocessingTotalTime() {
		return preprocessingTotalTime;
	}


	public void setPreprocessingTotalTime(double preprocessingTotalTime) {
		this.preprocessingTotalTime = preprocessingTotalTime;
	}


	public double getLocateTime() {
		return locateTime;
	}


	public void setLocateTime(double locateTime) {
		this.locateTime = locateTime;
	}
	
	
}
