package fr.gramlab.svn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


public class SvnInfo {
	
	private int revision;
	private String committer;
	private String date;
	private SvnStatus status;
	
	
	public SvnInfo(SvnStatus status) {
		this(status,-1,null,null);
		if (status==SvnStatus.MODIFIED || status==SvnStatus.UNMODIFIED) {
			throw new IllegalArgumentException("Cannot omit revision info for a versioned item");
		}
	}
	
	public SvnInfo(SvnStatus status,int r,String c,String d) {
		this.status=status;
		this.revision=r;
		this.committer=(c!=null)?c:"";
		this.date=d;
	}

	public int getRevision() {
		return revision;
	}

	public String getCommitter() {
		return committer;
	}

	public String getDate() {
		return date;
	}
	
	public SvnStatus getStatus() {
		return status;
	}

	public static void analyzeInfos(File baseDir,String info,HashMap<File,SvnInfo> map) {
		Scanner scanner=new Scanner(info);
		int r=-1;
		String login=null;
		String d=null;
		File file=null;
		while (scanner.hasNextLine()) {
			String line=scanner.nextLine();
			if (line.startsWith("Path: ")) {
				if (file!=null) {
					/* We may have to save a SvnInfo item */
					map.put(file,new SvnInfo(SvnStatus.UNMODIFIED,r,login,d));
					file=null;
					r=-1;
					login=null;
					d=null;
				}
				try {
					/* getCanonicalFile() is necessary to avoid having /tutu/. being
					 * considered different than /tutu/
					 */
					file=new File(baseDir,line.substring("Path: ".length())).getCanonicalFile();
				} catch (IOException e) {
					/* */
				}
			} else if (line.startsWith("Last Changed Rev: ")) {
				r=Integer.parseInt(line.substring("Last Changed Rev: ".length()));
			} else if (line.startsWith("Last Changed Author: ")) {
				login=line.substring("Last Changed Author: ".length());
			} else if (line.startsWith("Last Changed Date: ")) {
				String date=line.substring("Last Changed Date: ".length());
				int space=date.indexOf(' ');
				space=date.indexOf(' ',space+1);
				d=date.substring(0,space);
			}
		}
		if (file!=null) {
			map.put(file,new SvnInfo(SvnStatus.UNMODIFIED,r,login,d));
		}
	}
	
	@Override
	public String toString() {
		return revision+" "+date+" "+committer;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SvnInfo)) return false;
		SvnInfo foo=(SvnInfo)obj;
		return revision==foo.getRevision()
			&& date.equals(foo.getDate())
			&& committer.equals(foo.getCommitter());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public void setStatus(SvnStatus s) {
		this.status=s;
	}
	
}
