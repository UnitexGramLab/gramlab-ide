package org.gramlab.core.umlv.unitex.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

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
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.cassys.ConfigurationFileAnalyser;
import org.gramlab.core.umlv.unitex.cassys.ConfigurationFileAnalyser.EmptyLineException;
import org.gramlab.core.umlv.unitex.cassys.ConfigurationFileAnalyser.InvalidLineException;
import org.gramlab.core.umlv.unitex.cassys.DataList;
import org.gramlab.core.umlv.unitex.cassys.DataListFileNameRenderer;
import org.gramlab.core.umlv.unitex.cassys.DataListFileRankRenderer;
import org.gramlab.core.umlv.unitex.cassys.ListDataTransfertHandler;
import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList;
import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList.NotAnAbsolutePathException;
import org.gramlab.core.umlv.unitex.cassys.TransducerListTable;
import org.gramlab.core.umlv.unitex.cassys.TransducerListTableModel;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * User Configuration frame for Cassys.
 * <p/>
 * This class to provides an easy graphical way to set the configuration file of
 * Cassys. This configuration file is made of a sorted list of files. Each file
 * denotes a transducer. Transducers are applied in the order given by the
 * configuration file. Each transducer may be applied with the
 * <code>merge</code> or the <code>replace</code> option.
 * </P>
 * <p/>
 * This class displays a frame including three main elements :
 * <ul>
 * <li>A file system explorer to navigate and select transducer file in the file
 * system</li>
 * <li>A table containing the list of transducer. Each transducer has to be set
 * the option <code>merge</code> or the <code>replace</code></li>
 * <li>A panel of buttons
 * </ul>
 * <p/>
 * The functionalities provided by this class are the following :
 * <ul>
 * <li>Addition of file from the file explorer to the table
 * <ul>
 * <li>An <code>add</code> button is provided. Selection of a transducer file is
 * needed. If a row is selected in the table, the file is added above this row,
 * otherwise, the file is added at the bottom of the table</li>
 * <li>Drag and drop is supported from the file explorer to the table. Insertion
 * is made between rows in the table</li>
 * </ul>
 * </li>
 * <li>Suppression of file from the table
 * <ul>
 * <li><code>Delete</code> button : suppress the selected row from the table</li>
 * </ul>
 * </li>
 * <li>Move of files in the table
 * <ul>
 * <li>Drag and Drop supported inside the table</li>
 * <li>Buttons
 * <ul>
 * <li><code>Up</code> exchanges the selected row with the row above the
 * selected one</li>
 * <li><code>Down</code> exchanges the selected row with the row below the
 * selected one</li>
 * <li><code>Top</code> moves the selected row above all others</li>
 * <li><code>Bottom</code> moves the selected row below all others</li>
 * </ul>
 * </li>
 * <ul>
 * <p/>
 * </ul>
 * </li>
 * </ul>
 * <p/>
 * <p/>
 * </P>
 * 
 * @author David Nott
 */
