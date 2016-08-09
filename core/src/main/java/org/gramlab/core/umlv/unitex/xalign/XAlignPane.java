/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.xalign;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;

import org.gramlab.core.umlv.unitex.listeners.AlignmentListener;

public class XAlignPane extends JPanel {

	final JList list1;
	final JList list2;
	final MyBean bean1;
	final MyBean bean2;
	final JComponent middle;
	final JScrollPane scrollPane1;
	final JScrollPane scrollPane2;
	final XAlignModel alignmentModel;

	public XAlignPane(final ConcordanceModel model1,
			final ConcordanceModel model2, XAlignModel model, Font sourceFont,
			Font targetFont) {
		super(new GridBagLayout());
		this.alignmentModel = model;
		middle = createMiddleComponent();
		bean1 = new MyBean();
		list1 = createList(model1, bean1, true, sourceFont);
		list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane1 = new JScrollPane(list1);
		scrollPane1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		bean2 = new MyBean();
		list2 = createList(model2, bean2, false, targetFont);
		list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane2 = new JScrollPane(list2);
		scrollPane1.getVerticalScrollBar().addAdjustmentListener(
				createAdjustmentListener(bean1, bean2, list1, list2, true));
		scrollPane1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.getVerticalScrollBar().addAdjustmentListener(
				createAdjustmentListener(bean2, bean1, list2, list1, false));
		scrollPane2
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		list1.addMouseListener(createMouseListener(bean1, bean2, list1, list2,
				true));
		list2.addMouseListener(createMouseListener(bean2, bean1, list2, list1,
				false));
		list1.addMouseMotionListener(createMouseMotionListener(bean1, bean2,
				list1, list2));
		list2.addMouseMotionListener(createMouseMotionListener(bean2, bean1,
				list2, list1));
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		add(scrollPane1, gbc);
		gbc.weightx = 0;
		add(middle, gbc);
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(scrollPane2, gbc);
		alignmentModel.addAlignmentListener(new AlignmentListener() {
			@Override
			public void alignmentChanged(AlignmentEvent e) {
				refresh();
			}
		});
		model1.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				refresh();
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				refresh();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				refresh();
			}
		});
		model2.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				refresh();
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				refresh();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				refresh();
			}
		});
	}

	private MouseMotionAdapter createMouseMotionListener(final MyBean b1,
			final MyBean b2, final JList l1, final JList l2) {
		return new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int sentenceNumber2;
				if ((sentenceNumber2 = b2.getCurrentClickedSentenceNumber()) != -1
						&& ((ConcordanceModel) l2.getModel())
								.getSentenceIndex(sentenceNumber2) != -1) {
					final int oldSentenceNumber1 = b1
							.getCurrentFlownOverSentenceNumber();
					final int oldIndex1 = ((ConcordanceModel) l1.getModel())
							.getSentenceIndex(oldSentenceNumber1);
					final int newIndex1 = l1.locationToIndex(e.getPoint());
					final int newSentenceNumber1 = ((ConcordanceModel) l1
							.getModel()).getSentence(newIndex1);
					if (newSentenceNumber1 != oldSentenceNumber1) {
						b1.setCurrentFlownOverSentenceNumber(newSentenceNumber1);
						l1.paintImmediately(l1.getCellBounds(oldIndex1,
								oldIndex1));
						l1.paintImmediately(l1.getCellBounds(newIndex1,
								newIndex1));
						refresh();
					}
				}
			}
		};
	}

	private MouseAdapter createMouseListener(final MyBean b1, final MyBean b2,
			final JList l1, final JList l2, final boolean fromSrc) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index1 = l1.locationToIndex(e.getPoint());
				final int sentenceNumber1 = ((ConcordanceModel) l1.getModel())
						.getSentence(index1);
				if (sentenceNumber1 != -1
						&& sentenceNumber1 == b1
								.getCurrentClickedSentenceNumber()) {
					/*
					 * If we click on the selected area in the same row, we
					 * unselect it.
					 */
					b1.setCurrentClickedSentenceNumber(-1);
					l1.paintImmediately(l1.getCellBounds(index1, index1));
					refresh();
					return;
				}
				int sentenceNumber2;
				int index2;
				if ((sentenceNumber2 = b2.getCurrentClickedSentenceNumber()) != -1
						&& (index2 = ((ConcordanceModel) l2.getModel())
								.getSentenceIndex(sentenceNumber2)) != -1) {
					/*
					 * If we click on a dest area while a src one is selected,
					 * then we must align them
					 */
					if (fromSrc) {
						alignmentModel.changeAlignment(sentenceNumber1,
								sentenceNumber2);
					} else {
						alignmentModel.changeAlignment(sentenceNumber2,
								sentenceNumber1);
					}
					b2.setCurrentClickedSentenceNumber(-1);
					final int old = b1.getCurrentFlownOverSentenceNumber();
					b1.setCurrentFlownOverSentenceNumber(-1);
					index1 = ((ConcordanceModel) l1.getModel())
							.getSentenceIndex(old);
					if (index1 != -1) {
						l1.paintImmediately(l1.getCellBounds(index1, index1));
					}
					l2.paintImmediately(l2.getCellBounds(index2, index2));
					refresh();
					return;
				}
				/* Otherwise, we have to select the clicked area */
				b1.setCurrentClickedSentenceNumber(sentenceNumber1);
				l1.paintImmediately(l1.getCellBounds(index1, index1));
				refresh();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				final int sentenceNumber2 = b2
						.getCurrentClickedSentenceNumber();
				if (sentenceNumber2 != -1
						&& ((ConcordanceModel) l2.getModel())
								.getSentenceIndex(sentenceNumber2) != -1) {
					final int index1 = l1.locationToIndex(e.getPoint());
					final int sentenceNumber1 = ((ConcordanceModel) l1
							.getModel()).getSentence(index1);
					b1.setCurrentFlownOverSentenceNumber(sentenceNumber1);
					l1.paintImmediately(l1.getCellBounds(index1, index1));
					refresh();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				final int sentenceNumber2 = b2
						.getCurrentClickedSentenceNumber();
				if (sentenceNumber2 != -1
						&& ((ConcordanceModel) l2.getModel())
								.getSentenceIndex(sentenceNumber2) != -1) {
					final int sentenceNumber1 = b1
							.getCurrentFlownOverSentenceNumber();
					b1.setCurrentFlownOverSentenceNumber(-1);
					if (sentenceNumber1 != -1) {
						final int index1 = ((ConcordanceModel) l1.getModel())
								.getSentenceIndex(sentenceNumber1);
						if (index1 != -1) {
							l1.paintImmediately(l1
									.getCellBounds(index1, index1));
						}
					}
					refresh();
				}
			}
		};
	}

	private AdjustmentListener createAdjustmentListener(final MyBean b1,
			final MyBean b2, final JList l1, final JList l2,
			final boolean fromSrc) {
		return new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (b1.isScrollAdjusting())
					return;
				try {
					b1.setScrollAdjusting(true);
					if (!b2.isScrollAdjusting()) {
						final ConcordanceModel model1 = (ConcordanceModel) l1
								.getModel();
						final ConcordanceModel model2 = (ConcordanceModel) l2
								.getModel();
						final int last = l1.getLastVisibleIndex();
						for (int index = l1.getFirstVisibleIndex(); index <= last; index++) {
							final int val = model1.getSentence(index);
							final ArrayList<Integer> l = alignmentModel
									.getAlignedSequences(val, fromSrc);
							final int v = getMinimumVisibleIndex(l, model2);
							// System.out.println("pour phrase "+val+", plus petit="+v);
							if (v != -1) {
								l2.ensureIndexIsVisible(l2.getModel().getSize() - 1);
								l2.ensureIndexIsVisible(v);
								break;
							}
						}
					}
				} finally {
					b1.setScrollAdjusting(false);
				}
				refresh();
			}
		};
	}

	/**
	 * l represents a list of sentence numbers. model will be used to know which
	 * of those sentences are visible. Then, the function will return the
	 * minimal visible index, or -1 if not found.
	 */
	int getMinimumVisibleIndex(ArrayList<Integer> l, ConcordanceModel model) {
		if (l == null || l.size() == 0)
			return -1;
		int min = model.getSentenceIndex(l.get(0));
		for (final Integer i : l) {
			if (model.getSentenceIndex(i) < min)
				min = model.getSentenceIndex(i);
		}
		return min;
	}

	void refresh() {
		middle.repaint();
	}

	private JComponent createMiddleComponent() {
		return new JComponent() {
			int srcSelectedSentenceIndex = -1;
			int destSelectedSentenceIndex = -1;

			class Alignment {
				final Line2D.Double line;
				/*
				 * We consider alignements between list cell indices, not
				 * sentence numbers.
				 */
				final int srcIndex;
				final int destIndex;

				Alignment(Line2D.Double line, int src, int dest) {
					this.line = line;
					this.srcIndex = src;
					this.destIndex = dest;
				}
			}

			final ArrayList<Alignment> alignments = new ArrayList<Alignment>();
			private final Dimension preferredSize = new Dimension(100, 100);
			{
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						final int old = srcSelectedSentenceIndex;
						for (final Alignment a : alignments) {
							final Line2D.Double l = a.line;
							if (l.ptLineDist(e.getX(), e.getY()) < 10) {
								srcSelectedSentenceIndex = a.srcIndex;
								destSelectedSentenceIndex = a.destIndex;
								repaint();
								return;
							}
						}
						srcSelectedSentenceIndex = -1;
						destSelectedSentenceIndex = -1;
						if (old != srcSelectedSentenceIndex)
							repaint();
					}
				});
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						for (final Alignment a : alignments) {
							final Line2D.Double l = a.line;
							if (l.ptLineDist(e.getX(), e.getY()) < 10) {
								int srcSentenceNumber = a.srcIndex;
								if (list1.getModel() instanceof ConcordanceModel) {
									srcSentenceNumber = ((ConcordanceModel) list1
											.getModel())
											.getSentence(srcSentenceNumber);
								}
								int destSentenceNumber = a.destIndex;
								if (list2.getModel() instanceof ConcordanceModel) {
									destSentenceNumber = ((ConcordanceModel) list2
											.getModel())
											.getSentence(destSentenceNumber);
								}
								alignmentModel.unAlign(srcSentenceNumber,
										destSentenceNumber);
								repaint();
								return;
							}
						}
					}

					@Override
					public void mouseExited(MouseEvent e) {
						final int old = srcSelectedSentenceIndex;
						srcSelectedSentenceIndex = -1;
						destSelectedSentenceIndex = -1;
						if (old != srcSelectedSentenceIndex)
							repaint();
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

			final BasicStroke stroke = new BasicStroke(10,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			final Composite composite = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.7f);

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				final Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.LIGHT_GRAY);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(stroke);
				alignments.clear();
				boolean drawn = false;
				final int first1 = list1.getFirstVisibleIndex();
				final int last1 = list1.getLastVisibleIndex();
				final int first2 = list2.getFirstVisibleIndex();
				final int last2 = list2.getLastVisibleIndex();
				if (first1 == -1 || first2 == -1) {
					/* Nothing to do if no cell is visible */
					return;
				}
				final int srcHeightOrigin = (int) scrollPane1.getViewport()
						.getViewPosition().getY();
				final int destHeightOrigin = (int) scrollPane2.getViewport()
						.getViewPosition().getY();
				final int currentSentenceNumber1 = bean1
						.getCurrentClickedSentenceNumber();
				final int currentSentenceNumber2 = bean2
						.getCurrentClickedSentenceNumber();
				final int currentFlownOverSentenceNumber1 = bean1
						.getCurrentFlownOverSentenceNumber();
				final int currentFlownOverSentenceNumber2 = bean2
						.getCurrentFlownOverSentenceNumber();
				for (int srcIndex = first1; srcIndex <= last1; srcIndex++) {
					int srcSentence = srcIndex;
					if (list1.getModel() instanceof ConcordanceModel) {
						srcSentence = ((ConcordanceModel) list1.getModel())
								.getSentence(srcIndex);
					}
					final ArrayList<Integer> a = alignmentModel
							.getAlignedSrcSequences(srcSentence);
					for (final Integer destSentence : a) {
						int destIndex = destSentence;
						if (list2.getModel() instanceof ConcordanceModel) {
							destIndex = ((ConcordanceModel) list2.getModel())
									.getSentenceIndex(destIndex);
							if (destIndex == -1) {
								/* If the sentence is not visible, we do nothing */
								continue;
							}
						}
						if (destIndex >= first2 && destIndex <= last2) {
							final Rectangle srcBounds = list1.getCellBounds(
									srcIndex, srcIndex);
							final int srcY = (int) (srcBounds.getY() + srcBounds
									.getHeight() / 2) - srcHeightOrigin;
							final int srcX = 10;
							final Rectangle destBounds = list2.getCellBounds(
									destIndex, destIndex);
							final int destY = (int) (destBounds.getY() + destBounds
									.getHeight() / 2) - destHeightOrigin;
							final Line2D.Double line = new Line2D.Double(srcX,
									srcY, getWidth() - 10, destY);
							alignments.add(new Alignment(line, srcIndex,
									destIndex));
							if ((srcIndex == currentSentenceNumber1 && destIndex == currentFlownOverSentenceNumber2)
									|| (srcIndex == currentFlownOverSentenceNumber1 && destIndex == currentSentenceNumber2)) {
								g2.setColor(Color.BLACK);
								drawn = true;
							} else if (srcIndex == srcSelectedSentenceIndex
									&& destIndex == destSelectedSentenceIndex)
								g2.setColor(Color.BLACK);
							else
								g2.setColor(Color.RED);
							g2.draw(line);
						}
					}
				}
				/* Now, we draw the selection line, if any */
				if (drawn)
					return;
				int A, B;
				if (currentSentenceNumber1 != -1
						&& currentFlownOverSentenceNumber2 != -1) {
					A = ((ConcordanceModel) list1.getModel())
							.getSentenceIndex(currentSentenceNumber1);
					if (A == -1)
						return;
					B = ((ConcordanceModel) list2.getModel())
							.getSentenceIndex(currentFlownOverSentenceNumber2);
					if (B == -1)
						return;
				} else if (currentFlownOverSentenceNumber1 != -1
						&& currentSentenceNumber2 != -1) {
					A = ((ConcordanceModel) list1.getModel())
							.getSentenceIndex(currentFlownOverSentenceNumber1);
					if (A == -1)
						return;
					B = ((ConcordanceModel) list2.getModel())
							.getSentenceIndex(currentSentenceNumber2);
					if (B == -1)
						return;
				} else
					return;
				g2.setColor(Color.YELLOW);
				final Composite old = g2.getComposite();
				g2.setComposite(composite);
				final Rectangle srcBounds = list1.getCellBounds(A, A);
				final int srcY = (int) (srcBounds.getY() + srcBounds
						.getHeight() / 2) - srcHeightOrigin;
				final Rectangle destBounds = list2.getCellBounds(B, B);
				final int destY = (int) (destBounds.getY() + destBounds
						.getHeight() / 2) - destHeightOrigin;
				g2.drawLine(10, srcY, getWidth() - 10, destY);
				g2.setComposite(old);
			}
		};
	}

	JList createList(ListModel model, final MyBean bean, final boolean left,
			final Font font) {
		final JList list = new JList(model) {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}

			@Override
			public String getToolTipText(MouseEvent event) {
				return null;
			}

			@Override
			public String getToolTipText() {
				return null;
			}
		};
		list.setCellRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList l,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				/* First we compute the size of the sentence number label */
				label.setText("" + index);
				final ConcordanceModel m = (ConcordanceModel) l.getModel();
				final String s = "" + m.getNumberOfSentences();
				final int labelWidth = 5 + s.length() * 10;
				label.setPreferredSize(new Dimension(labelWidth, 0));
				/* Then we test the nature of the model */
				JTextComponent textComponent;
				JTextComponent fooTextComponent;
				if (m.isMatchedSentenceIndex(index)
						&& m.getMode() != DisplayMode.TEXT) {
					/*
					 * If we have to display a sentence with matches, we must
					 * use an HTML rendering
					 */
					textComponent = textPane;
					fooTextComponent = fooTextPane;
					value = "<html>" + "<font style=\"Font: " + font.getSize()
							+ "pt " + font.getName() + ";\">" + value
							+ "</font></html>";
					textComponent.setText((String) value);
					fooTextComponent.setText((String) value);
				} else {
					textComponent = textArea;
					fooTextComponent = fooTextArea;
					textComponent.setText((String) value);
					fooTextComponent.setText((String) value);
				}
				if (fooTextComponent != lastFooTextComponent) {
					/* If the text component we need is not the current one */
					pp.remove(lastFooTextComponent);
					pp.add(fooTextComponent, BorderLayout.NORTH);
					panel.remove(lastTextComponent);
					panel.add(textComponent, BorderLayout.CENTER);
					lastFooTextComponent = fooTextComponent;
					lastTextComponent = textComponent;
				}
				fooTextComponent.setMinimumSize(new Dimension(list.getWidth()
						- labelWidth, 10));
				fooTextComponent.setMaximumSize(new Dimension(list.getWidth()
						- labelWidth, 10000));
				p.setPreferredSize(new Dimension(list.getWidth() - labelWidth,
						40));
				pp.setPreferredSize(new Dimension(list.getWidth() - labelWidth,
						400));
				pp.setSize(pp.getLayout().preferredLayoutSize(pp));
				f.pack();
				panel.invalidate();
				textComponent.setPreferredSize(fooTextComponent
						.getPreferredSize());
				final int sentenceNumber = m.getSentence(index);
				if (sentenceNumber == bean.getCurrentClickedSentenceNumber())
					textComponent.setBackground(Color.PINK);
				else if (sentenceNumber == bean
						.getCurrentFlownOverSentenceNumber())
					textComponent.setBackground(Color.GREEN);
				else
					textComponent.setBackground(Color.WHITE);
				label.setText("" + sentenceNumber);
				return panel;
			}

			private JTextComponent lastTextComponent;
			private JTextComponent lastFooTextComponent;
			private final JTextArea textArea = new JTextArea();
			private final JTextArea fooTextArea = new JTextArea();
			private final JTextPane textPane = new JTextPane();
			private final JTextPane fooTextPane = new JTextPane();
			private final JFrame f = new JFrame();
			private final JPanel p = new JPanel();
			private final JPanel pp = new JPanel(new BorderLayout());
			private final JPanel panel = new JPanel(new BorderLayout());
			private final JLabel label = new JLabel();
			{
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				textArea.setFont(font);
				fooTextArea.setWrapStyleWord(true);
				fooTextArea.setLineWrap(true);
				fooTextArea.setBorder(BorderFactory
						.createLineBorder(Color.BLACK));
				fooTextArea.setFont(font);
				pp.add(fooTextArea, BorderLayout.NORTH);
				pp.add(p, BorderLayout.CENTER);
				f.setContentPane(pp);
				label.setOpaque(true);
				label.setBorder(new EmptyBorder(0, 3, 0, 3) {
					@Override
					public void paintBorder(Component c, Graphics g, int x,
							int y, int width, int height) {
						super.paintBorder(c, g, x, y, width, height);
						g.setColor(Color.BLACK);
						g.drawLine(x, y, x, y + height - 1);
						g.drawLine(x + width - 1, y, x + width - 1, y + height
								- 1);
					}
				});
				if (left) {
					panel.add(label, BorderLayout.WEST);
					label.setHorizontalAlignment(SwingConstants.RIGHT);
				} else
					panel.add(label, BorderLayout.EAST);
				panel.add(textArea, BorderLayout.CENTER);
				textPane.setEditorKit(new HTMLEditorKit());
				textPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				textPane.setFont(font);
				fooTextPane.setEditorKit(new HTMLEditorKit());
				fooTextPane.setBorder(BorderFactory
						.createLineBorder(Color.BLACK));
				fooTextPane.setFont(font);
				lastFooTextComponent = fooTextArea;
				lastTextComponent = textArea;
			}
		});
		list.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				final ListModel m = list.getModel();
				if (m instanceof ConcordanceModel) {
					final ConcordanceModel m2 = (ConcordanceModel) m;
					m2.refresh();
				}
			}
		});
		return list;
	}

	public class MyBean {
		private int currentClickedSentenceNumber;
		private int currentFlownOverSentenceNumber;
		private boolean scrollAdjusting;

		public MyBean() {
			currentClickedSentenceNumber = -1;
			currentFlownOverSentenceNumber = -1;
			scrollAdjusting = false;
		}

		public int getCurrentClickedSentenceNumber() {
			return currentClickedSentenceNumber;
		}

		public void setCurrentClickedSentenceNumber(int currentClickedSentence) {
			this.currentClickedSentenceNumber = currentClickedSentence;
		}

		public int getCurrentFlownOverSentenceNumber() {
			return currentFlownOverSentenceNumber;
		}

		public void setCurrentFlownOverSentenceNumber(
				int currentFlownOverSentence) {
			this.currentFlownOverSentenceNumber = currentFlownOverSentence;
		}

		public boolean isScrollAdjusting() {
			return scrollAdjusting;
		}

		public void setScrollAdjusting(boolean scrollAdjusting) {
			this.scrollAdjusting = scrollAdjusting;
		}
	}
}
