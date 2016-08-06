package org.gramlab.core.gramlab.workspace;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.frames.ConsoleFrame;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.frames.TabbableInternalFrame;

@SuppressWarnings("serial")
public class FrameTabManager extends JTabbedPane {
	
	private final PropertyChangeListener titleListener=new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateTabNames();
		}
	};
	
	private JDesktopPane desktop;
	
	public FrameTabManager(InternalFrameManager m) {
		desktop=m.getDesktop();
		setMinimumSize(new Dimension(10,20));
		getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				FrameTab tab=(FrameTab) getSelectedComponent();
				if (tab==null) return;
				TabbableInternalFrame frame=tab.getFrame();
				try {
					frame.setIcon(false);
					frame.setSelected(true);
				} catch (PropertyVetoException e1) {
					/* Do nothing */
				}
			}
		});
		desktop.addContainerListener(new ContainerListener() {
			
			private TabbableInternalFrame getFrame(Component e) {
				if (e instanceof JInternalFrame.JDesktopIcon) {
					e=((JInternalFrame.JDesktopIcon)e).getInternalFrame();
					if (e==null) return null;
				}
				if (e instanceof TabbableInternalFrame) {
					return (TabbableInternalFrame)e;
				}
				return null;
			}
			
			@Override
			public void componentRemoved(ContainerEvent e) {
				/* */
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {
				TabbableInternalFrame f=getFrame(e.getChild());
				if (f==null) return;
				final FrameTab tab=new FrameTab(f);
				f.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentShown(ComponentEvent e) {
						addTabbedFrame(tab);
					}
					@Override
					public void componentHidden(ComponentEvent e) {
						removeTabbedFrame(tab);
					}
				});
				f.addInternalFrameListener(new InternalFrameAdapter() {
					
					@Override
					public void internalFrameActivated(InternalFrameEvent e) {
						int n=getCustomIndexOfComponent(tab);
						if (-1==n) return;
						setSelectedIndex(n);
					}
					
					@Override
					public void internalFrameClosed(InternalFrameEvent e) {
						removeTabbedFrame(tab);
					}
					
				});
				if (!f.isVisible() && f instanceof ConsoleFrame) {
					/* To avoid a bug with the console that may have been added
					 * to the desktop with no immediate intention to make it
					 * visible */
				} else {
					addTabbedFrame(tab);
				}
			}
		});
		setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
	}

	protected void updateTabNames() {
		for (int i=0;i<getTabCount();i++) {
			try {
				FrameTab tab=(FrameTab)getComponentAt(i);			
				Box box=(Box)getTabComponentAt(i);
				JLabel label=(JLabel)box.getComponent(0);
				label.setText(tab.getTabName());
				revalidate();
				repaint();
			} catch (ClassCastException e) {
				/* Do nothing */
				e.printStackTrace();
			}
		}
	}

	private int getCustomIndexOfComponent(FrameTab tab) {
		FrameTab t=getTab(tab.getFrame());
		if (t==null) return -1;
		return indexOfComponent(t);
	}
	
	protected void addTabbedFrame(FrameTab tab) {
		int pos=getCustomIndexOfComponent(tab);
		if (pos!=-1) return;
		addTab(null,tab);
		pos=getCustomIndexOfComponent(tab);
		setTabComponentAt(pos,createTabComponent(tab));
		tab.getFrame().addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY,titleListener);
		revalidate();
		repaint();
	}

	private Box createTabComponent(final FrameTab tab) {
		Box b=new Box(BoxLayout.X_AXIS);
		b.add(new JLabel(tab.getTabName()));
		final JLabel close=new JLabel("X");
		close.setForeground(Color.GRAY);
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				close.setForeground(Color.RED);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				close.setForeground(Color.GRAY);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				tab.getFrame().doDefaultCloseAction();
				revalidate();
				repaint();
			}
		});
		b.add(Box.createHorizontalStrut(8));
		b.add(close);
		return b;
	}

	protected void removeTabbedFrame(FrameTab tab) {
		tab.getFrame().removePropertyChangeListener(JInternalFrame.TITLE_PROPERTY,titleListener);
		selectNextFrameToBeFocused(tab.getFrame());
		int index=getCustomIndexOfComponent(tab);
		if (index==-1) return;
		remove(index);
		revalidate();
		repaint();
	}

	/**
	 * When removing the selected frame tab, we want to have focus on
	 * the frame that was just below the frame that is about to be discarded.
	 * But, the default behavior of the tabbed pane is to select an arbitrary 
	 * new tab when it is the selected one that is removed. To avoid that,
	 * the trick is to select the frame we want on top before removing the tab.
	 * To find that frame, we sort all frames by their z order and we pick the second
	 * one from the top.
	 */
	private void selectNextFrameToBeFocused(TabbableInternalFrame f) {
		desktop.moveToBack(f);
		int min=1000000000;
		JInternalFrame frame=null;
		Component[] comps=desktop.getComponents();
		for (Component c:comps) {
			if (!(c instanceof JInternalFrame)) {
				continue;
			}
			int z=desktop.getComponentZOrder(c);
			if (z<min) {
				min=z;
				frame=(JInternalFrame)c;
			}
		}
		if (frame!=null) {
			try {
				frame.setSelected(true);
			} catch (PropertyVetoException e) {
				/* */
			}
		}
	}

	protected FrameTab getTab(TabbableInternalFrame f) {
		for (Component c:getComponents()) {
			try {
				FrameTab t=(FrameTab)c;
				if (t.getFrame().equals(f)) return t;
			} catch (ClassCastException e) {
				/* Do nothing */
			}
		}
		return null;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10,getTabRunCount()*20);
	}
	
}
