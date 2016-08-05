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
package org.gramlab.core.umlv.unitex.grf;

import java.awt.Color;

import org.gramlab.core.umlv.unitex.FontInfo;

public class GraphPresentationInfo {
	private Color backgroundColor;
	private Color foregroundColor;
	private Color subgraphColor;
	private Color selectedColor;
	private Color commentColor;
	private final Color outputVariableColor;
	private final Color packageColor;
	private final Color contextColor;
        private final Color genericGrfColor;
	private final Color morphologicalModeColor;
	private final Color unreachableGraphColor;
	private FontInfo input;
	private FontInfo output;
	private boolean date;
	private boolean filename;
	private boolean pathname;
	private boolean frame;
	private boolean rightToLeft;
	private boolean antialiasing;
	private String iconBarPosition;

	public GraphPresentationInfo(Color backgroundColor, Color foregroundColor,
			Color subgraphColor, Color selectedColor, Color commentColor,
			Color outputVariableColor, Color packageColor, Color contextColor,
			Color morphologicalModeColor, Color unreachableGraphColor,
			FontInfo input, FontInfo output, boolean date, boolean filename,
			boolean pathname, boolean frame, boolean rightToLeft,
			boolean antialiasing, String iconBarPosition, Color gGrfColor) {
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
                this.genericGrfColor = gGrfColor;
	}

	@Override
	public GraphPresentationInfo clone() {
		return new GraphPresentationInfo(backgroundColor, foregroundColor,
				subgraphColor, selectedColor, commentColor,
				outputVariableColor, packageColor, contextColor,
				morphologicalModeColor, unreachableGraphColor, input.clone(),
				output.clone(), date, filename, pathname, frame, rightToLeft,
				antialiasing, iconBarPosition, genericGrfColor);
	}

	public boolean isFrame() {
		return frame;
	}

	public void setFrame(boolean frame) {
		this.frame = frame;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public Color getSubgraphColor() {
		return subgraphColor;
	}

	public void setSubgraphColor(Color subgraphColor) {
		this.subgraphColor = subgraphColor;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public Color getCommentColor() {
		return commentColor;
	}

	public void setCommentColor(Color commentColor) {
		this.commentColor = commentColor;
	}

	public FontInfo getInput() {
		return input;
	}

	public void setInput(FontInfo input) {
		this.input = input;
	}

	public FontInfo getOutput() {
		return output;
	}

	public void setOutput(FontInfo output) {
		this.output = output;
	}

	public boolean isDate() {
		return date;
	}

	public void setDate(boolean date) {
		this.date = date;
	}

	public boolean isFilename() {
		return filename;
	}

	public void setFilename(boolean filename) {
		this.filename = filename;
	}

	public boolean isPathname() {
		return pathname;
	}

	public void setPathname(boolean pathname) {
		this.pathname = pathname;
	}

	public boolean isRightToLeft() {
		return rightToLeft;
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	public String getIconBarPosition() {
		return iconBarPosition;
	}

	public void setIconBarPosition(String iconBarPosition) {
		this.iconBarPosition = iconBarPosition;
	}

	public Color getOutputVariableColor() {
		return outputVariableColor;
	}

	public Color getPackageColor() {
		return packageColor;
	}

	public Color getContextColor() {
		return contextColor;
	}
        
        public Color getGenericGrfColor() {
		return genericGrfColor;
	}

	public Color getMorphologicalModeColor() {
		return morphologicalModeColor;
	}

	public Color getUnreachableGraphColor() {
		return unreachableGraphColor;
	}
}
