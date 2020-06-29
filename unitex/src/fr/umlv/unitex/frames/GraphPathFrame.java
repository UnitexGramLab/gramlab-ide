/**
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.frames;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Fst2ListCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class GraphPathFrame extends JInternalFrame {
    MultiCommands preprocessCommands;
    Boolean flattenMode = false;
    String flattenDepth = "10";
		
    final ItemListener flattenCheckBoxListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if ( flattenCheckbox.isSelected() ) {
                            flattenOptionButton.setEnabled(true);
                    } else {
                            flattenOptionButton.setEnabled(false);
                    }
            }
    };
    
    final ItemListener maxSeqListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if ( maxSeqCheckbox.isSelected() ) {
                            maxSeqSpinner.setEnabled(true);
                    } else {
                            maxSeqSpinner.setEnabled(false);
                    }
            }
    };
    
    ListDataListener listListener = new ListDataListener() {
		@Override
		public void intervalRemoved(ListDataEvent e) {
			/* */
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			final int n = outputArea.getModel().getSize();
			setTitle(n + " line" + (n > 1 ? "s" : ""));
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			/* */
		}
	};

    /**
     * Creates new form GPF
     */
    public GraphPathFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        graphFileLabel = new javax.swing.JLabel();
        outputFileLabel = new javax.swing.JLabel();
        inputGraphName = new javax.swing.JTextField();
        outputFileName = new javax.swing.JTextField();
        setFileButton = new javax.swing.JButton();
        optionSeparator = new javax.swing.JSeparator();
        optionLabel = new javax.swing.JLabel();
        outputsLabel = new javax.swing.JLabel();
        ignoreOutputsButton = new javax.swing.JRadioButton();
        splitOutputsButton = new javax.swing.JRadioButton();
        mergeOutputsButton = new javax.swing.JRadioButton();
        exploreLabel = new javax.swing.JLabel();
        exploreRecButton = new javax.swing.JRadioButton();
        exploreIndepButton = new javax.swing.JRadioButton();
        maxSeqCheckbox = new javax.swing.JCheckBox();
        maxSeqSpinner = new javax.swing.JSpinner();
        flattenCheckbox = new javax.swing.JCheckBox();
        flattenOptionButton = new javax.swing.JButton();
        checkLoopsCheckbox = new javax.swing.JCheckBox();
        resultLabel = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        resultSeparator = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        outputArea = new fr.umlv.unitex.text.BigTextList();
        makeDicCheckBox = new javax.swing.JCheckBox();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Explore graph paths");
        setToolTipText("");

        graphFileLabel.setText("Graph:");

        outputFileLabel.setText("Output file:");

        inputGraphName.setEditable(false);
        inputGraphName.setText("jTextField1");
        inputGraphName.setPreferredSize(new java.awt.Dimension(70, 25));
        inputGraphName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputGraphNameActionPerformed(evt);
            }
        });

        outputFileName.setText("jTextField1");
        outputFileName.setName(""); // NOI18N
        outputFileName.setPreferredSize(new java.awt.Dimension(70, 25));

        setFileButton.setText("Set File");
        setFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setFileButtonActionPerformed(evt);
            }
        });

        optionLabel.setText("Options");

        outputsLabel.setText("Outputs:");

        buttonGroup1.add(ignoreOutputsButton);
        ignoreOutputsButton.setSelected(true);
        ignoreOutputsButton.setText("Ignore");

        buttonGroup1.add(splitOutputsButton);
        splitOutputsButton.setText("Split inputs and outputs");

        buttonGroup1.add(mergeOutputsButton);
        mergeOutputsButton.setText("Merge inputs and outputs");

        exploreLabel.setText("Explore subraphs:");

        buttonGroup2.add(exploreRecButton);
        exploreRecButton.setSelected(true);
        exploreRecButton.setText("Recursively");
        exploreRecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreRecButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(exploreIndepButton);
        exploreIndepButton.setText("Independently, printing names of called subgraphs");
        exploreIndepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreIndepButtonActionPerformed(evt);
            }
        });

        maxSeqCheckbox.setText("Max sequences:");
        maxSeqCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxSeqCheckboxActionPerformed(evt);
            }
        });

        maxSeqSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        maxSeqSpinner.setEnabled(false);
        maxSeqSpinner.setValue(50);

        flattenCheckbox.setText("Flatten graphs");
        flattenCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenCheckboxActionPerformed(evt);
            }
        });

        flattenOptionButton.setText("Options");
        flattenOptionButton.setEnabled(false);
        flattenOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenOptionButtonActionPerformed(evt);
            }
        });

        checkLoopsCheckbox.setText("Check for loops");
        checkLoopsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkLoopsCheckboxActionPerformed(evt);
            }
        });

        resultLabel.setText("Results");

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        makeDicCheckBox.setText("Process as dictionary-graph");
        makeDicCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeDicCheckboxActionPerformed(evt);
            }
        });

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(outputArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(optionSeparator)
                    .addComponent(optionLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(graphFileLabel)
                            .addComponent(outputFileLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(outputFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(setFileButton))
                            .addComponent(inputGraphName, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outputsLabel)
                            .addComponent(exploreLabel)
                            .addComponent(maxSeqCheckbox)
                            .addComponent(makeDicCheckBox))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ignoreOutputsButton)
                            .addComponent(exploreRecButton)
                            .addComponent(maxSeqSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exploreIndepButton)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(splitOutputsButton)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(flattenCheckbox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(flattenOptionButton)))
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkLoopsCheckbox)
                                    .addComponent(mergeOutputsButton)))))
                    .addComponent(resultLabel)
                    .addComponent(resultSeparator)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(helpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(runButton)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(graphFileLabel)
                    .addComponent(inputGraphName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputFileLabel)
                    .addComponent(outputFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setFileButton))
                .addGap(16, 16, 16)
                .addComponent(optionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputsLabel)
                    .addComponent(ignoreOutputsButton)
                    .addComponent(splitOutputsButton)
                    .addComponent(mergeOutputsButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exploreLabel)
                    .addComponent(exploreRecButton)
                    .addComponent(exploreIndepButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxSeqCheckbox)
                    .addComponent(maxSeqSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flattenCheckbox)
                    .addComponent(flattenOptionButton)
                    .addComponent(checkLoopsCheckbox))
                .addGap(18, 18, 18)
		.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(makeDicCheckBox))
                .addComponent(resultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(helpButton)
                    .addComponent(cancelButton)
                    .addComponent(runButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void maxSeqCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxSeqCheckboxActionPerformed
        if ( maxSeqCheckbox.isSelected() ) {
                            maxSeqSpinner.setEnabled(true);
        } else {
                maxSeqSpinner.setEnabled(false);
        }
    }//GEN-LAST:event_maxSeqCheckboxActionPerformed

    private void flattenCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenCheckboxActionPerformed
        if ( flattenCheckbox.isSelected() ) {
                            flattenOptionButton.setEnabled(true);
        } else {
                flattenOptionButton.setEnabled(false);
        }
    }//GEN-LAST:event_flattenCheckboxActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void exploreRecButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreRecButtonActionPerformed
    	if(!makeDicCheckBox.isSelected()) {
            outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                          .getText()) + "-recursive-paths.txt");
        }
        else {
            outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                          .getText()) + "-recursive-paths.dic");
        }
    }//GEN-LAST:event_exploreRecButtonActionPerformed

    private void inputGraphNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputGraphNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputGraphNameActionPerformed

    private void exploreIndepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreIndepButtonActionPerformed
        if(!makeDicCheckBox.isSelected()) {
            outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                    .getText()) + "-paths.txt");
        }
        else {
            outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                    .getText()) + "-paths.dic");
        }
    }//GEN-LAST:event_exploreIndepButtonActionPerformed

    private void makeDicCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputGraphNameActionPerformed
        if(makeDicCheckBox.isSelected()) {
                splitOutputsButton.setEnabled(false);
                mergeOutputsButton.setEnabled(false);
                ignoreOutputsButton.setEnabled(false);
            if(exploreRecButton.isSelected()) {
                outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                        .getText()) + "-recursive-paths.dic");
            }
            else {
                outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                        .getText()) + "-paths.dic");
            }
        }
        else {
                splitOutputsButton.setEnabled(true);
                mergeOutputsButton.setEnabled(true);
                ignoreOutputsButton.setEnabled(true);
            if(exploreRecButton.isSelected()) {
                outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                        .getText()) + "-recursive-paths.txt");
            }
            else {
                outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                        .getText()) + "-paths.txt");
            }
        }
    }//GEN-LAST:event_inputGraphNameActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        Fst2ListCommand cmd = new Fst2ListCommand();
        final Grf2Fst2Command grfCmd = new Grf2Fst2Command();
        File fst2;
        File list; /* output file name */
        int n;
        cmd = cmd.morphologicalDic(ConfigManager.getManager().morphologicalDictionaries(null));
        if (maxSeqCheckbox.isSelected()) {
                try {
                    maxSeqSpinner.commitEdit();
                    n = (Integer) maxSeqSpinner.getValue();
                } catch (final NumberFormatException | ParseException e) {
                        JOptionPane.showMessageDialog(null,
                                        "You must specify a valid limit", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                cmd = cmd.limit(n);
        } else {
                cmd = cmd.noLimit();
        }
	if(makeDicCheckBox.isSelected()) {
                cmd = cmd.makeDic();
        }
        else {
	        if (ignoreOutputsButton.isSelected()) {
	            cmd = cmd.ignoreOutputs();
	        } else {
	            cmd = cmd.separateOutputs(splitOutputsButton.isSelected());
	        }
        }
        if (ConfigManager.getManager().isKorean(null)) {
			cmd = cmd.korean();
	}
        if ( !checkLoopsCheckbox.isSelected() ) {
            cmd = cmd.noLoopCheck();
	}
        // check if flatten was checked or not
        if( !flattenCheckbox.isSelected() ) {
                grfCmd.grf(new File(inputGraphName.getText()))
                        .enableLoopAndRecursionDetection(true).repositories()
                        .emitEmptyGraphWarning().displayGraphNames();
        } else if ( preprocessCommands == null ) {
                // if no specific option were given, preprocess with default
                File graphFile = new File(inputGraphName.getText());
                String name_fst2 = FileUtil.getFileNameWithoutExtension(graphFile);
                name_fst2 = name_fst2 + ".fst2";
                final MultiCommands commands = new MultiCommands();
                commands.addCommand(new Grf2Fst2Command().grf(graphFile)
                                .enableLoopAndRecursionDetection(true)
                                .tokenizationMode(null, graphFile).repositories()
                                .emitEmptyGraphWarning().displayGraphNames());
                commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
                                .resultType(flattenMode).depth(Integer.parseInt(flattenDepth)));
        }
        else {
                Launcher.exec(preprocessCommands, false);
        }

        fst2 = new File(FileUtil.getFileNameWithoutExtension(inputGraphName
                        .getText()) + ".fst2");
        if (exploreRecButton.isSelected()) {
                // set file to user input
                list = new File(outputFileName.getText());
                cmd = cmd.listOfPaths(fst2, list);
        } else {
                // we can't set non recursive file name to user selection yet because the name is hard coded in UnitexToolLogger (Fst2List.cpp line 1230)
                // if we change it here ShowPathsDo will throw a FileNotFoundException 
                // we will rename the file once the UnitexToolLogger process has completed
                // alternatively that process could be changed to remove the hard coding
                list = new File(
                                FileUtil.getFileNameWithoutExtension(inputGraphName
                                                .getText()) + "autolst.txt");
                cmd = cmd.listsOfSubgraph(fst2);
        }
        final MultiCommands commands = new MultiCommands();
        if ( !flattenCheckbox.isSelected() ) {
                commands.addCommand(grfCmd);
        }
        commands.addCommand(cmd);
        outputArea.reset();
        Launcher.exec(commands, true, new ShowPathsDo(list), false,true);
    }//GEN-LAST:event_runButtonActionPerformed

    private void flattenOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenOptionButtonActionPerformed
        File graphFile = new File(inputGraphName.getText());
        Map<String,Object> flattenOptions = UnitexFrame
                        .flattenGraph(graphFile,flattenMode,flattenDepth);
        if( flattenOptions != null ) {
        // unpack the commands and its options
                preprocessCommands = (MultiCommands) flattenOptions.get("commands");
                flattenMode = (boolean) flattenOptions.get("flattenMode");
                flattenDepth = (String) flattenOptions.get("flattenDepth");
        }
    }//GEN-LAST:event_flattenOptionButtonActionPerformed

    private void checkLoopsCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkLoopsCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkLoopsCheckboxActionPerformed

    private void setFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setFileButtonActionPerformed
        openOutputFile();
    }//GEN-LAST:event_setFileButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphPathFrame().setVisible(true);
            }
        });
        
    }
    
    void close() {
        setVisible(false);
        outputArea.reset();
        outputArea.clearSelection();
        outputArea.getModel().removeListDataListener(listListener);
    }
    
    class ShowPathsDo implements ToDo {
		private final File name;

		ShowPathsDo(File name) {
			this.name = name;
		}

		@Override
		public void toDo(boolean success) {
			outputArea.load(name);
			outputArea.getModel().addListDataListener(listListener);
			
			try {
				// issue #61 - recursive path option invokes UnitexToolLogger which hard codes the name of the output file to GraphNameautolst.txt 
				// once that process has completed and loaded the file rename it using the user input if that differs from the default
				if (!name.getAbsolutePath().equals(outputFileName.getText())) {
					File dest = new File(outputFileName.getText());
					Files.move(name.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}  catch (final IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Could not save path list to " + outputFileName.getText(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} 
		}
	}
    
    void setInputGraphName(String string) {
        inputGraphName.setText(string);
    }
    
    public void setOutputFileDefaultName(String graphFileName) {
    	 if(exploreRecButton.isSelected()) {
             outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                     .getText()) + "-recursive-paths.dic");
         }
         else {
             outputFileName.setText(FileUtil.getFileNameWithoutExtension(inputGraphName
                     .getText()) + "-paths.dic");
         }
    }
    
    private void openOutputFile() {
        final int returnVal = Config.getExploreGraphOutputDialogBox().showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
                // we return if the user has clicked on CANCEL
                return;
        }
        final String name;
        try {
                name = Config.getExploreGraphOutputDialogBox().getSelectedFile()
                                .getCanonicalPath();
        } catch (final IOException e) {
                return;
        }
        outputFileName.setText(name);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox checkLoopsCheckbox;
    private javax.swing.JRadioButton exploreIndepButton;
    private javax.swing.JLabel exploreLabel;
    private javax.swing.JRadioButton exploreRecButton;
    private javax.swing.JCheckBox flattenCheckbox;
    private javax.swing.JButton flattenOptionButton;
    private javax.swing.JLabel graphFileLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JRadioButton ignoreOutputsButton;
    private javax.swing.JTextField inputGraphName;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox maxSeqCheckbox;
    private javax.swing.JSpinner maxSeqSpinner;
    private javax.swing.JRadioButton mergeOutputsButton;
    private javax.swing.JLabel optionLabel;
    private javax.swing.JSeparator optionSeparator;
    private fr.umlv.unitex.text.BigTextList outputArea;
    private javax.swing.JLabel outputFileLabel;
    private javax.swing.JTextField outputFileName;
    private javax.swing.JLabel outputsLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JSeparator resultSeparator;
    private javax.swing.JButton runButton;
    private javax.swing.JButton setFileButton;
    private javax.swing.JRadioButton splitOutputsButton;
    private javax.swing.JCheckBox makeDicCheckBox;
    // End of variables declaration//GEN-END:variables
}
