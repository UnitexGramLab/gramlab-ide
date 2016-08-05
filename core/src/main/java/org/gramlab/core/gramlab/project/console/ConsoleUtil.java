package org.gramlab.core.gramlab.project.console;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.process.commands.CommandBuilder;
import org.gramlab.core.umlv.unitex.process.commands.DicoCommand;
import org.gramlab.core.umlv.unitex.process.commands.Fst2TxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.LocateCommand;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.TokenizeCommand;


public class ConsoleUtil {

	/**
	 * Every file whose name starts with this prefix won't be displayed 
	 * in any project tree.
	 */
	public final static String TIME_PFX="..time.";

	
	/**
	 * We get the content of the given file and we remove it.
	 * @param f
	 * @return
	 */
	public static String getTime(File f) {
		String s=trim(Encoding.getContent(f));
		FileUtil.rm(f);
		return s;
	}


	/**
	 * Removes all ending \r and/or \n
	 */
	private static String trim(String s) {
		if (s==null) return null;
		Scanner scanner=new Scanner(s);
		String res="";
		while (scanner.hasNextLine()) {
			if (!res.equals("")) {
				res=res+"\n";
			}
			res=res+scanner.nextLine();
		}
		return res;
	}


	public String getResult(CommandBuilder c) {
		if (c instanceof LocateCommand) {
			return getLocateResult(c);
		}
		if (c instanceof TokenizeCommand) {
			return getTokenizeResult(c);
		}
		return null;
	}

	private String getLocateResult(CommandBuilder c) {
		for (String s:c.getCommandArguments()) {
			if (s.startsWith("-t")) {
				File snt=new File(s.substring(2));
				File sntDir=FileUtil.getSntDir(snt);
				File res=new File(sntDir,"concord.n");
				return trim(Encoding.getContent(res));
			}
		}
		return null;
	}

	private String getTokenizeResult(CommandBuilder c) {
		for (String s:c.getCommandArguments()) {
			if (s.endsWith(".snt")) {
				File snt=new File(s);
				File sntDir=FileUtil.getSntDir(snt);
				File res=new File(sntDir,"stats.n");
				return trim(Encoding.getContent(res));
			}
		}
		return null;
	}

	private int currentBackground=1;
	private Color[] bgColors=new Color[] {Color.WHITE,new Color(231,244,213)};