public class TransducerListConfigurationFrame extends JInternalFrame implements
		ActionListener, TableModelListener {
	
	/**
	 * Table storing the sorted list of transducer files to be applied.
	 * <p/>
	 * The table has seven columns. The <code>name</code> column indicates the
	 * name of the transducer file. The <code>merge</code> and
	 * <code>replace</code> columns indicate whether merge or replace are
	 * selected. To ensure that, only one of <code>merge</code> or
	 * <code>replace</code> is selected, the <code>DataChanged</code> method has
	 * been redefined.
	 * </P>
	 */
	private TransducerListTable table;
	/**
	 * The <code>up</code> button. This class is listening to it.
	 */
	private JButton up;
	/**
	 * The <code>down</code> button. This class is listening to it.
	 */
	private JButton down;
	/**
	 * The <code>top</code> button. This class is listening to it.
	 */
	private JButton top;
	/**
	 * The <code>bottom</code> button. This class is listening to it.
	 */
	private JButton bottom;
	/**
	 * The <code>delete_</code> button. This class is listening to it.
	 */
	private JButton delete_;
	/**
	 * The <code>add_below</code> button. This class is listening to it.
	 */
	private JButton add_below;
	/**
	 * The <code>open</code> button. This class is listening to it.
	 */
	private JButton open;
	/**
	 * The <code>save</code> button. This class is listening to it.
	 */
	private JButton save;
	/**
	 * The <code>save</code> button. This class is listening to it.
	 */
	private JButton saveAs;
	/**
	 * The <code>close</code> button. This class is listening to it.
	 */
	private JButton close;
	/**
	 * The <code>recompile graphs button</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private JButton recompile_graphs;

	
	/**
	 * The <code>enable_all</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private JButton enable_all;
	/**
	 * The <code>disable_all</code> button.
	 * <p/>
	 * This class is listenning to it
	 */
	private JButton disable_all;
	
	
	
	/**
	 * The system file explorer.
	 * <p/>
	 * User may use it to select file. Buttons of this frame interacts with
	 * selected buttons
	 */
	private final JFileChooser fileBrowse;
	/**
	 * Whether transducers list has changed.
	 * <p/>
	 * Used to display an <code>unsaved</code> symbol next to the transducer
	 * name and to query the user to save before leaving.
	 */
	private boolean configurationHasChanged;
	/**
	 * Whether edited file has comments.
	 * <p/>
	 * Used to warn the user that comment would be erased if he saves the file.
	 */
	private boolean editedFileHasCommentOrError;
	/**
	 * The panel containing all the button
	 */
	private JPanel button_panel;

	public TransducerListConfigurationFrame() {
		super("Cassys", true, true, true, true);
		// configurationFile = Config.getCurrentTransducerList();
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		fileBrowse = new JFileChooser(Config.getCurrentGraphDir());
		fileBrowse.setDragEnabled(true);
		fileBrowse.setControlButtonsAreShown(false);
		//fileBrowse.setMinimumSize(new Dimension(350, 370));
		final FileFilter filter_fst2 = new FileFilter() {
			@Override
			public String getDescription() {
				return "*.fst2";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				final String fileName = f.getName();
				return fileName.lastIndexOf(".") >= 0
						&& fileName.substring(fileName.lastIndexOf("."))
								.equals(".fst2");
			}
		};
		fileBrowse.addChoosableFileFilter(filter_fst2);
		fileBrowse.setFileFilter(filter_fst2);
		editedFileHasCommentOrError = false;
		create_table();
		configurationHasChanged = false;
		setFrameTitle();
		final JScrollPane tableScroller = new JScrollPane(table);
		create_panel();
		final JPanel mainContainer = new JPanel();
		final GridBagLayout layout = new GridBagLayout();
		mainContainer.setLayout(layout);
		final GridBagConstraints fb_constraints = new GridBagConstraints();
		fb_constraints.gridwidth = 1;
		fb_constraints.gridx = 0;
		layout.setConstraints(fileBrowse, fb_constraints);
		final GridBagConstraints bp_constraints = new GridBagConstraints();
		bp_constraints.gridwidth = 1;
		bp_constraints.gridx = 1;
		layout.setConstraints(button_panel, bp_constraints);
		final GridBagConstraints ts_constraints = new GridBagConstraints();
		tableScroller.setMinimumSize(new Dimension(620, 370));
		ts_constraints.gridwidth = 1;
		ts_constraints.gridx = 2;
		ts_constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(tableScroller, ts_constraints);
		mainContainer.add(fileBrowse);
		mainContainer.add(button_panel);
		mainContainer.add(tableScroller);
		this.getContentPane().add(mainContainer);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Creates the table used by the configuration frame
	 * <p/>
	 * <p/>
	 * The table is created with three columns <code>Name</code>,
	 * <code>Merge</code> and <code>Replace</code> which are expected to contain
	 * a <code>String</code> and two <code>booleans</code>.
	 * <p/>
	 * <code>Merge</code> and <code>Replace</code> fields cannot have the same
	 * value. Integrity test is done in the overridden
	 * {@link JTable#tableChanged(TableModelEvent)} method.
	 * <p/>
	 * The table is set with the following properties :
	 * <ul>
	 * <li>Single selection only allowed</li>
	 * <li>Insertion between rows only</li>
	 * <li>Drag and drop enabled for <code>String</code> and
	 * <code>ListData</code></li>
	 * </ul>
	 */
	void create_table() {
		final TransducerListTableModel tableModel = new TransducerListTableModel();
		
		tableModel.addTableModelListener(this);
		
		table = new TransducerListTable(tableModel);
		// ensure that empty table is visible
		table.setFillsViewportHeight(true);
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.setDefaultRenderer(String.class, new DataListFileNameRenderer());
		table.setDefaultRenderer(Integer.class, new DataListFileRankRenderer());
		
		table.setTransferHandler(new ListDataTransfertHandler());
		final Dimension defaultTableViewPortSize = table
				.getPreferredScrollableViewportSize();
		final Dimension currentTableViewPortSize = new Dimension(630,
				defaultTableViewPortSize.height);
		table.setPreferredScrollableViewportSize(currentTableViewPortSize);
		table.getTableHeader().setReorderingAllowed(false);
		
		/* *** table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(340);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		*/
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setPreferredWidth(340);
		table.getColumnModel().getColumn(3).setPreferredWidth(70);
		table.getColumnModel().getColumn(4).setPreferredWidth(70);
		table.getColumnModel().getColumn(5).setPreferredWidth(90);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
	}

	void fill_table(File f) {
		final TransducerListTableModel tableModel = (TransducerListTableModel) table
				.getModel();
		if (f != null) {
			String FormatErrorLine = "";
			LineNumberReader r;
			try {
				r = new LineNumberReader(new FileReader(f));
				String line;
				try {
					while ((line = r.readLine()) != null) {
						try {
							final ConfigurationFileAnalyser cfa = new ConfigurationFileAnalyser(
									line);
							// ***final Object[] o = { DataList.UNRANKED, cfa.getFileName(), 
							// ***		cfa.isMergeMode(), cfa.isReplaceMode(), cfa.isDisabled(), cfa.isStar() };
							final Object[] o = { DataList.UNRANKED, cfa.isDisabled(), cfa.getFileName(), 
										cfa.isMergeMode(), cfa.isReplaceMode(), cfa.isStar(), cfa.isGeneric() };
							tableModel.addRow(o);
							if (cfa.isCommentFound()) {
								editedFileHasCommentOrError = true;
							}
						} catch (final EmptyLineException e) {
							// do nothing
							editedFileHasCommentOrError = true;
						} catch (final InvalidLineException e) {
							// keep track of the error to warn the user
							FormatErrorLine = FormatErrorLine
									.concat("line " + r.getLineNumber() + ": "
											+ e.getMessage());
							editedFileHasCommentOrError = true;
						}
					}
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					try {
						r.close();
					} catch (final IOException e1) {
						e1.printStackTrace();
					}
				}
				if (!FormatErrorLine.equals("")) {
					final String t = "Format line Error Found";
					JOptionPane.showMessageDialog(this, FormatErrorLine, t,
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (final FileNotFoundException e) {
				final String t = "File Not Found";
				final String message = "Please select an existing file";
				JOptionPane.showMessageDialog(this, message, t,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		configurationHasChanged = false;
		setFrameTitle();
	}

	void void_table() {
		final TransducerListTableModel tableModel = (TransducerListTableModel) table
				.getModel();
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
		configurationHasChanged = false;
		setFrameTitle();
	}

	/**
	 * Constructs and return a panel of buttons for user interaction with the
	 * table. <code>ConfigurationFrame</code> is added as listener for all
	 * buttons.
	 * 
	 * @return the panel with all the buttons
	 */
	JPanel create_panel() {
		final Dimension defaultButtonDimension = new Dimension(80, 28);
		button_panel = new JPanel();
		button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
		button_panel.add(Box.createRigidArea(new Dimension(90, 20)));
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
		button_panel.add(Box.createRigidArea(new Dimension(90, 20)));
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
		add_below
				.setToolTipText("Insert the selected file in the explorer file above the selected transducer in the list");
		button_panel.add(add_below);
		button_panel.add(Box.createRigidArea(new Dimension(50, 20)));
		open = new JButton("View");
		open.addActionListener(this);
		open.setMaximumSize(defaultButtonDimension);
		open.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(open);
		button_panel.add(Box.createRigidArea(new Dimension(50, 20)));
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
		recompile_graphs = new JButton("Compile");
		recompile_graphs.setToolTipText("Recompile all graphs");
		recompile_graphs.setMaximumSize(defaultButtonDimension);
		recompile_graphs.setAlignmentX(Component.CENTER_ALIGNMENT);
		recompile_graphs.addActionListener(this);
		button_panel.add(recompile_graphs);
		button_panel.add(Box.createRigidArea(new Dimension(50, 20)));
		disable_all = new JButton("Disable all");
		disable_all.addActionListener(this);
		disable_all.setToolTipText("Disable all graphs");
		disable_all.setAlignmentX(Component.CENTER_ALIGNMENT);
		disable_all.setMaximumSize(defaultButtonDimension);
		button_panel.add(disable_all);
		enable_all = new JButton("Enable all");
		enable_all.addActionListener(this);
		enable_all.setToolTipText("Enable all graphs");
		enable_all.setAlignmentX(Component.CENTER_ALIGNMENT);
		enable_all.setMaximumSize(defaultButtonDimension);
		button_panel.add(enable_all);
		button_panel.add(Box.createRigidArea(new Dimension(50, 20)));
		close = new JButton("Close");
		close.addActionListener(this);
		close.setMaximumSize(defaultButtonDimension);
		close.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.add(close);
		KeyUtil.addEscListener(button_panel, close);
		return button_panel;
	}

	/**
	 * Identifies the source of the event and reacts to it.
	 * <p/>
	 * Event may come from the button <code>add_below</code>,
	 * <code>delete</code>, <code>up</code>, <code>down</code>, <code>top</code>
	 * or <code>bottom</code>. Except with the <code>delete</code> event,
	 * selection is kept on the same logical row (to oppose to the same row
	 * index). When a selected row is moved, selection moves with it.
	 * 
	 * @param a
	 *            Action event notified to this class.
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		
		if (up == a.getSource()) {
			final int selected_row = table.getSelectedRow();
			if (selected_row > 0) {
				final TransducerListTableModel dtm = (TransducerListTableModel) table
						.getModel();
				try {
					dtm.moveRow(selected_row, selected_row, selected_row - 1);
					configurationHasChanged = true;
					setFrameTitle();
					table.getSelectionModel().setSelectionInterval(
							selected_row - 1, selected_row - 1);
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		if (down == a.getSource()) {
			final int selected_row = table.getSelectedRow();
			if (selected_row != -1 && selected_row < table.getRowCount() - 1) {
				final TransducerListTableModel dtm = (TransducerListTableModel) table
						.getModel();
				try {
					dtm.moveRow(selected_row, selected_row, selected_row + 1);
					configurationHasChanged = true;
					setFrameTitle();
					table.getSelectionModel().setSelectionInterval(
							selected_row + 1, selected_row + 1);
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		if (top == a.getSource()) {
			final int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				final TransducerListTableModel dtm = (TransducerListTableModel) table
						.getModel();
				try {
					dtm.moveRow(selected_row, selected_row, 0);
					configurationHasChanged = true;
					setFrameTitle();
					table.getSelectionModel().setSelectionInterval(0, 0);
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		if (bottom == a.getSource()) {
			final int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				final TransducerListTableModel dtm = (TransducerListTableModel) table
						.getModel();
				try {
					dtm.moveRow(selected_row, selected_row,
							table.getRowCount() - 1);
					configurationHasChanged = true;
					setFrameTitle();
					table.getSelectionModel().setSelectionInterval(
							table.getRowCount() - 1, table.getRowCount() - 1);
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		if (delete_ == a.getSource()) {
			final int selected_row = table.getSelectedRow();
			if (selected_row != -1) {
				final TransducerListTableModel dtm = (TransducerListTableModel) table
						.getModel();
				try {
					dtm.removeRow(selected_row);
					configurationHasChanged = true;
					setFrameTitle();
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			} else {
				final String t = "No transducer selected";
				final String message = "Please select the transducer to be deleted from the list";
				JOptionPane.showMessageDialog(this, message, t,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		if (add_below == a.getSource()) {
			int selected_row = table.getSelectedRow();
			final TransducerListTableModel dtm = (TransducerListTableModel) table.getModel();
			
			final File selected_file = fileBrowse.getSelectedFile();
			
						
			
			if (selected_file != null) {
				try {
					ShareTransducerList stl = new ShareTransducerList();
					final Object[] row = { DataList.UNRANKED, false, stl.relativize(selected_file.getAbsolutePath()), true, false,false,false };
					if (selected_row == -1) {
						selected_row = dtm.getRowCount();
					}
					dtm.insertRow(selected_row, row);
					table.getSelectionModel().setSelectionInterval(
							selected_row, selected_row);
					configurationHasChanged = true;
					setFrameTitle();
				} catch (final ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				catch (NotAnAbsolutePathException e1) {
					e1.printStackTrace();
				}
			} else {
				final String t = "No graph selected";
				final String message = "Please select in the file explorer the graph to be added";
				JOptionPane.showMessageDialog(this, message, t,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		if (open == a.getSource()) {
			if (table.getSelectedRow() != -1) {
				final TransducerListTableModel model = (TransducerListTableModel) table.getModel();
				final File graph = new File(Config.getCurrentGraphDir(),(String) model.getValueAt(
							table.getSelectedRow(), 
							model.getNameIndex()));
				viewGraph(graph);
			} else {
				JOptionPane
						.showMessageDialog(
								this,
								"Please select a transducer in the list.\nIf you wish to view a graph in the file explorer, use the view button below the file explorer\n",
								"No transducer selected in the list",
								JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if (saveAs == a.getSource()) {
			showSaveFrame();
		}
		if (save == a.getSource()) {
			if (Config.getCurrentTransducerList() == null) {
				showSaveFrame();
			} else {
				if (editedFileHasCommentOrError) {
					final String message = "Empty lines, format line error or comments were found when loading the file.\n"
							+ "Saving with the original name will result in erasing original data\n\n"
							+ "If you want to keep all information in your original file, you should use the 'save as' button instead\n\n"
							+ "Do you want to save anyway ?";
					final Object[] options = { "Save", "Save As", "Cancel" };
					final String t = "Warning : Data loss may happen";
					final int return_val = JOptionPane.showOptionDialog(this,
							message, t, JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[1]);
					if (return_val == JOptionPane.YES_OPTION) {
						saveListToFile();
					} else if (return_val == JOptionPane.NO_OPTION) {
						showSaveFrame();
					}
				} else {
					saveListToFile();
				}
			}
		}
		if (recompile_graphs == a.getSource()) {
			final TransducerListTableModel dtm = (TransducerListTableModel) table.getModel();
			
			MultiCommands commands = new MultiCommands();
			String ErrorList = "";
			
			for (int i = 0; i < dtm.getRowCount(); i++) {
				final TransducerListTableModel model = (TransducerListTableModel) table.getModel();
				final File f_alphabet = ConfigManager.getManager().getAlphabet(
						null);
				final String graphFileName = (String) dtm.getValueAt(i, model.getNameIndex());
				File graphFile = new File(Config.getCurrentGraphDir(),graphFileName);
				
				

				if (!graphFile.exists()) {
					ErrorList = ErrorList.concat(graphFileName
							+ " does not exist\n");
				} else {
					if (!graphFileName.endsWith("fst2")) {
						ErrorList = ErrorList.concat(graphFileName
								+ " has not the fst2 extension\n");
					} else {

						graphFile = getGrfFromFst2(graphFile);

						commands.addCommand(new Grf2Fst2Command()
								.grf(graphFile)
								.enableLoopAndRecursionDetection(true)
								.alphabetTokenization(f_alphabet));

					}
					
				}
			}
			if(!ErrorList.equals("")){
				final String t = "Recompile Graph Error";
				final String message = ErrorList;
				JOptionPane.showMessageDialog(this, message, t,
						JOptionPane.ERROR_MESSAGE);
			}
			
			Launcher.exec(commands, false);

		}
		
		if(disable_all == a.getSource()){
			final TransducerListTableModel dtm = (TransducerListTableModel) table.getModel();
			
			for (int i = 0; i < dtm.getRowCount(); i++) {
				dtm.setValueAt(true, i, dtm.getDisabledIndex());
			}
		}
		
		if(enable_all == a.getSource()){
			final TransducerListTableModel dtm = (TransducerListTableModel) table.getModel();
			
			for (int i = 0; i < dtm.getRowCount(); i++) {
				dtm.setValueAt(false, i, dtm.getDisabledIndex());
			}
		}
		
		if (close == a.getSource()) {
			quit_asked();
		}
	}

	void quit_asked() {
		if (configurationHasChanged) {
			final String message = "Changes to the transducer list may not have been saved.\n Do you want to save changes before leaving ?";
			final int return_val = JOptionPane.showConfirmDialog(this, message);
			if (return_val == JOptionPane.YES_OPTION) {
				showSaveFrame();
			} else if (return_val == JOptionPane.NO_OPTION) {
				quit();
			} else { //return_val == JOptionPane.CANCEL_OPTION
				// DO NOTHING
			}
		} else {
			quit();
		}
	}

	void quit() {
		void_table();
		dispose();
	}

	void showSaveFrame() {
		JFileChooser saveFileChooser;
		if (Config.getCurrentTransducerList() != null) {
			saveFileChooser = new JFileChooser(Config
					.getCurrentTransducerList().getParentFile());
			saveFileChooser.setSelectedFile(Config.getCurrentTransducerList());
		} else {
			saveFileChooser = new JFileChooser(Config.getCassysDir());
		}
		final int returnVal = saveFileChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Config.setCurrentTransducerList(saveFileChooser.getSelectedFile());
			saveListToFile();
		}
	}
	
	

	void saveListToFile() {
		try {
			final BufferedWriter fw = new BufferedWriter(new FileWriter(
					Config.getCurrentTransducerList()));
			for (int i = 0; i < table.getModel().getRowCount(); i++) {
				final TransducerListTableModel model = (TransducerListTableModel) table.getModel();
				final String fileName = (String) table.getValueAt(i, model.getNameIndex());
				fw.write("\"" + fileName + "\" ");
				if ((Boolean) table.getValueAt(i, model.getMergeIndex())) {
					fw.write("Merge");
				} else {
					fw.write("Replace");
				}
				if((Boolean) table.getValueAt(i, model.getDisabledIndex())) {
					fw.write(" Disabled");
				} else {
					fw.write(" Enabled");
				}
				if((Boolean) table.getValueAt(i, model.getStarIndex())) {
					fw.write(" *");
				} else {
					fw.write(" 1");
				}
                                if((Boolean) table.getValueAt(i, model.getGenericIndex())) {
                                    fw.write(" @");
                                }
                                else {
                                    fw.write(" 0");
                                }
				fw.newLine();
			}
			fw.close();
			editedFileHasCommentOrError = false;
			configurationHasChanged = false;
			setFrameTitle();
			// parent.rescanCurrentDirectory();
			Config.getTransducerListDialogBox().rescanCurrentDirectory();
		} catch (final IOException e) {
			System.out.println("IO Error");
		}
	}

	public void setFrameTitle() {
		String frameTitle = "CasSys Transducer Configuration : ";
		if (Config.getCurrentTransducerList() != null) {
			frameTitle = frameTitle.concat(Config.getCurrentTransducerList()
					.getName());
		}
		if (configurationHasChanged) {
			frameTitle = frameTitle.concat(" (unsaved)");
		}
		setTitle(frameTitle);
	}

	void viewGraph(File f) {
		File grf = null;
		System.out.println(f);
		if (f.getName().endsWith(".grf")) {
			grf = f;
		}
		if (f.getName().endsWith(".fst2")) {
			grf = getGrfFromFst2(f);
		}
		if (grf != null) {
			UnitexProjectManager.search(grf)
					.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(grf);
		} else {
			final String t = "Wrong file selected";
			final String message = "Please select a file with the fst2 extension";
			JOptionPane.showMessageDialog(this, message, t,
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public File getGrfFromFst2(File f){
		final String nameWithoutExtension = f.getPath().substring(0,
				f.getPath().lastIndexOf("."));
		final String nameWithExtension = nameWithoutExtension
				.concat(".grf");
		final File grf = new File(nameWithExtension);
		return grf;
	}
	
	

	public void setConfigurationHasChanged(Boolean b) {
		configurationHasChanged = b;
	}

	public Boolean isConfigurationHasChanged() {
		return configurationHasChanged;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		//Modify the frame title and mark configuration as changed
		setConfigurationHasChanged(true);
		setFrameTitle();
	}
}
