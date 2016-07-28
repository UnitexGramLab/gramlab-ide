package org.gramlab.core.gramlab.svn;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import org.gramlab.core.Main;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand.ResolveOp;
import org.gramlab.core.umlv.unitex.svn.SvnConflict;

@SuppressWarnings("serial")
public class SvnConflictDialog extends JDialog {
	
	ResolveOp op=ResolveOp.ACCEPT_WORKING;
	
	SvnConflict conflict;
	
	public SvnConflictDialog(SvnConflict c) {
		super(Main.getMainFrame(), "Handle conflict", true);
		this.conflict=c;
		JPanel pane=new JPanel(new BorderLayout());
		JPanel mainPanel=createMainPanel();
		pane.add(mainPanel,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(op) {
				case ACCEPT_BASE: conflict.useBase(); break;
				case ACCEPT_MINE: conflict.useMine(); break;
				case ACCEPT_OTHER: conflict.useOther(); break;
				case ACCEPT_WORKING: conflict.useWorking(); break;
				}
				setVisible(false);
				dispose();
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

	private JPanel createMainPanel() {
		JPanel p=new JPanel(new GridLayout(4,1));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		String[] names=new String[] {
				"Conflicts have been resolved in the file",
				"Resolve the conflict using my version of the file",
				"Resolve the conflict using the incoming version of the file",
				"Resolve the conflict using the base version of the file"
		};
		ResolveOp[] values=new ResolveOp[] {
				ResolveOp.ACCEPT_WORKING,
				ResolveOp.ACCEPT_MINE,
				ResolveOp.ACCEPT_OTHER,
				ResolveOp.ACCEPT_BASE
		};
		ButtonGroup bg=new ButtonGroup();
		for (int i=0;i<names.length;i++) {
			final JRadioButton b=new JRadioButton(names[i],i==0);
			final ResolveOp op1=values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						op=op1;
					}
				}
			});
			bg.add(b);
			p.add(b);
		}
		return p;
	}

}
