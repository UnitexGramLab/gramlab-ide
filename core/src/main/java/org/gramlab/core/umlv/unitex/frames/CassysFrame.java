package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList;
import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList.FormatFileException;
import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList.RequiredDirectoryNotExist;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.ToDoBeforeSingleCommand;
import org.gramlab.core.umlv.unitex.process.commands.CassysCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;

/**
 * Main frame of the cassys menu.
 * <p/>
 * <p/>
 * This class displays a frame allowing the user to do the following actions :
 * <ul>
 * <li>select a transducers list file and launch a cascade
 * <li>create a new transducer list file or edit an existing one
 * </ul>
 * <p/>
 * Internally, this class is made of <code>JFileChooser</code> which allows the
 * user to select a transducer list file and three <code>JButton</code> which
 * allows the user to launch the cascade, edit an existing transducer list file
 * or create a new existing transducer list file.
 * 
 * @author David Nott and Nathalie Friburger
 */
public class CassysFrame extends JInternalFrame implements ActionListener {
	static CassysFrame frame;
	/**
	 * The file explorer which allows the user to select a transducer list file.
	 * <p/>
	 * This file explorer is currently initiated on the cassys directory which
	 * is expected to contain transducers list files. <code>open</code> and
	 * <code>cancel</code> buttons are hidden since an editing already exists
	 */
	private final JFileChooser fc;
	/**
	 * The <code>launch</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private final JButton launch;
	/**
	 * The <code>new</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private final JButton _new;
	/**
	 * The <code>edit</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private final JButton edit;
	
	/**
	 * The <code>import transducer file</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	//private final JButton _import;
	
	/**
	 * The <code>export transducer file</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private final JButton export;
	

	/**
	 * The <code>CassysFrame</code> constructor
	 * <p/>
	 * This class creates the <code>JFileChooser</code> and the
	 * <code>JButton</code>. It uses the <code>BorderLayout</code> layout
	 * manager to display these elements on the frame.
	 */
	public CassysFrame() {
		super("Cassys", true, true, true, true);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		fc = Config.getTransducerListDialogBox();
		this.getContentPane().add(fc, BorderLayout.WEST);
		final JPanel jpan = new JPanel();
		jpan.setLayout(new BoxLayout(jpan, BoxLayout.Y_AXIS));
		final Dimension defaultButtonDimension = new Dimension(110, 28);
		jpan.add(Box.createRigidArea(new Dimension(150, 40)));
		_new = new JButton("New");
		_new.setMaximumSize(defaultButtonDimension);
		_new.setAlignmentX(Component.CENTER_ALIGNMENT);
		_new.addActionListener(this);
		jpan.add(_new);
		edit = new JButton("Edit");
		edit.setMaximumSize(defaultButtonDimension);
		edit.setAlignmentX(Component.CENTER_ALIGNMENT);
		edit.addActionListener(this);
		jpan.add(edit);
		jpan.add(Box.createRigidArea(new Dimension(150, 60)));
		launch = new JButton("Launch");
		launch.setMaximumSize(defaultButtonDimension);
		launch.setAlignmentX(Component.CENTER_ALIGNMENT);
		launch.addActionListener(this);
		jpan.add(launch);
		jpan.add(Box.createRigidArea(new Dimension(150, 60)));
		/*_import = new JButton("Import");
		_import.setMaximumSize(defaultButtonDimension);
		_import.setAlignmentX(Component.CENTER_ALIGNMENT);
		_import.addActionListener(this);
		jpan.add(_import);*/
		export = new JButton("Export");
		export.setMaximumSize(defaultButtonDimension);
		export.setAlignmentX(Component.CENTER_ALIGNMENT);
		export.addActionListener(this);
		jpan.add(export);
		jpan.add(Box.createRigidArea(new Dimension(150, 40)));
		this.getContentPane().add(jpan, BorderLayout.EAST);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * This functions defines reactions when an action event is listened by this
	 * class
	 * 
	 * @param a
	 *            the action listened by the frame
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		
		File selected_file = null;
		
		if (a.getSource() == edit) {
			selected_file = fc.getSelectedFile();
		}
		
		if (a.getSource() == edit || a.getSource() == _new) {
			TransducerListConfigurationFrame t = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class).getTransducerListConfigurationFrame();
			
			// If save has to be done. Do it first
			if (t != null && t.isConfigurationHasChanged()) {
				t.quit_asked();
			} else { // Open
				if (t != null) {
					t.quit();
				}
				Config.setCurrentTransducerList(selected_file);
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newTransducerListConfigurationFrame(selected_file);
			}
		}
		if (a.getSource() == launch) {
			if (fc.getSelectedFile() != null) {
				final MultiCommands cassysCommand = new MultiCommands();
				final File f_alphabet = ConfigManager.getManager().getAlphabet(
						null);
				final File f_transducer = fc.getSelectedFile();
				final File f_target = Config.getCurrentSnt();
				CassysCommand com = new CassysCommand()
						.alphabet(f_alphabet)
						.targetText(f_target)
						.transducerList(f_transducer)
						.morphologicalDic(ConfigManager.getManager().morphologicalDictionaries(null))
						.separatorsToSystem()
						.transducerDir(Config.getCurrentGraphDir())
                                                .inputOffset(new File(Config.getCurrentSntDir(), "normalize.out.offsets"));
                                                                                              
				com.setWhatToDoBefore(new BeforeCassysDo(f_target.getAbsolutePath()));
				
				cassysCommand.addCommand(com);
				
				//System.out.println(com.getCommandLine());
				
				// new ProcessInfoFrame(com, true, new CassysDo());
				Launcher.exec(cassysCommand, true, new CassysDo(Config.getUserCurrentLanguageDir(), f_target));
				System.out.println(cassysCommand);
			}
		}
		/*
		if (a.getSource() == _import){
			ShareTransducerList stl = new ShareTransducerList();
			try {
				File importFile = stl.importList(fc.getSelectedFile());
				
				final String message = new String ("The imported file is stored in " + importFile.getPath());
				
				JOptionPane.showMessageDialog(this, message, "Success",
						JOptionPane.INFORMATION_MESSAGE);
				
				
			} catch (IOException e) {
				final String t = "I/O Error";
				JOptionPane.showMessageDialog(this, e.getMessage(), t,
						JOptionPane.ERROR_MESSAGE);
			} catch (FormatFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		if (a.getSource() == export){
			ShareTransducerList stl = new ShareTransducerList();
			try {
				File exportFile = stl.exportList(fc.getSelectedFile());
				
				final String message = new String ("The exported file is stored in " + exportFile.getPath());
				
				JOptionPane.showMessageDialog(this, message, "Success",
						JOptionPane.INFORMATION_MESSAGE);
				
			} catch (IOException e) {
				final String t = "I/O Error";
				JOptionPane.showMessageDialog(this, e.getMessage(), t,
						JOptionPane.ERROR_MESSAGE);
			} catch (FormatFileException e) {
				final String t = "Format file Error";
				JOptionPane.showMessageDialog(this, e.getMessage(), t,
						JOptionPane.ERROR_MESSAGE);
			} catch (RequiredDirectoryNotExist e){
				final String t = "Required directory not found";
				JOptionPane.showMessageDialog(this, e.getMessage(), t,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Defines the action to take when the Cassys command ends.
	 * 
	 * @author David Nott
	 */
	class CassysDo implements ToDo {
		File languageDirectory;
		File resultFile;

		public CassysDo(File languageDir, File targetSntFile) {
			languageDirectory = languageDir;
			resultFile = new File(FileUtil.getFileNameWithoutExtension(targetSntFile) + "_csc.txt");
				
		}

		@Override
		public void toDo(boolean success) {
			
			
			GlobalProjectManager.search(languageDirectory).getFrameManagerAs(InternalFrameManager.class)
					.newConcordanceParameterFrame();
		}
	}
	
	/**
	 * Defines the action to take before the cassys command
	 * @author David Nott, Nathalie Friburger (nathalie.friburger@univ-tours.fr)
	 *
	 */
	class BeforeCassysDo implements ToDoBeforeSingleCommand{

		private String targetSntFile;
		
		
		public BeforeCassysDo(String targetSntFile){
			
			this.targetSntFile = targetSntFile;
		}
		


		@Override
		public void toDo(ConsoleEntry entry) {
			
			File CorpusDir = new File(FileUtil.getFileNameWithoutExtension(targetSntFile)+"_csc");
			if (CorpusDir.exists()) {
				FileUtil.rm(CorpusDir);
			}
			CorpusDir.mkdir();
			
		}
		
		
		
		
		
	}
}
