package fr.umlv.unitex.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import fr.umlv.unitex.cassys.DataListFileNameRenderer;
import fr.umlv.unitex.cassys.ListDataTransfertHandler;
import fr.umlv.unitex.Config;
import fr.umlv.unitex.frames.UnitexFrame;


/**
 * User Configuration frame for Cassys.
 * <P>
 * This class to provides an easy graphical way to set the configuration file of Cassys. This configuration file
 * is made of a sorted list of files. Each file denotes a transducer. Transducers are applied in the order given 
 * by the configuration file. Each transducer may be applied with the <code>merge</code> or the <code>replace</code> 
 * option.
 * </P>
 * <P>
 * This class displays a frame including three main elements :
 * <ul>
 * <li> A file system explorer to navigate and select transducer file in the file system</li>
 * <li> A table containing the list of transducer. Each transducer has to be set the option <code>merge</code> 
 * or the <code>replace</code></li>
 * <li> A panel of buttons
 * </ul>
 * 
 * The functionalities provided by this class are the following :
 * <ul>
 * 		<li> Addition of file from the file explorer to the table <ul>
 *			<li>An <code>add</code> button is provided. Selection of a transducer file is needed. If a row is selected
 *				in the table, the file is added above this row, otherwise, the file is added at the bottom of the
 *				table</li>
 *			<li> Drag and drop is supported from the file explorer to the table. Insertion is made between rows 
 *				in the table</li>
 * 		</ul></li>
 * 		<li> Suppression of file from the table 
 * 			<ul><li><code>Delete</code> button : suppress the selected row from the table</li></ul>
 * 		</li>
 * 		<li> Move of files in the table
 * 			<ul>
 * 				<li>Drag and Drop supported inside the table</li>
 * 				<li>Buttons<ul>
 * 				<li><code>Up</code> exchanges the selected row with the row above the selected one</li>
 * 				<li><code>Down</code> exchanges the selected row with the row below the selected one</li>
 * 				<li><code>Top</code> moves the selected row above all others</li>
 * 				<li><code>Bottom</code> moves the selected row below all others</li>
 * 				</ul></li>
 * 			<ul>
 * 			
 * 			</ul>
 * 		</li>
 * </ul>
 * <P>
 * 
 * </P>
 * @author David Nott
 *
 */
public class TransducerListConfigurationFrame extends JInternalFrame implements ActionListener{
	
	static TransducerListConfigurationFrame frame;
	
	/**
	 * Table storing the sorted list of transducer files to be applied.
	 * <P>
	 * The table has three columns. The <code>name</code> column indicates the name of the transducer file. The
	 * <code>merge</code> and <code>replace</code> columns indicate whether merge or replace are selected. 
	 * To ensure that, only one of <code>merge</code> or <code>replace</code> is selected, the <code>DataChanged</code>
	 * method has been redefined.
	 * </P>
	 */
	JTable table;
	
	/**
	 * The <code>up</code> button. This class is listening to it.
	 */
	JButton up;
	
	/**
	 * The <code>down</code> button. This class is listening to it.
	 */
	JButton down;
	
	/**
	 * The <code>top</code> button. This class is listening to it.
	 */
	JButton top;
	
	/**
	 * The <code>bottom</code> button. This class is listening to it.
	 */
	JButton bottom;
	
	/**
	 * The <code>delete_</code> button. This class is listening to it.
	 */
	JButton delete_; 
	
	/**
	 * The <code>add_below</code> button. This class is listening to it.
	 */
	JButton add_below;
	
	/**
	 * The <code>new</code> button. This class is listening to it.
	 */
	JButton _new;
	
	
	
	/**
	 * The <code>open</code> button. This class is listening to it.
	 */
	JButton open;
	
	/**
	 * The <code>save</code> button. This class is listening to it.
	 */
	JButton save;
	
	/**
	 * The <code>save</code> button. This class is listening to it.
	 */
	JButton saveAs;
	
	/**
	 * The <code>close</code> button. This class is listening to it.
	 */
	JButton close;
	
	/**
	 * The system file explorer.
	 * <P>
	 * User may use it to select file. Buttons of this frame interacts with selected buttons
	 */
	JFileChooser fileBrowse;
	
	/**
	 * The name of transducers list file name
	 */

	
	
	/**
	 * The file explorer of the frame constructing this class.
	 * <P>
	 * Used to allow a refresh this file explorer when a new file is added
	 */
	JFileChooser parent;
	
