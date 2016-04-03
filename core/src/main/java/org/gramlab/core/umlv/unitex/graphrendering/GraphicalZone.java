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
package fr.umlv.unitex.graphrendering;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.undo.UndoableEdit;

import fr.umlv.unitex.MyCursors;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.frames.GraphFrame;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.print.PrintManager;
import fr.umlv.unitex.undo.AddBoxEdit;
import fr.umlv.unitex.undo.BoxTextEdit;
import fr.umlv.unitex.undo.MultipleEdit;
import fr.umlv.unitex.undo.RemoveBoxEdit;
import fr.umlv.unitex.undo.SelectEdit;
import fr.umlv.unitex.undo.TransitionEdit;
import fr.umlv.unitex.undo.TranslationGroupEdit;

/**
 * This class describes a component on which a graph can be drawn.
 *
 * @author Sébastien Paumier
 */
public class GraphicalZone extends GenericGraphicalZone implements Printable {
	boolean dragBegin = true;
	int dX;
	int dY;
	int popupX = -1, popupY = -1;
	JMenu submenu;

	final int dragTreshold = 3;
	boolean dragTresholdReached = false;
	boolean textFieldWasModified = false;
	boolean ignoreThisMouseAction = false;
	int X_pressed_raw;
	int Y_pressed_raw;

	GenericGraphBox rolloveredBox = null;
    File subgraphFileSelected = null;

	/**
	 * Constructs a new <code>GraphicalZone</code>.
	 *
	 * @param w
	 *            width of the drawing area
	 * @param h
	 *            height of the drawing area
	 * @param t
	 *            text field to edit box contents
	 * @param p
	 *            frame that contains the component
	 */
	public GraphicalZone(GraphIO gio, TextField t, GraphFrame p,
			GraphDecorator diff) {
		super(gio, t, p, diff);
		if (diff == null) {
			/* No need to have mouse listeners on a read-only diff display */
			MyMouseListener m = new MyMouseListener();
			addMouseListener(m);
			addMouseMotionListener(m);
		}
		createPopup();
	}

	Action surroundWithInputVar;

	public Action getSurroundWithInputVarAction() {
		return surroundWithInputVar;
	}

	Action surroundWithOutputVar;

	public Action getSurroundWithOutputVarAction() {
		return surroundWithOutputVar;
	}

	Action surroundWithMorphologicalMode;

	public Action getSurroundWithMorphologicalModeAction() {
		return surroundWithMorphologicalMode;
	}

	Action surroundWithLeftContext;

	public Action getSurroundWithLeftContextAction() {
		return surroundWithLeftContext;
	}

	Action surroundWithRightContext;

	public Action getSurroundWithRightContextAction() {
		return surroundWithRightContext;
	}

	Action surroundWithNegativeRightContext;

	public Action getSurroundWithNegativeRightContextAction() {
		return surroundWithNegativeRightContext;
	}

        Action addGenericGraphIndicator;

        public Action getAddGenericGraphIndicator() {
		return addGenericGraphIndicator;
	}