	public void doBeforeMonitoring(GramlabProject p,CommandBuilder c) {
		currentBackground=1-currentBackground;
		JComponent comp=createCommandComponent(c);
		p.getConsolePanel().add(comp);
	}
	
	
	@SuppressWarnings("serial")
	private JTextComponent createCommandComponent(final CommandBuilder c) {
		final JTextArea area=new JTextArea(c.getUltraSimplifiedCommandLine()) {
			
			boolean simplified=true;
			
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return false;
			}
			{
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount()==2) {
							simplified=(!simplified);
							setText(simplified?c.getUltraSimplifiedCommandLine():c.getSimplifiedCommandLine());
						}
					}
				});	
			}
		};
		area.setLineWrap(true);
		area.setEditable(false);
		area.setForeground(Color.BLACK);
		area.setBackground(bgColors[currentBackground]);
		return area;
	}

	@SuppressWarnings("serial")
	private JTextComponent createTextComponent(String text,Color fg) {
		final JTextArea area=new JTextArea(text) {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};
		area.setLineWrap(false);
		area.setEditable(false);
		area.setForeground(Color.BLACK);
		area.setForeground(fg);
		area.setBackground(bgColors[currentBackground]);
		return area;
	}


	public void doAfterMonitoring(GramlabProject p,boolean success,ConsoleEntry entry,
			CommandBuilder c,File f) {
		if (entry==null) {
			throw new IllegalStateException("Should not be invoked with a null entry !");
		}
		if (entry.getErrorMessage()!=null) {
			if (c instanceof Grf2Fst2Command) {
				p.getConsolePanel().add(createGrf2Fst2ErrorComponent((Grf2Fst2Command) c,entry.getErrorMessage()));
			} else if (c instanceof Fst2TxtCommand) {
				p.getConsolePanel().add(createFst2TxtErrorComponent((Fst2TxtCommand) c,entry.getErrorMessage()));
			} else if (c instanceof LocateCommand) {
				p.getConsolePanel().add(createLocateErrorComponent((LocateCommand) c,entry.getErrorMessage()));
			} else {
				p.getConsolePanel().add(createTextComponent(entry.getErrorMessage(),Color.RED));
			}
		}
		String r=getResult(c);
		if (r!=null) {
			p.getConsolePanel().add(createTextComponent(r,Color.BLACK));
		}
		
		String time=ConsoleUtil.getTime(f);
		String timeMsg="[no time information]";
		double t=-1;
		if (time!=null) {
			timeMsg="[time elapsed: "+time+" secs]";
			t=Double.parseDouble(time);
			if (isLocateCommand(c)) {
				double current=p.getConsolePanel().getLocateTime();
				if (current<0) current=0;
				current+=t;
				/* If we have to set up the locate time */
				p.getConsolePanel().setLocateTime(current);
			} else if (isPreprocessCommand(c)) {
				double current=p.getConsolePanel().getPreprocessingTotalTime();
				if (current<0) current=0;
				current+=t;
				p.getConsolePanel().setPreprocessingTotalTime(current);
			}
		}
		p.getConsolePanel().add(createTextComponent(timeMsg,Color.BLACK));
		if (!success) {
			p.getConsolePanel().add(createTextComponent("[FAILED]",Color.RED));
		}
	}


	private boolean isPreprocessCommand(CommandBuilder c) {
		return (c instanceof NormalizeCommand)
			|| (c instanceof Fst2TxtCommand)
			|| (c instanceof TokenizeCommand)
			|| (c instanceof DicoCommand);
	}

	private boolean isLocateCommand(CommandBuilder c) {
		return (c instanceof LocateCommand);
	}

	@SuppressWarnings("serial")
	private JEditorPane createGrf2Fst2ErrorComponent(Grf2Fst2Command cmd,String text) {
		final JEditorPane editor=new JEditorPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
			
			/**
			 * We have to use this trick because Swing reformat the HTML text
			 * and it is then impossible to get the actual text that was set by
			 * the user
			 */
			private String myText;
			@Override
			public void setText(String t) {
				myText=t;
				super.setText(t);
			}
			
			@Override
			public String getText() {
				return myText;
			}
		};
		editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,Boolean.TRUE);
		editor.setContentType("text/html");
		editor.setText(buildHtmlContentGrf2Fst2(cmd,text));
		editor.setFont(new JTextArea().getFont());
		editor.setEditable(false);
		editor.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) return;
				File file=new File(e.getDescription());
				GlobalProjectManager.search(file)
					.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(file);
			}
		});
		editor.setForeground(Color.BLACK);
		editor.setForeground(Color.RED);
		editor.setBackground(bgColors[currentBackground]);
		return editor;
	}


	private String buildHtmlContentGrf2Fst2(Grf2Fst2Command cmd,String text) {
		StringBuilder b=new StringBuilder();
		b.append("<html><body>");
		Scanner scanner=new Scanner(text);
		while (scanner.hasNextLine()) {
			String line=htmlizeGrf2Fst2(cmd,scanner.nextLine());
			b.append(line+"<br/>\n");
		}
		b.append("</body></html>");
		return b.toString();
	}


	private final static String ERR0="ERROR: Main graph ";
	private final static String ERR1="which is called from ";
	private final static String ERR2="Graph ";
	
	private String htmlizeGrf2Fst2(Grf2Fst2Command cmd,String s) {
		if (s.startsWith(ERR0)) {
			String grf=s.substring(ERR0.length(),s.lastIndexOf(".grf")+4);
			return ERR0+"<a color=\"#FF0000\" href=\""+getMainGraph(cmd)+"\">"+grf+"</a>"+htmlize(s.substring(s.lastIndexOf(".grf")+4));
		}
		if (s.startsWith(ERR1)) {
			String grf,grf2;
			int pos=s.lastIndexOf(".grf");
			if (pos!=-1) {
				pos=pos+4;
				grf=s.substring(ERR1.length(),pos);
				grf2=grf;
			} else {
				pos=s.length();
				grf=s.substring(ERR1.length(),pos);
				grf2=grf+".grf";
			}
			return ERR1+"<a color=\"#FF0000\" href=\""+grf2+"\">"+grf+"</a>";
		}
		if (s.startsWith(ERR2) && -1!=s.indexOf(": ")) {
			String grf=s.substring(ERR2.length(),s.indexOf(": "));
			return ERR2+"<a color=\"#FF0000\" href=\""+grf+".grf\">"+grf+"</a>"+htmlize(s.substring(s.indexOf(": ")));
		}
		return htmlize(s);
	}

	
	private String getMainGraph(Grf2Fst2Command cmd) {
		for (String s:cmd.getCommandArguments()) {
			if (s.endsWith(".grf")) return s;
		}
		return "";
	}

	@SuppressWarnings("serial")
	private JEditorPane createFst2TxtErrorComponent(Fst2TxtCommand cmd,String text) {
		final JEditorPane editor=new JEditorPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
			
			/**
			 * We have to use this trick because Swing reformat the HTML text
			 * and it is then impossible to get the actual text that was set by
			 * the user
			 */
			private String myText;
			@Override
			public void setText(String t) {
				myText=t;
				super.setText(t);
			}
			
			@Override
			public String getText() {
				return myText;
			}
		};
		editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,Boolean.TRUE);
		editor.setContentType("text/html");
		editor.setText(buildHtmlContentFst2Txt(cmd,text));
		editor.setFont(new JTextArea().getFont());
		editor.setEditable(false);
		editor.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) return;
				File file=new File(e.getDescription());
				GlobalProjectManager.search(file)
					.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(file);
			}
		});
		editor.setForeground(Color.BLACK);
		editor.setForeground(Color.RED);
		editor.setBackground(bgColors[currentBackground]);
		return editor;
	}

	
	private String buildHtmlContentFst2Txt(Fst2TxtCommand cmd,String text) {
		StringBuilder b=new StringBuilder();
		b.append("<html><body>");
		Scanner scanner=new Scanner(text);
		while (scanner.hasNextLine()) {
			String line=htmlizeFst2Txt(cmd,scanner.nextLine());
			b.append(line+"<br/>\n");
		}
		b.append("</body></html>");
		return b.toString();
	}

	private static final String FST2TXT_ERR="Error in graph ";
	
	private String htmlizeFst2Txt(Fst2TxtCommand cmd,String s) {
		if (s.startsWith(FST2TXT_ERR)) {
			String grf=s.substring(FST2TXT_ERR.length(),s.lastIndexOf(":"));
			File dir=cmd.getSrcGrfPath();
			if (dir==null) {
				return htmlize(s);
			}
			File f=new File(dir,grf+".grf");
			if (!f.exists()) return htmlize(s);
			return FST2TXT_ERR+"<a color=\"#FF0000\" href=\""+f.getAbsolutePath()+"\">"+grf+"</a>"+htmlize(s.substring(s.lastIndexOf(":")));
		}
		return htmlize(s);
	}

	
	
	@SuppressWarnings("serial")
	private JEditorPane createLocateErrorComponent(LocateCommand cmd,String text) {
		final JEditorPane editor=new JEditorPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
			
			/**
			 * We have to use this trick because Swing reformat the HTML text
			 * and it is then impossible to get the actual text that was set by
			 * the user
			 */
			private String myText;
			@Override
			public void setText(String t) {
				myText=t;
				super.setText(t);
			}
			
			@Override
			public String getText() {
				return myText;
			}
		};
		editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,Boolean.TRUE);
		editor.setContentType("text/html");
		editor.setText(buildHtmlContentLocate(cmd,text));
		editor.setFont(new JTextArea().getFont());
		editor.setEditable(false);
		editor.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) return;
				File file=new File(e.getDescription());
				GlobalProjectManager.search(file)
					.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(file);
			}
		});
		editor.setForeground(Color.BLACK);
		editor.setForeground(Color.RED);
		editor.setBackground(bgColors[currentBackground]);
		return editor;
	}

	private String buildHtmlContentLocate(LocateCommand cmd,String text) {
		StringBuilder b=new StringBuilder();
		b.append("<html><body>");
		Scanner scanner=new Scanner(text);
		while (scanner.hasNextLine()) {
			String line=htmlizeLocate(cmd,scanner.nextLine());
			b.append(line+"<br/>\n");
		}
		b.append("</body></html>");
		return b.toString();
	}

	private static final String LOCATE_ERR="Error in graph ";
	
	private String htmlizeLocate(LocateCommand cmd,String s) {
		if (s.startsWith(LOCATE_ERR)) {
			String grf=s.substring(LOCATE_ERR.length(),s.lastIndexOf(":"));
			File dir=cmd.getFst2Path();
			if (dir==null) {
				return htmlize(s);
			}
			File f=new File(dir,grf+".grf");
			if (!f.exists()) return htmlize(s);
			return LOCATE_ERR+"<a color=\"#FF0000\" href=\""+f.getAbsolutePath()+"\">"+grf+"</a>"+htmlize(s.substring(s.lastIndexOf(":")));
		}
		return htmlize(s);
	}
	
	
	private String htmlize(String s) {
		s=s.replaceAll("&","&amp;");
		s=s.replaceAll("<","&lt;");
		s=s.replaceAll(">","&gt;");
		return s;
	}
	
}