	/**
	 * Whether transducers list has changed.
	 * <P>
	 * Used to display an <code>unsaved</code> symbol next to the transducer name and to query the user
	 * to save before leaving.
	 */
	boolean configurationHasChanged;
	
	/**
	 * The panel containing all the button
	 */
	JPanel button_panel;
	
	
	
	
	public TransducerListConfigurationFrame(){
		
		super("Cassys",true,true,true,true);
		//configurationFile = Config.getCurrentTransducerList();
		
		
		
		
		//this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
		
		
		
		fileBrowse = new JFileChooser(Config.getCurrentGraphDir());
		fileBrowse.setDragEnabled(true);
		fileBrowse.setControlButtonsAreShown(false);
		
		FileFilter filter_fst2 = new FileFilter() {

			@Override
			public String getDescription() {
				return new String("*.fst2");
			}
			
			@Override
			public boolean accept(File f) {
				if(f.isDirectory()){
					return true;
				}
				String fileName = f.getName();
				if(fileName.lastIndexOf(".")>=0 && fileName.substring(fileName.lastIndexOf(".")).equals(".fst2")){
					return true;
				}
				return false;
			}
		};
		fileBrowse.addChoosableFileFilter(filter_fst2);
		fileBrowse.setFileFilter(filter_fst2);
		
		//this.getContentPane().add(fileBrowse,BorderLayout.WEST);
		//this.getContentPane().add(fileBrowse);
		
		
		
		create_table(Config.getCurrentTransducerList());
		configurationHasChanged = false;
		setFrameTitle();
		
		JScrollPane tableScroller = new JScrollPane(table);
		
		//this.getContentPane().add(tableScroller,BorderLayout.EAST);
		
		//this.getContentPane().add(create_panel(),BorderLayout.CENTER);
		
		create_panel();
		
		
		JPanel mainContainer = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		mainContainer.setLayout(layout);
		
		GridBagConstraints fb_constraints = new GridBagConstraints();
		fb_constraints.gridwidth = 1;
		fb_constraints.gridx = 0;
		layout.setConstraints(fileBrowse, fb_constraints);
		
		GridBagConstraints bp_constraints = new GridBagConstraints();
		bp_constraints.gridwidth = 1;
		bp_constraints.gridx = 1;
		layout.setConstraints(button_panel, bp_constraints);
		
		GridBagConstraints ts_constraints = new GridBagConstraints();
		tableScroller.setMinimumSize(new Dimension(450,364));
		ts_constraints.gridwidth = 1;
		ts_constraints.gridx = 2;
		ts_constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(tableScroller, ts_constraints);
		
		
		
		mainContainer.add(fileBrowse);
		mainContainer.add(button_panel);
		mainContainer.add(tableScroller);
		
		System.out.println("table scroller min = " + tableScroller.getMinimumSize());
		System.out.println("table scroller pref = " + tableScroller.getPreferredSize());
		System.out.println("table scroller curr = " + tableScroller.getSize());
		
		System.out.println("table min = " + table.getMinimumSize());
		System.out.println("table pref = " + table.getPreferredSize());
		System.out.println("table curr = " + table.getSize());
		
		System.out.println("panel min = " + button_panel.getMinimumSize());
		System.out.println("panel pref = " + button_panel.getPreferredSize());
		System.out.println("panel curr = " + button_panel.getSize());
		
		System.out.println("browse min = " + fileBrowse.getMinimumSize());
		System.out.println("browse pref = " + fileBrowse.getPreferredSize());
		System.out.println("browse curr = " + fileBrowse.getSize());
		
		this.getContentPane().add(mainContainer);
		this.pack();
		
		this.setVisible(true);
	}

	
	/**
	 * Creates the table used by the configuration frame
	 * <P>
	 * 
	 * The table is created with three columns <code>Name</code>, <code>Merge</code> and <code>Replace</code> which
	 * are expected to contain a <code>String</code> and two <code>booleans</code>. 
	 * 
	 * <code>Merge</code> and <code>Replace</code> fields
	 * cannot have the same value. Integrity test is done in the overridden 
	 * {@link JTable#tableChanged(TableModelEvent)} method.
	 * 
	 * The table is set with the following properties :
	 * <ul>
	 * <li>Single selection only allowed</li>
	 * <li>Insertion between rows only</li>
	 * <li>Drag and drop enabled for <code>String</code> and <code>ListData</code></li>
	 * </ul>
	 */
	void create_table(File f){
		DefaultTableModel tableModel = new DefaultTableModel(){
			/**
			 * Redefinition of the getColumnClass method in order to provide a check box for boolean values
			 */
			@Override
			public Class<?> getColumnClass(int c) {
				return getValueAt(0, c).getClass();
	        }
		};
		tableModel.addColumn("Name");
		tableModel.addColumn("Merge");
		tableModel.addColumn("Replace");
		
		if (f != null) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(f));