	private void createPopup() {
		final JPopupMenu popup = new JPopupMenu();
		final Action newBox = new AbstractAction("Create box") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphBox b = (GraphBox) createBox(
						(int) (popupX / scaleFactor),
						(int) (popupY / scaleFactor));
				// if some boxes are selected, we rely them to the new one
				if (!selectedBoxes.isEmpty()) {
					addTransitionsFromSelectedBoxes(b, false);
				}
				// then, the only selected box is the new one
				unSelectAllBoxes();
				b.setSelected(true);
				selectedBoxes.add(b);
				postEdit(new SelectEdit(selectedBoxes));
				fireGraphTextChanged(b.content);
				fireGraphChanged(true);
				fireBoxSelectionChanged();
			}
		};
		newBox.setEnabled(true);
		newBox.putValue(Action.SHORT_DESCRIPTION, "Create a new box");
        popup.add(new JMenuItem(newBox));

        final Action openSubgraph = new AbstractAction("Open subgraph") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subgraphFileSelected == null) return;
                GlobalProjectManager.search(null)
                        .getFrameManagerAs(InternalFrameManager.class)
                        .newGraphFrame(subgraphFileSelected);
            }
        };
        openSubgraph.setEnabled(false);
        openSubgraph.putValue(Action.SHORT_DESCRIPTION,
                "Open the subgraph if you point the mouse to a call");
        popup.add(new JMenuItem(openSubgraph));

		popup.addSeparator();
		submenu = new JMenu("Surround with...");
		surroundWithInputVar = new AbstractAction("Input variable") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				final String name = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newVariableInsertionDialog(true);
				if (name == null || name.equals(""))
					return;
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(), "$"
								+ name + "(", "$" + name + ")");
			}
		};
		surroundWithInputVar.setEnabled(false);
		surroundWithInputVar.putValue(Action.SHORT_DESCRIPTION,
				"Surround box selection with an input variable");
		submenu.add(new JMenuItem(surroundWithInputVar));
		surroundWithOutputVar = new AbstractAction("Output variable") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				final String name = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newVariableInsertionDialog(false);
				if (name == null || name.equals(""))
					return;
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(),
						"$|" + name + "(", "$|" + name + ")");
			}
		};
		surroundWithOutputVar.setEnabled(false);
		surroundWithOutputVar.putValue(Action.SHORT_DESCRIPTION,
				"Surround box selection with an output variable");
		submenu.add(new JMenuItem(surroundWithOutputVar));
		submenu.addSeparator();
		surroundWithMorphologicalMode = new AbstractAction("Morphological mode") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(),
						"$<", "$>");
			}
		};
		surroundWithMorphologicalMode.setEnabled(false);
		surroundWithMorphologicalMode.putValue(Action.SHORT_DESCRIPTION,
				"Surround box selection with morphological mode tags");
		submenu.add(new JMenuItem(surroundWithMorphologicalMode));
		submenu.addSeparator();
		surroundWithLeftContext = new AbstractAction("Left context") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(),
						"$*", null);
			}
		};
		surroundWithLeftContext.setEnabled(false);
		surroundWithLeftContext.putValue(Action.SHORT_DESCRIPTION,
				"Inserts left context mark before box selection");
		submenu.add(new JMenuItem(surroundWithLeftContext));
		surroundWithRightContext = new AbstractAction("Right context") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(),
						"$[", "$]");
			}
		};
		surroundWithRightContext.setEnabled(false);
		surroundWithRightContext.putValue(Action.SHORT_DESCRIPTION,
				"Surround box selection with right context tags");
		submenu.add(new JMenuItem(surroundWithRightContext));
		surroundWithNegativeRightContext = new AbstractAction(
				"Negative right context") {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				surroundWithBoxes(
						(ArrayList<GenericGraphBox>) selectedBoxes.clone(),
						"$![", "$]");
			}
		};
		surroundWithNegativeRightContext.setEnabled(false);
		surroundWithNegativeRightContext.putValue(Action.SHORT_DESCRIPTION,
				"Surround box selection with negative right context tags");
		submenu.add(new JMenuItem(surroundWithNegativeRightContext));

                addGenericGraphIndicator = new AbstractAction(
                        "Generic graph indicator") {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void actionPerformed(ActionEvent e) {
                        surroundWithBoxes(
                            (ArrayList<GenericGraphBox>) selectedBoxes.clone(),
                            "$G", null);
                    }
                };
                addGenericGraphIndicator.setEnabled(false);
                addGenericGraphIndicator.putValue(Action.SHORT_DESCRIPTION,
                        "Insert generic graph mark before the selected box");
                submenu.add(new JMenuItem(addGenericGraphIndicator));
		popup.add(submenu);
		final Action mergeBoxesAction = new AbstractAction("Merge boxes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mergeSelectedBoxes();
			}
		};
		mergeBoxesAction.putValue(Action.SHORT_DESCRIPTION,
				"Merge selected boxes");
		mergeBoxesAction.setEnabled(false);
		final JMenuItem mergeBoxes = new JMenuItem(mergeBoxesAction);
		popup.add(mergeBoxes);
		final Action newGraphAction = new AbstractAction("Export as new graph") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final MultipleSelection selection = new MultipleSelection(
						selectedBoxes, true);
				final ArrayList<GenericGraphBox> inputBoxes = new ArrayList<GenericGraphBox>();
				final ArrayList<GenericGraphBox> outputBoxes = new ArrayList<GenericGraphBox>();
				computeInputOutputBoxes(selectedBoxes, inputBoxes, outputBoxes);
				final boolean[] inputBox = new boolean[selectedBoxes.size()];
				final boolean[] outputBox = new boolean[selectedBoxes.size()];
				for (int i = 0; i < selectedBoxes.size(); i++) {
					final GenericGraphBox box = selectedBoxes.get(i);
					if (inputBoxes.contains(box))
						inputBox[i] = true;
					if (outputBoxes.contains(box))
						outputBox[i] = true;
				}
				createNewGraph(selection, inputBox, outputBox);
			}
		};
		newGraphAction.putValue(Action.SHORT_DESCRIPTION,
				"Create a new graph from selected boxes");
		newGraphAction.setEnabled(false);
		final JMenuItem newGraph = new JMenuItem(newGraphAction);
		popup.add(newGraph);
		addBoxSelectionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final boolean selected = selectedBoxes.size() != 0;
				submenu.setEnabled(selected);
				surroundWithInputVar.setEnabled(selected);
				surroundWithOutputVar.setEnabled(selected);
				surroundWithMorphologicalMode.setEnabled(selected);
				surroundWithLeftContext.setEnabled(selected);
				surroundWithRightContext.setEnabled(selected);
				surroundWithNegativeRightContext.setEnabled(selected);
				mergeBoxesAction.setEnabled(selected);
				newGraphAction.setEnabled(selected);
                                addGenericGraphIndicator.setEnabled(selected);
			}
		});
		popup.addSeparator();
		final Action save = new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.saveGraph();
			}
		};
		popup.add(new JMenuItem(save));
		final Action saveAs = new AbstractAction("Save as...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.saveAsGraph();
			}
		};

		popup.add(new JMenuItem(saveAs));

		final JMenu exportMenu = GraphMenuBuilder.createExportMenu(this);
		popup.add(exportMenu);

		final Action setup = new AbstractAction("Page Setup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintManager.pageSetup();
			}
		};
		popup.add(new JMenuItem(setup));
		final Action print = new AbstractAction("Print...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintManager.print(parentFrame);
			}
		};
		popup.add(new JMenuItem(print));
		popup.addSeparator();
		final JMenu tools = new JMenu("Tools");
		final JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.sortNodeLabel();
			}
		});
		final JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					parentFrame.setSelected(true);
				} catch (final PropertyVetoException e1) {
					/* */
				}
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).newGraphPathDialog();
			}
		});
		final JMenuItem compileFST = new JMenuItem("Compile FST2");
		compileFST.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.compileGraph();
			}
		});
		final JMenuItem flatten = new JMenuItem("Compile & Flatten FST2");
		flatten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UnitexFrame.compileAndFlattenGraph();
			}
		});
		final JMenuItem graphCollection = new JMenuItem(
				"Build Graph Collection");
		graphCollection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).newGraphCollectionFrame();
			}
		});
		final JMenuItem svn = new JMenuItem("Look for SVN conflicts");
		svn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigManager.getManager().getSvnMonitor(null).monitor(false);
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		tools.addSeparator();
		tools.add(svn);
		final JMenu format = new JMenu("Format");
		final JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphAlignmentDialog(f);
			}
		});
		final JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.changeAntialiasingValue();
			}
		});
		final JMenuItem presentation = new JMenuItem("Presentation...");
		presentation.setAccelerator(KeyStroke
				.getKeyStroke('R', Event.CTRL_MASK));
		presentation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				final GraphPresentationInfo info = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphPresentationDialog(f.getGraphPresentationInfo(), true);
				if (info != null) {
					f.setGraphPresentationInfo(info);
				}
			}
		});
		final JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphSizeDialog(f);
			}
		});
		format.add(antialiasing);
		format.addSeparator();
		format.add(alignment);
		format.add(presentation);
		format.add(graphSize);
		final JMenu zoom = new JMenu("Zoom");
		final ButtonGroup groupe = new ButtonGroup();
		final JRadioButtonMenuItem fitInScreen = new JRadioButtonMenuItem(
				"Fit in screen");
		final JRadioButtonMenuItem fitInWindow = new JRadioButtonMenuItem(
				"Fit in window");
		final JRadioButtonMenuItem fit60 = new JRadioButtonMenuItem("60%");
		final JRadioButtonMenuItem fit80 = new JRadioButtonMenuItem("80%");
		final JRadioButtonMenuItem fit100 = new JRadioButtonMenuItem("100%");
		final JRadioButtonMenuItem fit120 = new JRadioButtonMenuItem("120%");
		final JRadioButtonMenuItem fit140 = new JRadioButtonMenuItem("140%");
		groupe.add(fitInScreen);
		groupe.add(fitInWindow);
		groupe.add(fit60);
		groupe.add(fit80);
		groupe.add(fit100);
		fit100.setSelected(true);
		groupe.add(fit120);
		groupe.add(fit140);
		fitInScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				final Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				final double scale_x = screenSize.width
						/ (double) f.getGraphicalZone().getWidth();
				final double scale_y = screenSize.height
						/ (double) f.getGraphicalZone().getHeight();
				if (scale_x < scale_y)
					f.setScaleFactor(scale_x);
				else
					f.setScaleFactor(scale_y);
			}
		});
		fitInWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				final Dimension d = f.getScroll().getSize();
				final double scale_x = (d.width - 3)
						/ (double) f.getGraphicalZone().getWidth();
				final double scale_y = (d.height - 3)
						/ (double) f.getGraphicalZone().getHeight();
				if (scale_x < scale_y)
					f.setScaleFactor(scale_x);
				else
					f.setScaleFactor(scale_y);
				f.compListener = new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e2) {
						final Dimension d2 = f.getScroll().getSize();
						final double scale_x2 = (d2.width - 3)
								/ (double) f.getGraphicalZone().getWidth();
						final double scale_y2 = (d2.height - 3)
								/ (double) f.getGraphicalZone().getHeight();
						if (scale_x2 < scale_y2)
							f.setScaleFactor(scale_x2);
						else
							f.setScaleFactor(scale_y2);
					}
				};
				f.addComponentListener(f.compListener);
			}
		});
		fit60.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				f.setScaleFactor(0.6);
			}
		});
		fit80.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				f.setScaleFactor(0.8);
			}
		});
		fit100.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				f.setScaleFactor(1.0);
			}
		});
		fit120.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				f.setScaleFactor(1.2);
			}
		});
		fit140.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = (GraphFrame) parentFrame;
				f.removeComponentListener(f.compListener);
				f.setScaleFactor(1.4);
			}
		});
		zoom.add(fitInScreen);
		zoom.add(fitInWindow);
		zoom.add(fit60);
		zoom.add(fit80);
		zoom.add(fit100);
		zoom.add(fit120);
		zoom.add(fit140);
		popup.add(tools);
		popup.add(format);
		popup.add(zoom);

		addMouseListener(new MouseAdapter() {
			void show(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupX = e.getX();
					popupY = e.getY();
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				show(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				show(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				show(e);
			}
		});

        // Support for opening subgraph by right-click
        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                openSubgraph.setEnabled(false);

                int x_tmp = (int) (popupX / scaleFactor);
                int y_tmp = (int) (popupY / scaleFactor);
                int boxSelected = getSelectedBox(x_tmp, y_tmp);
                if (boxSelected == -1) return;
                GraphBox box = (GraphBox) graphBoxes.get(boxSelected);
                final File file = box.getGraphClicked(y_tmp);
                if (file == null) return;

                subgraphFileSelected = file;
                openSubgraph.setEnabled(true);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

	}


	/**
	 * We create a new graph from a box selection.
	 *
	 * @param outputBox
	 * @param inputBox
	 */
 	protected void createNewGraph(MultipleSelection selection,
			boolean[] inputBox, boolean[] outputBox) {
		final GraphFrame f = GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.newGraphFrame(null);
		final GraphicalZone zone = f.getGraphicalZone();
		zone.pasteSelection(selection);
		/* Now, we compute the coordinates of the pasted box area */
		GenericGraphBox box = zone.graphBoxes.get(2);
		int minX = box.X;
		int minY = box.Y;
		int maxX = box.X + box.Width;
		int maxY = box.Y + box.Height;
		for (int i = 3; i < zone.graphBoxes.size(); i++) {
			box = zone.graphBoxes.get(i);
			if (box.X < minX)
				minX = box.X;
			if (box.Y < minY)
				minY = box.Y;
			if (box.X + box.Width > maxX)
				maxX = box.X + box.Width;
			if (box.Y + box.Height > maxY)
				maxY = box.Y + box.Height;
		}
		/*
		 * We adjust the pasted boxes' coordinates so that the upper left corner
		 * of the rectangle is at x=150,y=50
		 */
		final int shiftX = 150 - minX;
		final int shiftY = 50 - minY;
		for (int i = 2; i < zone.graphBoxes.size(); i++) {
			box = zone.graphBoxes.get(i);
			box.translate(shiftX, shiftY);
		}
		/*
		 * Then we adjust accordingly the positions of the initial and final
		 * states
		 */
		box = zone.graphBoxes.get(0);
		box.translateToPosition(box.X, 50 + (maxY - minY) / 2);
		box = zone.graphBoxes.get(1);
		box.translateToPosition(maxX + shiftX + 100, 50 + (maxY - minY) / 2);
		for (int i = 2; i < zone.graphBoxes.size(); i++) {
			box = zone.graphBoxes.get(i);
			if (inputBox[i - 2]) {
				zone.graphBoxes.get(0).addTransitionTo(box);
			}
			if (outputBox[i - 2]) {
				box.addTransitionTo(zone.graphBoxes.get(1));
			}
		}
		box = zone.graphBoxes.get(1);
		final Dimension d = zone.getSize();
		if (d.width < box.Y + 50) {
			d.width = box.Y + 50;
		}
		if (d.height < maxY - minY + 50) {
			d.height = maxY - minY + 50;
		}
		f.reSizeGraphicalZone(d.width, d.height);
	}

	/**
	 * This function merge all the selected boxes in one box. If a box X is
	 * linked to a box Y, then the two of them are replace by a unique box whose
	 * content is the combination of X's and Y's content. For instance, if
	 * X=one+the and Y=cat+dog+pet, then the resulting box contains:
	 *
	 * one cat+one dog+one pet+the cat+the dog+the pet
	 *
	 * If there is no link relation, then the box contents are added and the
	 * resulting box receive all incoming and outgoing transitions from X and Y.
	 */
	@SuppressWarnings("unchecked")
	protected void mergeSelectedBoxes() {
		if (selectedBoxes.size() <= 1)
			return;
		if (!mergeableBoxes(selectedBoxes))
			return;
		final ArrayList<GenericGraphBox> selection = (ArrayList<GenericGraphBox>) selectedBoxes
				.clone();
		unSelectAllBoxes();
		/*
		 * We check if the popup trigger click was on a selected box. If it is
		 * the case, we will try to make this box the one that absorbs the
		 * others in the merge process
		 */
		GenericGraphBox clicked = null;
		final int n = getSelectedBox(popupX, popupY);
		if (n != -1) {
			clicked = graphBoxes.get(n);
			if (!selection.contains(clicked)) {
				clicked = null;
			}
		}
		final MultipleEdit edit = new MultipleEdit();
		edit.addEdit(new SelectEdit(selection));
		/* Iteratively, we merge pairs of boxes until there remains only one */
		int i;
		while (selection.size() != 1) {
			GenericGraphBox a = selection.get(0);
			boolean merge = false;
			for (i = 1; i < selection.size(); i++) {
				final GenericGraphBox b = selection.get(i);
				if (a.transitions.contains(b)) {
					mergeLinkedBoxes(a, b, edit);
					selection.remove(b);
					if (b == clicked) {
						clicked = null;
					}
					edit.addEdit(new RemoveBoxEdit(b, graphBoxes, this));
					removeBox(b);
					merge = true;
					break;
				}
				if (b.transitions.contains(a)) {
					mergeLinkedBoxes(b, a, edit);
					selection.remove(a);
					if (a == clicked) {
						clicked = null;
					}
					edit.addEdit(new RemoveBoxEdit(a, graphBoxes, this));
					removeBox(a);
					merge = true;
					break;
				}
			}
			if (!merge) {
				/*
				 * If we found no linked boxes to merge, then we sum two boxes
				 */
				GenericGraphBox b;
				if (clicked == null) {
					b = selection.get(1);
				} else {
					/* if we have a preferred main box, we use it */
					a = clicked;
					if (a == selection.get(0)) {
						b = selection.get(1);
					} else {
						b = selection.get(0);
					}
				}
				mergeUnlinkedBoxes(a, b, edit);
				selection.remove(b);
				edit.addEdit(new RemoveBoxEdit(b, graphBoxes, this));
				removeBox(b);
			}
		}
		postEdit(edit);
		fireGraphChanged(true);
		repaint();
	}

	private boolean mergeableBoxes(ArrayList<GenericGraphBox> boxes) {
		for (final GenericGraphBox b : boxes) {
			/* We don't merge comment boxes nor special boxes */
			if (b.content.startsWith("/")) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge comment boxes", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (b.content.equals("$<") || b.content.equals("$>")) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge morphological mode boxes", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (b.content.equals("$[") || b.content.equals("$![")
					|| b.content.equals("$]") || b.content.equals("$*")) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge context boxes", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
                        if (b.content.equals("$G") || b.content.startsWith("$G/")) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge generic graph box", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (b.content.startsWith("$")
					&& (b.content.endsWith("(") || b.content.endsWith(")"))) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge variable boxes", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (b.transduction != null && !b.transduction.equals("")) {
				JOptionPane.showMessageDialog(null,
						"Cannot merge boxes with outputs", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * This function must be called to merge boxes a and b, when there is a link
	 * from a to b
	 */
	private void mergeLinkedBoxes(GenericGraphBox a, GenericGraphBox b,
			UndoableEdit edit) {
		String content = "";
		final ArrayList<String> foo = new ArrayList<String>();
		ArrayList<String> A_lines = a.lines;
		if (a.lines.size() == 0) {
			A_lines = new ArrayList<String>();
			A_lines.add("<E>");
		}
		ArrayList<String> B_lines = b.lines;
		if (b.lines.size() == 0) {
			B_lines = new ArrayList<String>();
			B_lines.add("<E>");
		}
		for (int i = 0; i < A_lines.size(); i++) {
			for (int j = 0; j < B_lines.size(); j++) {
				final String A = A_lines.get(i);
				final String B = B_lines.get(j);
				if (A.equals("<E>")) {
					if (B.equals("<E>")) {
						/* We don't insert <E><E>, but just <E> */
						if (!foo.contains("<E>")) {
							if (!content.equals("")) {
								content = content + "+";
							}
							content = content + "<E>";
							foo.add("<E>");
						}
					} else {
						/* We don't insert <E>ABC but just ABC */
						if (!foo.contains(B)) {
							if (!content.equals("")) {
								content = content + "+";
							}
							content = content + B;
							foo.add(B);
						}
					}
				} else {
					if (B.equals("<E>")) {
						/* We don't insert ABC<E>, but just ABC */
						if (!foo.contains(A)) {
							if (!content.equals("")) {
								content = content + "+";
							}
							content = content + A;
							foo.add(A);
						}
					} else {
						/* We don't insert ABCXYZ but ABC XYZ */
						if (!foo.contains(A + " " + B)) {
							if (!content.equals("")) {
								content = content + "+";
							}
							content = content + A + " " + B;
							foo.add(A + " " + B);
						}
					}
				}
			}
		}
		/* We replace all lines of a by the new ones */
		edit.addEdit(new BoxTextEdit(a, content, this));
		a.setContent(content);
		/* Finally, we give a b's outgoing transitions */
		for (final GenericGraphBox dest : b.transitions) {
			if (dest != a && dest != b && !a.transitions.contains(dest)) {
				final TransitionEdit edit2 = new TransitionEdit(a, dest);
				a.addTransitionTo(dest);
				edit.addEdit(edit2);
			}
		}
	}

	/**
	 * This function must be called to merge boxes a and b, when there is no
	 * link from a to b
	 */
	private void mergeUnlinkedBoxes(GenericGraphBox a, GenericGraphBox b,
			UndoableEdit edit) {
		final ArrayList<String> lines = new ArrayList<String>();
		for (int i = 0; i < a.lines.size(); i++) {
			final String s = (a.greyed.get(i) ? ":" : "") + a.lines.get(i);
			if (!lines.contains(s)) {
				lines.add(s);
			}
		}
		for (int i = 0; i < b.lines.size(); i++) {
			final String s = (b.greyed.get(i) ? ":" : "") + b.lines.get(i);
			if (!lines.contains(s)) {
				lines.add(s);
			}
		}
		String content = lines.get(0);
		for (int i = 1; i < lines.size(); i++) {
			content = content + "+" + lines.get(i);
		}
		/* We replace all lines of a by the new ones */
		edit.addEdit(new BoxTextEdit(a, content, this));
		a.setContent(content);
		/* We add b's outgoing transitions to a */
		for (final GenericGraphBox dest : b.transitions) {
			if (dest != a && dest != b && !a.transitions.contains(dest)) {
				final TransitionEdit edit2 = new TransitionEdit(a, dest);
				a.addTransitionTo(dest);
				edit.addEdit(edit2);
			}
		}
		/* Finally, we add b's incoming transitions to a */
		for (final GenericGraphBox box : graphBoxes) {
			if (box == a || box == b)
				continue;
			if (box.transitions.contains(b) && !box.transitions.contains(a)) {
				final TransitionEdit edit2 = new TransitionEdit(box, a);
				box.addTransitionTo(a);
				edit.addEdit(edit2);
			}
		}
	}

	protected void surroundWithBoxes(ArrayList<GenericGraphBox> selection,
			String box1, String box2) {
		final ArrayList<GenericGraphBox> inputBoxes = new ArrayList<GenericGraphBox>();
		final ArrayList<GenericGraphBox> outputBoxes = new ArrayList<GenericGraphBox>();
		computeInputOutputBoxes(selection, inputBoxes, outputBoxes);
		if (box1 != null && inputBoxes.isEmpty()) {
			return;
		}
		if (box2 != null && outputBoxes.isEmpty()) {
			return;
		}
		final MultipleEdit edit = new MultipleEdit();
		edit.addEdit(new SelectEdit(selection));
		if (box1 != null) {
			final GraphBox inputBox = createInputBox(selection, inputBoxes,
					box1, edit);
			graphBoxes.add(inputBox);
		}
		if (box2 != null) {
			final GraphBox outputBox = createOutputBox(selection, outputBoxes,
					box2, edit);
			graphBoxes.add(outputBox);
		}
		postEdit(edit);
		fireGraphChanged(true);
	}

	private GraphBox createInputBox(ArrayList<GenericGraphBox> selection,
			ArrayList<GenericGraphBox> inputBoxes, String content,
			MultipleEdit edit) {
		if (inputBoxes.isEmpty())
			throw new IllegalArgumentException(
					"Cannot compute the y average of no boxes");
		final int y = getAverageY(inputBoxes);
		int x = inputBoxes.get(0).X_in;
		for (final GenericGraphBox b : inputBoxes) {
			if (b.X_in < x)
				x = b.X_in;
		}
		final GraphBox newBox = new GraphBox(x - 40, y, GenericGraphBox.NORMAL,
				this);
                System.out.println("create");
		edit.addEdit(new AddBoxEdit(newBox, graphBoxes, this));
		/* Finally, we set up all transitions */
		for (final GenericGraphBox from : graphBoxes) {
			if (selection.contains(from))
				continue;
			for (int i = from.transitions.size() - 1; i >= 0; i--) {
				final GenericGraphBox dest = from.transitions.get(i);
				if (inputBoxes.contains(dest)) {
					from.removeTransitionTo(dest);
					edit.addEdit(new TransitionEdit(from, dest));
					if (!from.transitions.contains(newBox)) {
						from.addTransitionTo(newBox);
						edit.addEdit(new TransitionEdit(from, newBox));
					}
				}
			}
		}
		for (final GenericGraphBox b : inputBoxes) {
			newBox.addTransitionTo(b);
			edit.addEdit(new TransitionEdit(newBox, b));
		}
		newBox.setContent(content);
		edit.addEdit(new BoxTextEdit(newBox, content, this));
		return newBox;
	}

	private GraphBox createOutputBox(ArrayList<GenericGraphBox> selection,
			ArrayList<GenericGraphBox> outputBoxes, String content,
			MultipleEdit edit) {
		if (outputBoxes.isEmpty())
			throw new IllegalArgumentException(
					"Cannot compute the y average of no boxes");
		final int y = getAverageY(outputBoxes);
		int x = outputBoxes.get(0).X_out;
		for (final GenericGraphBox b : outputBoxes) {
			if (b.X_out > x)
				x = b.X_out;
		}
		final GraphBox newBox = new GraphBox(x + 30, y, GenericGraphBox.NORMAL,
				this);
		edit.addEdit(new AddBoxEdit(newBox, graphBoxes, this));
		/* Finally, we set up all transitions */
		for (final GenericGraphBox from : outputBoxes) {
			for (int i = from.transitions.size() - 1; i >= 0; i--) {
				final GenericGraphBox dest = from.transitions.get(i);
				if (selection.contains(dest))
					continue;
				from.removeTransitionTo(dest);
				edit.addEdit(new TransitionEdit(from, dest));
				if (!newBox.transitions.contains(dest)) {
					newBox.addTransitionTo(dest);
					edit.addEdit(new TransitionEdit(newBox, dest));
				}
			}
		}
		for (final GenericGraphBox b : outputBoxes) {
			b.addTransitionTo(newBox);
			edit.addEdit(new TransitionEdit(b, newBox));
		}
		newBox.setContent(content);
		edit.addEdit(new BoxTextEdit(newBox, content, this));
		return newBox;
	}

	/**
	 * We return the average Y coordinate computed from all the given boxes.
	 */
	private int getAverageY(ArrayList<GenericGraphBox> boxes) {
		if (boxes.isEmpty())
			throw new IllegalArgumentException(
					"Cannot compute the y average of no boxes");
		int y = 0;
		for (final GenericGraphBox b : boxes)
			y = y + b.Y_in;
		return y / boxes.size();
	}

	/**
	 * This method considers a box group and computes which boxes within this
	 * group are to be considered as input and/or output ones. Those input and
	 * output boxes are useful when one wants to surround a box selection with,
	 * for instance, a variable declaration. In such a case, transitions to
	 * input boxes are turned into transitions to the $aaa( box that is then
	 * created, and the $aaa( box is then linked to all input boxes. The same
	 * for output boxes.
	 */
	void computeInputOutputBoxes(ArrayList<GenericGraphBox> selection,
			ArrayList<GenericGraphBox> inputBoxes,
			ArrayList<GenericGraphBox> outputBoxes) {
		final ArrayList<GenericGraphBox> accessible = new ArrayList<GenericGraphBox>();
		for (final GenericGraphBox ggb : graphBoxes) {
			if (selection.contains(ggb))
				continue;
			for (final GenericGraphBox dest : ggb.transitions) {
				if (selection.contains(dest)) {
					if (!inputBoxes.contains(dest)
							&& dest.type == GenericGraphBox.NORMAL)
						inputBoxes.add(dest);
				}
			}
		}
		for (final GenericGraphBox ggb : graphBoxes) {
			for (final GenericGraphBox dest : ggb.transitions) {
				if (!accessible.contains(dest))
					accessible.add(dest);
			}
		}
		for (final GenericGraphBox ggb : selection) {
			if (ggb.transitions.isEmpty()) {
				/*
				 * A selected box with no outgoing transition is considered an
				 * output box
				 */
				if (!outputBoxes.contains(ggb)
						&& ggb.type == GenericGraphBox.NORMAL) {
					outputBoxes.add(ggb);
				}
			} else
				for (final GenericGraphBox dest : ggb.transitions) {
					if (selection.contains(dest))
						continue;
					if (!outputBoxes.contains(ggb)
							&& ggb.type == GenericGraphBox.NORMAL) {
						outputBoxes.add(ggb);
					}
				}
		}
		/*
		 * A selected box with no incoming transition is considered an input box
		 */
		for (final GenericGraphBox selected : selection) {
			if (inputBoxes.contains(selected))
				continue;
			boolean add = true;
			for (final GenericGraphBox box : graphBoxes) {
				if (box.transitions.contains(selected)) {
					add = false;
					break;
				}
			}
			if (add) {
				inputBoxes.add(selected);
			}
		}

	}

	@Override
	protected void initializeEmptyGraph() {
		GraphBox g, g2;
		// creating the final state
		g = new GraphBox(300, 200, 1, this);
		g.setContent("<E>");
		// and the initial state
		g2 = new GraphBox(70, 200, 0, this);
		g2.n_lines = 0;
		g2.setContent("<E>");
		addBox(g2);
		addBox(g);
		final Dimension d = new Dimension(1188, 840);
		setSize(d);
		setPreferredSize(new Dimension(d));
	}

	@Override
	protected GenericGraphBox createBox(int x, int y) {
		final GraphBox g = new GraphBox(x, y, 2, this);
		g.setContent("<E>");
		addBox(g);
		return g;
	}

	@Override
	protected GenericGraphBox newBox(int x, int y, int type,
			GenericGraphicalZone p) {
		return new GraphBox(x, y, type, (GraphicalZone) p);
	}

	/**
	 * Method updates the rollover status. Returns true if rollover status has
	 * changed, otherwise returns false.
	 */
	public boolean updateRollover(MouseEvent e) {
		int i = getSelectedBox((int) (e.getX() / scaleFactor),
				(int) (e.getY() / scaleFactor));
		GenericGraphBox box;

		if (i == -1)
			box = null;
		else
			box = graphBoxes.get(i);

		if (box != rolloveredBox) {
			rolloveredBox = box;
			return true;
		}
		return false;
	}

	/**
	 * Method clears the rollover status. Returns true if rollover status has
	 * changed, otherwise returns false.
	 */
	public boolean clearRollover() {
		if (rolloveredBox == null)
			return false;
		rolloveredBox = null;
		return true;
	}

	private final Stroke rolloverStroke = new BasicStroke((float)(2.1/scaleFactor));

	public void drawRollover(Graphics2D g, DrawGraphParams params) {
		if (rolloveredBox != null) {
			g.setColor(params.getForegroundColor());
			g.setStroke(rolloverStroke);
			GraphicalToolBox.drawRect(g, rolloveredBox.X1, rolloveredBox.Y1,
					rolloveredBox.Width, rolloveredBox.Height);
		}
	}

	class MyMouseListener implements MouseListener, MouseMotionListener {
		/**
		 * Thanks to Steve f*$%!# Jobs, Meta replaces Ctrl on mac os
		 */
		boolean isControlDown(MouseEvent e) {
			if (Config.getSystem() != Config.MAC_OS_X_SYSTEM)
				return e.isControlDown();
			return e.isMetaDown();
		}

		// Shift+click
		// reverse transitions
		boolean isReverseTransitionClick(MouseEvent e) {
			return (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES || (EDITING_MODE == MyCursors.NORMAL
					&& e.isShiftDown() && !isControlDown(e)));
		}

		// Control+click
		// creation of a new box
		boolean isBoxCreationClick(MouseEvent e) {
			return EDITING_MODE == MyCursors.CREATE_BOXES
					|| (EDITING_MODE == MyCursors.NORMAL && ((isControlDown(e) && !e
							.isShiftDown()) || e.getButton() == MouseEvent.BUTTON3));
		}

		// Alt+click
		// opening of a sub-graph
		boolean isOpenGraphClick(MouseEvent e) {
			return EDITING_MODE == MyCursors.OPEN_SUBGRAPH
					|| (EDITING_MODE == MyCursors.NORMAL && e.isAltDown());
		}

		// Ctrl+Shift+click
		// multiple box selection
		boolean isMultipleSelectionClick(MouseEvent e) {
			return (EDITING_MODE == MyCursors.NORMAL && isControlDown(e) && e
					.isShiftDown());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// We have implemented custom logic to decide when a mouse release
			// should be considered as a mouse click, and in appropriate
			// circumstances
			// customMouseClicked will be called.
		}

		/**
		 * Method should be called from the mouseRelease() event handler when
		 * the event is not an end of dragging, but should be considered as a
		 * mouse click
		 */
		public void customMouseClicked(MouseEvent e) {
			int boxSelected;
			GraphBox b;
			int x_tmp, y_tmp;
			if (e.getButton() == MouseEvent.BUTTON3)
				return;
			if (isReverseTransitionClick(e)) {
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					fireBoxSelectionChanged();
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current
						addReverseTransitionsFromSelectedBoxes(b);
						unSelectAllBoxes();
					} else {
						if (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES) {
							// if we click on a box while there is no box
							// selected in REVERSE_LINK_BOXES mode,
							// we select it
							b.setSelected(true);
							selectedBoxes.add(b);
							postEdit(new SelectEdit(selectedBoxes));
							fireBoxSelectionChanged();
							fireGraphTextChanged(b.content);
						}
					}
				} else {
					// simple click not on a box
					unSelectAllBoxes();
				}
			} else if (isBoxCreationClick(e)) {
				b = (GraphBox) createBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				// if some boxes are selected, we rely them to the new one
				if (!selectedBoxes.isEmpty()) {
					addTransitionsFromSelectedBoxes(b, false);
				}
				// then, the only selected box is the new one
				unSelectAllBoxes();
				b.setSelected(true);
				selectedBoxes.add(b);
				fireGraphTextChanged(b.content); /* Should be "<E>" */
				fireGraphChanged(true);
				fireBoxSelectionChanged();
			} else if (isOpenGraphClick(e)) {
				x_tmp = (int) (e.getX() / scaleFactor);
				y_tmp = (int) (e.getY() / scaleFactor);
				boxSelected = getSelectedBox(x_tmp, y_tmp);
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					final File file = b.getGraphClicked(y_tmp);
					if (file != null) {
						GlobalProjectManager.search(null)
								.getFrameManagerAs(InternalFrameManager.class)
								.newGraphFrame(file);
					}
				}
			} else if (EDITING_MODE == MyCursors.KILL_BOXES) {
				// killing a box
				if (!selectedBoxes.isEmpty()) {
					// if boxes are selected, we remove them
					removeSelected();
				} else {
					// else, we check if we clicked on a box
					x_tmp = (int) (e.getX() / scaleFactor);
					y_tmp = (int) (e.getY() / scaleFactor);
					boxSelected = getSelectedBox(x_tmp, y_tmp);
					if (boxSelected != -1) {
						b = (GraphBox) graphBoxes.get(boxSelected);
						b.setSelected(true);
						selectedBoxes.add(b);
						removeSelected();
					}
				}
			} else if (isMultipleSelectionClick(e)) {
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					if (!b.isSelected()) {
						b.setSelected(true);
						selectedBoxes.add(b);
					} else {
						b.setSelected(false);
						selectedBoxes.remove(b);
					}
					fireGraphChanged(false);
					fireBoxSelectionChanged();
					return;
				}
			} else {
				/* NORMAL BOX SELECTION */
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current one
						addTransitionsFromSelectedBoxes(b, true);
						unSelectAllBoxes();
					} else {
						if (!((EDITING_MODE == MyCursors.LINK_BOXES) && (b.type == 1))) {
							// if not, we just select this one, but only if we
							// are not clicking
							// on final state in LINK_BOXES mode
							b.setSelected(true);
							selectedBoxes.add(b);
							postEdit(new SelectEdit(selectedBoxes));
							fireGraphTextChanged(b.content);
							fireBoxSelectionChanged();
						}
					}
					fireBoxSelectionChanged();
				} else {
					// simple click not on a box
					unSelectAllBoxes();
				}
			}
			fireGraphChanged(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (clearRollover())
				fireGraphChanged(false);
			textFieldWasModified= text.isModified();
			ignoreThisMouseAction = false;
			if(textFieldWasModified) {
				if(!text.validateContent())
					ignoreThisMouseAction = true;
				else
					unSelectAllBoxes();
			}
			X_pressed_raw = e.getX();
			Y_pressed_raw = e.getY();
			dragTresholdReached = false;
		}

		/**
		 * Method should be called when mouse is dragging add dragging threshold
		 * is just reached.
		 */
		public void initiateDragging(MouseEvent e) {
			int selectedBox;
			if (e.isPopupTrigger()
					|| (EDITING_MODE == MyCursors.NORMAL && (e.isShiftDown()
							|| e.isAltDown() || e.isControlDown()))
					|| (EDITING_MODE == MyCursors.OPEN_SUBGRAPH)
					|| (EDITING_MODE == MyCursors.KILL_BOXES)) {
				return;
			}
			X_start_drag = (int) (X_pressed_raw / scaleFactor);
			Y_start_drag = (int) (Y_pressed_raw / scaleFactor);
			X_end_drag = X_start_drag;
			Y_end_drag = Y_start_drag;
			X_drag = X_start_drag;
			Y_drag = Y_start_drag;
			dragWidth = 0;
			dragHeight = 0;
			selectedBox = getSelectedBox(X_start_drag, Y_start_drag);
			singleDragging = false;
			dragging = false;
			selecting = false;
			if (selectedBox != -1) {
				// if we start dragging a box
				singleDraggedBox = graphBoxes.get(selectedBox);
				if (!singleDraggedBox.isSelected()) {
					/*
					 * Dragging a selected box is handled below with the general
					 * multiple box draggind case
					 */
					dragging = true;
					singleDragging = true;
					singleDraggedBox.singleDragging = true;
					fireGraphChanged(true);
					return;
				}
			}
			if (!selectedBoxes.isEmpty()) {
				dragging = true;
				fireGraphChanged(true);
				return;
			}
			if ((selectedBox == -1) && selectedBoxes.isEmpty()) {
				// being drawing a selection rectangle
				dragging = false;
				selecting = true;
				fireGraphChanged(false);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(ignoreThisMouseAction)
				return;
			updateRollover(e);
			if (!dragTresholdReached)
				customMouseClicked(e);
			else
				mouseDragFinished(e);
		}

		public void mouseDragFinished(MouseEvent e) {
			if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
				return;
			final int dx = X_end_drag - X_start_drag;
			final int dy = Y_end_drag - Y_start_drag;
			if (singleDragging) {
				// save position after the dragging
				selectedBoxes.add(singleDraggedBox);
				final UndoableEdit edit = new TranslationGroupEdit(
						selectedBoxes, dx, dy);
				postEdit(edit);
				selectedBoxes.remove(singleDraggedBox);
				dragging = false;
				singleDragging = false;
				singleDraggedBox.singleDragging = false;
				fireGraphChanged(true);
				return;
			}
			if (dragging && EDITING_MODE == MyCursors.NORMAL) {
				// save the position of all the translated boxes
				final UndoableEdit edit = new TranslationGroupEdit(
						selectedBoxes, dx, dy);
				postEdit(edit);
				fireGraphChanged(true);
			}
			dragging = false;
			if (selecting) {
				selectByRectangle(X_drag, Y_drag, dragWidth, dragHeight);
				selecting = false;
			}
			fireGraphChanged(false);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseInGraphicalZone = true;
			fireGraphChanged(false);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			clearRollover();
			mouseInGraphicalZone = false;
			fireGraphChanged(false);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(ignoreThisMouseAction)
				return;
			int X_raw = e.getX();
			int Y_raw = e.getY();
			if (!dragTresholdReached) {
				if ((X_raw - X_pressed_raw) * (X_raw - X_pressed_raw)
						+ (Y_raw - Y_pressed_raw) * (Y_raw - Y_pressed_raw) < dragTreshold
						* dragTreshold) {
					return;
				}
				dragTresholdReached = true;
				initiateDragging(e);
			}

			final int Xtmp = X_end_drag;
			final int Ytmp = Y_end_drag;
			X_end_drag = (int) (X_raw / scaleFactor);
			Y_end_drag = (int) (Y_raw / scaleFactor);
			final int dx = X_end_drag - Xtmp;
			final int dy = Y_end_drag - Ytmp;
			dX += dx;
			dY += dy;
			if (singleDragging) {
				// translates the single dragged box
				singleDraggedBox.translate(dx, dy);
				fireGraphChanged(true);
				return;
			}
			if (dragging && EDITING_MODE == MyCursors.NORMAL) {
				// translates all the selected boxes
				translateAllSelectedBoxes(dx, dy);
				// if we were dragging, we have nothing else to do
				return;
			}
			/* If the user is setting the selection rectangle */
			if (X_start_drag < X_end_drag) {
				X_drag = X_start_drag;
				dragWidth = X_end_drag - X_start_drag;
			} else {
				X_drag = X_end_drag;
				dragWidth = X_start_drag - X_end_drag;
			}
			if (Y_start_drag < Y_end_drag) {
				Y_drag = Y_start_drag;
				dragHeight = Y_end_drag - Y_start_drag;
			} else {
				Y_drag = Y_end_drag;
				dragHeight = Y_start_drag - Y_end_drag;
			}
			fireGraphChanged(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Xmouse = (int) (e.getX() / scaleFactor);
			Ymouse = (int) (e.getY() / scaleFactor);
			boolean graphChanged = false;
			if ((EDITING_MODE == MyCursors.REVERSE_LINK_BOXES || EDITING_MODE == MyCursors.LINK_BOXES)
					&& !selectedBoxes.isEmpty()) {
				graphChanged = true;
			}
			if (updateRollover(e))
				graphChanged = true;
			if (graphChanged)
				fireGraphChanged(false);
		}
	}



	/**
	 * Draws the graph. This method should only be called by the virtual
	 * machine.
	 *
	 * @param f_old
	 *            the graphical context
	 */
	@Override
	public void paintComponent(Graphics f_old) {
		setClipZone(f_old.getClipBounds());
		final Graphics2D f = (Graphics2D) f_old;
		DrawGraphParams params = defaultDrawParams();
		drawGraph(f, params);
	}

	@Override
	public void drawGraph(Graphics2D f, DrawGraphParams params) {
		f.scale(params.getTotalScale(), params.getTotalScale());
		if (params.isAntialiasing()) {
			f.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			f.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		//f.setColor(new Color(205, 205, 205));
		//f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getBackgroundColor());
		f.fillRect(0, 0, getWidth(), getHeight());
		if (params.isFrame()) {
			f.setColor(params.getForegroundColor());
			f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
			f.drawRect(9, 9, getWidth() - 18, getHeight() - 18);
		}
		f.setColor(params.getForegroundColor());
		if (decorator == null) {
			final File file = ((GraphFrame) parentFrame).getGraph();
			if (params.isFilename()) {
				if (params.isPathname())
					f.drawString((file != null) ? file.getAbsolutePath() : "",
							20, getHeight() - 45);
				else
					f.drawString((file != null) ? file.getName() : "", 20,
							getHeight() - 45);
			}
		}
		if (params.isDate())
			f.drawString(new Date().toString(), 20, getHeight() - 25);
		drawGrid(f, params);
		if (mouseInGraphicalZone && !selectedBoxes.isEmpty()) {
			if (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES) {
				drawTransitionsFromMousePointerToSelectedBoxes(f, params);
			} else if (EDITING_MODE == MyCursors.LINK_BOXES) {
				drawTransitionsFromSelectedBoxesToMousePointer(f, params);
			}
		}
		drawAllTransitions(f,params);
		drawAllBoxes(f,params);
		drawRollover(f,params);
		if (selecting) {
			// here we draw the selection rectangle
			f.setColor(params.getForegroundColor());
			f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
		}
	}

	/**
	 * Prints the graph.
	 *
	 * @param g
	 *            the graphical context
	 * @param p
	 *            the page format
	 * @param pageIndex
	 *            the page index
	 */
	@Override
	public int print(Graphics g, PageFormat p, int pageIndex) {
		if (pageIndex != 0)
			return Printable.NO_SUCH_PAGE;
		final Graphics2D f = (Graphics2D) g;
		DrawGraphParams params = defaultDrawParams();

		final double DPI = 96.0;
		final double WidthInInches = p.getImageableWidth() / 72;
		final double realWidthInInches = (getWidth() / DPI);
		final double HeightInInches = p.getImageableHeight() / 72;
		final double realHeightInInches = (getHeight() / DPI);
		final double scale_x = WidthInInches / realWidthInInches;
		final double scale_y = HeightInInches / realHeightInInches;
		f.translate(p.getImageableX(), p.getImageableY());
		if (scale_x < scale_y)
			f.scale(0.99 * 0.72 * scale_x, 0.99 * 0.72 * scale_x);
		else
			f.scale(0.99 * 0.72 * scale_y, 0.99 * 0.72 * scale_y);
		f.setColor(params.getBackgroundColor());
		f.fillRect(0, 0, getWidth(), getHeight());
		if (params.isFrame()) {
			f.setColor(params.getForegroundColor());
			final Stroke oldStroke = f.getStroke();
			f.setStroke(GraphicalToolBox.frameStroke);
			f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
			f.setStroke(oldStroke);
		}
		f.setColor(params.getForegroundColor());
		final File file = ((GraphFrame) parentFrame).getGraph();
		if (getGraphPresentationInfo().isFilename()) {
			if (getGraphPresentationInfo().isPathname())
				f.drawString((file != null) ? file.getAbsolutePath() : "", 20,
						getHeight() - 45);
			else
				f.drawString((file != null) ? file.getName() : "", 20,
						getHeight() - 45);
		}
		if (params.isDate())
			f.drawString(new Date().toString(), 20, getHeight() - 25);
		drawGrid(f,params);
		drawAllTransitions(f,params);
		drawAllBoxes(f,params);
		if (selecting) {
			// here we draw the selection rectangle
			f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
		}
		return Printable.PAGE_EXISTS;
	}

	private int currentFindBox = 0;
	private int currentFindLine = 0;
	private String lastPattern = null;

	private static final int NOT_FOUND = 0;
	private static final int NO_MORE_MATCHES = 1;
	private static final int FOUND = 2;

	public int find(String pattern) {
		boolean firstFind = false;
		if (lastPattern == null || !pattern.equals(lastPattern)) {
			firstFind = true;
			lastPattern = pattern;
			currentFindBox = 0;
			currentFindLine = 0;
		}
		while (currentFindBox < graphBoxes.size()) {
			final GraphBox box = (GraphBox) graphBoxes.get(currentFindBox);
			while (currentFindLine < box.lines.size()) {
				final String line = box.lines.get(currentFindLine);
				if (line.contains(pattern)) {
					setHighlight(true);
					currentFindLine++;
					return FOUND;
				}
				currentFindLine++;
			}
			if (box.transduction != null && box.transduction.contains(pattern)) {
				currentFindLine = -2;
				setHighlight(true);
				currentFindLine = 0;
				currentFindBox++;
				return FOUND;
			}
			currentFindBox++;
			currentFindLine = 0;
		}
		lastPattern = null;
		currentFindBox = 0;
		currentFindLine = 0;
		if (firstFind) {
			return NOT_FOUND;
		}
		return NO_MORE_MATCHES;
	}

	public void setHighlight(boolean highlight) {
		if (!highlight) {
			setDecorator(null);
			repaint();
			return;
		}
		final GraphDecorator d = new GraphDecorator(null);
		d.highlightBoxLine(-1, currentFindBox, currentFindLine);
		setDecorator(d);
		revalidate();
		repaint();
		final GraphBox b = (GraphBox) graphBoxes.get(currentFindBox);
		final JViewport viewport = ((GraphFrame) parentFrame).scroll
				.getViewport();
		Rectangle visibleRect = viewport.getViewRect();
		if (visibleRect.width == 0 && visibleRect.height == 0) {
			/*
			 * If the view port has not been given a size, we consider the panel
			 * area as default
			 */
			visibleRect = new Rectangle(0, 0, getWidth(), getHeight());
		}
		/*
		 * If necessary, we adjust the scrolling so that the middle of the box
		 * will be visible
		 */
		int newX = visibleRect.x;
		if (b.X < visibleRect.x + 50) {
			newX = b.X1 - 50;
		} else if ((b.X1 + b.Width) > (visibleRect.x + visibleRect.width)) {
			newX = b.X1 - 50;
		}
		int newY = visibleRect.y;
		if (b.Y < visibleRect.y + 50) {
			newY = b.Y1 - 50;
		} else if ((b.Y1 + b.Height) > (visibleRect.y + visibleRect.height)) {
			newY = b.Y1 - 50;
		}
		viewport.setViewPosition(new Point(newX, newY));
	}
}
