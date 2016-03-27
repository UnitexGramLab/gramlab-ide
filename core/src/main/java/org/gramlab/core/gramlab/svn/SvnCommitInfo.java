package fr.gramlab.svn;

public class SvnCommitInfo implements Comparable<SvnCommitInfo> {
	
	private String name;
	private boolean selected;
	private SvnStatus status;
	
	public SvnCommitInfo(String name,boolean selected,SvnStatus status) {
		this.name=name;
		this.selected=selected;
		this.status=status;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public SvnStatus getStatus() {
		return status;
	}

	@Override
	public int compareTo(SvnCommitInfo o) {
		return getName().compareTo(o.getName());
	}
	
}
