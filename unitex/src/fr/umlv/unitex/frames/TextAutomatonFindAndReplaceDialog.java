/*
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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphtools.FindAndReplace;
import fr.umlv.unitex.graphtools.FindAndReplaceData;
import fr.umlv.unitex.utils.KeyUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TextAutomatonFindAndReplaceDialog extends JDialog {
	private JTextField findTextField;
	private JTextField replaceTextField;
	private JCheckBox useRegularExpressionsCheckBox;
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox matchOnlyAWholeCheckBox;
	private JCheckBox ignoreCommentBoxesCheckBox;
	private JButton closeButton;
	private JButton findNextButton;
	private JButton findPreviousButton;
	private JButton replaceAllButton;
	private JButton replaceButton;
	private JTextField statusBarTextField;

	private TextAutomatonFrame frame;
	private FindAndReplaceData data;

	private TextAutomatonFindAndReplaceDialog(TextAutomatonFrame frame, FindAndReplaceData data) {
		super(UnitexFrame.mainFrame, "Text Automaton | Find and replace", true);
		this.frame = frame;
		this.data = data;
		setResizable(false);
		setContentPane(constructPanel());
		setModalityType(ModalityType.MODELESS);
		pack();
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		createListeners();
	}

	public static TextAutomatonFindAndReplaceDialog createFindAndReplaceDialog() {
		TextAutomatonFrame frame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.getTextAutomatonFrame();
		FindAndReplaceData data = new FindAndReplaceData(frame.getGraphicalZone().getBoxes(), frame.getGraphicalZone());
		return new TextAutomatonFindAndReplaceDialog(frame, data);
	}

	private void createListeners() {
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		replaceAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onReplaceAll();
				getParent().repaint();
			}
		});
		replaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onReplace();
				getParent().repaint();
			}
		});
		findNextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onNext();
				getParent().repaint();
			}
		});
		findPreviousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onPrev();
				getParent().repaint();
			}
		});
		findTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTextField();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTextField();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Do nothing
			}
		});
		replaceTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTextField();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTextField();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Do nothing
			}
		});
		useRegularExpressionsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
		caseSensitiveCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
		matchOnlyAWholeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
		ignoreCommentBoxesCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
	}

	private JPanel constructPanel() {
		JPanel panel1 = new JPanel();
		KeyUtil.addCloseDialogListener(panel1);
		panel1.setLayout(new GridBagLayout());
		panel1.setPreferredSize(new Dimension(480, 250));
		final JLabel label1 = new JLabel();
		label1.setText("Find what:");
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(label1, gbc);
		final JLabel label2 = new JLabel();
		label2.setText("Replace with:");
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(label2, gbc);
		final JLabel label3 = new JLabel();
		label3.setText(" ");
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(label3, gbc);
		caseSensitiveCheckBox = new JCheckBox();
		caseSensitiveCheckBox.setText("Case sensitive");
		caseSensitiveCheckBox.setSelected(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 7;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(caseSensitiveCheckBox, gbc);
		matchOnlyAWholeCheckBox = new JCheckBox();
		matchOnlyAWholeCheckBox.setText("Match only current line");
		matchOnlyAWholeCheckBox.setSelected(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.WEST;
		matchOnlyAWholeCheckBox.setVisible(true);
		panel1.add(matchOnlyAWholeCheckBox, gbc);
		closeButton = new JButton();
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.setMaximumSize(new Dimension(30, 32));
		closeButton.setPreferredSize(new Dimension(90, 26));
		closeButton.setText("Close");
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 12;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(closeButton, gbc);
		final JPanel spacer1 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer1, gbc);
		findNextButton = new JButton();
		findNextButton.setMnemonic(KeyEvent.VK_N);
		findNextButton.setText("Find Next");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(findNextButton, gbc);
		findPreviousButton = new JButton();
		findPreviousButton.setMnemonic(KeyEvent.VK_P);
		findPreviousButton.setText("Find Previous");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(findPreviousButton, gbc);
		replaceAllButton = new JButton();
		replaceAllButton.setMnemonic(KeyEvent.VK_A);
		replaceAllButton.setText("Replace All");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 12;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(replaceAllButton, gbc);
		replaceButton = new JButton();
		replaceButton.setMnemonic(KeyEvent.VK_R);
		replaceButton.setText("Replace");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 12;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(replaceButton, gbc);
		final JPanel spacer2 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer2, gbc);
		final JPanel spacer3 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer3, gbc);
		final JPanel spacer4 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer4, gbc);
		final JPanel spacer5 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 11;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer5, gbc);
		final JPanel spacer6 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 12;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer6, gbc);
		final JPanel spacer7 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer7, gbc);
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridBagLayout());
		panel2.setPreferredSize(new Dimension(480, 24));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 14;
		gbc.gridwidth = 7;
		gbc.fill = GridBagConstraints.BOTH;
		panel1.add(panel2, gbc);
		statusBarTextField = new JTextField("");
		statusBarTextField.setEditable(false);
		statusBarTextField.setPreferredSize(new Dimension(480, 24));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel2.add(statusBarTextField, gbc);
		final JPanel spacer8 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer8, gbc);
		final JPanel spacer9 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 13;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer9, gbc);
		final JPanel spacer10 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer10, gbc);
		final JPanel spacer11 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(spacer11, gbc);
		ignoreCommentBoxesCheckBox = new JCheckBox();
		ignoreCommentBoxesCheckBox.setText("Ignore comment boxes");
		ignoreCommentBoxesCheckBox.setSelected(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.WEST;
		ignoreCommentBoxesCheckBox.setVisible(false);
		panel1.add(ignoreCommentBoxesCheckBox, gbc);
		useRegularExpressionsCheckBox = new JCheckBox();
		useRegularExpressionsCheckBox.setText("Use regular expressions");
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 7;
		gbc.anchor = GridBagConstraints.WEST;
		panel1.add(useRegularExpressionsCheckBox, gbc);
		final JPanel spacer12 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 9;
		gbc.fill = GridBagConstraints.VERTICAL;
		panel1.add(spacer12, gbc);
		findTextField = new JTextField("");
		findTextField.setEditable(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(findTextField, gbc);
		replaceTextField = new JTextField("");
		replaceTextField.setEditable(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(replaceTextField, gbc);
		return panel1;
	}

	private void onClose() {
		data.getGraphicalZone().setHighlight(false);
		dispose();
	}

	private void onNext() {
		updateTextField();
		int i = 0;
		data.getGraphicalZone().unSelectAllBoxes();
		int res = 0;
		res = FindAndReplace.findAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
				useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
				ignoreCommentBoxesCheckBox.isSelected());
		if (res == 0) {
			data.getGraphicalZone().setHighlight(false);
			selectNextSentence();
			return;
		}
		if (isValidFindTextField()) {
			GenericGraphBox nextBox = data.nextBox();
			if (nextBox == null) {
				selectNextSentence();
				onNext();
				return;
			}
			while (!FindAndReplace.find(data.getGraphicalZone(), nextBox, findTextField.getText(),
					useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
					ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
				i++;
				nextBox = data.nextBox();
				if (nextBox == null) {
					selectNextSentence();
					onNext();
					return;
				}
			}
		}
	}

	private void selectNextSentence() {
		frame.loadNextSentence();
	}

	private void onPrev() {
		updateTextField();
		int i = 0;
		data.getGraphicalZone().unSelectAllBoxes();
		int res = 0;
		res = FindAndReplace.findAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
				useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
				ignoreCommentBoxesCheckBox.isSelected());
		if (res == 0) {
			data.getGraphicalZone().setHighlight(false);
			selectPrevSentence();
			return;
		}
		if (isValidFindTextField()) {
			GenericGraphBox prevBox = data.prevBox();
			if (prevBox == null) {
				selectPrevSentence();
				onPrev();
				return;
			}
			while (!FindAndReplace.find(data.getGraphicalZone(), prevBox, findTextField.getText(),
					useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
					ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
				i++;
				prevBox = data.prevBox();
				if (prevBox == null) {
					selectPrevSentence();
					onPrev();
					return;
				}
			}
		}
	}

	private void selectPrevSentence() {
		frame.loadPrevSentence();
	}

	private void onReplace() {
		if (!isValidTextField()) {
			return;
		}
		data.getGraphicalZone().unSelectAllBoxes();
		if (findTextField.getText().equals(replaceTextField.getText())) {
			return;
		}
		boolean wasReplaced = FindAndReplace.replace(data.getCurrentBox(), findTextField.getText(),
				replaceTextField.getText(), data.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
				caseSensitiveCheckBox.isSelected(), false, ignoreCommentBoxesCheckBox.isSelected());
		if (wasReplaced) {
			updateReplaceResultTextField(1);
		} else {
			data.getGraphicalZone().setHighlight(false);
			updateReplaceResultTextField(-1);
		}
	}

	private void onReplaceAll() {
		if (!isValidTextField()) {
			return;
		}
		int i = 0;
		if (matchOnlyAWholeCheckBox.isSelected()) {
			i = FindAndReplace.replaceAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
					replaceTextField.getText(), frame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
					caseSensitiveCheckBox.isSelected(), false, ignoreCommentBoxesCheckBox.isSelected());
		} else {
			for (int j = 1; j <= frame.getSentenceCount(); j++) {
				i += FindAndReplace.replaceAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
						replaceTextField.getText(), frame.getGraphicalZone(),
						useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
						ignoreCommentBoxesCheckBox.isSelected());
				frame.loadNextSentence();
				updateData();
			}
		}
		updateReplaceResultTextField(i);
	}

	private boolean isValidTextField() {
		return isValidFindTextField() && isValidReplaceTextField();
	}

	private boolean isValidReplaceTextField() {
		return replaceTextField.getText() != null;
	}

	private boolean isValidFindTextField() {
		return findTextField.getText() != null && !findTextField.getText().equals("");
	}

	private void updateFoundResultTextField() {
		if (!isValidFindTextField()) {
			statusBarTextField.setText("");
			return;
		}
		int res = 0;
		res = FindAndReplace.findAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
				useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), false,
				ignoreCommentBoxesCheckBox.isSelected());
		String msg;
		switch (res) {
		case 0:
			msg = "No match found with: " + findTextField.getText();
			break;
		case 1:
			msg = "Found 1 box which matches with: " + findTextField.getText();
			break;
		default:
			msg = "Found " + res + " boxes which match with: " + findTextField.getText();
		}
		statusBarTextField.setText(msg);
		statusBarTextField.setForeground(Color.BLACK);
	}

	private void updateReplaceResultTextField(int i) {
		String msg;
		switch (i) {
		case -1:
			msg = "No box is selected";
			break;
		case 0:
			msg = "No box was replaced with: " + replaceTextField.getText();
			break;
		case 1:
			msg = "Replaced 1 box with: " + replaceTextField.getText();
			break;
		default:
			msg = "Replaced " + i + " boxes with: " + replaceTextField.getText();
			break;
		}
		statusBarTextField.setText(msg);
		statusBarTextField.setForeground(Color.BLACK);
	}

	private void updateTextField() {
		if (replaceTextField.getText().equals("")) {
			updateFoundResultTextField();
			return;
		}
		updateReplaceErrorTextField();
	}

	private void updateReplaceErrorTextField() {
		String msg = "";
		msg = FindAndReplace.checkReplaceAll(frame.getGraphicalZone().getBoxes(), findTextField.getText(),
				replaceTextField.getText(), frame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
				caseSensitiveCheckBox.isSelected(), false, ignoreCommentBoxesCheckBox.isSelected());
		if (!msg.isEmpty()) {
			statusBarTextField.setText("Error one or more boxes won't be replaced: " + msg);
			statusBarTextField.setForeground(Color.red);
		} else {
			updateFoundResultTextField();
		}
	}

	public void updateData() {
		frame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getTextAutomatonFrame();
		data = new FindAndReplaceData(frame.getGraphicalZone().getBoxes(), frame.getGraphicalZone());
		updateFoundResultTextField();
	}

	public void clearHighlight() {
		data.getGraphicalZone().setHighlight(false);
	}
}