				String line;
				while ((line = r.readLine()) != null) {
					String fileName = line.substring(0, line.lastIndexOf(' '));
					String fileMode = line.substring(line.lastIndexOf(' ') + 1);

					if (fileName.startsWith("\"")) {
						fileName = fileName.substring(1, fileName
								.lastIndexOf('\"'));
					}
					// System.out.println("line read = "+ fileName + "--" +
					// fileMode);

					if (fileMode.equals("M") || fileMode.equals("Merge")
							|| fileMode.equals("merge")) {
						Object[] row = { fileName, new Boolean(true),
								new Boolean(false) };
						tableModel.addRow(row);
					} else if (fileMode.equals("R")
							|| fileMode.equals("Replace")
							|| fileMode.equals("replace")) {
						Object[] row = { fileName, new Boolean(false),
								new Boolean(true) };
						tableModel.addRow(row);
					}

				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		table = new JTable(tableModel){
			/**
			 * Redefinition of the tableChanged method to ensure integrity data (ie both merge an replace cannot be 
			 * set at true)
			 */
			@Override
			public void tableChanged(TableModelEvent e){
			
			if(e.getColumn() == 1){
				if(e.getFirstRow() != TableModelEvent.HEADER_ROW){
					for(int i=e.getFirstRow();i<=e.getLastRow();i++){
						if((Boolean) getValueAt(i, 1)== true && (Boolean) getValueAt(i, 2)== true){
							setValueAt(Boolean.FALSE, i, 2);
						}
						
						if((Boolean) getValueAt(i, 1)== false && (Boolean) getValueAt(i, 2)== false){
							setValueAt(Boolean.TRUE, i, 2);
						}
						// make sure that custom renderer repaint all the changed value
						repaint();
						
					}
				}
			}
			
			if(e.getColumn() == 2){
				if(e.getFirstRow() != TableModelEvent.HEADER_ROW){
					
					for(int i=e.getFirstRow();i<=e.getLastRow();i++){
						if((Boolean) getValueAt(i, 2)== true && (Boolean) getValueAt(i, 1)== true){
							setValueAt(Boolean.FALSE, i, 1);
						}
						if((Boolean) getValueAt(i, 2)== false && (Boolean) getValueAt(i, 1)== false){
							setValueAt(Boolean.TRUE, i, 1);
						}
						// make sure that custom renderer repaint all the changed value
						repaint();
						
					}
				}
			}
			
			configurationHasChanged = true;
			setFrameTitle();
			
			super.tableChanged(e);	
			
			}
		};
		
		// ensure that empty table is visible
		table.setFillsViewportHeight(true);
		
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.setDefaultRenderer(String.class, new DataListFileNameRenderer());
		table.setTransferHandler(new ListDataTransfertHandler());

		Dimension defaultTableViewPortSize = table
				.getPreferredScrollableViewportSize();
		Dimension currentTableViewPortSize = new Dimension(
				460,//defaultTableViewPortSize.width * 2,
				defaultTableViewPortSize.height);
		table.setPreferredScrollableViewportSize(currentTableViewPortSize);

		//TableColumn col = table.getColumnModel().getColumn(0);
		//col.setPreferredWidth(600);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(340);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		
		
		
	}
	
	
	
