/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.graphrendering;

import java.awt.Color;
import java.awt.Event;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.frames.GraphFrame;
import fr.umlv.unitex.frames.UnitexFrame;

/**
 * This class describes the text field used to get the box text in a graph.
 *
 * @author Sébastien Paumier
 */
public class TextField extends GraphTextField {
    /**
     * Frame that contains this component
     */
    private final GraphFrame parent;
    /**
     * Indicates if the text field content has been modified
     */
    private boolean modified = false;

    /**
     * <code>TextAction</code> that defines what to do for a "paste" operation
     */
    private final SpecialPaste specialPaste;
    /**
     * <code>TextAction</code> that defines what to do for a "copy" operation
     */
    private final SpecialCopy specialCopy;
    /**
     * <code>TextAction</code> that defines what to do for a "cut" operation
     */
    private final Cut cut;

    
    private final Completion completion=new Completion("completion");
    
    /**
     * <code>TextAction</code> that shows the graph presentation frame
     */
    private final Presentation presentation;
    /**
     * <code>TextAction</code> that shows a dialog box to open a graph
     */
    private final Open OPEN;
    /**
     * <code>TextAction</code> that saves the current graph
     */
    private final Save SAVE;

    /**
     * Constructs a new <code>TextField</code>
     *
     * @param n number of columns
     * @param p frame that contains this component
     */
    public TextField(int n, GraphFrame p) {
        super(n);
        setEditable(false);
        modified = false;
        parent = p;
        setDisabledTextColor(Color.white);
        setBackground(Color.white);
        Keymap k = getKeymap();
        k = addKeymap("textfield-keymap", k);
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('l', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('L', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('k', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('K', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('o', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('s', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('p', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('m', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('r', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('x', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('c', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('C', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('v', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('a', Event.CTRL_MASK));
        k.removeKeyStrokeBinding(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
        specialPaste = new SpecialPaste("special-paste");
        specialCopy = new SpecialCopy("special-copy");
        presentation = new Presentation("presentation");
        // TextAction that defines what to do for a "select all" operation
        final SelectAll selectAll = new SelectAll("select all");
        cut = new Cut("cut");
        OPEN = new Open("open");
        SAVE = new Save("save");
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('v', Event.CTRL_MASK),
                specialPaste);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('V', Event.CTRL_MASK),
                specialPaste);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('c', Event.CTRL_MASK),
                specialCopy);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('C', Event.CTRL_MASK),
                specialCopy);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('r', Event.CTRL_MASK),
                presentation);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('R', Event.CTRL_MASK),
                presentation);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('o', Event.CTRL_MASK),
                OPEN);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('O', Event.CTRL_MASK),
                OPEN);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('s', Event.CTRL_MASK),
                SAVE);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('S', Event.CTRL_MASK),
                SAVE);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('a', Event.CTRL_MASK),
                selectAll);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('A', Event.CTRL_MASK),
                selectAll);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('x', Event.CTRL_MASK),
                cut);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke('X', Event.CTRL_MASK),
                cut);
        k.addActionForKeyStroke(KeyStroke.getKeyStroke(' ', Event.CTRL_MASK),
                completion);
        this.setKeymap(k);
        addKeyListener(new MyKeyListener());
    }

    public class SpecialCopy extends TextAction implements ClipboardOwner {
        public SpecialCopy(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            ArrayList<GenericGraphBox> boxes = parent.getSelectedBoxes();
            if (boxes.size() < 2) {
                // is there is no or one box selected, we copy normally
                copy();
                UnitexFrame.clip.setContents(null, this);
                return;
            }
            UnitexFrame.clip.setContents(new MultipleBoxesSelection(
                    new MultipleSelection(boxes, true)), this);
        }

        public void lostOwnership(Clipboard c, Transferable d) {
            // nothing to do
        }
    }

    class SelectAll extends TextAction implements ClipboardOwner {
        public SelectAll(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            ArrayList<GenericGraphBox> boxes = parent.getSelectedBoxes();
            if (boxes.size() == 1) {
                selectAll();
            } else {
                parent.selectAllBoxes();
            }
        }

        public void lostOwnership(Clipboard c, Transferable d) {
            // nothing
        }
    }

    public class Cut extends TextAction implements ClipboardOwner {
        public Cut(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            ArrayList<GenericGraphBox> boxes = parent.getSelectedBoxes();
            if (boxes.size() == 1) {
                cut();
            } else {
                UnitexFrame.clip.setContents(new MultipleBoxesSelection(
                        new MultipleSelection(boxes, true)),
                        this);
                parent.removeSelected();
                setText("");
            }
        }

        public void lostOwnership(Clipboard c, Transferable d) {
            // nothing to do
        }
    }

    public class SpecialPaste extends TextAction {
        public SpecialPaste(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            Transferable data;
            MultipleSelection res = null;
            data = UnitexFrame.clip.getContents(this);
            try {
                if (data != null)
                    res = (MultipleSelection) data
                            .getTransferData(new DataFlavor("unitex/boxes",
                                    "Unitex dataflavor"));
            } catch (UnsupportedFlavorException e2) {
                e2.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (res == null || TextField.this.modified) {
                // if there is no boxes to copy we do a simple paste
                paste();
                return;
            }
            res.n++;
            parent.pasteSelection(res);
        }
    }

    class Presentation extends TextAction {
        public Presentation(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            GraphFrame f = UnitexFrame.getFrameManager()
                    .getCurrentFocusedGraphFrame();
            if (f == null)
                return;
            GraphPresentationInfo info = UnitexFrame.getFrameManager()
                    .newGraphPresentationDialog(f.getGraphPresentationInfo(),
                            true);
            if (info != null) {
                f.setGraphPresentationInfo(info);
            }
        }
    }

    class Open extends TextAction {
        public Open(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            UnitexFrame.mainFrame.openGraph();
        }
    }

    class Save extends TextAction {
        public Save(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
            UnitexFrame.mainFrame.saveGraph(parent);
        }
    }

    class Completion extends TextAction {
        public Completion(String s) {
            super(s);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    
    @Override
    public void setContent(String s) {
        modified = false;
        if (s == null) {
            /* We want to make the text field non editable */
            setEditable(false);
            setText("");
            return;
        }
        setEditable(true);
        setText(s);
        requestFocus();
        getCaret().setVisible(true);
        selectAll();
    }

    /**
     * Returns a new <code>ListDocument</code> object.
     *
     * @return the <code>ListDocument</code>
     */
    @Override
    public Document createDefaultModel() {
        return new ListDocument();
    }

    @Override
    public boolean validateContent() {
        if (!hasChangedTextField())
            return false;
        if (isValidGraphBoxContent(getText())) {
            parent.setTextForSelected(getText());
            parent.unSelectAllBoxes();
            return true;
        }
        return false;
    }

    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() || e.isAltDown()) {
                // if the control key or alt key is pressed, we do nothing: the
                // event we be caught by the ActionListeners
                return;
            }
            if (e.getKeyCode() == 10)
                validateContent();
            modified = true;
        }
    }

    /**
     * Tests if the content of the text field has changed.
     *
     * @return <code>true</code> if the content has changed, <code>false</code>
     *         otherwise
     */
    boolean hasChangedTextField() {
        return modified;
    }

    private int test_transduction(char s[], int i) {
        int compteur;
        if (s[i] != '/')
            return 0;
        i--;
        compteur = 0;
        while (i >= 0 && s[i] == '\\') {
            compteur++;
            i--;
        }
        if ((compteur % 2) != 0)
            return 0;
        return 1;
    }

    private boolean tokenize(String s) {
        int L = s.length(), i = 0;
        String tmp;
        char ligne[] = new char[10000];
        ligne = s.toCharArray();
        if (ligne[0] == '+') {
            JOptionPane.showMessageDialog(null,
                    "Unexpected \"+\" as first character of the line", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (L >= 2 && ligne[L - 1] == '+' && ligne[L - 2] != '\\') {
            JOptionPane.showMessageDialog(null,
                    "Unexpected \"+\" as last character of the line", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        while (i < L) {
            tmp = "";
            if (ligne[i] == ':') {
                // case of a sub graph call
                i++;
                while ((i < L) && (ligne[i] != '+')) {
                    if (ligne[i] == '\\') {
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                        if (i >= L) {
                            JOptionPane.showMessageDialog(null,
                                    "Unexpected \"\\\" at end of line",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                    tmp = tmp.concat(String.valueOf(ligne[i++]));
                }
                if (tmp.length() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "Missing graph name after \":\"", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                i++;
            } else {
                // all other cases
                if (ligne[i] == '+') {
                    // if we find a + just after a + it is an error
                    JOptionPane.showMessageDialog(null,
                            "Empty line error: \"++\"", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                while ((i < L) && (ligne[i] != '+')) {
                    if (ligne[i] == '"') {
                        // case of a quote expression
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                        while ((i < L) && ligne[i] != '"') {
                            if (ligne[i] == '\\') {
                                tmp = tmp.concat(String.valueOf(ligne[i++]));
                                if (i >= L) {
                                    JOptionPane.showMessageDialog(null,
                                            "Unexpected \"\\\" at end of line",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                            }
                            tmp = tmp.concat(String.valueOf(ligne[i++]));
                        }
                        if (i >= L) {
                            JOptionPane.showMessageDialog(null,
                                    "No closing \"", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                    } else if (ligne[i] == '<') {
                        // case of a <...> expression
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                        while ((i < L) && ligne[i] != '>') {
                            if (ligne[i] == '\\') {
                                tmp = tmp.concat(String.valueOf(ligne[i++]));
                                if (i >= L) {
                                    JOptionPane.showMessageDialog(null,
                                            "Unexpected \"\\\" at end of line",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                            }
                            tmp = tmp.concat(String.valueOf(ligne[i++]));
                        }
                        if (i >= L) {
                            JOptionPane.showMessageDialog(null,
                                    "No closing \">\"", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                    } else if (ligne[i] == '{') {
                        // case of a {...} expression
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                        while ((i < L) && ligne[i] != '}') {
                            if (ligne[i] == '\\') {
                                tmp = tmp.concat(String.valueOf(ligne[i++]));
                                if (i >= L) {
                                    JOptionPane.showMessageDialog(null,
                                            "Unexpected \"\\\" at end of line",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                            }
                            tmp = tmp.concat(String.valueOf(ligne[i++]));
                        }
                        if (i >= L) {
                            JOptionPane.showMessageDialog(null,
                                    "No closing \"}\"", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                    } else {
                        if (ligne[i] == '\\') {
                            tmp = tmp.concat(String.valueOf(ligne[i++]));
                            if (i >= L) {
                                JOptionPane.showMessageDialog(null,
                                        "Unexpected \"\\\" at end of line",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        }
                        tmp = tmp.concat(String.valueOf(ligne[i++]));
                    }
                }
                i++;
            }
        }
        return true;
    }

    /**
     * Tests if a content is a valid content for a graph box.
     *
     * @param s the content to test
     * @return <code>true</code> if the content is valid, <code>false</code>
     *         otherwise
     */
    boolean isValidGraphBoxContent(String s) {
        if (s.equals(""))
            return true;
        char ligne[];
        String tmp = "";
        int i, L;
        ligne = s.toCharArray();
        i = 0;
        L = s.length();
        if (L == 2 && ligne[0] == '$' && (ligne[1] == '(' || ligne[1] == ')')) {
            JOptionPane.showMessageDialog(null,
                    "You must indicate a variable name between $ and ( or )",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        while ((i != L) && (test_transduction(ligne, i) == 0))
            tmp = tmp.concat(String.valueOf(ligne[i++]));
        if ((i != L) && (i == 0)) {
            JOptionPane.showMessageDialog(null, "Empty text before \"/\"",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (L > 2 && ligne[0] == '$'
                && (ligne[L - 1] == '(' || ligne[L - 1] == ')')
                && s.lastIndexOf('+') == -1) {
            int k = 1;
            if (ligne[k] == '|') {
                /* An output variable is of the form $|XYZ( */
                k++;
            }
            // case of a variable start $a( or end $a)
            for (; k < L - 1; k++)
                if (ligne[k] != '_' && !(ligne[k] >= '0' && ligne[k] <= '9')
                        && !(ligne[k] >= 'a' && ligne[k] <= 'z')
                        && !(ligne[k] >= 'A' && ligne[k] <= 'Z')) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "A variable name can only contain the following characters:\nA..Z,a..z,0..9 and the underscore '_'",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            return true;
        }
        return "$<".equals(s) || "$>".equals(s) || "$*".equals(s) || tokenize(tmp);
    }

    public SpecialPaste getSpecialPaste() {
        return specialPaste;
    }

    public SpecialCopy getSpecialCopy() {
        return specialCopy;
    }

    public Cut getCut() {
        return cut;
    }
}
