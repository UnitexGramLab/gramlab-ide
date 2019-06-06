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

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GenericGraphicalZone;

import java.util.ArrayList;

/**
 * This class defines the data used to find and replace the content of a graph.
 *
 * @author Maxime Petit
 */
public class FindAndReplaceData {
  private final ArrayList<GenericGraphBox> boxes;
  private final GenericGraphicalZone graphicalZone;
  private int currentIndex = 0;

  public FindAndReplaceData(ArrayList<GenericGraphBox> boxes, GenericGraphicalZone graphicalZone) {
    this.boxes = boxes;
    this.graphicalZone = graphicalZone;
  }

  public ArrayList<GenericGraphBox> getBoxes() {
    return boxes;
  }

  public GenericGraphBox getCurrentBox() {
    return boxes.get(currentIndex);
  }

  public GenericGraphicalZone getGraphicalZone() {
    return graphicalZone;
  }

  /**
   * Returns the next box in the list and set it as the current box.
   *
   * @return the next box in the list.
   */
  public GenericGraphBox nextBox() {
    if (currentIndex == (boxes.size() - 1)) {
      return null;
    }
    currentIndex = (currentIndex + 1) % boxes.size();
    return boxes.get(currentIndex);
  }

  /**
   * Returns the previous box in the list and set it as the current box.
   *
   * @return the previous box in the list.
   */
  public GenericGraphBox prevBox() {
    if (currentIndex == 1) {
      return null;
    }
    currentIndex = floorMod(currentIndex - 1, boxes.size());
    return boxes.get(currentIndex);
  }

  private int floorMod(int x, int y) {
    return x - floorDiv(x, y) * y;
  }

  private int floorDiv(int x, int y) {
    int r = x / y;
    // if the signs are different and modulo not zero, round down
    if ((x ^ y) < 0 && (r * y != x)) {
      r--;
    }
    return r;
  }
}
