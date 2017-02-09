package fr.umlv.unitex.frames;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.tfst.TfstTagFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

public class TextAutomatonTagFilterDialog extends JDialog {

	private JCheckBox surfaceCheckBox;
	private JCheckBox lemmaCheckBox;
	private JCheckBox allTagsInfClassCheckBox;
	private JCheckBox directListingCheckBox;
	private JTextField directListingTextField;
	private JCheckBox tagLetterNumberCheckBox;
	private JCheckBox tagPrefixCheckBox;
	private JTextField tagLetterNumberTextField;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField tagPrefixTextField;

	public TextAutomatonTagFilterDialog() {
		super(UnitexFrame.mainFrame, "Tag Filter", true);
		setResizable(false);
		setContentPane(constructPanel());
		setModalityType(ModalityType.MODELESS);
		pack();
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		createListeners();
	}

	private void createListeners() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		directListingTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				directListingCheckBox.setSelected(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Do nothing
			}
		});
		tagLetterNumberTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				tagLetterNumberCheckBox.setSelected(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Do nothing
			}
		});
		tagPrefixTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				tagPrefixCheckBox.setSelected(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Do nothing
			}
		});
	}

	private void onCancel() {
		dispose();
	}

	private void onOk() {
		final JFileChooser chooserInput = new JFileChooser();
		chooserInput.setDialogTitle("Input File Name");
		chooserInput.addChoosableFileFilter(new PersonalFileFilter("txt", "Unicode Raw Texts"));
		chooserInput.setDialogType(JFileChooser.OPEN_DIALOG);
		chooserInput.setCurrentDirectory(Config.getCurrentCorpusDir());
		final int returnValInput = chooserInput.showOpenDialog(null);
		if (returnValInput != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}

		final JFileChooser chooserOutput = new JFileChooser();
		chooserOutput.setDialogTitle("Output File Name");
		chooserOutput.addChoosableFileFilter(new PersonalFileFilter("txt", "Unicode Raw Texts"));
		chooserOutput.setDialogType(JFileChooser.SAVE_DIALOG);
		chooserOutput.setCurrentDirectory(Config.getCurrentCorpusDir());
		final int returnValOutput = chooserOutput.showOpenDialog(null);
		if (returnValOutput != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}

		checkAllFields();

		// This will reference one line at a time
		String line = null;

		TfstTagFilter filter = new TfstTagFilter(surfaceCheckBox.isSelected(), lemmaCheckBox.isSelected(),
				allTagsInfClassCheckBox.isSelected(), directListingTextField.getText(),
				tagLetterNumberTextField.getText(), tagPrefixTextField.getText());

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(chooserInput.getSelectedFile());

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			FileWriter fileWriter = new FileWriter(chooserOutput.getSelectedFile());

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			while ((line = bufferedReader.readLine()) != null) {
				String filteredLine = filter.filterLine(line);
				bufferedWriter.write(filteredLine);
			}

			// Always close files.
			bufferedReader.close();
			bufferedWriter.close();

		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file");
		} catch (IOException ex) {
			System.out.println("Error reading file");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		dispose();
	}

	private void checkAllFields() {
		if (!tagLetterNumberCheckBox.isSelected()) {
			tagLetterNumberTextField.setText("");
		}
		if (!directListingCheckBox.isSelected()) {
			directListingTextField.setText("");
		}
		if (!tagPrefixCheckBox.isSelected()) {
			tagPrefixTextField.setText("");
		}

	}

	public static TextAutomatonTagFilterDialog createFindAndReplaceDialog() {
		return new TextAutomatonTagFilterDialog();
	}

	private JPanel constructPanel() {
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout());
		surfaceCheckBox = new JCheckBox();
		surfaceCheckBox.setSelected(true);
		surfaceCheckBox.setText("Surface");
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(surfaceCheckBox, gbc);
		final JPanel spacer1 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer1, gbc);
		final JPanel spacer2 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer2, gbc);
		lemmaCheckBox = new JCheckBox();
		lemmaCheckBox.setSelected(true);
		lemmaCheckBox.setText("Lemma");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(lemmaCheckBox, gbc);
		allTagsInfClassCheckBox = new JCheckBox();
		allTagsInfClassCheckBox.setText("All Tags -InfClass");
		gbc = new GridBagConstraints();
		gbc.gridx = 7;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(allTagsInfClassCheckBox, gbc);
		final JPanel spacer3 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer3, gbc);
		directListingCheckBox = new JCheckBox();
		directListingCheckBox.setText("Direct Listing");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(directListingCheckBox, gbc);
		tagLetterNumberCheckBox = new JCheckBox();
		tagLetterNumberCheckBox.setText("Tag-Letter-Number");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 7;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(tagLetterNumberCheckBox, gbc);
		directListingTextField = new JTextField("");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(directListingTextField, gbc);
		tagPrefixCheckBox = new JCheckBox();
		tagPrefixCheckBox.setText("Tag-Prefix");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 9;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(tagPrefixCheckBox, gbc);
		final JPanel spacer4 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 6;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer4, gbc);
		final JPanel spacer5 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 8;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer5, gbc);
		tagLetterNumberTextField = new JTextField("");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 7;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(tagLetterNumberTextField, gbc);
		tagPrefixTextField = new JTextField("");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 9;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(tagPrefixTextField, gbc);
		okButton = new JButton();
		okButton.setText("Ok");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 12;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(okButton, gbc);
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		gbc = new GridBagConstraints();
		gbc.gridx = 7;
		gbc.gridy = 12;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(cancelButton, gbc);
		final JPanel spacer6 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer6, gbc);
		final JPanel spacer7 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 13;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer7, gbc);
		final JPanel spacer8 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 8;
		gbc.gridy = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer8, gbc);
		final JPanel spacer9 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer9, gbc);
		final JPanel spacer10 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer10, gbc);
		final JPanel spacer11 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 11;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer11, gbc);
		final JSeparator separator1 = new JSeparator();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		panel1.add(separator1, gbc);
		final JPanel spacer12 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer12, gbc);
		return panel1;
	}
}
