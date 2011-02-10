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

package fr.umlv.unitex;

import java.awt.Color;

public class GraphPresentationInfo {

    public Color backgroundColor;
    public Color foregroundColor;
    public Color subgraphColor;
    public Color selectedColor;
    public Color commentColor;
    public final Color outputVariableColor;
    public final Color packageColor;
    public final Color contextColor;
    public final Color morphologicalModeColor;
    public final Color unreachableGraphColor;
    public FontInfo input;
    public FontInfo output;
    public boolean date;
    public boolean filename;
    public boolean pathname;
    public boolean frame;
    public boolean rightToLeft;
    public boolean antialiasing;
    public String iconBarPosition;

    public GraphPresentationInfo(Color backgroundColor, Color foregroundColor,
                                 Color subgraphColor, Color selectedColor, Color commentColor,
                                 Color outputVariableColor, Color packageColor, Color contextColor,
                                 Color morphologicalModeColor, Color unreachableGraphColor, FontInfo input, FontInfo output,
                                 boolean date, boolean filename, boolean pathname, boolean frame,
                                 boolean rightToLeft, boolean antialiasing, String iconBarPosition) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.subgraphColor = subgraphColor;
        this.selectedColor = selectedColor;
        this.commentColor = commentColor;
        this.outputVariableColor = outputVariableColor;
        this.packageColor = packageColor;
        this.contextColor = contextColor;
        this.morphologicalModeColor = morphologicalModeColor;
        this.unreachableGraphColor = unreachableGraphColor;
        this.input = input;
        this.output = output;
        this.date = date;
        this.filename = filename;
        this.pathname = pathname;
        this.frame = frame;
        this.rightToLeft = rightToLeft;
        this.antialiasing = antialiasing;
        this.iconBarPosition = iconBarPosition;
    }

    @Override
    public GraphPresentationInfo clone() {
        return new GraphPresentationInfo(backgroundColor, foregroundColor,
                subgraphColor, selectedColor, commentColor, outputVariableColor, packageColor,
                contextColor, morphologicalModeColor, unreachableGraphColor, input.clone(), output.clone(), date,
                filename, pathname, frame, rightToLeft, antialiasing,
                iconBarPosition);
    }
}
