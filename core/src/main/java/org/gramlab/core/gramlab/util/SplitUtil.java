package fr.gramlab.util;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

/**
 * This class is used to manage all the split panes in the left area, so
 * that it becomes easy to maximize a component 
 * 
 * @author paumier
 *
 */
public class SplitUtil {
	
	public enum Pane {
		Tree,Text,Config,Console
	}

	private JSplitPane split2; /* up=text,         down=split3 */
	private JSplitPane split3; /* up=checkboxes,   down=console */
	private int divider2,divider3;
	private boolean maximized=false;
	private PropertyChangeListener dividerChangeListener2;
	private PropertyChangeListener dividerChangeListener3;
	private int lastDivider2=-1,lastDivider3=-1;


	
	public SplitUtil(JSplitPane s2,JSplitPane s3) {
		setSplit2(s2);
		setSplit3(s3);
	}

	
	private void maximize(Pane p) {
		/* We save the current state */
		this.divider2=split2.getDividerLocation();
		this.divider3=split3.getDividerLocation();
		/* And we maximize the appropriate component */
		switch (p) {
		case Config: {
			updateDividerLocations(0.0,0.0,1.0);
			break;
		}
		case Console: {
			updateDividerLocations(0.0,0.0,0.0);
			break;
		}
		case Text: {
			updateDividerLocations(0.0,1.0,0.0);
			break;
		}
		case Tree: break;		
		}
		maximized=true;
	}
	
	private void updateDividerLocations(final double v1,final double v2,final double v3) {
		split2.removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener2);
		split3.removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener3);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				split2.setDividerLocation(v2);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						split3.setDividerLocation(v3);
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								split2.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener2);
								split3.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener3);
							}
						});
					}
				});
			}
		});
	}

	private void restore() {
		maximized=false;
		split3.setDividerLocation(divider3);
		split2.setDividerLocation(divider2);
	}
	
	public void action(Pane p) {
		if (maximized) {
			restore();
		} else {
			maximize(p);
		}
	}

	public void setSplit2(JSplitPane s2) {
		if (split2!=null) {
			split2.removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener2);
		}
		this.split2=s2;
		if (split2!=null) {
			lastDivider2=split2.getDividerLocation();
			dividerChangeListener2=new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (split2.getDividerLocation()!=lastDivider2) {
						maximized=false;
						lastDivider2=split2.getDividerLocation();
					}
				}
			};
			split2.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener2);
		}
	}

	public void setSplit3(JSplitPane s3) {
		if (split3!=null) {
			split3.removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener3);
		}
		this.split3=s3;
		if (split3!=null) {
			lastDivider3=split3.getDividerLocation();
			dividerChangeListener3=new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (split3.getDividerLocation()!=lastDivider3) {
						maximized=false;
						lastDivider3=split3.getDividerLocation();
					}
				}
			};
			split3.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerChangeListener3);
		}
	}


}
