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
package fr.umlv.unitex.graphtools;

import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GraphBox;
import fr.umlv.unitex.graphrendering.GenericGraphicalZone;
import fr.umlv.unitex.graphrendering.GraphicalZone;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class contains static methods that allow to find and replace the content of one or more boxes.
 *
 * @author Maxime Petit
 */
public class FindAndReplace {

  private static boolean contains(String search, String content, boolean caseSensitive) {
    if (caseSensitive) {
      return content.contains(search);
    }
    return Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE).matcher(content).find();
  }

  /**
   * Returns true if and only if the specified regex is a valid regex.
   *
   * @param regex the regex to test.
   * @return true if regex is valid, false otherwise.
   */
  private static boolean isRegex(String regex) {
    boolean isRegex;
    try {
      Pattern.compile(regex);
      isRegex = true;
    } catch (PatternSyntaxException e) {
      isRegex = false;
    }
    return isRegex;
  }

  /**
   * Returns true if and only if the content of g has been replaced.
   * Replace the content of g with replace if it contains search.
   *
   * @param g       the box containing the string to replace.
   * @param search  the sequence to search for.
   * @param replace the sequence to replace with.
   * @param zone    the GenericGraphicalZone containing the box.
   * @return true if the content has been replaced, false otherwise.
   */
  private static boolean replace(GenericGraphBox g, String search, String replace, GenericGraphicalZone zone) {
    if (g.getType() == GraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replace(search, replace);
      return setNewText(g, zone, newContent);
    }
    return false;
  }

  private static boolean setNewText(GenericGraphBox g, GenericGraphicalZone zone, String newContent) {
    if (!newContent.equals(g.getContent())) {
      newContent = newContent.replaceAll("\\++", "+").replaceAll("^\\+", "")
        .replaceAll("\\+$", "");
      if (newContent.isEmpty()) {
        newContent = "<E>";
      }
      return zone.setTextBox(g, newContent);
    }
    return false;
  }

  private static boolean replaceCaseInsensitive(GenericGraphBox g, String search, String replace, GenericGraphicalZone zone) {
    if (g.getType() == GraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replaceAll("(?i)" + Pattern.quote(search), replace);
      return setNewText(g, zone, newContent);
    }
    return false;
  }

  /**
   * Returns true if and only if the content of g has been replaced.
   * Replace the content of g with replace if it matches regex.
   *
   * @param g       the box containing the string to replace.
   * @param regex   the sequence to search for.
   * @param replace the sequence to replace with.
   * @param zone    the GenericGraphicalZone containing the box.
   * @return true if the content has been replaced, false otherwise.
   */
  private static boolean replaceRegex(GenericGraphBox g, String regex, String replace, GenericGraphicalZone zone) {
    if (isRegex(regex) && g.getType() == GraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replaceAll(regex, replace);
      return setNewText(g, zone, newContent);
    }
    return false;
  }

  /**
   * Returns the number of occurrence of search in boxes.
   *
   * @param boxes         the list containing the boxes.
   * @param search        the sequence to search for.
   * @param useRegex      true if the search must use regular expressions, false otherwise.
   * @param caseSensitive true if the search must be case sensitive, false otherwise.
   * @param wholeLine     true if the search must match a whole line only, false otherwise.
   * @param ignoreComment true if the search must ignore comment boxes, false otherwise.
   * @return the number of occurrence of search in boxes.
   */
  public static int findAll(ArrayList<GenericGraphBox> boxes, String search, boolean useRegex, boolean caseSensitive,
                            boolean wholeLine, boolean ignoreComment) {
    int i = 0;
    if (wholeLine) {
      for (GenericGraphBox box : boxes) {
        String[] tokens = box.getContent().split("\\+");
        if (!(box.isStandaloneBox() && ignoreComment) && searchArray(tokens, search, useRegex, caseSensitive) && box
          .getType() == GenericGraphBox.NORMAL && !box.getContent().equals("<E>")) {
          i++;
        }
      }
    } else if (!useRegex) {
      for (GenericGraphBox box : boxes) {
        if (!(box.isStandaloneBox() && ignoreComment) && contains(search, box.getContent(), caseSensitive) && box
          .getType() == GenericGraphBox.NORMAL && !box.getContent().equals("<E>")) {
          i++;
        }
      }
    } else {
      for (GenericGraphBox box : boxes) {
        if (!(box.isStandaloneBox() && ignoreComment) && isRegex(search) && Pattern.compile(search).matcher(box
          .getContent()).find() && box.getType() == GenericGraphBox.NORMAL && !box.getContent().equals("<E>")) {
          i++;
        }
      }
    }
    return i;
  }

  /**
   * Returns true if and only if this box contains the specified
   * search.
   *
   * @param box           the box containing the string to search
   * @param search        the sequence to search for.
   * @param useRegex      true if the search must use regular expressions, false otherwise.
   * @param caseSensitive true if the search must be case sensitive, false otherwise.
   * @param wholeLine     true if the search must match a whole line only, false otherwise.
   * @param ignoreComment true if the search must ignore comment boxes, false otherwise.
   * @return true if the box contains search, false otherwise.
   */
  public static boolean find(GenericGraphicalZone zone, GenericGraphBox box, String search, boolean useRegex, boolean caseSensitive, boolean wholeLine, boolean ignoreComment) {
    if (box.isStandaloneBox() && ignoreComment || box.getContent().equals("<E>") || box.getType() != GenericGraphBox.NORMAL) {
      zone.setHighlight(false);
      return false;
    }
    if (wholeLine) {
      String[] tokens = box.getContent().split("\\+");
      if (searchArray(tokens, search, useRegex, caseSensitive)) {
        zone.setHighlight(box, true);
        return true;
      }
    } else {
      if (!useRegex) {
        if (contains(search, box.getContent(), caseSensitive)) {
          zone.setHighlight(box, true);
          return true;
        }
      } else {
        if (isRegex(search) && Pattern.compile(search).matcher(box.getContent()).find()) {
          zone.setHighlight(box, true);
          return true;
        }
      }
    }
    zone.setHighlight(false);
    return false;
  }

  private static boolean searchArray(String[] tokens, String search, boolean useRegex, boolean caseSensitive) {
    if (useRegex) {
      for (String token : tokens) {
        if (isRegex("^" + search + "$") && Pattern.compile("^" + search + "$").matcher(token).find()) {
          return true;
        }
      }
    } else {
      for (String token : tokens) {
        if (caseSensitive) {
          if (token.equals(search)) {
            return true;
          }
        } else {
          if (token.equalsIgnoreCase(search)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Returns true if and only if the content of box has been replaced.
   * Replace the content of box with replace if it contains search.
   *
   * @param box           the box containing the string to replace.
   * @param search        the sequence to search for.
   * @param replace       the sequence to replace with.
   * @param zone          the GenericGraphicalZone containing the box.
   * @param useRegex      true if the search must use regular expressions, false otherwise.
   * @param caseSensitive true if the search must be case sensitive, false otherwise.
   * @param wholeLine     true if the search must match a whole line only, false otherwise.
   * @param ignoreComment true if the search must ignore comment boxes, false otherwise.
   * @return true if the content has been replaced, false otherwise.
   */
  public static boolean replace(GenericGraphBox box, String search, String replace, GenericGraphicalZone zone, boolean useRegex, boolean caseSensitive, boolean wholeLine, boolean ignoreComment) {
    if (box.isStandaloneBox() && ignoreComment) {
      return false;
    }
    if (wholeLine) {
      String[] tokens = box.getContent().split("\\+");
      String newContent = replaceArrayToString(tokens, search, replace, useRegex, caseSensitive);
      if (setNewText(box, zone, newContent)) return true;
    } else {
      if (useRegex) {
        return replaceRegex(box, search, replace, zone);
      } else {
        if (caseSensitive) {
          return replace(box, search, replace, zone);
        } else {
          return replaceCaseInsensitive(box, search, replace, zone);
        }
      }
    }
    return false;
  }

  /**
   * Replace all boxes and returns the number of boxes which content has been replaced.
   *
   * @param boxes         the list containing the boxes.
   * @param search        the sequence to search for.
   * @param replace       the sequence to replace with.
   * @param zone          the GenericGraphicalZone containing the box.
   * @param useRegex      true if the search must use regular expressions, false otherwise.
   * @param caseSensitive true if the search must be case sensitive, false otherwise.
   * @param wholeLine     true if the search must match a whole line only, false otherwise.
   * @param ignoreComment true if the search must ignore comment boxes, false otherwise.
   * @return the number of boxes which content has been replaced.
   */
  public static int replaceAll(ArrayList<GenericGraphBox> boxes, String search, String replace, GenericGraphicalZone zone, boolean useRegex, boolean caseSensitive, boolean wholeLine, boolean ignoreComment) {
    int i = 0;
    for (GenericGraphBox box : boxes) {
      if (replace(box, search, replace, zone, useRegex, caseSensitive, wholeLine, ignoreComment)) {
        i++;
      }
    }
    return i;
  }

  private static String replaceArrayToString(String[] tokens, String search, String replace, boolean useRegex,
                                             boolean caseSensitive) {
    StringBuilder sb = new StringBuilder();
    String prefix = "";
    if (useRegex) {
      for (String token : tokens) {
        sb.append(prefix);
        prefix = "+";
        if (isRegex("^" + search + "$") && Pattern.compile("^" + search + "$").matcher(token).find()) {
          sb.append(token.replaceAll(search, replace));
        } else {
          sb.append(token);
        }
      }
    } else {
      for (String token : tokens) {
        sb.append(prefix);
        prefix = "+";
        if (caseSensitive) {
          if (token.equals(search)) {
            sb.append(token.replace(search, replace));
          } else {
            sb.append(token);
          }
        } else {
          if (token.equalsIgnoreCase(search)) {
            sb.append(token.replaceAll("(?i)" + Pattern.quote(search), replace));
          } else {
            sb.append(token);
          }
        }
      }
    }
    return sb.toString();
  }

  private static void checkReplace(GenericGraphBox box, String search, String replace, GenericGraphicalZone zone, boolean useRegex, boolean caseSensitive, boolean wholeLine, boolean ignoreComment) throws BackSlashAtEndOfLineException, NoClosingSupException, NoClosingQuoteException, NoClosingRoundBracketException, MissingGraphNameException {
    if (box.isStandaloneBox() && ignoreComment) {
      return;
    }
    if (wholeLine) {
      String[] tokens = box.getContent().split("\\+");
      String newContent = replaceArrayToString(tokens, search, replace, useRegex, caseSensitive);
      checkNewText(box, zone, newContent);
    } else {
      if (useRegex) {
        checkReplaceRegex(box, search, replace, zone);
      } else {
        if (caseSensitive) {
          checkReplace(box, search, replace, zone);
        } else {
          checkReplaceCaseInsensitive(box, search, replace, zone);
        }
      }
    }
  }

  private static void checkReplaceCaseInsensitive(GenericGraphBox g, String search, String replace, GenericGraphicalZone zone) throws BackSlashAtEndOfLineException, NoClosingSupException, NoClosingQuoteException, NoClosingRoundBracketException, MissingGraphNameException {
    if (g.getType() == GenericGraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replaceAll("(?i)" + Pattern.quote(search), replace);
      checkNewText(g, zone, newContent);
    }
  }


  private static void checkReplaceRegex(GenericGraphBox g, String regex, String replace, GenericGraphicalZone zone) throws BackSlashAtEndOfLineException, NoClosingSupException, NoClosingQuoteException, NoClosingRoundBracketException, MissingGraphNameException {
    if (isRegex(regex) && g.getType() == GenericGraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replaceAll(regex, replace);
      checkNewText(g, zone, newContent);
    }
  }

  private static void checkReplace(GenericGraphBox g, String search, String replace, GenericGraphicalZone zone) throws BackSlashAtEndOfLineException, NoClosingSupException, NoClosingQuoteException, NoClosingRoundBracketException, MissingGraphNameException {
    if (g.getType() == GenericGraphBox.NORMAL && !g.getContent().equals("<E>")) {
      String newContent = g.getContent().replace(search, replace).replaceAll("\\++", "+").replaceAll("^\\+", "")
        .replaceAll("\\+$", "");
      if (newContent.isEmpty()) {
        newContent = "<E>";
      }
      checkNewText(g, zone, newContent);
    }
  }

  private static void checkNewText(GenericGraphBox box, GenericGraphicalZone zone, String newContent) throws BackSlashAtEndOfLineException, NoClosingSupException, NoClosingQuoteException, NoClosingRoundBracketException, MissingGraphNameException {
    if (!newContent.equals(box.getContent())) {
      zone.checkTextBox(box, newContent);
    }
  }

  /**
   * Returns a String containing an error message if one or more boxes cannot be replaced.
   *
   * @param boxes         the list containing the boxes.
   * @param search        the sequence to search for.
   * @param replace       the sequence to replace with.
   * @param zone          the GenericGraphicalZone containing the box.
   * @param useRegex      true if the search must use regular expressions, false otherwise.
   * @param caseSensitive true if the search must be case sensitive, false otherwise.
   * @param wholeLine     true if the search must match a whole line only, false otherwise.
   * @param ignoreComment true if the search must ignore comment boxes, false otherwise.
   * @return the string containing an error message.
   */
  public static String checkReplaceAll(ArrayList<GenericGraphBox> boxes, String search, String replace, GenericGraphicalZone zone, boolean useRegex, boolean caseSensitive, boolean wholeLine, boolean ignoreComment) {
    for (GenericGraphBox box : boxes) {
      try {
        checkReplace(box, search, replace, zone, useRegex, caseSensitive, wholeLine, ignoreComment);
      } catch (BackSlashAtEndOfLineException e) {
        return "Unexpected \'\\\' at end of line";
      } catch (NoClosingSupException e) {
        return "Boxes must be properly balanced with < >";
      } catch (NoClosingQuoteException e) {
        return "No closing \"";
      } catch (NoClosingRoundBracketException e) {
        return "Boxes must be properly balanced with { }";
      } catch (MissingGraphNameException e) {
        return "Missing graph name after \':\'";
      }
    }
    return "";
  }

  private static boolean findNextInSeq(GenericGraphicalZone zone, ArrayList<String> seq, GenericGraphBox box, int i, boolean
    highlight, boolean caseSensitive, boolean useRegex) {
    if (i >= seq.size()) {
      return true;
    }
    if (!boxEquals(seq.get(i), box.getContent(), caseSensitive, useRegex)) {
      return false;
    } else if (highlight) {
      box.setHighlight(true);
      zone.getSelectedBoxes().add(box);
    }
    if (box.getTransitions().isEmpty()) {
      return i == (seq.size() - 1);

    }
    if ((i - seq.size() != -1 && box.getTransitions().size() > 1) || (box.getHasIncomingTransitions() > 1)) {
      return false;
    }
    return findNextInSeq(zone, seq, box.getTransitions().get(0), i + 1, highlight, caseSensitive, useRegex);
  }

  /**
   * Returns true if and only if this box and the following boxes match the sequence.
   *
   * @param graphicalZone   the GenericGraphicalZone containing the sequence.
   * @param findSeqList     the list containing the sequence to search for.
   * @param genericGraphBox the first box of the sequence.
   * @param highlight       true if the sequence must be highlighted, false otherwise.
   * @param caseSensitive   true if the search must be case sensitive, false otherwise.
   * @param useRegex        true if the search must use regular expressions, false otherwise.
   * @return true if the box and the following boxes match the sequence, false otherwise.
   */
  public static boolean isSeq(GenericGraphicalZone graphicalZone, ArrayList<String> findSeqList, GenericGraphBox
    genericGraphBox, boolean highlight, boolean caseSensitive, boolean useRegex) {
    if (findSeqList.size() == 1 && boxEquals(findSeqList.get(0), genericGraphBox.getContent(), caseSensitive, useRegex)) {
      if (highlight) {
        //genericGraphBox.setSelected(highlight);
        genericGraphBox.setHighlight(true);
        graphicalZone.getSelectedBoxes().add(genericGraphBox);
        graphicalZone.setHighlight(genericGraphBox, true);
      }
      return true;
    }
    if (boxEquals(findSeqList.get(0), genericGraphBox.getContent(), caseSensitive, useRegex) && genericGraphBox
      .getTransitions().size() == 1) {
      if (highlight) {
        //genericGraphBox.setSelected(highlight);
        genericGraphBox.setHighlight(true);
        graphicalZone.getSelectedBoxes().add(genericGraphBox);
        graphicalZone.setHighlight(genericGraphBox, true);
      }
      if (!findNextInSeq(graphicalZone, findSeqList, genericGraphBox.getTransitions().get(0), 1, highlight,
        caseSensitive, useRegex)) {
        graphicalZone.unSelectAllBoxes();
        graphicalZone.setHighlight(false);
        graphicalZone.removeHighlight();
        return false;
      }
      return true;
    }
    graphicalZone.setHighlight(false);
    graphicalZone.unSelectAllBoxes();
    graphicalZone.removeHighlight();
    return false;
  }

  /**
   * Replace all sequence with replaceSeqList and returns the number of sequences
   * which have been replaced.
   *
   * @param zone           the GenericGraphicalZone containing the sequence.
   * @param findSeqList    the list containing the sequence to search for.
   * @param replaceSeqList the list containing the sequence to replace with.
   * @param caseSensitive  true if the search must be case sensitive, false otherwise.
   * @param useRegex       true if the search must use regular expressions, false otherwise.
   * @return the number of sequences which have been replaced.
   */
  public static int replaceAllSeq(GenericGraphicalZone zone, ArrayList<String> findSeqList, ArrayList<String>
    replaceSeqList, boolean caseSensitive, boolean useRegex) {
    ArrayList<GenericGraphBox> boxes = new ArrayList<GenericGraphBox>();
    for (GenericGraphBox box : zone.getBoxes()) {
      if (isSeq(zone, findSeqList, box, false, caseSensitive, useRegex)) {
        boxes.add(box);
      }
    }
    int res = 0;
    for (GenericGraphBox box : boxes) {
      isSeq(zone, findSeqList, box, true, caseSensitive, useRegex);
      ArrayList<GenericGraphBox> currentSeq = new ArrayList<GenericGraphBox>();
      for (int i = 0; i < zone.getSelectedBoxes().size(); i++) {
        currentSeq.add(zone.getSelectedBoxes().get(i));
      }
      if (replaceSeq(zone, replaceSeqList, currentSeq)) {
        res++;
      }
      zone.unSelectAllBoxes();
    }
    return res;
  }

  /**
   * Replace seq with replaceSeqList and returns true if and only if the sequence has been replaced.
   *
   * @param zone           the GenericGraphicalZone containing the sequence.
   * @param replaceSeqList the list containing the sequence to replace with.
   * @param seq            the list containing the sequence of boxes.
   * @return true if the sequence has been replaced.
   */
  public static boolean replaceSeq(GenericGraphicalZone zone, ArrayList<String> replaceSeqList, ArrayList<GenericGraphBox> seq) {
    if (seq.isEmpty()) {
      return false;
    } else if (seq.size() == replaceSeqList.size()) {
      // Case we do not need to delete or add boxes
      // we only modify the content
      for (int i = 0; i < seq.size(); i++) {
        setNewText(seq.get(i), zone, replaceSeqList.get(i));
      }
    } else if (seq.size() > replaceSeqList.size()) {
      // Case we have to delete one or more boxes
      GenericGraphBox box = seq.get(seq.size() - 1);
      zone.copyTransition((GraphBox) box, (GraphBox) seq.get(replaceSeqList.size() - 1));
      for (int i = 0; i < seq.size(); i++) {
        if (i >= replaceSeqList.size()) {
          zone.removeBox((GraphBox) seq.get(i));
        } else {
          setNewText(seq.get(i), zone, replaceSeqList.get(i));
        }
      }
    } else {
      // Case we have to add one or more boxes
      GraphBox lastBox = (GraphBox) seq.get(seq.size() - 1);
      ArrayList<GraphBox> boxes = new ArrayList<GraphBox>();
      for (int i = 0; i < replaceSeqList.size(); i++) {
        if (i >= seq.size()) {
          GraphBox box = new GraphBox(lastBox.getX() + (50 * i), lastBox.getY(), GraphBox.NORMAL, (GraphicalZone) zone);
          box.setSelected(true);
          box.setContent(replaceSeqList.get(i));
          zone.addBox(box);
          seq.add(box);
          boxes.add(box);
        } else {
          setNewText(seq.get(i), zone, replaceSeqList.get(i));
        }
      }
      zone.copyAndRemoveTransition(lastBox, boxes.get(boxes.size() - 1));
      zone.addTransition(lastBox, boxes.get(0));
      for (int i = 1; i < boxes.size(); i++) {
        zone.addTransition(boxes.get(i - 1), boxes.get(i));
      }
    }
    return true;
  }

  private static boolean boxEquals(String search, String content, boolean caseSensitive, boolean useRegex) {
    if (useRegex) {
      String pattern = "^" + search + "$";
      return isRegex(pattern) && Pattern.compile(pattern).matcher(content).find();
    } else if (caseSensitive) {
      return content.equals(search);
    }
    return content.equalsIgnoreCase(search);
  }

  /**
   * Returns a String containing an error message if one or more boxes cannot be replaced.
   *
   * @param zone           the GenericGraphicalZone containing the box.
   * @param box            a GraphBox in the GraphicalZone.
   * @param replaceSeqList the list containing the sequence to replace with.
   * @return the string containing an error message.
   */
  public static String checkNewBoxContent(GenericGraphicalZone zone, GraphBox box, ArrayList<String> replaceSeqList) {
    for (String s : replaceSeqList) {
      try {
        zone.checkTextBox(box, s);
      } catch (BackSlashAtEndOfLineException e) {
        return "Unexpected \'\\\' at end of line";
      } catch (NoClosingSupException e) {
        return "Boxes must be properly balanced with < >";
      } catch (NoClosingQuoteException e) {
        return "No closing \"";
      } catch (NoClosingRoundBracketException e) {
        return "Boxes must be properly balanced with { }";
      } catch (MissingGraphNameException e) {
        return "Missing graph name after \':\'";
      }
    }
    return "";
  }
}
