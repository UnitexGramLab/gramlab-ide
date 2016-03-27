package fr.gramlab.project.config.concordance;

public enum ConcordanceSortType {
	
	TO("Text Order",0),
	LC("Left, Center",1),
	LR("Left, Right",2),
	CL("Center, Left",3),
	CR("Center, Right",4),
	RL("Right, Left",5),
	RC("Right, Center",6);
	
	private String description;
	private int intValue;
	
	private ConcordanceSortType(String s,int n) {
		this.description=s;
		this.intValue=n;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getIntValue() {
		return intValue;
	}
	
}