	/**
	 * Constructs and return a panel of buttons for user interaction with the table. <code>ConfigurationFrame</code> is added
	 * as listener for all buttons.
	 * 
	 * @return the panel with all the buttons
	 */
	JPanel create_panel(){
		Dimension defaultButtonDimension = new Dimension(110,28);
		
		button_panel = new JPanel();
		button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
		
		
		button_panel.add(Box.createRigidArea(new Dimension(150,20)));
		
		up = new JButton("Up");
		up.addActionListener(this);
		up.setMaximumSize(defaultButtonDimension);
		up.setAlignmentX(Component.CENTER_ALIGNMENT);
		up.setToolTipText("Exchange the selected transducer with the transducer immediately above in the list");
		button_panel.add(up);
		
		
		
		down = new JButton("Down");
		down.addActionListener(this);
		down.setMaximumSize(defaultButtonDimension);
		down.setAlignmentX(Component.CENTER_ALIGNMENT);
		down.setToolTipText("Exchange the selected transducer with the transducer immediately below in the list");
		button_panel.add(down);
		
		top = new JButton("Top");
		top.addActionListener(this);
		top.setMaximumSize(defaultButtonDimension);
		top.setAlignmentX(Component.CENTER_ALIGNMENT);
		top.setToolTipText("Move the selected transducer to the top of the list");
		button_panel.add(top);
		
		bottom = new JButton("Bottom");
		bottom.addActionListener(this);
		bottom.setMaximumSize(defaultButtonDimension);
		bottom.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottom.setToolTipText("Move the selected transducer to the bottom of the list");
		button_panel.add(bottom);
		
		button_panel.add(Box.createRigidArea(new Dimension(150,20)));
		
		
		delete_ = new JButton("Delete");
		delete_.addActionListener(this);
		delete_.setMaximumSize(defaultButtonDimension);
		delete_.setToolTipText("Delete the selected transducer from the list");
		delete_.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(delete_);
		
		add_below = new JButton("Add");
		add_below.addActionListener(this);
		add_below.setMaximumSize(defaultButtonDimension);
		add_below.setAlignmentX(Component.CENTER_ALIGNMENT);
		add_below.setToolTipText("Insert the selected file in the explorer file above the selected transducer in the list");
		button_panel.add(add_below);
		
		_new = new JButton("New");
		_new.addActionListener(this);
		_new.setMaximumSize(defaultButtonDimension);
		_new.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(_new);
		
		button_panel.add(Box.createRigidArea(new Dimension(50,20)));
		
		
		open = new JButton("View");
		open.addActionListener(this);
		open.setMaximumSize(defaultButtonDimension);
		open.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(open);
		
		button_panel.add(Box.createRigidArea(new Dimension(50,20)));
		
		
		
		save = new JButton("Save");
		save.addActionListener(this);
		save.setMaximumSize(defaultButtonDimension);
		save.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(save);
		
		saveAs = new JButton("Save As ...");
		saveAs.addActionListener(this);
		saveAs.setMaximumSize(defaultButtonDimension);
		saveAs.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(saveAs);
		
		button_panel.add(Box.createRigidArea(new Dimension(50,20)));
		
		close = new JButton("Close");
		close.addActionListener(this);
		close.setMaximumSize(defaultButtonDimension);
		close.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(close);
		
		return button_panel;
	}
	
	
	
	
	
