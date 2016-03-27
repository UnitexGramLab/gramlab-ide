package fr.gramlab.svn;

public class SvnCommandResult {
	
	private SvnOpResult op;
	private String err;
	
	public SvnCommandResult(SvnOpResult op,String err) {
		this.op=op;
		this.err=err;
	}

	public SvnOpResult getOp() {
		return op;
	}

	public String getErr() {
		return err;
	}
	
}
