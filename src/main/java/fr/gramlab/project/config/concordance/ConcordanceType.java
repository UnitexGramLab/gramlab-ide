package fr.gramlab.project.config.concordance;

public enum ConcordanceType {
	
	TEXT("Text",true),
	HTML("HTML",true),
	XML("Index file in XML format",false),
	INDEX("Index file in text format",false),
	AXIS("Axis file in text format",false);
	
	private String description;
	private boolean useContext;
	
	private ConcordanceType(String s,boolean b) {
		this.description=s;
		this.useContext=b;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean useContext() {
		return useContext;
	}
	
}
