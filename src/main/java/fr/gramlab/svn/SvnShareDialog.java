package fr.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import fr.gramlab.GramlabConfigManager;
import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.frames.FrameUtil;
import fr.umlv.unitex.process.ExecParameters;
import fr.umlv.unitex.process.Executor;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.SvnCommand;
import fr.umlv.unitex.process.list.ProcessOutputList;
import fr.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class SvnShareDialog extends JDialog {
	
	GramlabProject project;
	DefaultComboBoxModel model;
	JComboBox url;
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	ArrayList<File> srcSubDirsToIgnore=new ArrayList<File>();
	ArrayList<JCheckBox> checkBoxes;
	
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());
	private boolean finished=false;

	public SvnShareDialog(final GramlabProject p) {
		super(Main.getMainFrame(), "SVN sharing project "+p.getName(), true);
		this.project=p;
		JPanel pane=new JPanel(new BorderLayout());
		mainPanel=createMainPanel(false);
		pane.add(mainPanel,BorderLayout.CENTER);
		JPanel down=new JPanel();
		cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executor!=null) {
					executor.interrupt();
				}
				setVisible(false);
				dispose();
			}
		});
		ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			
			private String getIgnore(ArrayList<File> files) {
				if (files==null || files.size()==0) return null;
				StringBuilder b=new StringBuilder();
				for (File f:files) {
					b.append(" "+f.getName());
				}
				return b.toString();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished) {
					setVisible(false);
					dispose();
					return;
				}
				stdout.empty();
				stderr.empty();
				if (url.getSelectedItem()==null || url.getSelectedItem().equals("")) {
					JOptionPane.showMessageDialog(null,
							"You must provide a URL !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String URL=(String) url.getSelectedItem();
				stdout.addLine(new Couple("Importing project...",false));
				SvnCommand c=new SvnCommand().initialImport(project.getProjectDirectory(),URL,getIgnore(srcSubDirsToIgnore));
				if (authPane!=null) {
					char[] tmp=authPane.getPassword();
					c=c.auth(authPane.getLogin(),tmp);
					/* We clear the password array, as suggested in JPasswordField.getPassword() */
					for (int i=0;i<tmp.length;i++) {
						tmp[i]=0;
					}
				}
				ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterImportDo(URL), false);
				ArrayList<String> list=GramlabConfigManager.getSvnRepositories();
				if (!list.contains(URL)) {
					list.add(0,URL);
					GramlabConfigManager.setSvnRepositories(list);
				}
				executor=new Executor(parameters);
				lock();
				executor.start();
			}
		});
		down.add(cancel);
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		setContentPane(pane);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}

	private JPanel createMainPanel(boolean askForAuthentication) {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		stderr.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("You must set the URL of the repository you want to create,"),gbc);
		p.add(new JLabel("<html><body><font color=\"red\">including the project name</font>.</body></html>"),gbc);
		p.add(new JLabel(" "),gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Repository: "),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		ArrayList<String> list0=GramlabConfigManager.getSvnRepositories();
		String[] tab = new String[list0.size()];
		int i = 0;
		for (String s:list0) {
			tab[i++]=s;
		}
		model=new DefaultComboBoxModel(tab);
		url = new JComboBox(model);
		url.setEditable(true);
		p.add(url,gbc);
		if (askForAuthentication) {
			p.add(new JLabel(" "),gbc);
			authPane=new SvnAuthenticationPane();
			p.add(authPane,gbc);
		}
		p.add(new JLabel(" "),gbc);
		File[] list=project.getSrcDirectory().listFiles();
		if (list==null || list.length==0) {
			JLabel l=new JLabel("There is no sub-directory to share in the src directory!");
			l.setForeground(Color.RED);
			p.add(l,gbc);
		} else {
			p.add(new JLabel("In addition to its configuration files, Gramlab only"),gbc);
			p.add(new JLabel("allows you to share data inside the src directory."),gbc);
			p.add(new JLabel("Moreover, Gramlab does not share compiled graphs (.fst2)"),gbc);
			p.add(new JLabel("and compressed dictionaries (.bin and .inf)."),gbc);
			p.add(new JLabel("Please select the src sub-directories that you want"),gbc);
			p.add(new JLabel("to share:"),gbc);
			p.add(new JLabel(" "),gbc);
			JPanel p2=new JPanel(null);
			p2.setLayout(new BoxLayout(p2,BoxLayout.Y_AXIS));
			p2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			checkBoxes=new ArrayList<JCheckBox>();
			for (final File f:list) {
				if (!f.isDirectory()) continue;
				String name=f.getName();
				if (f.getName().equals("Inflection")) {
					name=name+" (required to inflect dictionaries)";
				} else if (project.isRequiredByPreprocessing(f)) {
					name=name+" (required by your preprocessing configuration)";
				}
				final JCheckBox b=new JCheckBox(name,true);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (!b.isSelected()) {
							if (!srcSubDirsToIgnore.contains(f))  {
								srcSubDirsToIgnore.add(f);
							}
						} else {
							srcSubDirsToIgnore.remove(f);
						}
					}
				});
				p2.add(b);
				checkBoxes.add(b);
			}
			p2.add(Box.createVerticalGlue());
			p.add(p2,gbc);
		}
		p.add(new JLabel(" "),gbc);
		JScrollPane scroll=new JScrollPane(stdout);
		scroll.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll,gbc);
		return p;
	}

	
	class AfterImportDo implements ToDo {
		
		String URL;
		
		AfterImportDo(String url) {
			this.URL=url;
		}
		
		@Override
		public void toDo(boolean success) {
			SvnCommandResult r;
			if (success) {
				r=new SvnCommandResult(SvnOpResult.OK,"");
			} else {
				r=SvnExecutor.getSvnError(stderr);
			}
			switch (r.getOp()) {
			case OK: {
				stdout.addLine(new Couple("Checking out project...",false));
				SvnCommand c=new SvnCommand().checkout(URL,project.getProjectDirectory());
				ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterCheckoutDo(), false);
				executor=new Executor(parameters);
				executor.start();
				return;
			}
			case AUTHENTICATION_REQUIRED: {
				getContentPane().remove(mainPanel);
				mainPanel=createMainPanel(true);
				getContentPane().add(mainPanel,BorderLayout.CENTER);
				mainPanel.revalidate();
				repaint();
				pack();
				unlock(false);
				return;
			}
			default: {
				JOptionPane.showMessageDialog(null,
						"Something unexpected occurred:\n\n"+r.getErr(), "Error",
						JOptionPane.ERROR_MESSAGE);
				unlock(false);
				return;
			}
		}
	}
	}

	class AfterCheckoutDo implements ToDo {

		@Override
		public void toDo(boolean success) {
			SvnCommandResult r;
			if (success) {
				r=new SvnCommandResult(SvnOpResult.OK,"");
			} else {
				r=SvnExecutor.getSvnError(stderr);
			}
			if (r.getOp()==SvnOpResult.OK) {
				project.asyncUpdateSvnInfo(null,false);
				unlock(true);
				return;
			}
			JOptionPane.showMessageDialog(null,
					"Something unexpected occurred:\n\n"+r.getErr(), "Error",
					JOptionPane.ERROR_MESSAGE);
			unlock(false);
			return;
		}
		
	}
	
	void lock() {
		ok.setEnabled(false);
		url.setEnabled(false);
		for (JCheckBox b:checkBoxes) {
			b.setEnabled(false);
		}
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			url.setEnabled(true);
			for (JCheckBox b:checkBoxes) {
				b.setEnabled(true);
			}
			cancel.setEnabled(true);
		}
		finished=success;
	}
	
	
}
