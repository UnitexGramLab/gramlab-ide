package fr.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

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
public class SvnDeleteUrlDialog extends JDialog {
	
	SvnAuthenticationPane authPane=null;
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	GramlabProject project;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	ProcessOutputList stderr=new ProcessOutputList(new ProcessOutputListModel());
	String url;
	boolean finished=false;
	
	public SvnDeleteUrlDialog(String url) {
		super(Main.getMainFrame(), "SVN Deleting URL ", true);
		this.url=url;
		JPanel pane=new JPanel(new BorderLayout());
		mainPanel=createMainPanel(false);
		pane.add(mainPanel,BorderLayout.CENTER);
		JPanel down=new JPanel();
		cancel=new JButton("No");
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
		ok=new JButton("Yes");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished) {
					setVisible(false);
					dispose();
					return;
				}
				lock();
				svnDeleteUrl();
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

	protected void svnDeleteUrl() {
		stdout.empty();
		stderr.empty();
		stdout.addLine(new Couple("Deleting url...",false));
		SvnCommand c=new SvnCommand().delete(url,"Deleting project");
		if (authPane!=null) {
			char[] tmp=authPane.getPassword();
			c=c.auth(authPane.getLogin(),tmp);
			/* We clear the password array, as suggested in JPasswordField.getPassword() */
			for (int i=0;i<tmp.length;i++) {
				tmp[i]=0;
			}
		}
		ExecParameters parameters=new ExecParameters(true, c, stdout, stderr, new AfterDeleteDo(), false,null);
		executor=new Executor(parameters);
		executor.start();
	}


	private JPanel createMainPanel(boolean askForAuthentication) {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		stderr.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("This project is versioned with SVN. In addition to local removal,"),gbc);		
		p.add(new JLabel("do you want to delete the project on the repository ?"),gbc);		
		p.add(new JLabel(" "),gbc);
		p.add(new JLabel("URL: "+url),gbc);
		if (askForAuthentication) {
			p.add(new JLabel(" "),gbc);
			authPane=new SvnAuthenticationPane();
			p.add(authPane,gbc);
		}
		gbc.weighty=0;
		p.add(new JLabel(" "),gbc);
		JScrollPane scroll2=new JScrollPane(stdout);
		scroll2.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll2,gbc);
		return p;
	}

	
	class AfterDeleteDo implements ToDo {
		
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
				unlock(true);
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

	
	void lock() {
		ok.setEnabled(false);
		cancel.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			cancel.setEnabled(true);
		}
		finished=success;
	}
	
}