	/**
	 * Identifies the source of the event and reacts to it. 
	 * 
	 * Event may come from the button <code>add_below</code>, <code>delete</code>, <code>up</code>, 
	 * <code>down</code>, <code>top</code> or <code>bottom</code>. Except with the <code>delete</code> event,
	 * selection is kept on the same logical row (to oppose to the same row index). When a selected row is moved,
	 * selection moves with it.
	 * 
	 * @param a Action event notified to this class.
	 */
	public void actionPerformed(ActionEvent a) {
		
		if (up == a.getSource()) {
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			if (selected_row > 0) {
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();

				try {
					dtm.moveRow(selected_row, selected_row, selected_row - 1);
					table.getSelectionModel().setSelectionInterval(
							selected_row - 1, selected_row - 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}

		if (down == a.getSource()) {
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			if (selected_row != -1 && selected_row < table.getRowCount()-1) {
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();

				try {
					dtm.moveRow(selected_row, selected_row, selected_row + 1);
					table.getSelectionModel().setSelectionInterval(
							selected_row + 1, selected_row + 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(top == a.getSource()){
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();

				try {
					dtm.moveRow(selected_row, selected_row, 0);
					table.getSelectionModel().setSelectionInterval(
							0, 0);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(bottom == a.getSource()){
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();

				try {
					dtm.moveRow(selected_row, selected_row, table.getRowCount()-1);
					table.getSelectionModel().setSelectionInterval(
							table.getRowCount()-1, table.getRowCount()-1);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(delete_ == a.getSource()){
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				DefaultTableModel dtm = (DefaultTableModel) table.getModel();

				try {
					dtm.removeRow(selected_row);
					
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			else {
				String t = "No transducer selected";
				String message = "Please select the transducer to be deleted from the list";
				JOptionPane.showMessageDialog(this, message, t, JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(add_below == a.getSource()){
			configurationHasChanged = true;
			setFrameTitle();
			int selected_row = table.getSelectedRow();
			
			DefaultTableModel dtm = (DefaultTableModel) table.getModel();
			File selected_file = fileBrowse.getSelectedFile();

			try {
				Object[] row = { new String(selected_file.getPath()),
						new Boolean(true), new Boolean(false) };
				if (selected_row == -1) {
					selected_row = dtm.getRowCount();
				}
				dtm.insertRow(selected_row, row);
				table.getSelectionModel().setSelectionInterval(selected_row,
						selected_row);

			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			 
		}

		
		
		if(open == a.getSource()){
			if (table.getSelectedRow() != -1) {
				File graph = new File((String)table.getModel().getValueAt(table.getSelectedRow(), 0));
				viewGraph(graph);
			} else {
				JOptionPane
						.showMessageDialog(
								this,
								"Please select a transducer in the list.\nIf you wish to view a graph in the file explorer, use the view button below the file explorer\n",
								"No transducer selected in the list", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(saveAs == a.getSource()){
			showSaveFrame();
			
		}
		
		if(save == a.getSource()){
			if(Config.getCurrentTransducerList() != null){
				saveListToFile();
			}
			else {
				showSaveFrame();
			}
		}
		
		if(close == a.getSource()){
			quit_asked();
		}
		
		
		
	}
	
	public void quit_asked(){
		if(configurationHasChanged){
			String message = "Changes to the transducer list may not have been saved.\n Do you want to save changes before leaving ?";
			
			int return_val = JOptionPane.showConfirmDialog(this, message);
			
			
			if(return_val == JOptionPane.YES_OPTION){
				showSaveFrame();
			} else if(return_val == JOptionPane.NO_OPTION){
				System.out.println("NO OPTION");
				quit();
			}
			
			
		}else {
			quit();
		}
	}
	
	
	public void quit(){
		dispose();
	}
	
	public void showSaveFrame() {
		JFileChooser saveFileChooser;

		if (Config.getCurrentTransducerList() != null) {
			saveFileChooser = new JFileChooser(Config
					.getCurrentTransducerList().getParentFile());
			saveFileChooser.setSelectedFile(Config.getCurrentTransducerList());
		} else {
			saveFileChooser = new JFileChooser(Config.getCassysDir());
		}

		int returnVal = saveFileChooser.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Config.setCurrentTransducerList(saveFileChooser.getSelectedFile());
			saveListToFile();
		}

	}
	
	
	public void saveListToFile() {
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(
					Config.getCurrentTransducerList()));

			for (int i = 0; i < table.getModel().getRowCount(); i++) {
				String fileName = (String) table.getValueAt(i, 0);
				fw.write("\"" + fileName + "\" ");
				if ((Boolean) table.getValueAt(i, 1) == true) {
					fw.write("Merge");
				} else {
					fw.write("Replace");
				}
				fw.newLine();
			}

			fw.close();
			configurationHasChanged = false;
			setFrameTitle();
			//parent.rescanCurrentDirectory();
			Config.getTransducerListDialogBox().rescanCurrentDirectory();

		} catch (IOException e) {
			System.out.println("IO Error");
		}
	}
	
	public void setFrameTitle(){
		String frameTitle = new String("Cassys Transducer Configuration : ");
		
		if(Config.getCurrentTransducerList() != null){
			frameTitle = frameTitle.concat(Config.getCurrentTransducerList().getName());
		}
		
		if(configurationHasChanged){
			frameTitle = frameTitle.concat(" (unsaved)");
		}
		setTitle(frameTitle);
	}
	
	
	public void viewGraph(File f){
		File grf = null;
		
		if(f.getName().endsWith(".grf")){
			grf = f;
		}
		if(f.getName().endsWith(".fst2")){
			String nameWithoutExtension = f.getPath().substring(0,f.getPath().lastIndexOf("."));
			String nameWithExtension = nameWithoutExtension.concat(".grf");
			grf = new File(nameWithExtension);
		}
		
		if(grf != null){
			//UnitexFrame.mainFrame.loadGraph(grf);
			UnitexFrame.getFrameManager().newGraphFrame(grf);
		}
		else {
			String t = "Wrong file selected";
			String message = "Please select a file with the fst2 extension";
			JOptionPane.showMessageDialog(this, message, t, JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	
	public void setConfigurationHasChanged(Boolean b){
		configurationHasChanged = b;
	}
	
	public Boolean getConfigurationHasChanged(){
		return configurationHasChanged;
	}
	
	
	
}
