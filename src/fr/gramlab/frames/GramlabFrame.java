package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import fr.umlv.unitex.frames.InternalFrameManager;

@SuppressWarnings("serial")
public class GramlabFrame extends JFrame {
	
	Action editProject;
	Action deleteProject;
	JDesktopPane desktop;
	InternalFrameManager frameManager;
	
	public GramlabFrame() {
		super("GramLab");
		setJMenuBar(createMenuBar());
		JPanel tree=createWorkspacePane();
		desktop=new JDesktopPane();
		JSplitPane p=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,tree,desktop);
		frameManager=new InternalFrameManager(desktop);
		setContentPane(p);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JPanel createWorkspacePane() {
		JPanel p=new JPanel(new BorderLayout());
		p.add(new JTree());
		return p;
	}

	private JMenuBar createMenuBar() {
		JMenuBar bar=new JMenuBar();
		bar.add(createProjectMenu());
		bar.add(createActionsMenu());
		bar.add(createGraphsMenu());
		bar.add(createDictionariesMenu());
		bar.add(createSVNMenu());
		bar.add(createBuildMenu());
		bar.add(createTestMenu());
		bar.add(createConfigurationMenu());
		return bar;
	}

	private JMenu createProjectMenu() {
		JMenu m=new JMenu("Project");
		Action n=new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new CreateProjectDialog();
					}
				});
			}
		};
		m.add(new JMenuItem(n));
		Action open=new AbstractAction("Open") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(open));	
		editProject=new AbstractAction("Modify/Edit") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		editProject.setEnabled(false);
		m.add(new JMenuItem(editProject));
		deleteProject=new AbstractAction("Delete") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		deleteProject.setEnabled(false);
		m.add(new JMenuItem(deleteProject));
		m.addSeparator();
		Action modify=new AbstractAction("Modify a project") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(modify));	
		Action delete=new AbstractAction("Delete a project") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(delete));	
		return m;
	}

	
	private JMenu createActionsMenu() {
		JMenu m=new JMenu("Actions");
		return m;
	}

	
    public void openGraph() {
        final JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        final int returnVal = fc.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            // we return if the user has clicked on CANCEL
            return;
        }
        final File[] graphs = fc.getSelectedFiles();
        for (int i = 0; i < graphs.length; i++) {
            String s = graphs[i].getAbsolutePath();
            if (!graphs[i].exists() && !s.endsWith(".grf")) {
                s = s + ".grf";
                graphs[i] = new File(s);
                if (!graphs[i].exists()) {
                	JOptionPane.showMessageDialog(null, "File "+graphs[i].getAbsolutePath()
                            + " does not exist",
                            "Error", JOptionPane.ERROR_MESSAGE);
                	continue;
                }
            }
            frameManager.newGraphFrame(graphs[i]);
        }
    }

	
	private JMenu createGraphsMenu() {
		JMenu m=new JMenu("Graphs");
		Action n=new AbstractAction("New") {
			
			public void actionPerformed(ActionEvent e) {
				frameManager.newGraphFrame(null);
			}
		};
		m.add(new JMenuItem(n));
		Action open=new AbstractAction("Open") {
			
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};
		m.add(new JMenuItem(open));
		Action search=new AbstractAction("Search") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(search));
		m.addSeparator();
		Action saveAll=new AbstractAction("Save all") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(saveAll));
		Action closeAll=new AbstractAction("Close all") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(closeAll));		
		return m;
	}

	private JMenu createDictionariesMenu() {
		JMenu m=new JMenu("Dictionaries");
		Action n=new AbstractAction("New") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(n));
		Action open=new AbstractAction("Open") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(open));
		m.addSeparator();
		Action test=new AbstractAction("Test") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(test));
		Action check=new AbstractAction("Check") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(check));
		m.addSeparator();
		Action compress=new AbstractAction("Compress") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(compress));
		return m;
	}

	private JMenu createSVNMenu() {
		JMenu m=new JMenu("SVN");
		return m;
	}

	private JMenu createBuildMenu() {
		JMenu m=new JMenu("Build");
		Action launch=new AbstractAction("Launch") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(launch));
		Action report=new AbstractAction("Report") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(report));
		return m;
	}

	private JMenu createTestMenu() {
		JMenu m=new JMenu("Test");
		return m;
	}

	private JMenu createConfigurationMenu() {
		JMenu m=new JMenu("Configuration");
		JMenu visualization=new JMenu("Visualization");
		Action graphs=new AbstractAction("Graphs") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(graphs));
		Action text=new AbstractAction("Text") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(text));
		Action concordances=new AbstractAction("Concordances") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(concordances));
		Action diff=new AbstractAction("Diff") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(diff));
		m.add(visualization);

		Action project=new AbstractAction("Project") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(project));
		m.addSeparator();
		Action aboutUnitex=new AbstractAction("About Unitex") {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(aboutUnitex));
		return m;
	}


}
