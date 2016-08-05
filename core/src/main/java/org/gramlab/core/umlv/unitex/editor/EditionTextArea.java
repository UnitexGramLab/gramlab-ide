/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.gramlab.core.umlv.unitex.editor.ui.KeyErrorException;
import org.gramlab.core.umlv.unitex.editor.ui.TextAreaSeparatorException;

/**
 * This class describes a JTextArea that can correctly load unicode text an
 * search in it
 */
public class EditionTextArea extends JTextArea {
	Timer currentTimer = null;
	private int searchIndex = 0;
	private boolean modified = false;

	/**
	 * Default Constructor
	 */
	public EditionTextArea() {
		super();
		setLineWrap(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				setModified();
			}
		});
	}

	@Override
	public void paste() {
		setModified();
		super.paste();
	}

	void setModified() {
		modified = true;
	}

	public void setUnmodified() {
		modified = false;
	}

	public boolean isModified() {
		return modified;
	}

	public EditionTextArea(Document doc) {
		super(doc);
		setLineWrap(true);
	}

	/**
	 * Methode to get the text aera where retrieve data ( selection, text after
	 * or before the caret )
	 * 
	 * @return the text's data
	 */
	private String getData(boolean searchUp) throws BadLocationException {
		String textData = null;
		final EditionTextArea txtArea = this;
		int caretPos;
		// if there is a selection
		if (txtArea.getCaret().getDot() == txtArea.getCaret().getMark())
			if (searchUp)
				caretPos = txtArea.getSelectionStart();
			else
				caretPos = txtArea.getSelectionEnd();
		else
			caretPos = txtArea.getCaretPosition();
		final Document doc = txtArea.getDocument();
		if (searchUp) {
			// search data betwin the begining an the current position
			textData = doc.getText(0, caretPos);
			searchIndex = 0;
		} else {
			// search data betwin the current position and the end of the
			// document
			textData = doc.getText(caretPos, doc.getLength() - caretPos);
			searchIndex = caretPos;
		}
		return textData;
	}

	/**
	 * finds a word ( key ) in a text
	 * 
	 * @param searchUp
	 *            true if Search up mode is enabled
	 * @param modelCase
	 *            true if mode case is enabled
	 * @param modelWord
	 *            true if wall word only mode is selected
	 * @param modelPrefixe
	 *            true if prefixe search mode is selected
	 * @param modelSuffixe
	 *            true if suffixe search mode is selected
	 * @param modelRadical
	 *            true if radical search mode is selected
	 * @param key
	 *            the word to find
	 * @throws BadLocationException
	 * @throws KeyErrorException
	 *             if the key if null or with 0 size
	 */
	public void findNext(boolean searchUp, boolean modelCase,
			boolean modelWord, boolean modelPrefixe, boolean modelSuffixe,
			boolean modelRadical, String key) throws BadLocationException,
			KeyErrorException {
		// get the selection position if there is'nt selection
		// retrieve data fom the text with the caret position
		String data = getData(searchUp);
		String context = "";
		final String lineSeparator = System.getProperty("line.separator");
		// index where begin search
		int fromIndex = 0;
		// index of a found word
		int index = 0;
		if (key == null || key.length() == 0)
			throw new KeyErrorException("Search field Empty.");
		// "\n" search
		if (key.equals("\\n")) {
			key = lineSeparator;
		}
		// if case note selected
		if (!modelCase) {
			key = key.toLowerCase();
			data = data.toLowerCase();
		}
		// key searching
		// mininum one time ( for not whole words only )
		do {
			// if we are at the end of the data
			if (fromIndex + key.length() >= data.length())
				return;
			if (searchUp)
				index = data.lastIndexOf(key);
			else
				index = data.indexOf(key, fromIndex);
			// if data don't match key
			if (index == -1)
				return;
			// search whole word
			if (modelWord || modelPrefixe || modelSuffixe || modelRadical) {
				final int size = data.length();
				context = context(key, data, index);
				if (index == 0)
					context = " " + data.substring(0, index + key.length() + 1);
				else if ((index + key.length() + 1) > size)
					context = data.substring(index - 1, index + key.length())
							+ " ";
				else
					context = data.substring(index - 1, index + key.length()
							+ 1);
				if (modelWord && isWholeWord(key, context))
					break;
				else if (modelPrefixe && isaPrefixe(key, context))
					break;
				else if (modelSuffixe && isaSuffixe(key, context))
					break;
				else if (modelRadical && isaRadical(key, context))
					break;
				else if (searchUp) {
					data = data.substring(0, index + 1);
					fromIndex = 0;
				} else
					fromIndex = index + key.length();
			}
		} while (modelWord || modelPrefixe || modelSuffixe || modelRadical);
		// word selection
		if (key.equals(lineSeparator)) {
			this.setSelection(index + searchIndex - 1, index + searchIndex
					+ key.length(), searchUp);
		} else {
			setSelection(index + searchIndex,
					index + searchIndex + key.length(), searchUp);
		}
	}

	/**
	 * finds a word ( key ) with dictionary mode
	 * 
	 * @param searchUp
	 *            true if Search up mode is enabled
	 * @param modelCase
	 *            true if case mode is selected
	 * @param modelword
	 *            true if wall word only mode is selected
	 * @param flexionnalForm
	 *            true if flexional search mode is selected
	 * @param canonicalForm
	 *            true if canonical search mode is selected
	 * @param key
	 *            the word to find
	 * @throws BadLocationException
	 * @throws KeyErrorException
	 *             if the key if null or with 0 size
	 */
	public void dictionaryFindNext(boolean searchUp, boolean modelCase,
			boolean modelword, boolean flexionnalForm, boolean canonicalForm,
			String key) throws BadLocationException, KeyErrorException {
		// get the selection position if there is'nt selection
		// retrieve data fom the text with the caret position
		String data = getData(searchUp);
		String context = "";
		String context2 = "";
		final String lineSeparator = System.getProperty("line.separator");
		// index where begin search
		int fromIndex = 0;
		// index of a found word
		int index = 0;
		if (key == null || key.length() == 0)
			throw new KeyErrorException("Search field Empty.");
		// "\n" search
		if (key.equals("\\n")) {
			key = lineSeparator;
		}
		// if case note selected
		if (!modelCase) {
			key = key.toLowerCase();
			data = data.toLowerCase();
		}
		// key searching
		// mininum one time ( for not whole words only )
		do {
			// if we are at the end of the data
			if (fromIndex + key.length() >= data.length())
				return;
			if (searchUp)
				index = data.lastIndexOf(key);
			else
				index = data.indexOf(key, fromIndex);
			// if data don't match key
			if (index == -1)
				return;
			//
			if (flexionnalForm || canonicalForm || modelword) {
				final int size = data.length();
				// context = context(key, data, index);
				if (canonicalForm) {
					// +2 to search ".," after the key
					if (index == 0)
						context2 = " "
								+ data.substring(0, index + key.length() + 2);
					else
						context2 = data.substring(index - 1,
								index + key.length() + 2);
				}
				if (index == 0)
					context = "\n"
							+ data.substring(0, index + key.length() + 1);
				else if ((index + key.length() + 1) > size)
					context = data.substring(index - 1, index + key.length())
							+ " ";
				else
					context = data.substring(index - 1, index + key.length()
							+ 1);
				if (canonicalForm
						&& flexionnalForm
						&& (isaFlexionalForm(key, context) || isaCanonicalForm(
								key, context, context2)))
					break;
				else if (!canonicalForm && flexionnalForm
						&& isaFlexionalForm(key, context))
					break;
				else if (canonicalForm && !flexionnalForm
						&& isaCanonicalForm(key, context, context2))
					break;
				else if (modelword && isWholeWord(key, context))
					break;
				else if (searchUp) {
					data = data.substring(0, index + 1);
					fromIndex = 0;
				} else
					fromIndex = index + key.length();
			}
		} while (flexionnalForm || canonicalForm || modelword);
		// word selection
		if (key.equals(lineSeparator)) {
			this.setSelection(index + searchIndex - 1, index + searchIndex
					+ key.length(), searchUp);
		} else {
			setSelection(index + searchIndex,
					index + searchIndex + key.length(), searchUp);
		}
	}

	/**
	 * select a part of the text
	 * 
	 * @param xStart
	 *            the beginning of the selection
	 * @param xFinish
	 *            the end of the selection
	 * @param moveUp
	 *            the search direction mode ( up or down )
	 */
	public void setSelection(int xStart, int xFinish, boolean moveUp) {
		if (moveUp) {
			setCaretPosition(xFinish);
			moveCaretPosition(xStart);
		} else
			select(xStart, xFinish);
	}

	/**
	 * gets the context of a word ( 2 letters which surround it )
	 * 
	 * @param key
	 *            the word to find
	 * @param data
	 *            the text where find the text
	 * @param index
	 *            the key index
	 * @return the key context
	 */
	private String context(String key, String data, int index) {
		final int size = data.length();
		String context;
		if (index == 0)
			context = " " + data.substring(0, index + key.length() + 1);
		else if ((index + key.length() + 1) > size)
			context = data.substring(index - 1, index + key.length()) + " ";
		else
			context = data.substring(index - 1, index + key.length() + 1);
		return context;
	}

	private final static String separatorsRegexp = "[^\\p{javaLowerCase}\\p{javaUpperCase}]";
	private final static String lettersRegexp = "[\\p{javaLowerCase}\\p{javaUpperCase}]";

	/**
	 * checks is a word ( "key" ) is not surrounded by other letters
	 * 
	 * @param key
	 * @param context
	 *            the context of key ( keyIndex-1, key.length()+1 )
	 * @return true if the word is surrounded by separators; false otherwise
	 */
	private boolean isWholeWord(String key, String context) {
		return isMatchingExpression(key, context, separatorsRegexp,
				separatorsRegexp);
	}

	/**
	 * matches is a word ("key") is surrounded by leftRegexp and rightRegexp
	 * 
	 * @param key
	 * @param context
	 * @param leftRegexp
	 * @param rightRegexp
	 * @return true if a word ("key") is surrounded by leftRegexp and
	 *         rightRegexp
	 */
	private boolean isMatchingExpression(String key, String context,
			String leftRegexp, String rightRegexp) {
		final String expr = leftRegexp + key + rightRegexp;
		final Pattern p = Pattern.compile(expr);
		return context.matches(p.pattern());
	}

	/**
	 * matches is a word ("key") is ended by rightRegexp
	 * 
	 * @param key
	 * @param context
	 *            context of key
	 * @param rightRegexp
	 *            regular expression to match
	 * @return true if the context matches key+rightRegexp
	 */
	/*
	 * private boolean isMatchingExpression( String key, String context, String
	 * rightRegexp) {
	 * 
	 * String expr = key + rightRegexp; Pattern p = Pattern.compile(expr);
	 * return context.matches(p.pattern()); }
	 */
	/**
	 * matches if key is a radical
	 * 
	 * @param key
	 *            the word to find
	 * @param context
	 *            the context of key
	 * @return true if key is a radical
	 */
	private boolean isaRadical(String key, String context) {
		return isMatchingExpression(key, context, separatorsRegexp,
				separatorsRegexp);
	}

	/**
	 * matches if key is a prefixe
	 * 
	 * @param key
	 *            the word to find
	 * @param context
	 *            the context of key
	 * @return true if key is a prefixe
	 */
	private boolean isaPrefixe(String key, String context) {
		// String leftsepartorsRegexp = new String("[^\\d\\w���������������]");
		// String rightSepartorsRegexp = new String("[\\w���������������]");
		return isMatchingExpression(key, context, separatorsRegexp,
				lettersRegexp);
	}

	/**
	 * matches if key is a suffixe
	 * 
	 * @param key
	 *            the word to find
	 * @param context
	 *            the context of key
	 * @return true if key is a suffixe
	 */
	private boolean isaSuffixe(String key, String context) {
		// String leftsepartorsRegexp = new String("[\\w���������������]");
		// String rightSepartorsRegexp = new String("[^\\d\\w���������������]");
		return isMatchingExpression(key, context, lettersRegexp,
				separatorsRegexp);
	}

	/**
	 * matches if key is a flexional form
	 * 
	 * @param key
	 *            the word to find
	 * @param context
	 *            the context of key
	 * @return true if key is a flexional form
	 */
	private boolean isaFlexionalForm(String key, String context) {
		final String leftsepartorsRegexp = "\n";
		final String rightSepartorsRegexp = ",";
		return isMatchingExpression(key, context, leftsepartorsRegexp,
				rightSepartorsRegexp);
	}

	/**
	 * matches if key is a canonical form
	 * 
	 * @param key
	 *            the word to find
	 * @param context
	 *            the context of key
	 * @return true if key is a anonical form
	 */
	private boolean isaCanonicalForm(String key, String context, String context2) {
		final String leftsepartorsRegexp = ",";
		final String rightSepartorsRegexp = "\\.";
		return isMatchingExpression(key, context, leftsepartorsRegexp,
				rightSepartorsRegexp)
				|| isMatchingExpression(key, context2,
						separatorsRegexp/* "[^\\d\\w���������������]" */,
						",\\.");
	}

	/**
	 * find the sentence numbered X
	 * 
	 * @param number
	 *            the sentence's number
	 * @throws BadLocationException
	 * @throws KeyErrorException
	 *             if an error occure while sentences counting
	 * @throws TextAreaSeparatorException
	 *             if there isn't any sentence separator {S} or if number >
	 *             sentences number in the text
	 */
	public void findSentence(int number) throws BadLocationException,
			KeyErrorException, TextAreaSeparatorException {
		int index = 0;
		int SententenceStart = 0;
		int SententenceEnd = 0;
		final String key = "{S}";
		setCaretPosition(0);
		String textData = getText();
		if (countAll(key, false, false, false) + 1 < number)
			throw new TextAreaSeparatorException("This text doesn't contain "
					+ number + " sentences, please enter an other number.");
		for (int i = 1; i <= number; ++i) {
			if ((index = textData.indexOf(key)) != -1) {
				textData = textData.substring(index + key.length());
				SententenceStart = SententenceEnd;
				SententenceEnd += index + key.length();
			} else {
				// the last sentence
				if (i > 1) {
					SententenceStart = SententenceEnd;
					SententenceEnd += textData.length() + key.length();
				} else {
					throw new TextAreaSeparatorException(
							"This text doesn't contain sentence separators."
									+ "You should process the text before searching"
									+ " sentences\n");
				}
			}
		}
		setSelection(SententenceStart, SententenceEnd - key.length(), false);
	}

	/**
	 * count all the String occurrence in the text after or before the caret
	 * 
	 * @param key
	 *            the word to find
	 * @param searchUp
	 *            true if Search up mode is enabled
	 * @param modelCase
	 *            true if case mode is selected
	 * @param modelWord
	 *            true if wall word only mode is selected
	 * @return the count number of a String in the text,<br>
	 *         -1 if there is a location error
	 * @throws BadLocationException
	 * @throws KeyErrorException
	 *             if the key if null or with 0 size
	 */
	public int countAll(String key, boolean searchUp, boolean modelWord,
			boolean modelCase) throws BadLocationException, KeyErrorException {
		int index = 0;
		int occurenceCounter = 0;
		final String lineSeparator = System.getProperty("line.separator");
		if (key.length() == 0) {
			throw new KeyErrorException("Search field Empty.");
		}
		if (key.equals("\\n")) {
			key = lineSeparator;
		}
		// get data from the text
		String textData = getData(searchUp);
		// case: model case selected
		if (!modelCase) {
			textData = textData.toLowerCase();
			key = key.toLowerCase();
		}
		// count occurrences
		while (true) {
			if ((index = textData.indexOf(key)) != -1) {
				// case: whole word only selected
				if (modelWord) {
					final String context = context(key, textData, index);
					if (isWholeWord(key, context))
						occurenceCounter++;
				} else
					occurenceCounter++;
				textData = textData.substring(index + key.length());
			} else {
				return occurenceCounter;
			}
		}
	}

	/**
	 * Replaces all occurences of a word in a text
	 * 
	 * @param key
	 *            the word to find
	 * @param rKey
	 *            the new word
	 * @param modelUp
	 *            true if Search up mode is enabled
	 * @param modelCase
	 *            true if case mode is selected
	 * @param modelWord
	 *            true if wall word only mode is selected
	 * @throws TargetException
	 *             is key is empty
	 * @throws ReplacementTargetException
	 *             is the new word is empty
	 */
	public void replaceAll(String key, String rKey, boolean modelUp,
			boolean modelCase, boolean modelWord) throws TargetException,
			ReplacementTargetException {
		int index = 0;
		int textOffset = 0;
		try {
			if (key.length() == 0) {
				throw new TargetException("Please enter the target to search");
			}
			if (rKey.length() == 0) {
				throw new ReplacementTargetException(
						"Please enter replacement word");
			}
			// get data from the text
			String textData = getData(modelUp);
			// case: model case selected
			if (!modelCase) {
				textData = textData.toLowerCase();
				key = key.toLowerCase();
			}
			// replace occurrences
			while (true) {
				if ((index = textData.indexOf(key)) != -1) {
					// case: whole word only selected
					if (modelWord) {
						final String context = context(key, textData, index);
						if (isWholeWord(key, context))
							setSelection(index, index + key.length(), modelUp);
						replaceSelection(rKey);
					} else {
						setSelection(textOffset + index, textOffset + index
								+ key.length(), modelUp);
						replaceSelection(rKey);
					}
					textData = textData.substring(index + key.length());
					textOffset += index + key.length();
				} else {
					return;
				}
			}
		} catch (final BadLocationException e) {
			JOptionPane.showMessageDialog(this,
					"Bad location exception" + e.getMessage(), "Warning",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
