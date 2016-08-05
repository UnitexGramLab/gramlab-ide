package org.gramlab.core.gramlab.svn;

import java.awt.Color;

public enum SvnStatus {
	
	UNMODIFIED("unmodified",' ',Color.BLACK),
	ADDED("added",'A',Color.RED.darker()),
	DELETED("deleted",'D',Color.RED),
	MODIFIED("modified",'M',Color.BLACK),
	REPLACED("replaced",'R',Color.ORANGE),
	CONFLICT("conflict",'C',Color.RED),
	EXTERNAL("external",'X',Color.BLACK),
	IGNORED("ignored",'I',Color.BLACK),
	UNVERSIONED("unversioned",'?',Color.BLUE),
	MISSING("missing",'!',Color.RED),
	TYPE_CHANGED("type changed",'~',Color.RED);
	
	
	private String description;
	private Color color;
	private char symbol;
	
	SvnStatus(String description,char symbol,Color c) {
		this.description=description;
		this.symbol=symbol;
		this.color=c;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Color getColor() {
		return color;
	}
	
	private static SvnStatus[] values=SvnStatus.values();
	
	public static SvnStatus getStatus(char c) {
		for (SvnStatus s:values) {
			if (c==s.symbol) {
				return s;
			}
		}
		return null;
	}
}
