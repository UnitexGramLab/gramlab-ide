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
package fr.umlv.unitex.xalign;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class XAlignPane extends JPanel {

	static final int MAX_SENTENCES=40;
	
	JScrollBar scrollX;
	JScrollBar scrollY;
	
	volatile boolean scrollXAdjusting=false;
	volatile boolean scrollYAdjusting=false;
	
	JComponent middle;
	JTextArea sentencesX[];
	JTextArea sentencesY[];
	
	JPanel panelX;
	JPanel panelY;
	XMLTextModel model1;
	XMLTextModel model2;
	
	XAlignModel alignmentModel;
	
	int currentSentenceX=-1;
	int currentSentenceY=-1;
	
	Object lock=new Object();
	int tmpX=-1;
	int tmpY=-1;
	
	public XAlignPane(final XMLTextModel model1,final XMLTextModel model2, XAlignModel model) {
		super(new GridBagLayout());
		this.model1=model1;
		this.model2=model2;
		this.alignmentModel=model;
		scrollX=new JScrollBar(Adjustable.VERTICAL,0,1,0,(model1.getSize()>0)?model1.getSize():1);
		model1.addListDataListener(new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				scrollX.setMaximum(model1.getSize());
			}
			public void intervalRemoved(ListDataEvent e) {/* */}
			public void contentsChanged(ListDataEvent e) {/* */}
		});
		scrollY=new JScrollBar(Adjustable.VERTICAL,0,1,0,(model2.getSize()>0)?model2.getSize():1);
		model2.addListDataListener(new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				scrollY.setMaximum(model2.getSize());
			}
			public void intervalRemoved(ListDataEvent e) {/* */}
			public void contentsChanged(ListDataEvent e) {/* */}
		});
		middle=createMiddleComponent();
		sentencesX=new JTextArea[MAX_SENTENCES];
		sentencesY=new JTextArea[MAX_SENTENCES];
		panelX=new JPanel(null);
		panelX.setLayout(new BoxLayout(panelX,BoxLayout.Y_AXIS));
		panelY=new JPanel(null);
		panelY.setLayout(new BoxLayout(panelY,BoxLayout.Y_AXIS));
		for (int i=0;i<MAX_SENTENCES;i++) {
			final int z=i;
			sentencesX[i]=new JTextArea();
			sentencesX[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int tmp=z+scrollX.getValue();
					if (tmp>=model1.getSize()) {
						/* If we click outside the areas, we do nothing */ 
						refresh();
						return;
					}
					if (tmp==currentSentenceX) {
						/* If we click on the selected area in the same row,
						 * we unselect it. */
						currentSentenceX=-1;
						refresh();
						return;
					}
					if (currentSentenceY!=-1) {
						/* If we click on a src area while a dest one is selected, then
						 * we must align them */
						alignmentModel.changeAlignment(tmp,currentSentenceY);
						currentSentenceY=-1;
						tmpX=-1;
						refresh();
						return;
					}
					/* Otherwise, we have to select to clicked area */
					currentSentenceX=tmp;
					refresh();
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					if (currentSentenceY!=-1) {
						synchronized(lock) {
							tmpX=z+scrollX.getValue();
						}
						refresh();
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if (currentSentenceY!=-1) {
						synchronized(lock) {
							int tmp=z+scrollX.getValue();
							if (tmp==tmpX) {
								/* We protect this instruction, because when the
								 * mouse exits an area A to enter into an area B,
								 * we don't know if A.mouseExited is called before
								 * B.mouseEntered and so, we don't want to set tmpX
								 * to -1 if it was allready set to B
								 */
								tmpX=-1;
							}
							refresh();
						}
					}
				}
			});
			sentencesX[i].setEditable(false);
			sentencesX[i].setLineWrap(true);
			sentencesX[i].setWrapStyleWord(true);
			sentencesY[i]=new JTextArea();
			sentencesY[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int tmp=z+scrollY.getValue();
					if (tmp>=model2.getSize()) {
						/* If we click outside the areas, we do nothing */ 
						refresh();
						return;
					}
					if (tmp==currentSentenceY) {
						/* If we click on the selected area in the same row,
						 * we unselect it. */
						currentSentenceY=-1;
						refresh();
						return;
					}
					if (currentSentenceX!=-1) {
						/* If we click on a dest area while a src one is selected, then
						 * we must align them */
						alignmentModel.changeAlignment(currentSentenceX,tmp);
						currentSentenceX=-1;
						tmpY=-1;
						refresh();
						return;
					}
					/* Otherwise, we have to select to clicked area */
					currentSentenceY=tmp;
					refresh();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (currentSentenceX!=-1) {
						synchronized(lock) {
							tmpY=z+scrollY.getValue();
						}
						refresh();
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if (currentSentenceX!=-1) {
						synchronized(lock) {
							int tmp=z+scrollY.getValue();
							if (tmp==tmpY) {
								/* We protect this instruction, because when the
								 * mouse exits an area A to enter into an area B,
								 * we don't know if A.mouseExited is called before
								 * B.mouseEntered and so, we don't want to set tmpY
								 * to -1 if it was allready set to B
								 */
								tmpY=-1;
							}
						}
						refresh();
					}
				}
			});
			sentencesY[i].setEditable(false);
			sentencesY[i].setLineWrap(true);
			sentencesY[i].setWrapStyleWord(true);
			panelX.add(sentencesX[i]);
			panelY.add(sentencesY[i]);
		}
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.weighty=1;
		gbc.weightx=0;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridwidth=1;
		add(scrollX,gbc);
		gbc.weightx=1;
		add(panelX,gbc);
		gbc.weightx=0;
		add(middle,gbc);
		gbc.weightx=1;
		add(panelY,gbc);
		gbc.weightx=0;
		add(scrollY,gbc);
		refresh();
		scrollX.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (scrollXAdjusting) return;
				scrollXAdjusting=true;
				int val=scrollX.getValue();
				if (!scrollYAdjusting) {
					ArrayList<Integer> l=alignmentModel.getAlignedSrcSequences(val);
					int v=getMinimum(l);
					if (v!=-1) {
						scrollY.setValue(v);
					}
				}
				scrollXAdjusting=false;
				refresh();
			}
			
		});
		scrollY.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (scrollYAdjusting) return;
				scrollYAdjusting=true;
				int val=scrollY.getValue();
				if (!scrollXAdjusting) {
					ArrayList<Integer> l=alignmentModel.getAlignedDestSequences(val);
					int v=getMinimum(l);
					if (v!=-1) {
						scrollX.setValue(v);
					}
				}
				scrollYAdjusting=false;
				refresh();
			}
		});
		model.addAlignmentListener(new AlignmentListener() {
			public void alignmentChanged() {
				refresh();
			}});
	}


	int getMinimum(ArrayList<Integer> l) {
		if (l==null || l.size()==0) return -1;
		int min=l.get(0);
		for (Integer i:l) {
			if (i<min) min=i;
		}
		return min;
	}

	
	void refresh() {
		int z=scrollX.getValue();
		int max=z+MAX_SENTENCES;
		int j=0;
		for (int i=z;i<max;i++) {
			if (i==currentSentenceX) {
				sentencesX[j].setBackground(Color.PINK);
			} else if (i==tmpX) {
				sentencesX[j].setBackground(Color.GREEN);
			} else {
				sentencesX[j].setBackground(Color.WHITE);
			}
			if (i<model1.getSize()) {
				sentencesX[j].setText(model1.getElementAt(i));
				sentencesX[j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			} else {
				sentencesX[j].setText("");
				sentencesX[j].setBorder(BorderFactory.createEmptyBorder());
			}
			j++;
		}
		z=scrollY.getValue();
		max=z+MAX_SENTENCES;
		j=0;
		for (int i=z;i<max;i++) {
			if (i==currentSentenceY) {
				sentencesY[j].setBackground(Color.PINK);
			} else if (i==tmpY) {
				sentencesY[j].setBackground(Color.GREEN);
			} else {
				sentencesY[j].setBackground(Color.WHITE);
			}
			if (i<model2.getSize()) {
				sentencesY[j].setText(model2.getElementAt(i));
				sentencesY[j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			} else {
				sentencesY[j].setText("");
				sentencesY[j].setBorder(BorderFactory.createEmptyBorder());
			}
			j++;
		}
		middle.repaint();
	}



	private JComponent createMiddleComponent() {
		return new JComponent() {
			
			int selectedSrc=-1;
			int selectedDest=-1;
			
			class Alignment {
				
				Line2D.Double line;
				int src;
				int dest;
				
				Alignment(Line2D.Double line,int src,int dest) {
					this.line=line;
					this.src=src;
					this.dest=dest;
				}
			}
			
			ArrayList<Alignment> alignments=new ArrayList<Alignment>();
			
			private final Dimension preferredSize=new Dimension(100,100);
			
			{
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						int oldSrc=selectedSrc;
						for (Alignment a:alignments) {
							Line2D.Double l=a.line;
							if (l.ptLineDist(e.getX(),e.getY())<10) {
								selectedSrc=a.src;
								selectedDest=a.dest;
								repaint();
								return;
							}
						}
						selectedSrc=-1;
						selectedDest=-1;
						if (oldSrc!=selectedSrc) repaint();
					}
				});
				
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						for (Alignment a:alignments) {
							Line2D.Double l=a.line;
							if (l.ptLineDist(e.getX(),e.getY())<10) {
								alignmentModel.unAlign(a.src,a.dest);
								repaint();
								return;
							}
						}
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						int oldSrc=selectedSrc;
						selectedSrc=-1;
						selectedDest=-1;
						if (oldSrc!=selectedSrc) repaint();
					}
				});
			}
			
			@Override
			public Dimension getPreferredSize() {
				return preferredSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return preferredSize;
			}
			
			
			BasicStroke stroke=new BasicStroke(10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
			Composite composite=AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f);
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2=(Graphics2D)g;
				g.setColor(Color.LIGHT_GRAY);
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				int x=scrollX.getValue();
				int limit=x+MAX_SENTENCES;
				if (limit>=model1.getSize()) limit=model1.getSize();
				g2.setStroke(stroke);
				alignments.clear();
				boolean drawn=false;
				for (int i=x;i<limit;i++) {
					ArrayList<Integer> a=alignmentModel.getAlignedSrcSequences(i);
					for (Integer u:a) {
						int indexDest=u.intValue()-scrollY.getValue();
						if 	(indexDest>=0 && indexDest<MAX_SENTENCES) {
							JTextArea areaSrc=sentencesX[i-x];
							int srcY=areaSrc.getY()+areaSrc.getHeight()/2;
							int srcX=10;
							JTextArea areaDest=sentencesY[indexDest];
							int destY=areaDest.getY()+areaDest.getHeight()/2;
							Line2D.Double line=new Line2D.Double(srcX,srcY,getWidth()-10,destY);
							alignments.add(new Alignment(line,i,u.intValue()));
							if ((i==currentSentenceX && u.intValue()==tmpY) ||
								(i==tmpX && u.intValue()==currentSentenceY)) {
								g2.setColor(Color.BLACK);
								drawn=true;
							}
							else if (i==selectedSrc && u.intValue()==selectedDest) g2.setColor(Color.BLACK);
							else g2.setColor(Color.RED);
							g2.draw(line);
						}
					}
				}
				/* Now, we draw the selection line, if any */
				if (drawn) return;
				int A,B;
				if (currentSentenceX!=-1 && tmpY!=-1) {
					A=currentSentenceX;
					B=tmpY;
				} else if (tmpX!=-1 && currentSentenceY!=-1) {
					A=tmpX;
					B=currentSentenceY;
				} else return;
				A=A-scrollX.getValue();
				if (A<0 || A>=MAX_SENTENCES) return;
				B=B-scrollY.getValue();
				if (B<0 || B>=MAX_SENTENCES) return;
				g2.setColor(Color.YELLOW);
				Composite old=g2.getComposite();
				g2.setComposite(composite);
				JTextArea areaSrc=sentencesX[A];
				int srcY=areaSrc.getY()+areaSrc.getHeight()/2;
				JTextArea areaDest=sentencesY[B];
				int destY=areaDest.getY()+areaDest.getHeight()/2;
				g2.drawLine(10,srcY,getWidth()-10,destY);
				g2.setComposite(old);
			}
		};
	}
}
